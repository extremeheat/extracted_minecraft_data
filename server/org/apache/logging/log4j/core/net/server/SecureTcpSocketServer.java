package org.apache.logging.log4j.core.net.server;

import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;

public class SecureTcpSocketServer<T extends InputStream> extends TcpSocketServer<T> {
   public SecureTcpSocketServer(int var1, LogEventBridge<T> var2, SslConfiguration var3) throws IOException {
      super(var1, var2, var3.getSslServerSocketFactory().createServerSocket(var1));
   }
}
