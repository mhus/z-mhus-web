package de.mhus.cherry.web.core;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * This class prevent the call to get output stream to early. It's the first stream in a chain of filter streams
 *  
 * @author mikehummel
 *
 */
public class HttpWrapperOutoutStream extends OutputStream {

	private HttpServletResponse res;
	private ServletOutputStream outputStream;

	public HttpWrapperOutoutStream(HttpServletResponse res) {
		this.res = res;
	}

	@Override
	public void write(int b) throws IOException {
		check();
		outputStream.write(b);
	}

	
	
	private synchronized void check() throws IOException {
		if (outputStream == null) {
			outputStream = res.getOutputStream();
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		check();
		outputStream.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		check();
		outputStream.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		check();
		outputStream.flush();
	}

	@Override
	public void close() throws IOException {
		check();
		outputStream.close();
	}

}
