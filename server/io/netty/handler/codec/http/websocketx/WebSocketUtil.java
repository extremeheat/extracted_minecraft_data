package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.FastThreadLocal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class WebSocketUtil {
   private static final FastThreadLocal<MessageDigest> MD5 = new FastThreadLocal<MessageDigest>() {
      protected MessageDigest initialValue() throws Exception {
         try {
            return MessageDigest.getInstance("MD5");
         } catch (NoSuchAlgorithmException var2) {
            throw new InternalError("MD5 not supported on this platform - Outdated?");
         }
      }
   };
   private static final FastThreadLocal<MessageDigest> SHA1 = new FastThreadLocal<MessageDigest>() {
      protected MessageDigest initialValue() throws Exception {
         try {
            return MessageDigest.getInstance("SHA1");
         } catch (NoSuchAlgorithmException var2) {
            throw new InternalError("SHA-1 not supported on this platform - Outdated?");
         }
      }
   };

   static byte[] md5(byte[] var0) {
      return digest(MD5, var0);
   }

   static byte[] sha1(byte[] var0) {
      return digest(SHA1, var0);
   }

   private static byte[] digest(FastThreadLocal<MessageDigest> var0, byte[] var1) {
      MessageDigest var2 = (MessageDigest)var0.get();
      var2.reset();
      return var2.digest(var1);
   }

   static String base64(byte[] var0) {
      ByteBuf var1 = Unpooled.wrappedBuffer(var0);
      ByteBuf var2 = Base64.encode(var1);
      String var3 = var2.toString(CharsetUtil.UTF_8);
      var2.release();
      return var3;
   }

   static byte[] randomBytes(int var0) {
      byte[] var1 = new byte[var0];

      for(int var2 = 0; var2 < var0; ++var2) {
         var1[var2] = (byte)randomNumber(0, 255);
      }

      return var1;
   }

   static int randomNumber(int var0, int var1) {
      return (int)(Math.random() * (double)var1 + (double)var0);
   }

   private WebSocketUtil() {
      super();
   }
}
