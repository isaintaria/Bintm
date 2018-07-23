package com.ng.socialtest;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.ng.socialtest.base.BackButtonSupportFragment;
import com.ng.socialtest.base.BaseFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainFragment extends BaseFragment implements BackButtonSupportFragment {


    private Toast toast;

    @BindView(R.id.webview_map)
    WebView webview;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout mLayout;
    private final Handler handler = new Handler();

    @BindView(R.id.button_park_detail)
    Button buttonParkDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        WebViewSetting(webview);

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mLayout.setClickable(false);

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(getTitle(), "onPanelSlide, offset " + slideOffset);
            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(getTitle(), "onPanelStateChanged " + newState);
            }
        });

        mLayout.setFadeOnClickListener((v) ->  mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));

        MainActivity act = (MainActivity)getActivity();
       buttonParkDetail.setOnClickListener( (v)-> act.ShowParkInfoFragment());

        return view;
    }




    public static MainFragment NewInstance()
    {
        MainFragment instance = new MainFragment();
        return instance;
    }

    long backKeyPressedTime;
    private boolean doAppFinishByBackKey() {
        if(System.currentTimeMillis() - backKeyPressedTime >= 2000){
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(getActivity(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else if(System.currentTimeMillis() - backKeyPressedTime < 2000){
            toast.cancel();
            return false;
        }
        return true;
    }

    public void WebViewSetting(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        // alertSetting(webView);
        String userAgent = webView.getSettings().getUserAgentString();
        // 웹 뷰에서 해당 앱을 통해 실행 된다는 것을 구분하기 위한 값 설정
        webView.getSettings().setUserAgentString(userAgent + "JSReceiver");
        //웹뷰와 모바일 기기간의 내부적인 호출을 위한 설정 부분
        //가장 중요한 부분입니다.
        webView.addJavascriptInterface(new AndroidWebBridge(webView), "JSReceiver");
        webView.loadUrl("file:///android_asset/sample.html");
        webView.setWebViewClient(new WebViewClientClass() );
    }

    @Override
    public boolean onBackPressed() {
        return doAppFinishByBackKey();
    }

    @Override
    protected String getTitle() {
        return "빈틈";
    }


    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class AndroidWebBridge {
        private WebView webView;

        public AndroidWebBridge(WebView webView) {
            // TODO Auto-generated constructor stub
            this.webView = webView;
        }

        @JavascriptInterface
        public void androidLogWrite(final String log) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("##", log);
                }
            });
        }

        @JavascriptInterface
        public void ShowPanel() {
            handler.post(() -> mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));

        }

        @JavascriptInterface
        public void HidePanel() {
            handler.post(() -> mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN));
        }
    }

}
