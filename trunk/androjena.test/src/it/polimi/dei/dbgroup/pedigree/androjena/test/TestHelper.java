/*
 * Copyright 2010 Lorenzo Carrara
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polimi.dei.dbgroup.pedigree.androjena.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileUtils;
import com.ibm.icu.text.CharsetDetector;

public final class TestHelper {
	private TestHelper() {
	}

	public static final String sanitizeFileName(String filename) {
		String old = new String(filename);
		if (filename.startsWith("file:"))
			filename = filename.substring("file:".length());
		int start = 0;
		while (filename.charAt(start) == '/')
			start++;
		filename = filename.substring(start);
		if(!old.equals(filename)) log(old + " sanitized to " + filename);
		return filename;
	}
	
	private static final void log(String s) {
		Log.d("TestHelper", s);
	}

	public static final String getResourceURL(String filename) {
		String sanitized = sanitizeFileName(filename);
		URL resUrl = TestHelper.class.getClassLoader().getResource(sanitized);
		if (resUrl != null)
			return resUrl.toString();
		return sanitized;
	}

	public static final InputStream openResource(String filename) {
		InputStream is = TestHelper.class.getClassLoader().getResourceAsStream(
				sanitizeFileName(filename));
		if (is == null)
			throw new RuntimeException("file " + filename
					+ " not found in class resources");
		return is;
	}

	private static final Pattern XML_CHARSET_PATTERN = Pattern
			.compile("^.*<\\?xml.*encoding=\"([^\"]+)\".*\\?>.*$");

	public static interface StreamFactory {
		public InputStream createStream() throws IOException;
	}

	public static final Reader getXMLReader(StreamFactory factory)
			throws IOException {
		CharsetDetector detector = new CharsetDetector();
		Reader innerReader = detector.getReader(factory.createStream(), null);
		if(innerReader == null) innerReader = new InputStreamReader(
				factory.createStream());
		BufferedReader reader = new BufferedReader(innerReader);
		String s;
		StringBuilder sb = new StringBuilder();
		try {
			while ((s = reader.readLine()) != null) {
				sb.append(s);
			}
			reader.close();
		} catch (IOException ex) {
			throw new RuntimeException(
					"an error occurred while detecting declared charset", ex);
		}
		s = sb.toString();
		Matcher m = XML_CHARSET_PATTERN.matcher(s);
		if (m.matches()) {
			String encoding = m.group(1);
			if (encoding != null) {
				try {
					Reader r = detector.getReader(factory.createStream(), encoding);
					if(r == null) r = new InputStreamReader(factory.createStream(), encoding);
					log("detected encoding " + encoding + " for XML stream");
					return r;
				} catch (UnsupportedEncodingException ex) {
				}
			}
		}
		
		Reader r = detector.getReader(factory.createStream(), null);
		if(r == null) r = new InputStreamReader(factory.createStream());
		log("encoding not found");
		return r;
	}

	public static final void dumpModel(Model m) {
		dumpModel(m, "N-TRIPLE");
	}

	public static final void dumpModel(Model m, String lang) {
		StringWriter w = new StringWriter();
		m.write(w, lang);
		w.flush();
		Log.d("TestHelper.dumpModel", w.toString());
	}
	
	public static final File copyFile(String filename) throws IOException {
		final byte[] buffer = new byte[1024];
		InputStream input = openResource(filename);
		File tempFile = File.createTempFile(FileUtils.getBasename(filename), FileUtils.getFilenameExt(filename));
		OutputStream output = new FileOutputStream(tempFile);
		int read;
		while((read = input.read(buffer)) != -1) {
			output.write(buffer, 0, read);
		}
		input.close();
		output.flush();
		output.close();
		log("created temp file " + tempFile.getAbsolutePath() + " for resource " + filename);
		return tempFile;
	}
}
