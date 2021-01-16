package io.netty.handler.codec.http;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryStringDecoder {
   private static final int DEFAULT_MAX_PARAMS = 1024;
   private final Charset charset;
   private final String uri;
   private final int maxParams;
   private int pathEndIdx;
   private String path;
   private Map<String, List<String>> params;

   public QueryStringDecoder(String var1) {
      this(var1, HttpConstants.DEFAULT_CHARSET);
   }

   public QueryStringDecoder(String var1, boolean var2) {
      this(var1, HttpConstants.DEFAULT_CHARSET, var2);
   }

   public QueryStringDecoder(String var1, Charset var2) {
      this(var1, var2, true);
   }

   public QueryStringDecoder(String var1, Charset var2, boolean var3) {
      this(var1, var2, var3, 1024);
   }

   public QueryStringDecoder(String var1, Charset var2, boolean var3, int var4) {
      super();
      this.uri = (String)ObjectUtil.checkNotNull(var1, "uri");
      this.charset = (Charset)ObjectUtil.checkNotNull(var2, "charset");
      this.maxParams = ObjectUtil.checkPositive(var4, "maxParams");
      this.pathEndIdx = var3 ? -1 : 0;
   }

   public QueryStringDecoder(URI var1) {
      this(var1, HttpConstants.DEFAULT_CHARSET);
   }

   public QueryStringDecoder(URI var1, Charset var2) {
      this(var1, var2, 1024);
   }

   public QueryStringDecoder(URI var1, Charset var2, int var3) {
      super();
      String var4 = var1.getRawPath();
      if (var4 == null) {
         var4 = "";
      }

      String var5 = var1.getRawQuery();
      this.uri = var5 == null ? var4 : var4 + '?' + var5;
      this.charset = (Charset)ObjectUtil.checkNotNull(var2, "charset");
      this.maxParams = ObjectUtil.checkPositive(var3, "maxParams");
      this.pathEndIdx = var4.length();
   }

   public String toString() {
      return this.uri();
   }

   public String uri() {
      return this.uri;
   }

   public String path() {
      if (this.path == null) {
         this.path = decodeComponent(this.uri, 0, this.pathEndIdx(), this.charset, true);
      }

      return this.path;
   }

   public Map<String, List<String>> parameters() {
      if (this.params == null) {
         this.params = decodeParams(this.uri, this.pathEndIdx(), this.charset, this.maxParams);
      }

      return this.params;
   }

   public String rawPath() {
      return this.uri.substring(0, this.pathEndIdx());
   }

   public String rawQuery() {
      int var1 = this.pathEndIdx() + 1;
      return var1 < this.uri.length() ? this.uri.substring(var1) : "";
   }

   private int pathEndIdx() {
      if (this.pathEndIdx == -1) {
         this.pathEndIdx = findPathEndIndex(this.uri);
      }

      return this.pathEndIdx;
   }

   private static Map<String, List<String>> decodeParams(String var0, int var1, Charset var2, int var3) {
      int var4 = var0.length();
      if (var1 >= var4) {
         return Collections.emptyMap();
      } else {
         if (var0.charAt(var1) == '?') {
            ++var1;
         }

         LinkedHashMap var5 = new LinkedHashMap();
         int var6 = var1;
         int var7 = -1;

         int var8;
         label40:
         for(var8 = var1; var8 < var4; ++var8) {
            switch(var0.charAt(var8)) {
            case '#':
               break label40;
            case '&':
            case ';':
               if (addParam(var0, var6, var7, var8, var5, var2)) {
                  --var3;
                  if (var3 == 0) {
                     return var5;
                  }
               }

               var6 = var8 + 1;
               break;
            case '=':
               if (var6 == var8) {
                  var6 = var8 + 1;
               } else if (var7 < var6) {
                  var7 = var8 + 1;
               }
            }
         }

         addParam(var0, var6, var7, var8, var5, var2);
         return var5;
      }
   }

   private static boolean addParam(String var0, int var1, int var2, int var3, Map<String, List<String>> var4, Charset var5) {
      if (var1 >= var3) {
         return false;
      } else {
         if (var2 <= var1) {
            var2 = var3 + 1;
         }

         String var6 = decodeComponent(var0, var1, var2 - 1, var5, false);
         String var7 = decodeComponent(var0, var2, var3, var5, false);
         Object var8 = (List)var4.get(var6);
         if (var8 == null) {
            var8 = new ArrayList(1);
            var4.put(var6, var8);
         }

         ((List)var8).add(var7);
         return true;
      }
   }

   public static String decodeComponent(String var0) {
      return decodeComponent(var0, HttpConstants.DEFAULT_CHARSET);
   }

   public static String decodeComponent(String var0, Charset var1) {
      return var0 == null ? "" : decodeComponent(var0, 0, var0.length(), var1, false);
   }

   private static String decodeComponent(String var0, int var1, int var2, Charset var3, boolean var4) {
      int var5 = var2 - var1;
      if (var5 <= 0) {
         return "";
      } else {
         int var6 = -1;

         for(int var7 = var1; var7 < var2; ++var7) {
            char var8 = var0.charAt(var7);
            if (var8 == '%' || var8 == '+' && !var4) {
               var6 = var7;
               break;
            }
         }

         if (var6 == -1) {
            return var0.substring(var1, var2);
         } else {
            CharsetDecoder var17 = CharsetUtil.decoder(var3);
            int var18 = (var2 - var6) / 3;
            ByteBuffer var9 = ByteBuffer.allocate(var18);
            CharBuffer var10 = CharBuffer.allocate(var18);
            StringBuilder var11 = new StringBuilder(var5);
            var11.append(var0, var1, var6);

            for(int var12 = var6; var12 < var2; ++var12) {
               char var13 = var0.charAt(var12);
               if (var13 != '%') {
                  var11.append(var13 == '+' && !var4 ? ' ' : var13);
               } else {
                  var9.clear();

                  do {
                     if (var12 + 3 > var2) {
                        throw new IllegalArgumentException("unterminated escape sequence at index " + var12 + " of: " + var0);
                     }

                     var9.put(StringUtil.decodeHexByte(var0, var12 + 1));
                     var12 += 3;
                  } while(var12 < var2 && var0.charAt(var12) == '%');

                  --var12;
                  var9.flip();
                  var10.clear();
                  CoderResult var14 = var17.reset().decode(var9, var10, true);

                  try {
                     if (!var14.isUnderflow()) {
                        var14.throwException();
                     }

                     var14 = var17.flush(var10);
                     if (!var14.isUnderflow()) {
                        var14.throwException();
                     }
                  } catch (CharacterCodingException var16) {
                     throw new IllegalStateException(var16);
                  }

                  var11.append(var10.flip());
               }
            }

            return var11.toString();
         }
      }
   }

   private static int findPathEndIndex(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 == '?' || var3 == '#') {
            return var2;
         }
      }

      return var1;
   }
}
