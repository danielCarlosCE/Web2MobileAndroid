package br.ufc.web2mobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Decompress {
	
	private String _zipFile; 
	  private String _location; 
	 
	  public Decompress(String zipFile, String location, String arquivo) { 
		  
	    _zipFile = zipFile; 
	    _location = location; 
	    
	    File f = new File(_location + arquivo); 		
	    f.mkdirs(); 
	    
	    f = new File(_location + arquivo + "/Audios"); 		
	    f.mkdirs(); 
	 
	    _dirChecker(""); 
	  } 
	 
	  public void unzip(Context context) { 
		  
		  Toast toast = Toast.makeText(context.getApplicationContext(),
					"Descompactando e carregando o quadrinho..",
					Toast.LENGTH_SHORT);
			toast.show();
		  
	    try  { 
	      FileInputStream fin = new FileInputStream(_zipFile); 
	      ZipInputStream zin = new ZipInputStream(fin); 
	      ZipEntry ze = null; 
	      while ((ze = zin.getNextEntry()) != null) { 
	        Log.v("Decompress", "Unzipping " + ze.getName()); 
	 
	        if(ze.isDirectory()) { 
	            _dirChecker(ze.getName()); 
	          } else { 
	            FileOutputStream fout = new FileOutputStream(_location + ze.getName()); 
	            byte[] buf = new byte[1024];
	            int length;
	            while ((length = zin.read(buf)) > 0) {
	                fout.write(buf, 0, length);
	            }
	 
	          zin.closeEntry(); 
	          fout.close(); 
	        } 
	         
	      } 
	      zin.close(); 
	    } catch(Exception e) { 
	      Log.e("Decompress", "unzip", e); 
	    } 
	 
	  } 
	 
	  private void _dirChecker(String dir) { 
	    File f = new File(_location + dir); 
	 
	    if(!f.isDirectory()) { 
	      f.mkdirs(); 
	    } 
	  } 

}
