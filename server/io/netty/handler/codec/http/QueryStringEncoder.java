package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class QueryStringEncoder {
   private final String charsetName;
   private final StringBuilder uriBuilder;
   private boolean hasParams;

   public QueryStringEncoder(String var1) {
      this(var1, HttpConstants.DEFAULT_CHARSET);
   }

   public QueryStringEncoder(String var1, Charset var2) {
      super();
      this.uriBuilder = new StringBuilder(var1);
      this.charsetName = var2.name();
   }

   public void addParam(String var1, String var2) {
      ObjectUtil.checkNotNull(var1, "name");
      if (this.hasParams) {
         this.uriBuilder.append('&');
      } else {
         this.uriBuilder.append('?');
         this.hasParams = true;
      }

      appendComponent(var1, this.charsetName, this.uriBuilder);
      if (var2 != null) {
         this.uriBuilder.append('=');
         appendComponent(var2, this.charsetName, this.uriBuilder);
      }

   }

   public URI toUri() throws URISyntaxException {
      return new URI(this.toString());
   }

   public String toString() {
      return this.uriBuilder.toString();
   }

   private static void appendComponent(String var0, String var1, StringBuilder var2) {
      try {
         var0 = URLEncoder.encode(var0, var1);
      } catch (UnsupportedEncodingException var6) {
         throw new UnsupportedCharsetException(var1);
      }

      int var3 = var0.indexOf(43);
      if (var3 == -1) {
         var2.append(var0);
      } else {
         var2.append(var0, 0, var3).append("%20");
         int var4 = var0.length();
         ++var3;

         for(; var3 < var4; ++var3) {
            char var5 = var0.charAt(var3);
            if (var5 != '+') {
               var2.append(var5);
            } else {
               var2.append("%20");
            }
         }

      }
   }
}
