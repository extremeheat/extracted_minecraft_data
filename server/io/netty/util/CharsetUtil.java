package io.netty.util;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Map;

public final class CharsetUtil {
   public static final Charset UTF_16 = Charset.forName("UTF-16");
   public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
   public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
   public static final Charset UTF_8 = Charset.forName("UTF-8");
   public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
   public static final Charset US_ASCII = Charset.forName("US-ASCII");
   private static final Charset[] CHARSETS;

   public static Charset[] values() {
      return CHARSETS;
   }

   /** @deprecated */
   @Deprecated
   public static CharsetEncoder getEncoder(Charset var0) {
      return encoder(var0);
   }

   public static CharsetEncoder encoder(Charset var0, CodingErrorAction var1, CodingErrorAction var2) {
      ObjectUtil.checkNotNull(var0, "charset");
      CharsetEncoder var3 = var0.newEncoder();
      var3.onMalformedInput(var1).onUnmappableCharacter(var2);
      return var3;
   }

   public static CharsetEncoder encoder(Charset var0, CodingErrorAction var1) {
      return encoder(var0, var1, var1);
   }

   public static CharsetEncoder encoder(Charset var0) {
      ObjectUtil.checkNotNull(var0, "charset");
      Map var1 = InternalThreadLocalMap.get().charsetEncoderCache();
      CharsetEncoder var2 = (CharsetEncoder)var1.get(var0);
      if (var2 != null) {
         var2.reset().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
         return var2;
      } else {
         var2 = encoder(var0, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
         var1.put(var0, var2);
         return var2;
      }
   }

   /** @deprecated */
   @Deprecated
   public static CharsetDecoder getDecoder(Charset var0) {
      return decoder(var0);
   }

   public static CharsetDecoder decoder(Charset var0, CodingErrorAction var1, CodingErrorAction var2) {
      ObjectUtil.checkNotNull(var0, "charset");
      CharsetDecoder var3 = var0.newDecoder();
      var3.onMalformedInput(var1).onUnmappableCharacter(var2);
      return var3;
   }

   public static CharsetDecoder decoder(Charset var0, CodingErrorAction var1) {
      return decoder(var0, var1, var1);
   }

   public static CharsetDecoder decoder(Charset var0) {
      ObjectUtil.checkNotNull(var0, "charset");
      Map var1 = InternalThreadLocalMap.get().charsetDecoderCache();
      CharsetDecoder var2 = (CharsetDecoder)var1.get(var0);
      if (var2 != null) {
         var2.reset().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
         return var2;
      } else {
         var2 = decoder(var0, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
         var1.put(var0, var2);
         return var2;
      }
   }

   private CharsetUtil() {
      super();
   }

   static {
      CHARSETS = new Charset[]{UTF_16, UTF_16BE, UTF_16LE, UTF_8, ISO_8859_1, US_ASCII};
   }
}
