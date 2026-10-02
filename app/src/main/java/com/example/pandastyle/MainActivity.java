package com.example.pandastyle;
import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class MainActivity extends Activity {
 int bg=Color.rgb(20,23,27), card=Color.rgb(37,41,47), red=Color.rgb(215,25,32);
 LinearLayout root;
 public void onCreate(Bundle b){super.onCreate(b); getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); build();}
 TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);t.setGravity(Gravity.CENTER);return t;}
 void build(){
  root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);root.setPadding(18,12,18,12);root.setBackgroundColor(bg);setContentView(root);
  LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL); TextView brand=text("FIAT  •  GRANDE PANDA",20);brand.setTypeface(null,Typeface.BOLD);top.addView(brand,new LinearLayout.LayoutParams(0,55,1));top.addView(text("●  Bluetooth    ◉  Wi‑Fi    12:04",15));root.addView(top);
  LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setPadding(0,8,0,8);root.addView(row,new LinearLayout.LayoutParams(-1,0,1));
  LinearLayout left=new LinearLayout(this);left.setOrientation(LinearLayout.VERTICAL);left.setPadding(6,0,10,0);row.addView(left,new LinearLayout.LayoutParams(0,-1,1.05f));
  TextView clock=text("12:04\nΠΑΡΑΣΚΕΥΗ  •  2 ΟΚΤΩΒΡΙΟΥ",25);clock.setBackgroundColor(card);left.addView(clock,new LinearLayout.LayoutParams(-1,0,1));
  TextView weather=text("☀   18°   Μυτιλήνη\nΚαιρός",19);weather.setBackgroundColor(card);LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(-1,0,1);wp.topMargin=8;left.addView(weather,wp);
  LinearLayout mid=new LinearLayout(this);mid.setGravity(Gravity.CENTER);mid.setOrientation(LinearLayout.VERTICAL);row.addView(mid,new LinearLayout.LayoutParams(0,-1,1.1f));
  TextView car=text("🚗",58);mid.addView(car);TextView model=text("GRANDE PANDA",22);model.setTypeface(null,1);mid.addView(model);TextView status=text("Οδήγηση  •  Συνδεσιμότητα",14);mid.addView(status);
  LinearLayout right=new LinearLayout(this);right.setOrientation(LinearLayout.VERTICAL);row.addView(right,new LinearLayout.LayoutParams(0,-1,1));
  String[][] tiles={{"♫","Μουσική"},{"➤","Πλοήγηση"},{"☎","Τηλέφωνο"},{"◉","Ραδιόφωνο"},{"⚙","Ρυθμίσεις"},{"▣","Εφαρμογές"}};
  for(int i=0;i<tiles.length;i+=2){LinearLayout r=new LinearLayout(this);right.addView(r,new LinearLayout.LayoutParams(-1,0,1));for(int j=i;j<i+2;j++){final String label=tiles[j][1];TextView v=text(tiles[j][0]+"\n"+label,18);v.setBackgroundColor(card);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);p.setMargins(4,4,4,4);r.addView(v,p);v.setOnClickListener(x->open(label));}}
  TextView foot=text("Αγγίξτε μια επιλογή  •  Panda-style concept launcher",12);root.addView(foot,new LinearLayout.LayoutParams(-1,28));
 }
 void open(String s){
  try{
   Intent i=null;
   if(s.equals("Πλοήγηση")) i=new Intent(Intent.ACTION_VIEW,Uri.parse("geo:0,0?q="));
   else if(s.equals("Μουσική")) i=new Intent("android.intent.action.MUSIC_PLAYER");
   else if(s.equals("Τηλέφωνο")) i=new Intent(Intent.ACTION_DIAL);
   else if(s.equals("Ρυθμίσεις")) i=new Intent(Settings.ACTION_SETTINGS);
   else if(s.equals("Εφαρμογές")) i=new Intent(Settings.ACTION_APPLICATION_SETTINGS);
   else {Toast.makeText(this,"Άνοιγμα ραδιοφώνου: απαιτείται σύνδεση με την εργοστασιακή εφαρμογή.",Toast.LENGTH_LONG).show();return;}
   startActivity(i);
  }catch(Exception e){Toast.makeText(this,"Η αντίστοιχη εφαρμογή δεν βρέθηκε.",Toast.LENGTH_SHORT).show();}
 }
}
