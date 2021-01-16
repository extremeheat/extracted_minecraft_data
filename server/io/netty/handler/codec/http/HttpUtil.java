package io.netty.handler.codec.http;

import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class HttpUtil {
   private static final AsciiString CHARSET_EQUALS;
   private static final AsciiString SEMICOLON;

   private HttpUtil() {
      super();
   }

   public static boolean isOriginForm(URI var0) {
      return var0.getScheme() == null && var0.getSchemeSpecificPart() == null && var0.getHost() == null && var0.getAuthority() == null;
   }

   public static boolean isAsteriskForm(URI var0) {
      return "*".equals(var0.getPath()) && var0.getScheme() == null && var0.getSchemeSpecificPart() == null && var0.getHost() == null && var0.getAuthority() == null && var0.getQuery() == null && var0.getFragment() == null;
   }

   public static boolean isKeepAlive(HttpMessage var0) {
      String var1 = var0.headers().get((CharSequence)HttpHeaderNames.CONNECTION);
      if (var1 != null && HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(var1)) {
         return false;
      } else if (var0.protocolVersion().isKeepAliveDefault()) {
         return !HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(var1);
      } else {
         return HttpHeaderValues.KEEP_ALIVE.contentEqualsIgnoreCase(var1);
      }
   }

   public static void setKeepAlive(HttpMessage var0, boolean var1) {
      setKeepAlive(var0.headers(), var0.protocolVersion(), var1);
   }

   public static void setKeepAlive(HttpHeaders var0, HttpVersion var1, boolean var2) {
      if (var1.isKeepAliveDefault()) {
         if (var2) {
            var0.remove((CharSequence)HttpHeaderNames.CONNECTION);
         } else {
            var0.set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.CLOSE);
         }
      } else if (var2) {
         var0.set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.KEEP_ALIVE);
      } else {
         var0.remove((CharSequence)HttpHeaderNames.CONNECTION);
      }

   }

   public static long getContentLength(HttpMessage var0) {
      String var1 = var0.headers().get((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
      if (var1 != null) {
         return Long.parseLong(var1);
      } else {
         long var2 = (long)getWebSocketContentLength(var0);
         if (var2 >= 0L) {
            return var2;
         } else {
            throw new NumberFormatException("header not found: " + HttpHeaderNames.CONTENT_LENGTH);
         }
      }
   }

   public static long getContentLength(HttpMessage var0, long var1) {
      String var3 = var0.headers().get((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
      if (var3 != null) {
         return Long.parseLong(var3);
      } else {
         long var4 = (long)getWebSocketContentLength(var0);
         return var4 >= 0L ? var4 : var1;
      }
   }

   public static int getContentLength(HttpMessage var0, int var1) {
      return (int)Math.min(2147483647L, getContentLength(var0, (long)var1));
   }

   private static int getWebSocketContentLength(HttpMessage var0) {
      HttpHeaders var1 = var0.headers();
      if (var0 instanceof HttpRequest) {
         HttpRequest var2 = (HttpRequest)var0;
         if (HttpMethod.GET.equals(var2.method()) && var1.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1) && var1.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2)) {
            return 8;
         }
      } else if (var0 instanceof HttpResponse) {
         HttpResponse var3 = (HttpResponse)var0;
         if (var3.status().code() == 101 && var1.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN) && var1.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_LOCATION)) {
            return 16;
         }
      }

      return -1;
   }

   public static void setContentLength(HttpMessage var0, long var1) {
      var0.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)var1);
   }

   public static boolean isContentLengthSet(HttpMessage var0) {
      return var0.headers().contains((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
   }

   public static boolean is100ContinueExpected(HttpMessage var0) {
      if (!isExpectHeaderValid(var0)) {
         return false;
      } else {
         String var1 = var0.headers().get((CharSequence)HttpHeaderNames.EXPECT);
         return HttpHeaderValues.CONTINUE.toString().equalsIgnoreCase(var1);
      }
   }

   static boolean isUnsupportedExpectation(HttpMessage var0) {
      if (!isExpectHeaderValid(var0)) {
         return false;
      } else {
         String var1 = var0.headers().get((CharSequence)HttpHeaderNames.EXPECT);
         return var1 != null && !HttpHeaderValues.CONTINUE.toString().equalsIgnoreCase(var1);
      }
   }

   private static boolean isExpectHeaderValid(HttpMessage var0) {
      return var0 instanceof HttpRequest && var0.protocolVersion().compareTo(HttpVersion.HTTP_1_1) >= 0;
   }

   public static void set100ContinueExpected(HttpMessage var0, boolean var1) {
      if (var1) {
         var0.headers().set((CharSequence)HttpHeaderNames.EXPECT, (Object)HttpHeaderValues.CONTINUE);
      } else {
         var0.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
      }

   }

   public static boolean isTransferEncodingChunked(HttpMessage var0) {
      return var0.headers().contains((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (CharSequence)HttpHeaderValues.CHUNKED, true);
   }

   public static void setTransferEncodingChunked(HttpMessage var0, boolean var1) {
      if (var1) {
         var0.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
         var0.headers().remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
      } else {
         List var2 = var0.headers().getAll((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
         if (var2.isEmpty()) {
            return;
         }

         ArrayList var3 = new ArrayList(var2);
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            CharSequence var5 = (CharSequence)var4.next();
            if (HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase(var5)) {
               var4.remove();
            }
         }

         if (var3.isEmpty()) {
            var0.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
         } else {
            var0.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Iterable)var3);
         }
      }

   }

   public static Charset getCharset(HttpMessage var0) {
      return getCharset(var0, CharsetUtil.ISO_8859_1);
   }

   public static Charset getCharset(CharSequence var0) {
      return var0 != null ? getCharset(var0, CharsetUtil.ISO_8859_1) : CharsetUtil.ISO_8859_1;
   }

   public static Charset getCharset(HttpMessage var0, Charset var1) {
      String var2 = var0.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
      return var2 != null ? getCharset((CharSequence)var2, var1) : var1;
   }

   public static Charset getCharset(CharSequence var0, Charset var1) {
      if (var0 != null) {
         CharSequence var2 = getCharsetAsSequence(var0);
         if (var2 != null) {
            try {
               return Charset.forName(var2.toString());
            } catch (UnsupportedCharsetException var4) {
               return var1;
            }
         } else {
            return var1;
         }
      } else {
         return var1;
      }
   }

   /** @deprecated */
   @Deprecated
   public static CharSequence getCharsetAsString(HttpMessage var0) {
      return getCharsetAsSequence(var0);
   }

   public static CharSequence getCharsetAsSequence(HttpMessage var0) {
      String var1 = var0.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
      return var1 != null ? getCharsetAsSequence((CharSequence)var1) : null;
   }

   public static CharSequence getCharsetAsSequence(CharSequence var0) {
      if (var0 == null) {
         throw new NullPointerException("contentTypeValue");
      } else {
         int var1 = AsciiString.indexOfIgnoreCaseAscii(var0, CHARSET_EQUALS, 0);
         if (var1 != -1) {
            int var2 = var1 + CHARSET_EQUALS.length();
            if (var2 < var0.length()) {
               return var0.subSequence(var2, var0.length());
            }
         }

         return null;
      }
   }

   public static CharSequence getMimeType(HttpMessage var0) {
      String var1 = var0.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
      return var1 != null ? getMimeType((CharSequence)var1) : null;
   }

   public static CharSequence getMimeType(CharSequence var0) {
      if (var0 == null) {
         throw new NullPointerException("contentTypeValue");
      } else {
         int var1 = AsciiString.indexOfIgnoreCaseAscii(var0, SEMICOLON, 0);
         if (var1 != -1) {
            return var0.subSequence(0, var1);
         } else {
            return var0.length() > 0 ? var0 : null;
         }
      }
   }

   public static String formatHostnameForHttp(InetSocketAddress var0) {
      String var1 = NetUtil.getHostname(var0);
      if (NetUtil.isValidIpV6Address(var1)) {
         if (!var0.isUnresolved()) {
            var1 = NetUtil.toAddressString(var0.getAddress());
         }

         return "[" + var1 + "]";
      } else {
         return var1;
      }
   }

   static {
      CHARSET_EQUALS = AsciiString.of(HttpHeaderValues.CHARSET + "=");
      SEMICOLON = AsciiString.cached(";");
   }
}
