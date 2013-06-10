package br.ufc.web2mobile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class XMLReader {

	private List<String> imagens;
	private List<String> textos;
	private List<String> audios;

	private int numTelas;

	private static final String TAG = "XMLReader";
	private static String pasta = "H1";

	public XMLReader() {

		imagens = new ArrayList<String>();
		textos = new ArrayList<String>();
		audios = new ArrayList<String>();

	}

	public boolean checkComic(Context context) {

		boolean temRecursos = false;
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			Log.e("CHECK", "SD CARD UNMOUNTED");

			return false;
		}

		File directory = new File(Environment.getExternalStorageDirectory()
				+ "/web2mobile");
		File dirUnzipped = new File(Environment.getExternalStorageDirectory()
				+ "/unzipped");

		if (!directory.exists()) {
			directory.mkdir();
		}
		if (!dirUnzipped.exists()) {
			dirUnzipped.mkdir();
		}

		File[] contents = directory.listFiles();
		File[] contentsUnzipped = dirUnzipped.listFiles();

		for (int i = 0; i < contentsUnzipped.length; i++) {
			if (pasta.equalsIgnoreCase(contentsUnzipped[i].getName())) {
				temRecursos = true;
			}
		}

		// Folder is empty
		if (contents.length == 0) {
			Toast toast = Toast.makeText(context.getApplicationContext(),
					"Não há recursos.", Toast.LENGTH_SHORT);
			toast.show();
			Log.d("CHECK", "PASTA VAZIA");
			return false;
		}
		// Folder contains files
		else {
			String zipFile = Environment.getExternalStorageDirectory()
					+ "/web2mobile/" + pasta + ".zip";
			String unzipLocation = Environment.getExternalStorageDirectory()
					+ "/unzipped/";

			if (!temRecursos) {
				Decompress d = new Decompress(zipFile, unzipLocation);
				d.unzip(context.getApplicationContext());
				Toast toast = Toast.makeText(context.getApplicationContext(),
						"Descompactando aqrquivos..", Toast.LENGTH_SHORT);
				toast.show();
			}
			// ->deletar quadrinho.zip
			// File file = new File(zipFile);
			// file.delete();
			return true;
		}
	}

	public void read(Context context) {

		if (checkComic(context)) {

			try {
				// Get the Android-specific compiled XML parser.
				String string = new String();

				numTelas = 0;

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				XmlPullParser xpp = factory.newPullParser();
				FileReader in = new FileReader(
						Environment.getExternalStorageDirectory()
								+ "/unzipped/" + pasta + "/definition.xml");
				// ---------------------------
				xpp.setInput(in);

				while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {

					if (xpp.getEventType() == XmlPullParser.START_TAG) {

						String tagName = xpp.getName();

						if (tagName.equals("quadrinho")) {

						} else if (tagName.equals("tela")) {
							numTelas++;

						} else if (tagName.equals("imagem")) {
							string = xpp.getAttributeValue(null, "src");
							imagens.add(Environment
									.getExternalStorageDirectory()
									+ "/unzipped/" + pasta + "/" + string);
							Log.d("IMAGEM ADICIONADA", "Image path: " + string);

						} else if (tagName.equals("audio")) {
							string = xpp.getAttributeValue(null, "src");
							audios.add(Environment
									.getExternalStorageDirectory()
									+ "/unzipped/" + pasta + "/Audios/" + string);
							Log.d("AUDIO ADICIONADO", "Audio path: " + string);
						} else if (tagName.equals("texto")) {
							string = xpp.getAttributeValue(null,
									"txt");
							string = string.replaceAll("[*]", "\n");
							textos.add(string);
						}
					} else if (xpp.getEventType() == XmlResourceParser.END_TAG) {

					} else if (xpp.getEventType() == XmlResourceParser.TEXT) {
						// String s1 = xpp.getText();
					}
					xpp.next();
				}
				// ((InputStreamReader) xpp).close();
				setNumTelas(numTelas);
				Log.d("NUMERO DE TELAS", "Numero: " + getNumTelas());

			} catch (XmlPullParserException xppe) {

				Log.e(TAG,
						"Failure of .getEventType or .next, probably bad file format");
				xppe.toString();

			} catch (IOException ioe) {

				Log.e(TAG, "Unable to read resource file");
				ioe.printStackTrace();
			}
		} else {
			Toast toast = Toast
					.makeText(
							context.getApplicationContext(),
							"O seu SD Card não está montado, por favor monte o seu SD Card através das opções do seu telefone",
							Toast.LENGTH_LONG);
			toast.show();
		}
	}

	public List<String> getImagens() {
		return imagens;
	}

	public void setImagens(List<String> imagens) {
		this.imagens = imagens;
	}

	public List<String> getTextos() {
		return textos;
	}

	public void setTextos(List<String> textos) {
		this.textos = textos;
	}

	public List<String> getAudios() {
		return audios;
	}

	public void setAudios(List<String> audios) {
		this.audios = audios;
	}

	public int getNumTelas() {
		return numTelas;
	}

	public void setNumTelas(int numTelas) {
		this.numTelas = numTelas;
	}
}
