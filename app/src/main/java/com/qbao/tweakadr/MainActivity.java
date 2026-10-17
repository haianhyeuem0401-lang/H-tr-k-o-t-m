package com.qbao.tweakadr;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import android.media.ToneGenerator;
import android.media.AudioManager;
import org.json.JSONObject;
import java.net.*;
import java.io.*;

public class MainActivity extends Activity {
    int bg=Color.rgb(9,7,16), panel=Color.rgb(27,20,42), purple=Color.rgb(170,75,255), white=Color.WHITE;
    LinearLayout root, body, nav;
    android.content.SharedPreferences prefs;
    // TODO: thay bằng endpoint API kiểm tra key thực tế của bạn.
    // Website hiện cung cấp giao diện nhập key nhưng chưa xác định được API public để app nhận kết quả.
    static final String KEY_CHECK_API = "https://qbaonew1.onrender.com/api/check-key";
    static final String TIKTOK = "https://www.tiktok.com/@qbaotweak.x.vft?_r=1&_t=ZS-9AUDeeYlB37";
    String[] names={"AimLite ADR","Tâm Êm V1","AntiShake Aim","SmoothDrag ADR","AimFix Lite","ZeroJitter V1","StableTouch ADR","LiteAim Pro","Kéo Tâm Êm 1.0","ADR Smooth Sens"};
    @Override public void onCreate(Bundle b){super.onCreate(b); prefs=getSharedPreferences("adr",0); showKey();}
    GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);d.setStroke(1,Color.rgb(93,55,135));return d;}
    TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    TextView button(String s,Runnable r){TextView t=text(s,15,white);t.setGravity(Gravity.CENTER);t.setPadding(12,12,12,12);t.setBackground(shape(purple,18));t.setOnClickListener(v->r.run());return t;}
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setPadding(16,12,16,12);return l;}
    void showKey(){
        root=column();root.setBackgroundColor(bg);setContentView(root);
        TextView title=text("♛ QBAO × SADZ TWEAK MENU",24,white);title.setTypeface(null,Typeface.BOLD);title.setGravity(Gravity.CENTER);root.addView(title,new LinearLayout.LayoutParams(-1,100));
        TextView sub=text("NHẬP KEY ĐỂ MỞ ỨNG DỤNG",14,purple);sub.setGravity(Gravity.CENTER);root.addView(sub,new LinearLayout.LayoutParams(-1,50));
        EditText key=new EditText(this);key.setSingleLine(true);key.setHint("Nhập key của bạn");key.setHintTextColor(0xffaaaaaa);key.setTextColor(white);key.setPadding(16,8,16,8);key.setBackground(shape(panel,14));root.addView(key,new LinearLayout.LayoutParams(-1,56));
        TextView check=button("KIỂM TRA KEY",()->checkKey(key.getText().toString().trim()));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,52);p.topMargin=18;root.addView(check,p);
        TextView web=button("MỞ WEBSITE CHECK KEY",()->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://qbaonew1.onrender.com/"))));p=new LinearLayout.LayoutParams(-1,52);p.topMargin=10;root.addView(web,p);
        TextView note=text("Lưu ý: cần API kiểm tra key thật để xác thực tự động. Không nên cho vào app chỉ vì người dùng nhập một chuỗi bất kỳ.",12,0xffcfc6df);note.setPadding(4,22,4,4);root.addView(note);
    }
    void checkKey(String key){
        if(key.isEmpty()){toast("Vui lòng nhập key");return;}
        Toast.makeText(this,"Đang kiểm tra key…",Toast.LENGTH_SHORT).show();
        new Thread(()->{
            HttpURLConnection c=null;
            try{
                c=(HttpURLConnection)new URL(KEY_CHECK_API).openConnection();c.setRequestMethod("POST");c.setConnectTimeout(8000);c.setReadTimeout(8000);c.setDoOutput(true);c.setRequestProperty("Content-Type","application/json; charset=utf-8");
                JSONObject data=new JSONObject();data.put("key",key);
                OutputStream os=c.getOutputStream();os.write(data.toString().getBytes("UTF-8"));os.close();
                InputStream in=(c.getResponseCode()<400)?c.getInputStream():c.getErrorStream();
                ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] buf=new byte[1024];int n;while((n=in.read(buf))>0)out.write(buf,0,n);
                JSONObject result=new JSONObject(new String(out.toByteArray(),"UTF-8"));
                boolean valid=result.optBoolean("valid",false);
                runOnUiThread(()->{if(valid){prefs.edit().putBoolean("unlocked",true).apply();showApp();}else toast("Key không hợp lệ hoặc API chưa đúng");});
            }catch(Exception e){runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Chưa kết nối được API").setMessage("Website check key hiện chưa có endpoint API được xác nhận trong source. Hãy cấu hình KEY_CHECK_API đúng với backend của bạn.").setPositiveButton("Mở website", (d,w)->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://qbaonew1.onrender.com/")))).setNegativeButton("Đóng",null).show());}
            finally{if(c!=null)c.disconnect();}
        }).start();
    }
    void showApp(){
        root= new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(bg);setContentView(root);
        TextView header=text("♛ QBAO × SADZ TWEAK MENU",21,white);header.setTypeface(null,Typeface.BOLD);header.setGravity(Gravity.CENTER);root.addView(header,new LinearLayout.LayoutParams(-1,64));
        ScrollView scroll=new ScrollView(this);body=column();scroll.addView(body);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        nav=new LinearLayout(this);nav.setPadding(6,8,6,8);nav.setBackgroundColor(0xff130d1d);root.addView(nav,new LinearLayout.LayoutParams(-1,64));
        tab("🎯\nAim",()->aim());tab("⌂\nMáy",()->home());tab("♡\nAdmin",()->admin());aim();
    }
    void tab(String s,Runnable r){TextView t=button(s,r);t.setBackground(shape(panel,12));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);p.setMargins(4,0,4,0);nav.addView(t,p);}
    void reset(String title){body.removeAllViews();TextView h=text(title,22,white);h.setTypeface(null,Typeface.BOLD);body.addView(h,new LinearLayout.LayoutParams(-1,54));}
    void aim(){
        reset("🎯  AIM SENSITIVITY");
        addGameLauncher();
        TextView info=text("Cấu hình tham khảo — tự nhập lại độ nhạy trong game.",12,0xffd5c6e8);body.addView(info);
        for(String n:names){
            LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(12,5,8,5);row.setBackground(shape(panel,14));
            LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,58);rp.setMargins(0,5,0,5);body.addView(row,rp);
            TextView label=text(n,14,white);row.addView(label,new LinearLayout.LayoutParams(0,-1,1));
            Switch sw=new Switch(this);sw.setButtonTintList(null);sw.setChecked(prefs.getBoolean(n,false));sw.setOnCheckedChangeListener((v,on)->{prefs.edit().putBoolean(n,on).apply();new ToneGenerator(AudioManager.STREAM_NOTIFICATION,70).startTone(on?ToneGenerator.TONE_PROP_ACK:ToneGenerator.TONE_PROP_NACK,100);});row.addView(sw);
        }
        TextView cross=text("TÂM NGẮM TÙY CHỈNH",17,purple);cross.setPadding(0,18,0,8);body.addView(cross);
        String[] colors={"Tím","Đỏ","Xanh lá","Trắng"};Spinner spinner=new Spinner(this);spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,colors));body.addView(spinner);
        SeekBar size=new SeekBar(this);size.setMax(40);size.setProgress(prefs.getInt("crossSize",12));body.addView(text("Kích thước tâm",14,white));body.addView(size);
        TextView preview=text("+",36,purple);preview.setGravity(Gravity.CENTER);body.addView(preview,new LinearLayout.LayoutParams(-1,65));
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> a){} public void onItemSelected(android.widget.AdapterView<?> a,View v,int pos,long id){int[] c={purple,Color.RED,Color.GREEN,Color.WHITE};preview.setTextColor(c[pos]);prefs.edit().putInt("crossColor",pos).apply();}});
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int p,boolean u){preview.setTextSize(20+p);prefs.edit().putInt("crossSize",p).apply();}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});
        body.addView(button("BẬT / TẮT TÂM NỔI",()->{if(!Settings.canDrawOverlays(this)){startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName())));toast("Cấp quyền hiển thị trên ứng dụng khác rồi quay lại");}else{toast("Bản mẫu: tâm xem trước trong app. Lớp phủ nổi cần bật service riêng.");}}),new LinearLayout.LayoutParams(-1,50));
    }

    void addGameLauncher(){
        TextView gameTitle=text("🎮  CHỌN PHIÊN BẢN FREE FIRE",16,purple);
        gameTitle.setTypeface(null,Typeface.BOLD);
        gameTitle.setPadding(0,8,0,8);
        body.addView(gameTitle);
        String[] games={"Free Fire thường","Free Fire MAX"};
        Spinner gameSpinner=new Spinner(this);
        gameSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,games));
        gameSpinner.setSelection(prefs.getInt("selectedGame",0));
        gameSpinner.setBackground(shape(panel,12));
        body.addView(gameSpinner,new LinearLayout.LayoutParams(-1,50));
        gameSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){
            public void onNothingSelected(android.widget.AdapterView<?> a){}
            public void onItemSelected(android.widget.AdapterView<?> a,View v,int pos,long id){
                prefs.edit().putInt("selectedGame",pos).apply();
            }
        });
        LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,50);ap.topMargin=8;
        body.addView(button("ÁP DỤNG & VÀO GAME",()->runGameSequence(gameSpinner.getSelectedItemPosition())),ap);
        TextView note=text("Hiệu ứng dòng lệnh xanh khoảng 30 giây, sau đó thử mở game đã chọn. Hiệu ứng chỉ là giao diện chờ, không chỉnh sửa dữ liệu hay can thiệp vào game.",11,0xffcfc6df);
        note.setPadding(2,6,2,10);body.addView(note);
    }
    void runGameSequence(int game){
        final String packageName = game==1 ? "com.dts.freefiremax" : "com.dts.freefireth";
        final String gameName = game==1 ? "Free Fire MAX" : "Free Fire thường";
        final Dialog dialog=new Dialog(this);
        LinearLayout panelLayout=new LinearLayout(this);panelLayout.setOrientation(1);panelLayout.setPadding(18,18,18,18);
        panelLayout.setBackground(shape(Color.rgb(3,12,9),16));
        TextView heading=text("QBAO CONSOLE // "+gameName,15,0xff00ff78);
        heading.setTypeface(android.graphics.Typeface.MONOSPACE,Typeface.BOLD);panelLayout.addView(heading);
        TextView console=text("",12,0xff00ff78);console.setTypeface(android.graphics.Typeface.MONOSPACE);
        console.setGravity(Gravity.TOP);console.setMinHeight(190);panelLayout.addView(console,new LinearLayout.LayoutParams(-1,190));
        ProgressBar progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);progress.setMax(30);progress.setProgress(0);panelLayout.addView(progress,new LinearLayout.LayoutParams(-1,12));
        TextView countdown=text("Đang chuẩn bị... 30s",12,0xff00ff78);countdown.setGravity(Gravity.CENTER);panelLayout.addView(countdown,new LinearLayout.LayoutParams(-1,36));
        dialog.setContentView(panelLayout);
        Window w=dialog.getWindow();if(w!=null){w.setBackgroundDrawableResource(android.R.color.transparent);w.setLayout((int)(getResources().getDisplayMetrics().widthPixels*0.88),-2);}
        dialog.setCancelable(false);dialog.show();
        if(dialog.getWindow()!=null)dialog.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*0.88),-2);
        final Handler handler=new Handler(Looper.getMainLooper());
        final int[] elapsed={0};
        final String[] lines={"[ OK ] Initializing launcher interface","[ OK ] Checking selected game package","[ OK ] Preparing launch request","[ OK ] Waiting for user session","[ OK ] Finalizing startup sequence"};
        Runnable task=new Runnable(){public void run(){
            if(!dialog.isShowing())return;
            int sec=elapsed[0];
            progress.setProgress(sec);countdown.setText(sec<30?"Đang chuẩn bị... "+(30-sec)+"s":"Hoàn tất");
            StringBuilder b=new StringBuilder();
            int shown=Math.min(sec/3+1,lines.length);
            for(int i=0;i<shown;i++)b.append(lines[i]).append("\n");
            b.append("> ").append(sec%2==0?"CONNECTING":"LOADING").append(" ").append(String.format(java.util.Locale.US,"%02d",sec)).append("/30");
            console.setText(b.toString());
            if(sec>=30){
                dialog.dismiss();
                try{
                    Intent launch=getPackageManager().getLaunchIntentForPackage(packageName);
                    if(launch!=null){launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(launch);}
                    else new AlertDialog.Builder(MainActivity.this).setTitle("Chưa tìm thấy game").setMessage("Không tìm thấy "+gameName+" trên thiết bị. Hãy cài game chính thức rồi thử lại.").setPositiveButton("OK",null).show();
                }catch(Exception e){toast("Không thể mở game: "+gameName);}
                return;
            }
            elapsed[0]++;handler.postDelayed(this,1000);
        }};
        handler.post(task);
    }

    void home(){
        reset("⌂  THÔNG TIN THIẾT BỊ");
        body.addView(text("Thông số hệ thống (ước tính theo Android API)",13,0xffcbbddd));
        addMetric("CPU cores",String.valueOf(Runtime.getRuntime().availableProcessors()));
        addMetric("RAM app khả dụng",String.format("%.0f MB",Runtime.getRuntime().maxMemory()/1048576.0));
        addMetric("Tần số màn hình",getWindowManager().getDefaultDisplay().getRefreshRate()+" Hz");
        addMetric("Android",Build.VERSION.RELEASE+" (SDK "+Build.VERSION.SDK_INT+")");
        TextView title=text("TỐI ƯU MÁY AN TOÀN",18,purple);title.setPadding(0,20,0,10);body.addView(title);
        String[] tips={"Đóng ứng dụng nền không dùng qua màn hình đa nhiệm.","Bật chế độ hiệu năng/game mode nếu điện thoại có hỗ trợ.","Tắt tiết kiệm pin khi chơi nếu máy bị giới hạn hiệu năng.","Giữ máy mát, tránh vừa sạc vừa chơi lâu.","Dùng cài đặt đồ họa phù hợp để giữ FPS ổn định.","Không dùng app hứa tăng FPS bằng cách sửa game hoặc ép xung trái phép."};
        for(String s:tips){TextView t=text("• "+s,14,white);t.setPadding(8,8,4,8);body.addView(t);}
        body.addView(button("MỞ CÀI ĐẶT MÀN HÌNH",()->startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS))));
    }
    void addMetric(String k,String v){LinearLayout r=new LinearLayout(this);r.setPadding(12,14,12,14);r.setBackground(shape(panel,12));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,54);p.setMargins(0,5,0,5);body.addView(r,p);r.addView(text(k,14,white),new LinearLayout.LayoutParams(0,-1,1));r.addView(text(v,14,purple));}
    void admin(){
        reset("♡  ADMIN / DEVELOPER");
        TextView name=text("QBaoTweakxVft King",22,white);name.setTypeface(null,Typeface.BOLD);body.addView(name);
        body.addView(text("Phạm Gia Bảo",17,purple));
        TextView heart=text("♡",72,0xffff4d9d);heart.setGravity(Gravity.CENTER);body.addView(heart,new LinearLayout.LayoutParams(-1,140));
        body.addView(button("♡  MỞ KÊNH TIKTOK",()->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(TIKTOK)))),new LinearLayout.LayoutParams(-1,54));
    }
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
