package io.netty.channel.epoll;

import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

final class TcpMd5Util {
   static Collection<InetAddress> newTcpMd5Sigs(AbstractEpollChannel var0, Collection<InetAddress> var1, Map<InetAddress, byte[]> var2) throws IOException {
      ObjectUtil.checkNotNull(var0, "channel");
      ObjectUtil.checkNotNull(var1, "current");
      ObjectUtil.checkNotNull(var2, "newKeys");
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         byte[] var5 = (byte[])var4.getValue();
         if (var4.getKey() == null) {
            throw new IllegalArgumentException("newKeys contains an entry with null address: " + var2);
         }

         if (var5 == null) {
            throw new NullPointerException("newKeys[" + var4.getKey() + ']');
         }

         if (var5.length == 0) {
            throw new IllegalArgumentException("newKeys[" + var4.getKey() + "] has an empty key.");
         }

         if (var5.length > Native.TCP_MD5SIG_MAXKEYLEN) {
            throw new IllegalArgumentException("newKeys[" + var4.getKey() + "] has a key with invalid length; should not exceed the maximum length (" + Native.TCP_MD5SIG_MAXKEYLEN + ')');
         }
      }

      var3 = var1.iterator();

      while(var3.hasNext()) {
         InetAddress var7 = (InetAddress)var3.next();
         if (!var2.containsKey(var7)) {
            var0.socket.setTcpMd5Sig(var7, (byte[])null);
         }
      }

      if (var2.isEmpty()) {
         return Collections.emptySet();
      } else {
         ArrayList var6 = new ArrayList(var2.size());
         Iterator var8 = var2.entrySet().iterator();

         while(var8.hasNext()) {
            Entry var9 = (Entry)var8.next();
            var0.socket.setTcpMd5Sig((InetAddress)var9.getKey(), (byte[])var9.getValue());
            var6.add(var9.getKey());
         }

         return var6;
      }
   }

   private TcpMd5Util() {
      super();
   }
}
