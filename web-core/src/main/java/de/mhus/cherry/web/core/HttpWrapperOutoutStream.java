/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.web.core;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * This class prevent the call to get output stream to early. It's the first stream in a chain of
 * filter streams
 *
 * @author mikehummel
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
