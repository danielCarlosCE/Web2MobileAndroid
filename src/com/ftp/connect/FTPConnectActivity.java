package com.ftp.connect;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import br.ufc.web2mobile.R;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.ftp)
public class FTPConnectActivity extends Activity
{
	@ViewById public Button btn1;
	@ViewById public TextView tv1;
	@ViewById public ListView  list1;
	@ViewById public EditText editText1;
	
	@Bean public ConnectionClient cl;
	
	public boolean isConnected = false;
	private String ip;

	@AfterViews
	public void init()	{

		list1.setOnItemClickListener(new DownloadItemListener(cl));
		
		//connects as soon as start the application
		cl.ftpConnect("192.168.1.5", "android", "device");
	}

	@UiThread
	public void onConect() {
		btn1.setText("Disconnect");
		tv1.setText("Connection Successfull");
		isConnected = true;
		
		cl.ftpGetCurrentWorkingDirectory();
		cl.ftpPrintFilesList(".\\");		
//		cl.ftpGetFilesList();
		
	}

	@UiThread
	public void showFilesList(ArrayList<String> fileList) {
		ArrayList<String> adapter = fileList;		
		list1.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, adapter));
	}

	@Click
	public void btn1(View arg0){
		ip = editText1.getText().toString();   
		
		if(isConnected == false)
			cl.ftpConnect(ip, "android", "device");
		else
		{
			if(cl.ftpDisconnect())
			{
				btn1.setText("Connect");
				tv1.setText("Disconnection Successfull");
				list1.removeAllViewsInLayout();
				
				isConnected = false;
			}
			else
				Log.e("Disconnection try", "Unable to disconnect to the FTP host");
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
		
		if(cl.ftpDisconnect())
		{
			btn1.setText("Connect");
			tv1.setText("Disconnection Successfull");
			list1.removeAllViewsInLayout();
			
			isConnected = false;
		}
		else
			Log.e("Disconnection try", "Unable to disconnect to the FTP host");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop()
	{
		super.onStop();
		
		if(cl.ftpDisconnect())
		{
			btn1.setText("Connect");
			tv1.setText("Disconnection Successfull");
			list1.removeAllViewsInLayout();
			
			isConnected = false;
		}
		else
			Log.e("Disconnection try", "Unable to disconnect to the FTP host");
	}
	
	
}
