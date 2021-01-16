package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import java.security.cert.Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;

public abstract class OpenSslContext extends ReferenceCountedOpenSslContext {
   OpenSslContext(Iterable<String> var1, CipherSuiteFilter var2, ApplicationProtocolConfig var3, long var4, long var6, int var8, Certificate[] var9, ClientAuth var10, String[] var11, boolean var12, boolean var13) throws SSLException {
      super(var1, var2, var3, var4, var6, var8, var9, var10, var11, var12, var13, false);
   }

   OpenSslContext(Iterable<String> var1, CipherSuiteFilter var2, OpenSslApplicationProtocolNegotiator var3, long var4, long var6, int var8, Certificate[] var9, ClientAuth var10, String[] var11, boolean var12, boolean var13) throws SSLException {
      super(var1, var2, var3, var4, var6, var8, var9, var10, var11, var12, var13, false);
   }

   final SSLEngine newEngine0(ByteBufAllocator var1, String var2, int var3, boolean var4) {
      return new OpenSslEngine(this, var1, var2, var3, var4);
   }

   protected final void finalize() throws Throwable {
      super.finalize();
      OpenSsl.releaseIfNeeded(this);
   }
}
