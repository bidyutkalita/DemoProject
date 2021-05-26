/* Copyright � EasOfTech 2015. All rights reserved.
*
* This software is the confidential and proprietary information
* of EasOfTech. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms and
* conditions entered into with EasOfTech.
*
* Id: FileUtil.java
*
* Date Author Changes
* 28 Dec, 2015 Vinoth Created
*/
package com.eot.banking.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;


/**
 * The Class FileUtil.
 */
public class FileUtil {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(FileUtil.class.getName());
	
	/**
	 * Encode file to base64 binary string.
	 *
	 * @param fileName the file name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String encodeFileToBase64BinaryString(File fileName) throws IOException {
		return new String(Base64.encode(FileUtils.readFileToByteArray(fileName)));
	}
	
	/**
	 * Decode base64 binary string to bytes.
	 *
	 * @param encodedString the encoded string
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static byte[] decodeBase64BinaryStringToBytes(String encodedString) throws IOException {
		return Base64.decode(encodedString.getBytes());
	}
	
	/**
	 * Read file.
	 *
	 * @param file the file
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static byte[] readFile(File file) throws IOException {
		if (file.length() > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] buffer = new byte[4096];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			while(ios.read(buffer) == -1) {
				throw new IOException("EOF reached while trying to read the whole file");
			}
		} finally {
			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
			}
		}
		return buffer;
	}
	
	/**
	 * Extract zip file from byte array.
	 *
	 * @param bytes the bytes
	 * @param outpath the outpath
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void extractZipFileFromByteArray(byte[] bytes, String outpath) throws IOException {
		ZipInputStream stream = new ZipInputStream(new ByteArrayInputStream(bytes));
		try {
			ZipEntry entry;
			while ((entry = stream.getNextEntry()) != null) {
				String filepath = outpath + entry.getName();
				FileOutputStream fout = null;
				try {
					fout = new FileOutputStream(filepath);
					for (int c = stream.read(); c != -1; c = stream.read()) {
						fout.write(c);
					}
					stream.closeEntry();
					fout.close();
				} finally {
					if (fout != null)
						fout.close();
				}
			}
		} finally {
			stream.close();
		}
	}
	
	/**
	 * Creates the zip.
	 *
	 * @param zipFile the zip file
	 * @return the zip output stream
	 */
	public static ZipOutputStream createZip(File zipFile) {
		ZipOutputStream zipOut = null;
		try {
			zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
		}
		return zipOut;
	}
	
	/**
	 * Adds the file to zip.
	 *
	 * @param zos the zos
	 * @param file the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void addFileToZip(ZipOutputStream zos, File file) throws IOException {
		if (!file.canRead()) {
			logger.info("Cannot read file >>>>> " + file.getCanonicalPath());
			return;
		}
		zos.putNextEntry(new ZipEntry(file.getName()));
		IOUtils.copy(new FileInputStream(file), zos);
		zos.closeEntry();
	}
	
	/**
	 * Adds the dir to zip.
	 *
	 * @param zos the zos
	 * @param dir the dir
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void addDirToZip(ZipOutputStream zos, File dir) throws IOException {
		if (!dir.canRead()) {
			logger.info("Cannot read file >>>>>> " + dir.getCanonicalPath());
			return;
		}

		File[] files = dir.listFiles();

		for (File source : files) {
			if (source.isDirectory()) {
				addDirToZip(zos, source);
			} else {
				addFileToZip(zos, source);
			}
		}
	}
	
	/**
	 * Delete file.
	 *
	 * @param file the file
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (!file.delete()) {
				logger.info("Problem deleting file >>>>> " + file.getName());
			}
		} else {
			logger.info("File does not exist >>>>> " + file.getName());
		}
	}
	
	/**
	 * Make directory.
	 *
	 * @param path the path
	 * @return the file
	 * @throws Exception 
	 */
	public static File makeDirectory(String path) throws IOException {
		File dir = new File(path);
		if (!dir.exists()) {
			if (dir.mkdirs()) {
				logger.info("Directory created >>>>> " + dir.getPath());
			} else {
				throw new IOException("Problem creating directory");
			}
		} else {
			logger.info("Directory already exist. Duplicate DKCode...");
		}
		return dir;
	}
	
	/**
	 * Write file from encoded base64 string.
	 *
	 * @param encodedString the encoded string
	 * @param outpath the outpath
	 */
	public static void writeFileFromEncodedBase64String(String encodedString, String outpath) {
		try {
			byte[] filedata = FileUtil.decodeBase64BinaryStringToBytes(encodedString);
			FileOutputStream fos = new FileOutputStream(outpath);
			fos.write(filedata);
			fos.close();
		} catch (IOException e) {
			logger.info("Error writing to file >>>>> " + outpath);
//			e.printStackTrace();
		}
	}
	
}
