package io.netty.channel.unix;

final class LimitsStaticallyReferencedJniMethods {
   private LimitsStaticallyReferencedJniMethods() {
      super();
   }

   static native long ssizeMax();

   static native int iovMax();

   static native int uioMaxIov();

   static native int sizeOfjlong();

   static native int udsSunPathSize();
}
