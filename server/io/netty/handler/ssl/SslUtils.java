package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLHandshakeException;

final class SslUtils {
   static final String PROTOCOL_SSL_V2_HELLO = "SSLv2Hello";
   static final String PROTOCOL_SSL_V2 = "SSLv2";
   static final String PROTOCOL_SSL_V3 = "SSLv3";
   static final String PROTOCOL_TLS_V1 = "TLSv1";
   static final String PROTOCOL_TLS_V1_1 = "TLSv1.1";
   static final String PROTOCOL_TLS_V1_2 = "TLSv1.2";
   static final int SSL_CONTENT_TYPE_CHANGE_CIPHER_SPEC = 20;
   static final int SSL_CONTENT_TYPE_ALERT = 21;
   static final int SSL_CONTENT_TYPE_HANDSHAKE = 22;
   static final int SSL_CONTENT_TYPE_APPLICATION_DATA = 23;
   static final int SSL_CONTENT_TYPE_EXTENSION_HEARTBEAT = 24;
   static final int SSL_RECORD_HEADER_LENGTH = 5;
   static final int NOT_ENOUGH_DATA = -1;
   static final int NOT_ENCRYPTED = -2;
   static final String[] DEFAULT_CIPHER_SUITES = new String[]{"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA"};

   static void addIfSupported(Set<String> var0, List<String> var1, String... var2) {
      String[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (var0.contains(var6)) {
            var1.add(var6);
         }
      }

   }

   static void useFallbackCiphersIfDefaultIsEmpty(List<String> var0, Iterable<String> var1) {
      if (var0.isEmpty()) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            if (!var3.startsWith("SSL_") && !var3.contains("_RC4_")) {
               var0.add(var3);
            }
         }
      }

   }

   static void useFallbackCiphersIfDefaultIsEmpty(List<String> var0, String... var1) {
      useFallbackCiphersIfDefaultIsEmpty(var0, (Iterable)Arrays.asList(var1));
   }

   static SSLHandshakeException toSSLHandshakeException(Throwable var0) {
      return var0 instanceof SSLHandshakeException ? (SSLHandshakeException)var0 : (SSLHandshakeException)(new SSLHandshakeException(var0.getMessage())).initCause(var0);
   }

   static int getEncryptedPacketLength(ByteBuf var0, int var1) {
      int var2 = 0;
      boolean var3;
      switch(var0.getUnsignedByte(var1)) {
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
         var3 = true;
         break;
      default:
         var3 = false;
      }

      if (var3) {
         short var4 = var0.getUnsignedByte(var1 + 1);
         if (var4 == 3) {
            var2 = unsignedShortBE(var0, var1 + 3) + 5;
            if (var2 <= 5) {
               var3 = false;
            }
         } else {
            var3 = false;
         }
      }

      if (!var3) {
         int var6 = (var0.getUnsignedByte(var1) & 128) != 0 ? 2 : 3;
         short var5 = var0.getUnsignedByte(var1 + var6 + 1);
         if (var5 != 2 && var5 != 3) {
            return -2;
         }

         var2 = var6 == 2 ? (shortBE(var0, var1) & 32767) + 2 : (shortBE(var0, var1) & 16383) + 3;
         if (var2 <= var6) {
            return -1;
         }
      }

      return var2;
   }

   private static int unsignedShortBE(ByteBuf var0, int var1) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.getUnsignedShort(var1) : var0.getUnsignedShortLE(var1);
   }

   private static short shortBE(ByteBuf var0, int var1) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.getShort(var1) : var0.getShortLE(var1);
   }

   private static short unsignedByte(byte var0) {
      return (short)(var0 & 255);
   }

   private static int unsignedShortBE(ByteBuffer var0, int var1) {
      return shortBE(var0, var1) & '\uffff';
   }

   private static short shortBE(ByteBuffer var0, int var1) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.getShort(var1) : ByteBufUtil.swapShort(var0.getShort(var1));
   }

   static int getEncryptedPacketLength(ByteBuffer[] var0, int var1) {
      ByteBuffer var2 = var0[var1];
      if (var2.remaining() >= 5) {
         return getEncryptedPacketLength(var2);
      } else {
         ByteBuffer var3 = ByteBuffer.allocate(5);

         do {
            var2 = var0[var1++].duplicate();
            if (var2.remaining() > var3.remaining()) {
               var2.limit(var2.position() + var3.remaining());
            }

            var3.put(var2);
         } while(var3.hasRemaining());

         var3.flip();
         return getEncryptedPacketLength(var3);
      }
   }

   private static int getEncryptedPacketLength(ByteBuffer var0) {
      int var1 = 0;
      int var2 = var0.position();
      boolean var3;
      switch(unsignedByte(var0.get(var2))) {
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
         var3 = true;
         break;
      default:
         var3 = false;
      }

      if (var3) {
         short var4 = unsignedByte(var0.get(var2 + 1));
         if (var4 == 3) {
            var1 = unsignedShortBE(var0, var2 + 3) + 5;
            if (var1 <= 5) {
               var3 = false;
            }
         } else {
            var3 = false;
         }
      }

      if (!var3) {
         int var6 = (unsignedByte(var0.get(var2)) & 128) != 0 ? 2 : 3;
         short var5 = unsignedByte(var0.get(var2 + var6 + 1));
         if (var5 != 2 && var5 != 3) {
            return -2;
         }

         var1 = var6 == 2 ? (shortBE(var0, var2) & 32767) + 2 : (shortBE(var0, var2) & 16383) + 3;
         if (var1 <= var6) {
            return -1;
         }
      }

      return var1;
   }

   static void handleHandshakeFailure(ChannelHandlerContext var0, Throwable var1, boolean var2) {
      var0.flush();
      if (var2) {
         var0.fireUserEventTriggered(new SslHandshakeCompletionEvent(var1));
      }

      var0.close();
   }

   static void zeroout(ByteBuf var0) {
      if (!var0.isReadOnly()) {
         var0.setZero(0, var0.capacity());
      }

   }

   static void zerooutAndRelease(ByteBuf var0) {
      zeroout(var0);
      var0.release();
   }

   static ByteBuf toBase64(ByteBufAllocator var0, ByteBuf var1) {
      ByteBuf var2 = Base64.encode(var1, var1.readerIndex(), var1.readableBytes(), true, Base64Dialect.STANDARD, var0);
      var1.readerIndex(var1.writerIndex());
      return var2;
   }

   private SslUtils() {
      super();
   }
}
