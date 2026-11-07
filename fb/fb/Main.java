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

        LinearLayout bar = new LinearLayout(act);
        bar.setOrientation(LinearLayout.HORIZONTAL);

        urlField = new EditText(act);
        urlField.setSingleLine(true);
        urlField.setTextColor(Color.WHITE);
        urlField.setTextSize(13f);
        urlField.setImeOptions(EditorInfo.IME_ACTION_GO);
        urlField.setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {
            public boolean onEditorAction(android.widget.TextView v, int id, android.view.KeyEvent e) {
                web.loadUrl(normalize(urlField.getText().toString()));
                return true;
            }
        });
        bar.addView(urlField, new LinearLayout.LayoutParams(0, dp(44), 1f));

        bar.addView(makeButton("Go", new Runnable() {
            public void run() { web.loadUrl(normalize(urlField.getText().toString())); }
        }));
        bar.addView(makeButton("YT", new Runnable() {
            public void run() { web.loadUrl("https://m.youtube.com"); }
        }));
        bar.addView(makeButton("<", new Runnable() {
            public void run() { if (web.canGoBack()) web.goBack(); }
        }));
        bar.addView(makeButton("X", new Runnable() {
            public void run() { hideNow(); }
        }));

        web = new WebView(act);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setMediaPlaybackRequiresUserGesture(false);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView v, String url) {
                if (!urlField.hasFocus()) urlField.setText(url);
            }
        });

        panel.addView(bar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        panel.addView(web, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        int w = (int) (act.getResources().getDisplayMetrics().widthPixels * 0.7f);
        int h = (int) (act.getResources().getDisplayMetrics().heightPixels * 0.8f);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h, Gravity.CENTER);
        act.addContentView(panel, lp);
        added = true;
        web.loadUrl("https://www.google.com");
    }

    private static Button makeButton(String label, final Runnable action) {
        Button b = new Button(act);
        b.setText(label);
        b.setTextSize(12f);
        b.setMinWidth(0);
        b.setMinimumWidth(0);
        b.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) { action.run(); }
        });
        return b;
    }

    private static void hideNow() {
        if (panel != null) panel.setVisibility(android.view.View.GONE);
    }

    public static void toggle() {
        act.runOnUiThread(new Runnable() {
            public void run() {
                if (!added) { build(); return; }
                panel.setVisibility(panel.getVisibility() == android.view.View.VISIBLE
                        ? android.view.View.GONE : android.view.View.VISIBLE);
            }
        });
    }

    public static void open(final String url) {
        act.runOnUiThread(new Runnable() {
            public void run() {
                if (!added) build();
                panel.setVisibility(android.view.View.VISIBLE);
                web.loadUrl(normalize(url));
            }
        });
    }
            }
