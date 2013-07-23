package com.ftp.connect;

import java.io.File;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class DownloadItemListener implements OnItemClickListener {
	private ConnectionClient client;
	private final String localFolder = "/web2mobile/";
	private String licao;

	public DownloadItemListener(ConnectionClient cl) {
		client = cl;
	}

	public void onItemClick(AdapterView<?> parent, View item, int position,
			long id) {
		String s = (String) parent.getAdapter().getItem(position);
		Log.d("ItemClick", "Downloading item \"" + s + "\"");
		
		setLicao(s);
		String sdPath = new String();

		if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
			Log.e("Download to SDCard", "SD Card not mounted");
			return;
		} else {
			File sdComicPath = new File(Environment.getExternalStorageDirectory() + localFolder);
			Log.e("Env Fodler", Environment.getExternalStorageDirectory()
					.getAbsolutePath() + localFolder);

			if (!sdComicPath.exists()) {
				sdComicPath.mkdir();
				if (sdComicPath.isDirectory()) {
					Log.d("SD FILE PATH", "Can write to directory: "
							+ sdComicPath.getPath());
				}
			}

			sdPath = sdComicPath.getAbsolutePath() + "/" + s;

		}

		client.ftpDownload(s, sdPath);
//		} else {
//			Log.e("Item Download", "Error downloading file " + s + " from "
//					+ client.ftpGetCurrentWorkingDirectoryRes() + " to " + sdPath);
//			return;
//		}

	}

	public void setLicao(String licao) {
		this.licao = licao;
	}

	public String getLicao() {
		return licao;
	}

}
