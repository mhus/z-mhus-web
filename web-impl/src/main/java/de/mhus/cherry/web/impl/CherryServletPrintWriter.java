package de.mhus.cherry.web.impl;

import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;

public class CherryServletPrintWriter extends PrintWriter {

	public CherryServletPrintWriter(ServletOutputStream outputStream) {
		super(outputStream);
	}

	@Override
	public void close() {
		flush();
	}

}
