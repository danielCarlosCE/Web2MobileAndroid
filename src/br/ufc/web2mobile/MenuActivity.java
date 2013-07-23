package br.ufc.web2mobile;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ftp.connect.FTPConnectActivity_;

public class MenuActivity extends Activity {

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
//		this.finish();
	}

	public ListView menuList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		createDirectories();

		menuList = (ListView) findViewById(R.id.menuList);

		String list[] = new String[] { "Aulas", "Baixar mais.." };

		menuList.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, list));

		menuList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View item,
					int position, long id) {
				switch (position) {
				case 0:
					startActivity(new Intent(getApplicationContext(),
							ListarActivity.class));
					break;

				case 1:
					startActivity(new Intent(getApplicationContext(),
							FTPConnectActivity_.class));
					break;

				default:
					break;
				}

			}

		});
	}

	public void createDirectories() {

		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			Log.e("CHECK", "SD CARD UNMOUNTED");
		} else {

			File directory = new File(
//					"/storage/extSdCard"
					Environment.getExternalStorageDirectory()
					+ "/web2mobile");
			File dirUnzipped = new File(
//					"/storage/extSdCard"
					Environment.getExternalStorageDirectory() 
					+ "/unzipped");

			if (!directory.exists()) {
				directory.mkdir();
			}
			if (!dirUnzipped.exists()) {
				dirUnzipped.mkdir();
			}
		}
	}
}
