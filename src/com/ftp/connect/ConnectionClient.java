package com.ftp.connect;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

import android.util.Log;

@EBean
public class ConnectionClient {

	public FTPClient mFTPClient = null;
	
	@RootContext FTPConnectActivity activity;

	private String workingDir;

	private ArrayList<String> fileList;

	private FTPFile[] ftpFiles;

	@Background
	public void ftpConnect(String host, String username, String password) {
		try {
			mFTPClient = new FTPClient();

			// connecting to the host
			mFTPClient.connect(host);

			Log.d("ftpConnect", "Connected to the host");
			// now check the reply code, if positive mean connection success
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
				// login using username & password
				boolean status = mFTPClient.login(username, password);
				Log.d("ftpConnect", "Logged to the host");

				/*
				 * Set File Transfer Mode
				 * 
				 * To avoid corruption issue you must specified a correct
				 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
				 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
				 * transferring text, image, and compressed files.
				 */

				mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
				mFTPClient.enterLocalPassiveMode();

				if(status)
					activity.onConect();
				else
					Log.e("Connection try", "Unable to connect to the FTP host");
				
			}
		} catch (Exception e) {
			Log.d("FTPConnect", "Error: could not connect to host " + host + e.getMessage());
		}
	}

	public boolean ftpDisconnect() {
		try {
			// logout and disconnect from the ftp server
			mFTPClient.logout();
			mFTPClient.disconnect();

			return true;

		} catch (Exception e) {
			Log.e("ftpDisconnect", "Could not disconnect from current host.");
		}
		return false;
	}

	public String ftpGetCurrentWorkingDirectoryRes() {
		return workingDir;
	}
	
	@Background
	public void ftpGetCurrentWorkingDirectory() {
		try {
			workingDir = mFTPClient.printWorkingDirectory();
			Log.d("Directory",  workingDir);
		} catch (Exception e) {
			Log.d("ftpGetCurrentWorkingDirectory",
					"Error: could not get current working directory.");
		}
	}

	public boolean ftpChangeDirectory(String directory_path) {
		try {
			mFTPClient.changeWorkingDirectory(directory_path);
		} catch (Exception e) {
			Log.d("ftpChangeDirectory", "Error: could not change directory to "
					+ directory_path);
		}

		return false;
	}

	@Background
	public void ftpPrintFilesList(String dir_path) {
		try {
			ftpFiles = mFTPClient.listFiles(dir_path);
			fileList = new ArrayList<String>();
			int length = ftpFiles.length;

			for (int i = 0; i < length; i++) {
				String name = ftpFiles[i].getName();
				boolean isFile = ftpFiles[i].isFile();

				if (isFile) {
					Log.i("ftpPrintFileList", "File : " + name);
					fileList.add(name);
				} else {
					Log.i("ftpPrintFileList", "Directory : " + name);
				}
			}
			activity.showFilesList(fileList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Background
//	public void ftpGetFilesList() {
//		fileList = new ArrayList<String>();
//		try {
////			FTPFile[] ftpFiles = mFTPClient.listFiles(".\\");
//			int length = ftpFiles.length;
//
//			for (int i = 0; i < length; i++) {
//				if (ftpFiles[i].isFile()) {
//					fileList.add(ftpFiles[i].getName());
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			fileList.add("");
//			Log.e("FTPGetFileList", "Unable to return array of the file list");
//		}
//	}

	public boolean ftpMakeDirectory(String new_dir_path) {
		try {
			boolean status = mFTPClient.makeDirectory(new_dir_path);
			return status;
		} catch (Exception e) {
			Log.d("ftpMakeDirecgtory",
					"Error: could not create new directory named "
							+ new_dir_path);
		}

		return false;
	}

	public boolean ftpRemoveDirectory(String dir_path) {
		try {
			boolean status = mFTPClient.removeDirectory(dir_path);
			return status;
		} catch (Exception e) {
			Log.d("ftpRemoveDirectory",
					"Error: could not remove directory named " + dir_path);
		}

		return false;
	}

	public boolean ftpRemoveFile(String filePath) {
		try {
			boolean status = mFTPClient.deleteFile(filePath);
			return status;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("ftpRemoveFile", "Error: could not remove directory named "
					+ filePath);
		}

		return false;
	}

	public boolean ftpRenameFile(String from, String to) {
		try {
			boolean status = mFTPClient.rename(from, to);
			return status;
		} catch (Exception e) {
			Log.d("ftpRenameFile", "Could not rename file: " + from + " to: "
					+ to);
		}

		return false;
	}

	/**
	 * mFTPClient: FTP client connection object (see FTP connection example)
	 * srcFilePath: path to the source file in FTP server desFilePath: path to
	 * the destination file to be saved in sdcard
	 */
	@Background
	public void ftpDownload(String srcFilePath, String desFilePath) {
		boolean status = false;
		try {
			FileOutputStream desFileStream = new FileOutputStream(desFilePath);
			status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
			desFileStream.close();

			Log.d("Item Download", "Downloading file " + srcFilePath);
			Log.d("Item Download", "Final file path: " + desFilePath);
		} catch (Exception e) {
			Log.e("ftpDownloadFile", "download failed");
			Log.e("ftpDownloadFile",
					"Response string: " + mFTPClient.getReplyString());
			Log.e("ftpDownloadFile", "Exception: " + e.toString());
		}

//		return status;
	}

	/**
	 * mFTPClient: FTP client connection object (see FTP connection example)
	 * srcFilePath: source file path in sdcard desFileName: file name to be
	 * stored in FTP server desDirectory: directory path where the file should
	 * be upload to
	 */
	public boolean ftpUpload(String srcFilePath, String desFileName,
			String desDirectory) {
		boolean status = false;
		try {
			FileInputStream srcFileStream = new FileInputStream(srcFilePath);

			// change working directory to the destination directory
			if (ftpChangeDirectory(desDirectory)) {
				status = mFTPClient.storeFile(desFileName, srcFileStream);
			}

			srcFileStream.close();
			return status;
		} catch (Exception e) {
			Log.d("ftpUpload", "upload failed");
		}

		return status;
	}

}
