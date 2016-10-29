package de.mhus.cherry.portal.impl;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class CherryServletOutputStream extends ServletOutputStream {

	private ServletOutputStream instance;

	public CherryServletOutputStream(ServletOutputStream outputStream) {
		instance = outputStream;
	}

	@Override
	public void write(int b) throws IOException {
		instance.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		instance.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		instance.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		instance.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
	}
	

}
