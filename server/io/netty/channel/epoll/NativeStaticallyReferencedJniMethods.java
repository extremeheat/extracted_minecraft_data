package io.netty.channel.epoll;

final class NativeStaticallyReferencedJniMethods {
   private NativeStaticallyReferencedJniMethods() {
      super();
   }

   static native int epollin();

   static native int epollout();

   static native int epollrdhup();

   static native int epollet();

   static native int epollerr();

   static native long ssizeMax();

   static native int tcpMd5SigMaxKeyLen();

   static native int iovMax();

   static native int uioMaxIov();

   static native boolean isSupportingSendmmsg();

   static native boolean isSupportingTcpFastopen();

   static native String kernelVersion();
}
