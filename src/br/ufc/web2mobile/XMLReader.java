package br.ufc.web2mobile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
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

	private String arquivo;

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
			if (arquivo.equalsIgnoreCase(contentsUnzipped[i].getName())) {
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
			String zipFile = Environment.getExternalStorageDirectory() + "/web2mobile/" + arquivo + ".zip";
			String unzipLocation = Environment.getExternalStorageDirectory() + "/unzipped/";

			if (!temRecursos) {
				Decompress d = new Decompress(zipFile, unzipLocation, arquivo);
				d.unzip(context.getApplicationContext());
				Toast toast = Toast.makeText(context.getApplicationContext(),
						"Descompactando arquivos..", Toast.LENGTH_SHORT);
				toast.show();
			}
			// ->deletar quadrinho.zip
			// File file = new File(zipFile);
			// file.delete();
			return true;
		}
	}

	public void readDom(Context context) {

		String string = new String();
		
		numTelas = 0;

		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(Environment.getExternalStorageDirectory()
				+ "/unzipped/" + arquivo + "/" + arquivo + ".xml");

		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			List list = rootNode.getChildren("tela");

			for (int i = 0; i < list.size(); i++) {

				Element node = (Element) list.get(i);

				numTelas = Integer.parseInt(node.getAttributeValue("id"));

				imagens.add(Environment.getExternalStorageDirectory()
						+ "/unzipped/" + arquivo + "/" + node.getChild("imagem").getAttributeValue("src"));

				audios.add(Environment.getExternalStorageDirectory() + "/unzipped/" + arquivo + "/Audios/" + node.getChild("audio").getAttributeValue("src"));

				string = node.getChild("texto").getAttributeValue("txt");
				string = string.replaceAll("[*]", "\n");
				textos.add(string);
				//textos.add(node.getChild("texto").getAttributeValue("txt"));
				
				setNumTelas(numTelas);
				Log.d("NUMERO DE TELAS", "Numero: " + getNumTelas());
			}

		} catch (IOException io) {
			System.out.println(io.getMessage());
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
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

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}
}
