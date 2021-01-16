package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;
import java.util.List;

public class HttpPostRequestDecoder implements InterfaceHttpPostRequestDecoder {
   static final int DEFAULT_DISCARD_THRESHOLD = 10485760;
   private final InterfaceHttpPostRequestDecoder decoder;

   public HttpPostRequestDecoder(HttpRequest var1) {
      this(new DefaultHttpDataFactory(16384L), var1, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostRequestDecoder(HttpDataFactory var1, HttpRequest var2) {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostRequestDecoder(HttpDataFactory var1, HttpRequest var2, Charset var3) {
      super();
      if (var1 == null) {
         throw new NullPointerException("factory");
      } else if (var2 == null) {
         throw new NullPointerException("request");
      } else if (var3 == null) {
         throw new NullPointerException("charset");
      } else {
         if (isMultipart(var2)) {
            this.decoder = new HttpPostMultipartRequestDecoder(var1, var2, var3);
         } else {
            this.decoder = new HttpPostStandardRequestDecoder(var1, var2, var3);
         }

      }
   }

   public static boolean isMultipart(HttpRequest var0) {
      if (var0.headers().contains((CharSequence)HttpHeaderNames.CONTENT_TYPE)) {
         return getMultipartDataBoundary(var0.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE)) != null;
      } else {
         return false;
      }
   }

   protected static String[] getMultipartDataBoundary(String var0) {
      String[] var1 = splitHeaderContentType(var0);
      String var2 = HttpHeaderValues.MULTIPART_FORM_DATA.toString();
      if (var1[0].regionMatches(true, 0, var2, 0, var2.length())) {
         String var5 = HttpHeaderValues.BOUNDARY.toString();
         byte var3;
         byte var4;
         if (var1[1].regionMatches(true, 0, var5, 0, var5.length())) {
            var3 = 1;
            var4 = 2;
         } else {
            if (!var1[2].regionMatches(true, 0, var5, 0, var5.length())) {
               return null;
            }

            var3 = 2;
            var4 = 1;
         }

         String var6 = StringUtil.substringAfter(var1[var3], '=');
         if (var6 == null) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Needs a boundary value");
         } else {
            String var7;
            if (var6.charAt(0) == '"') {
               var7 = var6.trim();
               int var8 = var7.length() - 1;
               if (var7.charAt(var8) == '"') {
                  var6 = var7.substring(1, var8);
               }
            }

            var7 = HttpHeaderValues.CHARSET.toString();
            if (var1[var4].regionMatches(true, 0, var7, 0, var7.length())) {
               String var9 = StringUtil.substringAfter(var1[var4], '=');
               if (var9 != null) {
                  return new String[]{"--" + var6, var9};
               }
            }

            return new String[]{"--" + var6};
         }
      } else {
         return null;
      }
   }

   public boolean isMultipart() {
      return this.decoder.isMultipart();
   }

   public void setDiscardThreshold(int var1) {
      this.decoder.setDiscardThreshold(var1);
   }

   public int getDiscardThreshold() {
      return this.decoder.getDiscardThreshold();
   }

   public List<InterfaceHttpData> getBodyHttpDatas() {
      return this.decoder.getBodyHttpDatas();
   }

   public List<InterfaceHttpData> getBodyHttpDatas(String var1) {
      return this.decoder.getBodyHttpDatas(var1);
   }

   public InterfaceHttpData getBodyHttpData(String var1) {
      return this.decoder.getBodyHttpData(var1);
   }

   public InterfaceHttpPostRequestDecoder offer(HttpContent var1) {
      return this.decoder.offer(var1);
   }

   public boolean hasNext() {
      return this.decoder.hasNext();
   }

   public InterfaceHttpData next() {
      return this.decoder.next();
   }

   public InterfaceHttpData currentPartialHttpData() {
      return this.decoder.currentPartialHttpData();
   }

   public void destroy() {
      this.decoder.destroy();
   }

   public void cleanFiles() {
      this.decoder.cleanFiles();
   }

   public void removeHttpDataFromClean(InterfaceHttpData var1) {
      this.decoder.removeHttpDataFromClean(var1);
   }

   private static String[] splitHeaderContentType(String var0) {
      int var1 = HttpPostBodyUtil.findNonWhitespace(var0, 0);
      int var2 = var0.indexOf(59);
      if (var2 == -1) {
         return new String[]{var0, "", ""};
      } else {
         int var3 = HttpPostBodyUtil.findNonWhitespace(var0, var2 + 1);
         if (var0.charAt(var2 - 1) == ' ') {
            --var2;
         }

         int var4 = var0.indexOf(59, var3);
         if (var4 == -1) {
            var4 = HttpPostBodyUtil.findEndOfString(var0);
            return new String[]{var0.substring(var1, var2), var0.substring(var3, var4), ""};
         } else {
            int var5 = HttpPostBodyUtil.findNonWhitespace(var0, var4 + 1);
            if (var0.charAt(var4 - 1) == ' ') {
               --var4;
            }

            int var6 = HttpPostBodyUtil.findEndOfString(var0);
            return new String[]{var0.substring(var1, var2), var0.substring(var3, var4), var0.substring(var5, var6)};
         }
      }
   }

   public static class ErrorDataDecoderException extends DecoderException {
      private static final long serialVersionUID = 5020247425493164465L;

      public ErrorDataDecoderException() {
         super();
      }

      public ErrorDataDecoderException(String var1) {
         super(var1);
      }

      public ErrorDataDecoderException(Throwable var1) {
         super(var1);
      }

      public ErrorDataDecoderException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   public static class EndOfDataDecoderException extends DecoderException {
      private static final long serialVersionUID = 1336267941020800769L;

      public EndOfDataDecoderException() {
         super();
      }
   }

   public static class NotEnoughDataDecoderException extends DecoderException {
      private static final long serialVersionUID = -7846841864603865638L;

      public NotEnoughDataDecoderException() {
         super();
      }

      public NotEnoughDataDecoderException(String var1) {
         super(var1);
      }

      public NotEnoughDataDecoderException(Throwable var1) {
         super(var1);
      }

      public NotEnoughDataDecoderException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   protected static enum MultiPartStatus {
      NOTSTARTED,
      PREAMBLE,
      HEADERDELIMITER,
      DISPOSITION,
      FIELD,
      FILEUPLOAD,
      MIXEDPREAMBLE,
      MIXEDDELIMITER,
      MIXEDDISPOSITION,
      MIXEDFILEUPLOAD,
      MIXEDCLOSEDELIMITER,
      CLOSEDELIMITER,
      PREEPILOGUE,
      EPILOGUE;

      private MultiPartStatus() {
      }
   }
}
