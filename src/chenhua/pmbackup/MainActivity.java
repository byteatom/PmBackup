package chenhua.pmbackup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TextView mTxtStatus;
	ScrollView mScroll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTxtStatus = (TextView)findViewById(R.id.txtStatus);
		mScroll = (ScrollView)findViewById(R.id.sclStatus);
		
		Button bBackup = (Button)findViewById(R.id.bBackup);
		bBackup.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	mTxtStatus.setText("");
		    	new Backup().execute(MainActivity.this);
		    }});
		
		Button bRestore = (Button)findViewById(R.id.bRestore);
		bRestore.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	mTxtStatus.setText("");
		    	new Restore().execute(MainActivity.this);
		    }});
	}
	
	void ScrollStatusToBotton() {
		mScroll.post(new Runnable() {
			public void run() {
				mScroll.fullScroll(ScrollView.FOCUS_DOWN);
		    }
		});
	}
}
