package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.HeadersUtils;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public abstract class HttpHeaders implements Iterable<Entry<String, String>> {
   /** @deprecated */
   @Deprecated
   public static final HttpHeaders EMPTY_HEADERS = EmptyHttpHeaders.instance();

   /** @deprecated */
   @Deprecated
   public static boolean isKeepAlive(HttpMessage var0) {
      return HttpUtil.isKeepAlive(var0);
   }

   /** @deprecated */
   @Deprecated
   public static void setKeepAlive(HttpMessage var0, boolean var1) {
      HttpUtil.setKeepAlive(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static String getHeader(HttpMessage var0, String var1) {
      return var0.headers().get(var1);
   }

   /** @deprecated */
   @Deprecated
   public static String getHeader(HttpMessage var0, CharSequence var1) {
      return var0.headers().get(var1);
   }

   /** @deprecated */
   @Deprecated
   public static String getHeader(HttpMessage var0, String var1, String var2) {
      return var0.headers().get(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static String getHeader(HttpMessage var0, CharSequence var1, String var2) {
      return var0.headers().get(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setHeader(HttpMessage var0, String var1, Object var2) {
      var0.headers().set(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setHeader(HttpMessage var0, CharSequence var1, Object var2) {
      var0.headers().set(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setHeader(HttpMessage var0, String var1, Iterable<?> var2) {
      var0.headers().set(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setHeader(HttpMessage var0, CharSequence var1, Iterable<?> var2) {
      var0.headers().set(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void addHeader(HttpMessage var0, String var1, Object var2) {
      var0.headers().add(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void addHeader(HttpMessage var0, CharSequence var1, Object var2) {
      var0.headers().add(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void removeHeader(HttpMessage var0, String var1) {
      var0.headers().remove(var1);
   }

   /** @deprecated */
   @Deprecated
   public static void removeHeader(HttpMessage var0, CharSequence var1) {
      var0.headers().remove(var1);
   }

   /** @deprecated */
   @Deprecated
   public static void clearHeaders(HttpMessage var0) {
      var0.headers().clear();
   }

   /** @deprecated */
   @Deprecated
   public static int getIntHeader(HttpMessage var0, String var1) {
      return getIntHeader(var0, (CharSequence)var1);
   }

   /** @deprecated */
   @Deprecated
   public static int getIntHeader(HttpMessage var0, CharSequence var1) {
      String var2 = var0.headers().get(var1);
      if (var2 == null) {
         throw new NumberFormatException("header not found: " + var1);
      } else {
         return Integer.parseInt(var2);
      }
   }

   /** @deprecated */
   @Deprecated
   public static int getIntHeader(HttpMessage var0, String var1, int var2) {
      return var0.headers().getInt(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static int getIntHeader(HttpMessage var0, CharSequence var1, int var2) {
      return var0.headers().getInt(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setIntHeader(HttpMessage var0, String var1, int var2) {
      var0.headers().setInt(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setIntHeader(HttpMessage var0, CharSequence var1, int var2) {
      var0.headers().setInt(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setIntHeader(HttpMessage var0, String var1, Iterable<Integer> var2) {
      var0.headers().set(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setIntHeader(HttpMessage var0, CharSequence var1, Iterable<Integer> var2) {
      var0.headers().set(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void addIntHeader(HttpMessage var0, String var1, int var2) {
      var0.headers().add((String)var1, (Object)var2);
   }

   /** @deprecated */
   @Deprecated
   public static void addIntHeader(HttpMessage var0, CharSequence var1, int var2) {
      var0.headers().addInt(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static Date getDateHeader(HttpMessage var0, String var1) throws ParseException {
      return getDateHeader(var0, (CharSequence)var1);
   }

   /** @deprecated */
   @Deprecated
   public static Date getDateHeader(HttpMessage var0, CharSequence var1) throws ParseException {
      String var2 = var0.headers().get(var1);
      if (var2 == null) {
         throw new ParseException("header not found: " + var1, 0);
      } else {
         Date var3 = DateFormatter.parseHttpDate(var2);
         if (var3 == null) {
            throw new ParseException("header can't be parsed into a Date: " + var2, 0);
         } else {
            return var3;
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public static Date getDateHeader(HttpMessage var0, String var1, Date var2) {
      return getDateHeader(var0, (CharSequence)var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static Date getDateHeader(HttpMessage var0, CharSequence var1, Date var2) {
      String var3 = getHeader(var0, var1);
      Date var4 = DateFormatter.parseHttpDate(var3);
      return var4 != null ? var4 : var2;
   }

   /** @deprecated */
   @Deprecated
   public static void setDateHeader(HttpMessage var0, String var1, Date var2) {
      setDateHeader(var0, (CharSequence)var1, (Date)var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setDateHeader(HttpMessage var0, CharSequence var1, Date var2) {
      if (var2 != null) {
         var0.headers().set((CharSequence)var1, (Object)DateFormatter.format(var2));
      } else {
         var0.headers().set((CharSequence)var1, (Iterable)null);
      }

   }

   /** @deprecated */
   @Deprecated
   public static void setDateHeader(HttpMessage var0, String var1, Iterable<Date> var2) {
      var0.headers().set(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void setDateHeader(HttpMessage var0, CharSequence var1, Iterable<Date> var2) {
      var0.headers().set(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public static void addDateHeader(HttpMessage var0, String var1, Date var2) {
      var0.headers().add((String)var1, (Object)var2);
   }

   /** @deprecated */
   @Deprecated
   public static void addDateHeader(HttpMessage var0, CharSequence var1, Date var2) {
      var0.headers().add((CharSequence)var1, (Object)var2);
   }

   /** @deprecated */
   @Deprecated
   public static long getContentLength(HttpMessage var0) {
      return HttpUtil.getContentLength(var0);
   }

   /** @deprecated */
   @Deprecated
   public static long getContentLength(HttpMessage var0, long var1) {
      return HttpUtil.getContentLength(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static void setContentLength(HttpMessage var0, long var1) {
      HttpUtil.setContentLength(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static String getHost(HttpMessage var0) {
      return var0.headers().get((CharSequence)HttpHeaderNames.HOST);
   }

   /** @deprecated */
   @Deprecated
   public static String getHost(HttpMessage var0, String var1) {
      return var0.headers().get(HttpHeaderNames.HOST, var1);
   }

   /** @deprecated */
   @Deprecated
   public static void setHost(HttpMessage var0, String var1) {
      var0.headers().set((CharSequence)HttpHeaderNames.HOST, (Object)var1);
   }

   /** @deprecated */
   @Deprecated
   public static void setHost(HttpMessage var0, CharSequence var1) {
      var0.headers().set((CharSequence)HttpHeaderNames.HOST, (Object)var1);
   }

   /** @deprecated */
   @Deprecated
   public static Date getDate(HttpMessage var0) throws ParseException {
      return getDateHeader(var0, (CharSequence)HttpHeaderNames.DATE);
   }

   /** @deprecated */
   @Deprecated
   public static Date getDate(HttpMessage var0, Date var1) {
      return getDateHeader(var0, (CharSequence)HttpHeaderNames.DATE, var1);
   }

   /** @deprecated */
   @Deprecated
   public static void setDate(HttpMessage var0, Date var1) {
      var0.headers().set((CharSequence)HttpHeaderNames.DATE, (Object)var1);
   }

   /** @deprecated */
   @Deprecated
   public static boolean is100ContinueExpected(HttpMessage var0) {
      return HttpUtil.is100ContinueExpected(var0);
   }

   /** @deprecated */
   @Deprecated
   public static void set100ContinueExpected(HttpMessage var0) {
      HttpUtil.set100ContinueExpected(var0, true);
   }

   /** @deprecated */
   @Deprecated
   public static void set100ContinueExpected(HttpMessage var0, boolean var1) {
      HttpUtil.set100ContinueExpected(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static boolean isTransferEncodingChunked(HttpMessage var0) {
      return HttpUtil.isTransferEncodingChunked(var0);
   }

   /** @deprecated */
   @Deprecated
   public static void removeTransferEncodingChunked(HttpMessage var0) {
      HttpUtil.setTransferEncodingChunked(var0, false);
   }

   /** @deprecated */
   @Deprecated
   public static void setTransferEncodingChunked(HttpMessage var0) {
      HttpUtil.setTransferEncodingChunked(var0, true);
   }

   /** @deprecated */
   @Deprecated
   public static boolean isContentLengthSet(HttpMessage var0) {
      return HttpUtil.isContentLengthSet(var0);
   }

   /** @deprecated */
   @Deprecated
   public static boolean equalsIgnoreCase(CharSequence var0, CharSequence var1) {
      return AsciiString.contentEqualsIgnoreCase(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static void encodeAscii(CharSequence var0, ByteBuf var1) {
      if (var0 instanceof AsciiString) {
         ByteBufUtil.copy((AsciiString)var0, 0, var1, var0.length());
      } else {
         var1.writeCharSequence(var0, CharsetUtil.US_ASCII);
      }

   }

   /** @deprecated */
   @Deprecated
   public static CharSequence newEntity(String var0) {
      return new AsciiString(var0);
   }

   protected HttpHeaders() {
      super();
   }

   public abstract String get(String var1);

   public String get(CharSequence var1) {
      return this.get(var1.toString());
   }

   public String get(CharSequence var1, String var2) {
      String var3 = this.get(var1);
      return var3 == null ? var2 : var3;
   }

   public abstract Integer getInt(CharSequence var1);

   public abstract int getInt(CharSequence var1, int var2);

   public abstract Short getShort(CharSequence var1);

   public abstract short getShort(CharSequence var1, short var2);

   public abstract Long getTimeMillis(CharSequence var1);

   public abstract long getTimeMillis(CharSequence var1, long var2);

   public abstract List<String> getAll(String var1);

   public List<String> getAll(CharSequence var1) {
      return this.getAll(var1.toString());
   }

   public abstract List<Entry<String, String>> entries();

   public abstract boolean contains(String var1);

   /** @deprecated */
   @Deprecated
   public abstract Iterator<Entry<String, String>> iterator();

   public abstract Iterator<Entry<CharSequence, CharSequence>> iteratorCharSequence();

   public Iterator<String> valueStringIterator(CharSequence var1) {
      return this.getAll(var1).iterator();
   }

   public Iterator<? extends CharSequence> valueCharSequenceIterator(CharSequence var1) {
      return this.valueStringIterator(var1);
   }

   public boolean contains(CharSequence var1) {
      return this.contains(var1.toString());
   }

   public abstract boolean isEmpty();

   public abstract int size();

   public abstract Set<String> names();

   public abstract HttpHeaders add(String var1, Object var2);

   public HttpHeaders add(CharSequence var1, Object var2) {
      return this.add(var1.toString(), var2);
   }

   public abstract HttpHeaders add(String var1, Iterable<?> var2);

   public HttpHeaders add(CharSequence var1, Iterable<?> var2) {
      return this.add(var1.toString(), var2);
   }

   public HttpHeaders add(HttpHeaders var1) {
      if (var1 == null) {
         throw new NullPointerException("headers");
      } else {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.add((String)var3.getKey(), var3.getValue());
         }

         return this;
      }
   }

   public abstract HttpHeaders addInt(CharSequence var1, int var2);

   public abstract HttpHeaders addShort(CharSequence var1, short var2);

   public abstract HttpHeaders set(String var1, Object var2);

   public HttpHeaders set(CharSequence var1, Object var2) {
      return this.set(var1.toString(), var2);
   }

   public abstract HttpHeaders set(String var1, Iterable<?> var2);

   public HttpHeaders set(CharSequence var1, Iterable<?> var2) {
      return this.set(var1.toString(), var2);
   }

   public HttpHeaders set(HttpHeaders var1) {
      ObjectUtil.checkNotNull(var1, "headers");
      this.clear();
      if (var1.isEmpty()) {
         return this;
      } else {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.add((String)var3.getKey(), var3.getValue());
         }

         return this;
      }
   }

   public HttpHeaders setAll(HttpHeaders var1) {
      ObjectUtil.checkNotNull(var1, "headers");
      if (var1.isEmpty()) {
         return this;
      } else {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.set((String)var3.getKey(), var3.getValue());
         }

         return this;
      }
   }

   public abstract HttpHeaders setInt(CharSequence var1, int var2);

   public abstract HttpHeaders setShort(CharSequence var1, short var2);

   public abstract HttpHeaders remove(String var1);

   public HttpHeaders remove(CharSequence var1) {
      return this.remove(var1.toString());
   }

   public abstract HttpHeaders clear();

   public boolean contains(String var1, String var2, boolean var3) {
      Iterator var4 = this.valueStringIterator(var1);
      if (var3) {
         while(var4.hasNext()) {
            if (((String)var4.next()).equalsIgnoreCase(var2)) {
               return true;
            }
         }
      } else {
         while(var4.hasNext()) {
            if (((String)var4.next()).equals(var2)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean containsValue(CharSequence var1, CharSequence var2, boolean var3) {
      Iterator var4 = this.valueCharSequenceIterator(var1);

      do {
         if (!var4.hasNext()) {
            return false;
         }
      } while(!containsCommaSeparatedTrimmed((CharSequence)var4.next(), var2, var3));

      return true;
   }

   private static boolean containsCommaSeparatedTrimmed(CharSequence var0, CharSequence var1, boolean var2) {
      int var3 = 0;
      int var4;
      if (var2) {
         if ((var4 = AsciiString.indexOf(var0, ',', var3)) == -1) {
            if (AsciiString.contentEqualsIgnoreCase(AsciiString.trim(var0), var1)) {
               return true;
            }
         } else {
            do {
               if (AsciiString.contentEqualsIgnoreCase(AsciiString.trim(var0.subSequence(var3, var4)), var1)) {
                  return true;
               }

               var3 = var4 + 1;
            } while((var4 = AsciiString.indexOf(var0, ',', var3)) != -1);

            if (var3 < var0.length() && AsciiString.contentEqualsIgnoreCase(AsciiString.trim(var0.subSequence(var3, var0.length())), var1)) {
               return true;
            }
         }
      } else if ((var4 = AsciiString.indexOf(var0, ',', var3)) == -1) {
         if (AsciiString.contentEquals(AsciiString.trim(var0), var1)) {
            return true;
         }
      } else {
         do {
            if (AsciiString.contentEquals(AsciiString.trim(var0.subSequence(var3, var4)), var1)) {
               return true;
            }

            var3 = var4 + 1;
         } while((var4 = AsciiString.indexOf(var0, ',', var3)) != -1);

         if (var3 < var0.length() && AsciiString.contentEquals(AsciiString.trim(var0.subSequence(var3, var0.length())), var1)) {
            return true;
         }
      }

      return false;
   }

   public final String getAsString(CharSequence var1) {
      return this.get(var1);
   }

   public final List<String> getAllAsString(CharSequence var1) {
      return this.getAll(var1);
   }

   public final Iterator<Entry<String, String>> iteratorAsString() {
      return this.iterator();
   }

   public boolean contains(CharSequence var1, CharSequence var2, boolean var3) {
      return this.contains(var1.toString(), var2.toString(), var3);
   }

   public String toString() {
      return HeadersUtils.toString(this.getClass(), this.iteratorCharSequence(), this.size());
   }

   public HttpHeaders copy() {
      return (new DefaultHttpHeaders()).set(this);
   }

   /** @deprecated */
   @Deprecated
   public static final class Values {
      public static final String APPLICATION_JSON = "application/json";
      public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
      public static final String BASE64 = "base64";
      public static final String BINARY = "binary";
      public static final String BOUNDARY = "boundary";
      public static final String BYTES = "bytes";
      public static final String CHARSET = "charset";
      public static final String CHUNKED = "chunked";
      public static final String CLOSE = "close";
      public static final String COMPRESS = "compress";
      public static final String CONTINUE = "100-continue";
      public static final String DEFLATE = "deflate";
      public static final String GZIP = "gzip";
      public static final String GZIP_DEFLATE = "gzip,deflate";
      public static final String IDENTITY = "identity";
      public static final String KEEP_ALIVE = "keep-alive";
      public static final String MAX_AGE = "max-age";
      public static final String MAX_STALE = "max-stale";
      public static final String MIN_FRESH = "min-fresh";
      public static final String MULTIPART_FORM_DATA = "multipart/form-data";
      public static final String MUST_REVALIDATE = "must-revalidate";
      public static final String NO_CACHE = "no-cache";
      public static final String NO_STORE = "no-store";
      public static final String NO_TRANSFORM = "no-transform";
      public static final String NONE = "none";
      public static final String ONLY_IF_CACHED = "only-if-cached";
      public static final String PRIVATE = "private";
      public static final String PROXY_REVALIDATE = "proxy-revalidate";
      public static final String PUBLIC = "public";
      public static final String QUOTED_PRINTABLE = "quoted-printable";
      public static final String S_MAXAGE = "s-maxage";
      public static final String TRAILERS = "trailers";
      public static final String UPGRADE = "Upgrade";
      public static final String WEBSOCKET = "WebSocket";

      private Values() {
         super();
      }
   }

   /** @deprecated */
   @Deprecated
   public static final class Names {
      public static final String ACCEPT = "Accept";
      public static final String ACCEPT_CHARSET = "Accept-Charset";
      public static final String ACCEPT_ENCODING = "Accept-Encoding";
      public static final String ACCEPT_LANGUAGE = "Accept-Language";
      public static final String ACCEPT_RANGES = "Accept-Ranges";
      public static final String ACCEPT_PATCH = "Accept-Patch";
      public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
      public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
      public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
      public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
      public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
      public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
      public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
      public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
      public static final String AGE = "Age";
      public static final String ALLOW = "Allow";
      public static final String AUTHORIZATION = "Authorization";
      public static final String CACHE_CONTROL = "Cache-Control";
      public static final String CONNECTION = "Connection";
      public static final String CONTENT_BASE = "Content-Base";
      public static final String CONTENT_ENCODING = "Content-Encoding";
      public static final String CONTENT_LANGUAGE = "Content-Language";
      public static final String CONTENT_LENGTH = "Content-Length";
      public static final String CONTENT_LOCATION = "Content-Location";
      public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
      public static final String CONTENT_MD5 = "Content-MD5";
      public static final String CONTENT_RANGE = "Content-Range";
      public static final String CONTENT_TYPE = "Content-Type";
      public static final String COOKIE = "Cookie";
      public static final String DATE = "Date";
      public static final String ETAG = "ETag";
      public static final String EXPECT = "Expect";
      public static final String EXPIRES = "Expires";
      public static final String FROM = "From";
      public static final String HOST = "Host";
      public static final String IF_MATCH = "If-Match";
      public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
      public static final String IF_NONE_MATCH = "If-None-Match";
      public static final String IF_RANGE = "If-Range";
      public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
      public static final String LAST_MODIFIED = "Last-Modified";
      public static final String LOCATION = "Location";
      public static final String MAX_FORWARDS = "Max-Forwards";
      public static final String ORIGIN = "Origin";
      public static final String PRAGMA = "Pragma";
      public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
      public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
      public static final String RANGE = "Range";
      public static final String REFERER = "Referer";
      public static final String RETRY_AFTER = "Retry-After";
      public static final String SEC_WEBSOCKET_KEY1 = "Sec-WebSocket-Key1";
      public static final String SEC_WEBSOCKET_KEY2 = "Sec-WebSocket-Key2";
      public static final String SEC_WEBSOCKET_LOCATION = "Sec-WebSocket-Location";
      public static final String SEC_WEBSOCKET_ORIGIN = "Sec-WebSocket-Origin";
      public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
      public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
      public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
      public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";
      public static final String SERVER = "Server";
      public static final String SET_COOKIE = "Set-Cookie";
      public static final String SET_COOKIE2 = "Set-Cookie2";
      public static final String TE = "TE";
      public static final String TRAILER = "Trailer";
      public static final String TRANSFER_ENCODING = "Transfer-Encoding";
      public static final String UPGRADE = "Upgrade";
      public static final String USER_AGENT = "User-Agent";
      public static final String VARY = "Vary";
      public static final String VIA = "Via";
      public static final String WARNING = "Warning";
      public static final String WEBSOCKET_LOCATION = "WebSocket-Location";
      public static final String WEBSOCKET_ORIGIN = "WebSocket-Origin";
      public static final String WEBSOCKET_PROTOCOL = "WebSocket-Protocol";
      public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

      private Names() {
         super();
      }
   }
}
