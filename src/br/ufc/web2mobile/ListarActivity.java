package br.ufc.web2mobile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListarActivity extends ListActivity {
	private File file;
	private List<String> myList;
	String root_sd = Environment.getExternalStorageDirectory() + "/web2mobile";
//	String root_sd = "/storage/extSdCard" + "/web2mobile";
	String arquivo = new String();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myList = new ArrayList<String>();

		file = new File(root_sd);
		File list[] = file.listFiles();

		for (int i = 0; i < list.length; i++) {
			myList.add(list[i].getName());
		}

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, myList));

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		File temp_file = new File(file, myList.get(position));

		if (!temp_file.isFile()) {
			file = new File(file, myList.get(position));
			File list[] = file.listFiles();

			myList.clear();

			for (int i = 0; i < list.length; i++) {
				myList.add(list[i].getName());
			}
			Toast.makeText(getApplicationContext(), file.toString(),
					Toast.LENGTH_LONG).show();
			setListAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, myList));

		} else {
			arquivo = temp_file.getName().substring(0, temp_file.getName().lastIndexOf("."));
			
			Bundle parametros = new Bundle();
			parametros.putString("ARQUIVO", arquivo);

			Intent it = new Intent(ListarActivity.this, MainActivity.class);
			it.putExtras(parametros);
			startActivity(it);

			Toast.makeText(getApplicationContext(), temp_file.getName(),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onBackPressed() {

		if (!file.getAbsolutePath().equals(root_sd)) {
			String parent = file.getParent().toString();
			file = new File(parent);
			File list[] = file.listFiles();

			myList.clear();

			for (int i = 0; i < list.length; i++) {
				myList.add(list[i].getName());
			}
			Toast.makeText(getApplicationContext(), parent, Toast.LENGTH_SHORT)
					.show();
			setListAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, myList));
		} else {
			finish();
		}

	}

	public ArrayList<String> GetFiles(String DirectoryPath) {
		ArrayList<String> MyFiles = new ArrayList<String>();
		File f = new File(DirectoryPath);

		f.mkdirs();
		File[] files = f.listFiles();
		if (files.length == 0)
			return null;
		else {
			for (int i = 0; i < files.length; i++)
				MyFiles.add(files[i].getName());
		}

		return MyFiles;
	}

}
