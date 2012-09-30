package com.example.machinechecker;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import android.widget.ImageView;

public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";
	private Button buttonCheck;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * 画像を表示させるとき ImageView iv = new ImageView(this);
		 * iv.setImageResource(R.drawable.beer); setContentView(iv);
		 */
		setContentView(R.layout.activity_main);

		Intent intent = getIntent();

		if (Intent.ACTION_SEND.equals(intent.getAction())) {
			String extraText = intent.getExtras()
					.getCharSequence(Intent.EXTRA_TEXT).toString();
			URL url;
			try {
				Log.d(TAG, "extraText: " + extraText);

				// URLが与えられた時は、値を解析して入力欄に入れる。
				EditText editHostname = (EditText) findViewById(R.id.editText_editHostname);
				EditText editPortNo = (EditText) findViewById(R.id.editText_editPortNo);
				url = new URL(extraText);
				editHostname.setText(url.getHost());
				int port = url.getPort() > 0 ? url.getPort() : url
						.getDefaultPort();
				editPortNo.setText(port > 0 ? String.valueOf(port) : "");
			} catch (MalformedURLException e) {

			}
		}

		// event handlerの追加
		buttonCheck = (Button) findViewById(R.id.button_check);

		buttonCheck.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ButtonCheck_OnClick();
				// EditBoxにフォーカスが残らないように以下の処理を行う
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
						0);
			}
		});
	}

	private void ButtonCheck_OnClick() {
		// TextView textview1=(TextView)findViewById(R.id.textView_humor);
		// textview1.setText("test");

		// 前準備
		NetworkTools nt = getNetworkTools();
		Result result = null;
		int index = 0;
		String strResultText = "";
		
		// 検証して回る
		do {
			// 検証
			result = nt.next();

			if (result == null) {
				Log.w(TAG, "next() returns null");
				break;
			}
			
			// 結果の文字列作成
			index++;
			strResultText += getResultText(result, index) + "\n";
		} while (!result.finished());

		// 結果表示
		strResultText = strResultText
				.substring(0, (strResultText.length() - 1));
		if (index > 0) {
			TextView textview_result = (TextView) findViewById(R.id.textView_result);
			textview_result.setText("Result:\n" + strResultText);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private NetworkTools getNetworkTools() {
		EditText editTextHostname = (EditText) findViewById(R.id.editText_editHostname);
		Editable inputHostname = editTextHostname.getText();

		EditText editTextPortNo = (EditText) findViewById(R.id.editText_editPortNo);
		Editable inputPortNo = editTextPortNo.getText();

		Integer port = null;
		try {
			port = Integer.valueOf(inputPortNo.toString());
		} catch (Exception e) {
		}

		NetworkTools nt = new NetworkTools(inputHostname.toString(), port);
		return nt;
	}
	
	/**
	 * 
	 * @param result 結果オブジェクト
	 * @param index 何番目の試験結果か
	 * @return
	 */
	private String getResultText(Result result, int index) {
		String str = "";
		switch (result.getResult()) {
		case SUCCESS:
			str = "(" + index + ")　["
					+ result.getTestName() + ":OK] ";
			str += " " + result.getComment() + " in "
					+ result.getMsec() + "(msec)";
			break;
		case FAIL:
			str = "(" + index + ") ["
					+ result.getTestName() + ":NG]";
			break;
		case NA:
			str = "(" + index + ") ["
					+ result.getTestName() + ":Not Applicable]";
			break;
		case FINISHED:
			break;
		default:
			// never comes here
		break;
		}
		
		return str;
	}

}
