package fb;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Browser mengambang sederhana untuk MonetLoader.
 * Tidak ada jaringan selain yang diminta WebView, tidak ada UDP/server,
 * tidak ada hook keyboard. Semua method dipanggil dari Lua lewat JNI.
 */
public class Main {
    private static Activity act;
    private static LinearLayout panel;
    private static WebView web;
    private static EditText urlField;
    private static boolean added = false;

    public static void init(Activity a) {
        act = a;
    }

    private static int dp(int v) {
        return (int) (v * act.getResources().getDisplayMetrics().density);
    }

    private static String normalize(String in) {
        String t = in.trim();
        if (t.startsWith("http://") || t.startsWith("https://")) return t;
        if (t.contains(" ") || !t.contains(".")) {
            try {
                return "https://www.google.com/search?q=" + java.net.URLEncoder.encode(t, "UTF-8");
            } catch (Exception e) {
                return "https://www.google.com";
            }
        }
        return "https://" + t;
    }

    private static void build() {
        panel = new LinearLayout(act);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackgroundColor(Color.parseColor("#202124"));
          
