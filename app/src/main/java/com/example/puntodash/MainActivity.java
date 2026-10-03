package com.example.puntodash;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.content.*;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.view.*;
import android.widget.Toast;
import java.util.*;

public class MainActivity extends Activity {
    PuntoView view;
    SoundPool soundPool;
    int clickSound = 0;
    SharedPreferences prefs;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        prefs = getSharedPreferences("punto", MODE_PRIVATE);
        AudioAttributes aa = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        soundPool = new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(aa).build();
        clickSound = soundPool.load(this, com.example.puntodash.R.raw.button_click, 1);
        view = new PuntoView(this);
        setContentView(view);
    }
    void click() {
        if (prefs.getBoolean("button_sound", true) && clickSound != 0) soundPool.play(clickSound, 0.22f, 0.22f, 1, 0, 1f);
    }
    void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }

    void openMaps() {
        click();
        try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="))); }
        catch(Exception e) { toast("Δεν βρέθηκε εφαρμογή χαρτών"); }
    }
    void openPhone() {
        click();
        try { startActivity(new Intent(Intent.ACTION_DIAL)); }
        catch(Exception e) { toast("Δεν βρέθηκε εφαρμογή τηλεφώνου"); }
    }
    void openSettings() { click(); view.mode = 2; view.invalidate(); }

    void setVolume(int delta) {
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int cur = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, Math.max(0, Math.min(max, cur + delta)), 0);
        click(); view.invalidate();
    }

    void radioAction(String action) {
        click();
        // The Digital iQ BLG family uses a proprietary radio application/tuner.
        // The UI is ready for hardware integration; the exact intent/API is device firmware-specific.
        // We intentionally do not fake station changes when the tuner API is unknown.
        if (action.equals("OPEN")) {
            Intent launch = findRadioApp();
            if (launch != null) { try { startActivity(launch); return; } catch(Exception ignored) {} }
            toast("Το Radio interface είναι έτοιμο. Χρειάζεται το API της μονάδας για πραγματικό FM control.");
        } else if (action.equals("SCAN")) {
            toast("AUTO SCAN: θα αποθηκεύει αυτόματα τους σταθμούς όταν συνδεθεί το FM API της μονάδας.");
        } else {
            toast("Χειρισμός Radio: " + action);
        }
    }
    Intent findRadioApp() {
        Intent i = new Intent(Intent.ACTION_MAIN); i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(i, 0);
        for (ResolveInfo r : list) {
            String label = String.valueOf(r.loadLabel(getPackageManager())).toLowerCase(Locale.ROOT);
            if (label.contains("radio") || label.contains("fm")) {
                Intent x = new Intent(i); x.setClassName(r.activityInfo.packageName, r.activityInfo.name); return x;
            }
        }
        return null;
    }

    class PuntoView extends View {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap car;
        int mode = 0; // 0 home, 1 radio, 2 settings
        float vol=0.5f;
        int selectedPreset=3;
        String frequency="98.7";
        Handler handler = new Handler();
        PuntoView(Context c) { super(c); setFocusable(true); car=BitmapFactory.decodeResource(getResources(), R.drawable.black_punto); stroke.setStyle(Paint.Style.STROKE); }
        void rect(Canvas c,float l,float t,float r,float b,int color,float rad){p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawRoundRect(l,t,r,b,rad,rad,p);}
        void line(Canvas c,float x1,float y1,float x2,float y2,int color,float w){stroke.setColor(color);stroke.setStrokeWidth(w);c.drawLine(x1,y1,x2,y2,stroke);}
        void txt(Canvas c,String s,float x,float y,float size,int color,Paint.Align align,boolean bold){p.setTypeface(bold?Typeface.create("sans",Typeface.BOLD):Typeface.create("sans",Typeface.NORMAL));p.setTextSize(size);p.setTextAlign(align);p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawText(s,x,y,p);}
        @Override protected void onDraw(Canvas c){super.onDraw(c); c.drawColor(Color.rgb(7,10,13)); if(mode==0) home(c); else if(mode==1) radio(c); else settings(c);}
        void header(Canvas c){
            txt(c,"FIAT GRANDE PUNTO",getWidth()/2f,48,24,Color.WHITE,Paint.Align.CENTER,true);
            line(c,getWidth()/2f-115,62,getWidth()/2f+115,62,Color.rgb(55,141,255),2);
            txt(c,"Bluetooth   •   12:04",getWidth()-24,42,14,Color.LTGRAY,Paint.Align.RIGHT,false);
        }
        void home(Canvas c){
            header(c); float W=getWidth(), H=getHeight(); float pad=18, gap=12; float leftW=W*.30f, rightW=W*.22f, volW=58; float centerL=pad+leftW+gap, centerR=W-pad-rightW-gap-volW, top=78, bottom=H-18;
            // Left radio preview
            rect(c,pad,top,pad+leftW,top+250,Color.rgb(22,29,37),8); txt(c,"FM",pad+18,top+34,14,Color.LTGRAY,Paint.Align.LEFT,false); txt(c,frequency+" MHz",pad+leftW/2,top+82,32,Color.WHITE,Paint.Align.CENTER,false); txt(c,"ΔΙΚΟ ΜΟΥ RADIO",pad+leftW/2,top+108,13,Color.LTGRAY,Paint.Align.CENTER,true);
            button(c,pad+16,top+132,pad+82,top+184,"◀",false,()->radioAction("SEEK-") ); button(c,pad+90,top+132,pad+156,top+184,"−",false,()->radioAction("TUNE-") ); button(c,pad+164,top+132,pad+230,top+184,"+",false,()->radioAction("TUNE+") ); button(c,pad+238,top+132,pad+leftW-16,top+184,"▶",false,()->radioAction("SEEK+") );
            for(int i=0;i<6;i++){ final int preset=i+1; float x=pad+16+i*((leftW-32)/6f); float x2=pad+16+(i+1)*((leftW-32)/6f)-5; button(c,x,top+198,x2,top+238,String.valueOf(preset),preset==selectedPreset,()->{selectedPreset=preset;invalidate();});}
            rect(c,pad,top+262,pad+leftW,top+326,Color.rgb(22,29,37),8); txt(c,"⌖",pad+28,top+302,24,Color.WHITE,Paint.Align.CENTER,false); txt(c,"Χάρτες",pad+54,top+300,18,Color.WHITE,Paint.Align.LEFT,false); txt(c,"›",pad+leftW-20,top+301,28,Color.LTGRAY,Paint.Align.CENTER,false);
            // center car
            rect(c,centerL,top,centerR,bottom,Color.rgb(12,17,22),8); if(car!=null){Rect src=new Rect(0,0,car.getWidth(),car.getHeight()); RectF dst=new RectF(centerL+12,top+65,centerR-12,bottom-40); c.drawBitmap(car,src,dst,p);} txt(c,"GRANDE PUNTO",(centerL+centerR)/2,top+42,15,Color.LTGRAY,Paint.Align.CENTER,true);
            // Right
            float rx=centerR+gap; float rw=rightW; buttonLarge(c,rx,top,rx+rw,top+145,"☎","Phone / Bluetooth",()->openPhone()); buttonLarge(c,rx,top+160,rx+rw,top+305,"⚙","Ρυθμίσεις",()->openSettings());
            // volume
            float vx=W-pad-volW+8; rect(c,vx,top,vx+volW,bottom,Color.rgb(15,20,25),8); txt(c,"🔊",vx+volW/2,top+34,22,Color.WHITE,Paint.Align.CENTER,false); button(c,vx+9,top+50,vx+volW-9,top+92,"+",false,()->setVolume(1)); button(c,vx+9,bottom-64,vx+volW-9,bottom-16,"−",false,()->setVolume(-1)); AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE); float level=am.getStreamVolume(AudioManager.STREAM_MUSIC)/(float)Math.max(1,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)); p.setColor(Color.rgb(59,141,255)); c.drawRoundRect(vx+23,top+108,vx+35, bottom-86,6,6,p); float y=bottom-86-(bottom-194)*level; c.drawRoundRect(vx+23,y,vx+35,bottom-86,6,6,p);
            txt(c,"SCAN",pad+leftW-12,top+18,10,Color.rgb(90,165,255),Paint.Align.RIGHT,true); button(c,pad+leftW-72,top+2,pad+leftW-4,top+32,"AUTO",false,()->radioAction("SCAN"));
        }
        void radio(Canvas c){
            header(c); float W=getWidth(),H=getHeight(); txt(c,"‹",28,46,38,Color.WHITE,Paint.Align.CENTER,false); button(c,12,14,58,58,"‹",false,()->{mode=0;invalidate();});
            float l=28,t=82,r=W-28; rect(c,l,t,r,H-22,Color.rgb(12,18,24),10); txt(c,"FM1",l+24,t+34,15,Color.LTGRAY,Paint.Align.LEFT,false); txt(c,frequency+" MHz",W/2,t+80,42,Color.WHITE,Paint.Align.CENTER,false); txt(c,"ΔΙΚΟ ΜΟΥ RADIO",W/2,t+108,15,Color.LTGRAY,Paint.Align.CENTER,true);
            line(c,l+55,t+145,r-55,t+145,Color.rgb(75,88,100),2); for(int i=0;i<12;i++) line(c,l+55+i*(r-l-110)/11f,t+137,l+55+i*(r-l-110)/11f,t+153,Color.rgb(100,110,120),1); txt(c,"87.5",l+55,t+178,12,Color.LTGRAY,Paint.Align.CENTER,false); txt(c,"98.7",W/2,t+178,12,Color.LTGRAY,Paint.Align.CENTER,false); txt(c,"108.0",r-55,t+178,12,Color.LTGRAY,Paint.Align.CENTER,false);
            button(c,W/2-250,t+200,W/2-150,t+255,"◀◀",false,()->radioAction("SEEK-")); button(c,W/2-135,t+200,W/2-35,t+255,"−",false,()->radioAction("TUNE-")); button(c,W/2-20,t+200,W/2+80,t+255,"+",false,()->radioAction("TUNE+")); button(c,W/2+95,t+200,W/2+195,t+255,"▶▶",false,()->radioAction("SEEK+")); button(c,W/2+210,t+200,W/2+310,t+255,"AUTO",false,()->radioAction("SCAN"));
            for(int i=0;i<6;i++){ final int preset=i+1; float x=l+24+i*((r-l-48)/6f); float x2=l+24+(i+1)*((r-l-48)/6f)-7; button(c,x,t+278,x2,t+326,String.valueOf(preset),preset==selectedPreset,()->{selectedPreset=preset;invalidate();});}
            txt(c,"RDS   •   ST   •   AF   •   TA",W/2,H-45,13,Color.LTGRAY,Paint.Align.CENTER,false);
        }
        void settings(Canvas c){
            header(c); button(c,12,14,58,58,"‹",false,()->{mode=0;invalidate();}); txt(c,"ΡΥΘΜΙΣΕΙΣ",28,100,24,Color.WHITE,Paint.Align.LEFT,true);
            settingRow(c,28,125,"Ήχος","Γενική ένταση / multimedia",0); settingRow(c,28,200,"Bluetooth","Phone / Bluetooth",0); settingRow(c,28,275,"Button sound",prefs.getBoolean("button_sound",true)?"Ενεργό — απαλό click":"Απενεργοποιημένο",1); settingRow(c,28,350,"Radio","FM interface / tuner",0); settingRow(c,28,425,"Πληροφορίες","PuntoDash v2.0",0);
        }
        void settingRow(Canvas c,float x,float y,String a,String b,int type){rect(c,x,y,getWidth()-28,y+58,Color.rgb(18,25,32),6); txt(c,a,x+18,y+24,17,Color.WHITE,Paint.Align.LEFT,true); txt(c,b,x+18,y+46,11,Color.LTGRAY,Paint.Align.LEFT,false); if(type==1){boolean on=prefs.getBoolean("button_sound",true); button(c,getWidth()-100,y+10,getWidth()-42,y+48,on?"ON":"OFF",on,()->{prefs.edit().putBoolean("button_sound",!on).apply();click();invalidate();});}}
        void buttonLarge(Canvas c,float l,float t,float r,float b,String icon,String label,Runnable run){rect(c,l,t,r,b,Color.rgb(19,27,35),7); txt(c,icon,l+34,t+58,30,Color.WHITE,Paint.Align.CENTER,false); txt(c,label,l+70,t+56,18,Color.WHITE,Paint.Align.LEFT,false); txt(c,"›",r-24,t+58,28,Color.LTGRAY,Paint.Align.CENTER,false);}
        void button(Canvas c,float l,float t,float r,float b,String label,boolean active,Runnable run){rect(c,l,t,r,b,active?Color.rgb(35,108,190):Color.rgb(28,37,46),5); txt(c,label,(l+r)/2,(t+b)/2+7,16,Color.WHITE,Paint.Align.CENTER,active);}
        @Override public boolean onTouchEvent(android.view.MotionEvent e){ if(e.getAction()!=MotionEvent.ACTION_UP)return true; float x=e.getX(),y=e.getY(); float W=getWidth(),H=getHeight();
            if(mode==0){float pad=18,gap=12,leftW=W*.30f,rightW=W*.22f,volW=58,top=78,centerR=W-pad-rightW-gap-volW; if(x>=pad&&x<=pad+leftW&&y>=top&&y<=top+250){ if(y<top+125){radioAction("OPEN");} } else if(x>=pad&&x<=pad+leftW&&y>=top+262&&y<=top+326)openMaps(); else if(x>centerR&&x<W-pad-volW){ if(y<top+145)openPhone(); else if(y<top+320)openSettings(); } else if(x>=W-pad-volW+8){if(y<top+110)setVolume(1); else if(y>H-90)setVolume(-1);} }
            else if(mode==1){if(x<70&&y<70){mode=0;click();invalidate();}}
            else if(mode==2){if(x<70&&y<70){mode=0;click();invalidate();} else if(y>275&&y<340){boolean on=prefs.getBoolean("button_sound",true);prefs.edit().putBoolean("button_sound",!on).apply();click();invalidate();}}
            return true; }
    }
}
