package io.netty.handler.ssl;

interface OpenSslEngineMap {
   ReferenceCountedOpenSslEngine remove(long var1);

   void add(ReferenceCountedOpenSslEngine var1);

   ReferenceCountedOpenSslEngine get(long var1);
}
