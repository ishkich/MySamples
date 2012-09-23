package com.example.machinechecker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import android.widget.ImageView;

public class MainActivity extends Activity {

	private Button buttonCheck;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 画像を表示させるとき
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.beer);
        setContentView(iv);
        */
        setContentView(R.layout.activity_main);
        
        // event handlerの追加
        buttonCheck =(Button)findViewById(R.id.button_check);
        
        buttonCheck.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		ButtonCheck_OnClick();
        		// EditBoxにフォーカスが残らないように以下の処理を行う
        	    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
        	    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);        
        	       
        		
        	}
        });
    }

    private void ButtonCheck_OnClick(){
       //TextView textview1=(TextView)findViewById(R.id.textView_humor);
       //textview1.setText("test");
       
       EditText editTextHostname=(EditText)findViewById(R.id.editText_editHostname);
       Editable inputHostname=editTextHostname.getText();

       EditText editTextPortNo=(EditText)findViewById(R.id.editText_editPortNo);
       Editable inputPortNo=editTextPortNo.getText();
       Integer port = null;
       try {
    	   port = Integer.valueOf(inputPortNo.toString());
       } catch (Exception e) {
       }
       
	   NetworkTools nt = new NetworkTools(inputHostname.toString(), port);
		
	   // resolve
	   Result result = null;
	   int index = 0;
	   String strResultText = "";
		do {
			result = nt.next();
			String strTempText = "";
			if(result != null && !result.finished()) {
				switch(result.getResult()) {
				case SUCCESS:
					strTempText = "(" + (index+1) + ")　[" + result.getTestName() + ":OK] ";
					strTempText += " " + result.getComment() + " in " + result.getMsec()+"(msec)";
					break;
				case FAIL:
					strTempText = "(" + (index+1) + ") [" + result.getTestName() + ":NG] ";
					break;
				case FINISHED:
					break;
				default:
					// never comes here
					break;
				}
				index++;
				strResultText += strTempText + "\n";					
			}
		} while(result != null && result.succeeds());
		
		strResultText = strResultText.substring(0, (strResultText.length() - 1));
		if(index > 0) {
		       TextView textview_result=(TextView)findViewById(R.id.textView_result);
		       textview_result.setText("Result:\n"   +  strResultText);			
		}
     }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
