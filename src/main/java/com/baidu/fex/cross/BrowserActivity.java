package com.baidu.fex.cross;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Toast;

import com.baidu.fex.cross.browser.WebViewCallback;
import com.baidu.fex.cross.component.Album;
import com.baidu.fex.cross.utils.ShortcutUtils;

public class BrowserActivity extends FragmentActivity implements
		OnClickListener, WebViewCallback {

	private WebViewFragment fragment;

	private View mHomeBtn, mBackBtn, mLoadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);

		if (findViewById(R.id.center) != null) {
			if (savedInstanceState != null) {
				return;
			}
			fragment = new WebViewFragment();
			fragment.setWebViewCallback(this);
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.center, fragment).addToBackStack("webview")
					.commit();
		}
		if (!ShortcutUtils.ACTION_APP_LAUNCHER.equals(getIntent().getAction())) {
			findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
		}
		mHomeBtn = findViewById(R.id.home);
		mHomeBtn.setOnClickListener(this);
		mBackBtn = findViewById(R.id.back);
		mBackBtn.setOnClickListener(this);
		mLoadingView = findViewById(R.id.loading);
	}

	private boolean doubleBackToExitPressedOnce = false;

	private boolean doubleClickToExit() {
		if(doubleBackToExitPressedOnce){
			return true;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, getString(R.string.double_click_to_quit),
				Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			public void run() {
				doubleBackToExitPressedOnce = false;

			}
		}, 2000);

		return false;

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
				if (!fragment.goBack() && doubleClickToExit()) {

					finish();
				}
			} else {
				getSupportFragmentManager().popBackStack();
			}
			break;
		case R.id.home:
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mBackBtn.performClick();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		mLoadingView.setVisibility(View.VISIBLE);
		if (url.startsWith("http://tieba.baidu.com/mo/q/album")) {
			view.stopLoading();
			AlbumFragment albumFragment = new AlbumFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("AlbumListener", new Album.AlbumListener() {

				public void onQuitClick() {
					getSupportFragmentManager().popBackStack();

				}
			});
			bundle.putString("url", url);
			albumFragment.setArguments(bundle);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.center, albumFragment)
					.addToBackStack("albumFragment").commit();
		}

	}

	public void onPageFinished(WebView view, String url) {
		mLoadingView.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		fragment.onActivityResult(arg0, arg1, arg2);
	}
}
