package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;

public final class OpenSslEngine extends ReferenceCountedOpenSslEngine {
   OpenSslEngine(OpenSslContext var1, ByteBufAllocator var2, String var3, int var4, boolean var5) {
      super(var1, var2, var3, var4, var5, false);
   }

   protected void finalize() throws Throwable {
      super.finalize();
      OpenSsl.releaseIfNeeded(this);
   }
}
