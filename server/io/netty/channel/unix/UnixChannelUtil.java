package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class UnixChannelUtil {
   private UnixChannelUtil() {
      super();
   }

   public static boolean isBufferCopyNeededForWrite(ByteBuf var0) {
      return isBufferCopyNeededForWrite(var0, Limits.IOV_MAX);
   }

   static boolean isBufferCopyNeededForWrite(ByteBuf var0, int var1) {
      return !var0.hasMemoryAddress() && (!var0.isDirect() || var0.nioBufferCount() > var1);
   }

   public static InetSocketAddress computeRemoteAddr(InetSocketAddress var0, InetSocketAddress var1) {
      if (var1 == null) {
         return var0;
      } else {
         if (PlatformDependent.javaVersion() >= 7) {
            try {
               return new InetSocketAddress(InetAddress.getByAddress(var0.getHostString(), var1.getAddress().getAddress()), var1.getPort());
            } catch (UnknownHostException var3) {
            }
         }

         return var1;
      }
   }
}
