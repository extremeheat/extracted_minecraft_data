package io.netty.handler.codec.spdy;

public enum SpdyVersion {
   SPDY_3_1(3, 1);

   private final int version;
   private final int minorVersion;

   private SpdyVersion(int var3, int var4) {
      this.version = var3;
      this.minorVersion = var4;
   }

   int getVersion() {
      return this.version;
   }

   int getMinorVersion() {
      return this.minorVersion;
   }
}
