package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class HttpConversionUtil {
   private static final CharSequenceMap<AsciiString> HTTP_TO_HTTP2_HEADER_BLACKLIST = new CharSequenceMap();
   public static final HttpMethod OUT_OF_MESSAGE_SEQUENCE_METHOD;
   public static final String OUT_OF_MESSAGE_SEQUENCE_PATH = "";
   public static final HttpResponseStatus OUT_OF_MESSAGE_SEQUENCE_RETURN_CODE;
   private static final AsciiString EMPTY_REQUEST_PATH;

   private HttpConversionUtil() {
      super();
   }

   public static HttpResponseStatus parseStatus(CharSequence var0) throws Http2Exception {
      try {
         HttpResponseStatus var1 = HttpResponseStatus.parseLine(var0);
         if (var1 == HttpResponseStatus.SWITCHING_PROTOCOLS) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Invalid HTTP/2 status code '%d'", var1.code());
         } else {
            return var1;
         }
      } catch (Http2Exception var3) {
         throw var3;
      } catch (Throwable var4) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, var4, "Unrecognized HTTP status code '%s' encountered in translation to HTTP/1.x", var0);
      }
   }

   public static FullHttpResponse toFullHttpResponse(int var0, Http2Headers var1, ByteBufAllocator var2, boolean var3) throws Http2Exception {
      HttpResponseStatus var4 = parseStatus(var1.status());
      DefaultFullHttpResponse var5 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, var4, var2.buffer(), var3);

      try {
         addHttp2ToHttpHeaders(var0, var1, var5, false);
         return var5;
      } catch (Http2Exception var7) {
         var5.release();
         throw var7;
      } catch (Throwable var8) {
         var5.release();
         throw Http2Exception.streamError(var0, Http2Error.PROTOCOL_ERROR, var8, "HTTP/2 to HTTP/1.x headers conversion error");
      }
   }

   public static FullHttpRequest toFullHttpRequest(int var0, Http2Headers var1, ByteBufAllocator var2, boolean var3) throws Http2Exception {
      CharSequence var4 = (CharSequence)ObjectUtil.checkNotNull(var1.method(), "method header cannot be null in conversion to HTTP/1.x");
      CharSequence var5 = (CharSequence)ObjectUtil.checkNotNull(var1.path(), "path header cannot be null in conversion to HTTP/1.x");
      DefaultFullHttpRequest var6 = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(var4.toString()), var5.toString(), var2.buffer(), var3);

      try {
         addHttp2ToHttpHeaders(var0, var1, var6, false);
         return var6;
      } catch (Http2Exception var8) {
         var6.release();
         throw var8;
      } catch (Throwable var9) {
         var6.release();
         throw Http2Exception.streamError(var0, Http2Error.PROTOCOL_ERROR, var9, "HTTP/2 to HTTP/1.x headers conversion error");
      }
   }

   public static HttpRequest toHttpRequest(int var0, Http2Headers var1, boolean var2) throws Http2Exception {
      CharSequence var3 = (CharSequence)ObjectUtil.checkNotNull(var1.method(), "method header cannot be null in conversion to HTTP/1.x");
      CharSequence var4 = (CharSequence)ObjectUtil.checkNotNull(var1.path(), "path header cannot be null in conversion to HTTP/1.x");
      DefaultHttpRequest var5 = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(var3.toString()), var4.toString(), var2);

      try {
         addHttp2ToHttpHeaders(var0, var1, var5.headers(), var5.protocolVersion(), false, true);
         return var5;
      } catch (Http2Exception var7) {
         throw var7;
      } catch (Throwable var8) {
         throw Http2Exception.streamError(var0, Http2Error.PROTOCOL_ERROR, var8, "HTTP/2 to HTTP/1.x headers conversion error");
      }
   }

   public static HttpResponse toHttpResponse(int var0, Http2Headers var1, boolean var2) throws Http2Exception {
      HttpResponseStatus var3 = parseStatus(var1.status());
      DefaultHttpResponse var4 = new DefaultHttpResponse(HttpVersion.HTTP_1_1, var3, var2);

      try {
         addHttp2ToHttpHeaders(var0, var1, var4.headers(), var4.protocolVersion(), false, true);
         return var4;
      } catch (Http2Exception var6) {
         throw var6;
      } catch (Throwable var7) {
         throw Http2Exception.streamError(var0, Http2Error.PROTOCOL_ERROR, var7, "HTTP/2 to HTTP/1.x headers conversion error");
      }
   }

   public static void addHttp2ToHttpHeaders(int var0, Http2Headers var1, FullHttpMessage var2, boolean var3) throws Http2Exception {
      addHttp2ToHttpHeaders(var0, var1, var3 ? var2.trailingHeaders() : var2.headers(), var2.protocolVersion(), var3, var2 instanceof HttpRequest);
   }

   public static void addHttp2ToHttpHeaders(int var0, Http2Headers var1, HttpHeaders var2, HttpVersion var3, boolean var4, boolean var5) throws Http2Exception {
      HttpConversionUtil.Http2ToHttpHeaderTranslator var6 = new HttpConversionUtil.Http2ToHttpHeaderTranslator(var0, var2, var5);

      try {
         Iterator var7 = var1.iterator();

         while(var7.hasNext()) {
            Entry var8 = (Entry)var7.next();
            var6.translate(var8);
         }
      } catch (Http2Exception var9) {
         throw var9;
      } catch (Throwable var10) {
         throw Http2Exception.streamError(var0, Http2Error.PROTOCOL_ERROR, var10, "HTTP/2 to HTTP/1.x headers conversion error");
      }

      var2.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
      var2.remove((CharSequence)HttpHeaderNames.TRAILER);
      if (!var4) {
         var2.setInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), var0);
         HttpUtil.setKeepAlive(var2, var3, true);
      }

   }

   public static Http2Headers toHttp2Headers(HttpMessage var0, boolean var1) {
      HttpHeaders var2 = var0.headers();
      DefaultHttp2Headers var3 = new DefaultHttp2Headers(var1, var2.size());
      if (var0 instanceof HttpRequest) {
         HttpRequest var4 = (HttpRequest)var0;
         URI var5 = URI.create(var4.uri());
         var3.path(toHttp2Path(var5));
         var3.method(var4.method().asciiName());
         setHttp2Scheme(var2, var5, var3);
         if (!HttpUtil.isOriginForm(var5) && !HttpUtil.isAsteriskForm(var5)) {
            String var6 = var2.getAsString(HttpHeaderNames.HOST);
            setHttp2Authority(var6 != null && !var6.isEmpty() ? var6 : var5.getAuthority(), var3);
         }
      } else if (var0 instanceof HttpResponse) {
         HttpResponse var7 = (HttpResponse)var0;
         var3.status(var7.status().codeAsText());
      }

      toHttp2Headers(var2, var3);
      return var3;
   }

   public static Http2Headers toHttp2Headers(HttpHeaders var0, boolean var1) {
      if (var0.isEmpty()) {
         return EmptyHttp2Headers.INSTANCE;
      } else {
         DefaultHttp2Headers var2 = new DefaultHttp2Headers(var1, var0.size());
         toHttp2Headers(var0, var2);
         return var2;
      }
   }

   private static CharSequenceMap<AsciiString> toLowercaseMap(Iterator<? extends CharSequence> var0, int var1) {
      UnsupportedValueConverter var2 = UnsupportedValueConverter.instance();
      CharSequenceMap var3 = new CharSequenceMap(true, var2, var1);

      while(var0.hasNext()) {
         AsciiString var4 = AsciiString.of((CharSequence)var0.next()).toLowerCase();

         try {
            int var5 = var4.forEachByte(ByteProcessor.FIND_COMMA);
            if (var5 == -1) {
               var3.add(var4.trim(), AsciiString.EMPTY_STRING);
            } else {
               int var6 = 0;

               do {
                  var3.add(var4.subSequence(var6, var5, false).trim(), AsciiString.EMPTY_STRING);
                  var6 = var5 + 1;
               } while(var6 < var4.length() && (var5 = var4.forEachByte(var6, var4.length() - var6, ByteProcessor.FIND_COMMA)) != -1);

               var3.add(var4.subSequence(var6, var4.length(), false).trim(), AsciiString.EMPTY_STRING);
            }
         } catch (Exception var7) {
            throw new IllegalStateException(var7);
         }
      }

      return var3;
   }

   private static void toHttp2HeadersFilterTE(Entry<CharSequence, CharSequence> var0, Http2Headers var1) {
      if (AsciiString.indexOf((CharSequence)var0.getValue(), ',', 0) == -1) {
         if (AsciiString.contentEqualsIgnoreCase(AsciiString.trim((CharSequence)var0.getValue()), HttpHeaderValues.TRAILERS)) {
            var1.add(HttpHeaderNames.TE, HttpHeaderValues.TRAILERS);
         }
      } else {
         List var2 = StringUtil.unescapeCsvFields((CharSequence)var0.getValue());
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            CharSequence var4 = (CharSequence)var3.next();
            if (AsciiString.contentEqualsIgnoreCase(AsciiString.trim(var4), HttpHeaderValues.TRAILERS)) {
               var1.add(HttpHeaderNames.TE, HttpHeaderValues.TRAILERS);
               break;
            }
         }
      }

   }

   public static void toHttp2Headers(HttpHeaders var0, Http2Headers var1) {
      Iterator var2 = var0.iteratorCharSequence();
      CharSequenceMap var3 = toLowercaseMap(var0.valueCharSequenceIterator(HttpHeaderNames.CONNECTION), 8);

      while(true) {
         while(true) {
            Entry var4;
            AsciiString var5;
            do {
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  var4 = (Entry)var2.next();
                  var5 = AsciiString.of((CharSequence)var4.getKey()).toLowerCase();
               } while(HTTP_TO_HTTP2_HEADER_BLACKLIST.contains(var5));
            } while(var3.contains(var5));

            if (var5.contentEqualsIgnoreCase(HttpHeaderNames.TE)) {
               toHttp2HeadersFilterTE(var4, var1);
            } else if (var5.contentEqualsIgnoreCase(HttpHeaderNames.COOKIE)) {
               AsciiString var6 = AsciiString.of((CharSequence)var4.getValue());

               try {
                  int var7 = var6.forEachByte(ByteProcessor.FIND_SEMI_COLON);
                  if (var7 == -1) {
                     var1.add(HttpHeaderNames.COOKIE, var6);
                  } else {
                     int var8 = 0;

                     do {
                        var1.add(HttpHeaderNames.COOKIE, var6.subSequence(var8, var7, false));
                        var8 = var7 + 2;
                     } while(var8 < var6.length() && (var7 = var6.forEachByte(var8, var6.length() - var8, ByteProcessor.FIND_SEMI_COLON)) != -1);

                     if (var8 >= var6.length()) {
                        throw new IllegalArgumentException("cookie value is of unexpected format: " + var6);
                     }

                     var1.add(HttpHeaderNames.COOKIE, var6.subSequence(var8, var6.length(), false));
                  }
               } catch (Exception var9) {
                  throw new IllegalStateException(var9);
               }
            } else {
               var1.add(var5, var4.getValue());
            }
         }
      }
   }

   private static AsciiString toHttp2Path(URI var0) {
      StringBuilder var1 = new StringBuilder(StringUtil.length(var0.getRawPath()) + StringUtil.length(var0.getRawQuery()) + StringUtil.length(var0.getRawFragment()) + 2);
      if (!StringUtil.isNullOrEmpty(var0.getRawPath())) {
         var1.append(var0.getRawPath());
      }

      if (!StringUtil.isNullOrEmpty(var0.getRawQuery())) {
         var1.append('?');
         var1.append(var0.getRawQuery());
      }

      if (!StringUtil.isNullOrEmpty(var0.getRawFragment())) {
         var1.append('#');
         var1.append(var0.getRawFragment());
      }

      String var2 = var1.toString();
      return var2.isEmpty() ? EMPTY_REQUEST_PATH : new AsciiString(var2);
   }

   static void setHttp2Authority(String var0, Http2Headers var1) {
      if (var0 != null) {
         if (var0.isEmpty()) {
            var1.authority(AsciiString.EMPTY_STRING);
         } else {
            int var2 = var0.indexOf(64) + 1;
            int var3 = var0.length() - var2;
            if (var3 == 0) {
               throw new IllegalArgumentException("authority: " + var0);
            }

            var1.authority(new AsciiString(var0, var2, var3));
         }
      }

   }

   private static void setHttp2Scheme(HttpHeaders var0, URI var1, Http2Headers var2) {
      String var3 = var1.getScheme();
      if (var3 != null) {
         var2.scheme(new AsciiString(var3));
      } else {
         String var4 = var0.get((CharSequence)HttpConversionUtil.ExtensionHeaderNames.SCHEME.text());
         if (var4 != null) {
            var2.scheme(AsciiString.of(var4));
         } else {
            if (var1.getPort() == HttpScheme.HTTPS.port()) {
               var2.scheme(HttpScheme.HTTPS.name());
            } else {
               if (var1.getPort() != HttpScheme.HTTP.port()) {
                  throw new IllegalArgumentException(":scheme must be specified. see https://tools.ietf.org/html/rfc7540#section-8.1.2.3");
               }

               var2.scheme(HttpScheme.HTTP.name());
            }

         }
      }
   }

   static {
      HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpHeaderNames.CONNECTION, AsciiString.EMPTY_STRING);
      AsciiString var0 = HttpHeaderNames.KEEP_ALIVE;
      HTTP_TO_HTTP2_HEADER_BLACKLIST.add(var0, AsciiString.EMPTY_STRING);
      AsciiString var1 = HttpHeaderNames.PROXY_CONNECTION;
      HTTP_TO_HTTP2_HEADER_BLACKLIST.add(var1, AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpHeaderNames.TRANSFER_ENCODING, AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpHeaderNames.HOST, AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpHeaderNames.UPGRADE, AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP2_HEADER_BLACKLIST.add(HttpConversionUtil.ExtensionHeaderNames.PATH.text(), AsciiString.EMPTY_STRING);
      OUT_OF_MESSAGE_SEQUENCE_METHOD = HttpMethod.OPTIONS;
      OUT_OF_MESSAGE_SEQUENCE_RETURN_CODE = HttpResponseStatus.OK;
      EMPTY_REQUEST_PATH = AsciiString.cached("/");
   }

   private static final class Http2ToHttpHeaderTranslator {
      private static final CharSequenceMap<AsciiString> REQUEST_HEADER_TRANSLATIONS = new CharSequenceMap();
      private static final CharSequenceMap<AsciiString> RESPONSE_HEADER_TRANSLATIONS = new CharSequenceMap();
      private final int streamId;
      private final HttpHeaders output;
      private final CharSequenceMap<AsciiString> translations;

      Http2ToHttpHeaderTranslator(int var1, HttpHeaders var2, boolean var3) {
         super();
         this.streamId = var1;
         this.output = var2;
         this.translations = var3 ? REQUEST_HEADER_TRANSLATIONS : RESPONSE_HEADER_TRANSLATIONS;
      }

      public void translate(Entry<CharSequence, CharSequence> var1) throws Http2Exception {
         CharSequence var2 = (CharSequence)var1.getKey();
         CharSequence var3 = (CharSequence)var1.getValue();
         AsciiString var4 = (AsciiString)this.translations.get(var2);
         if (var4 != null) {
            this.output.add((CharSequence)var4, (Object)AsciiString.of(var3));
         } else if (!Http2Headers.PseudoHeaderName.isPseudoHeader(var2)) {
            if (var2.length() == 0 || var2.charAt(0) == ':') {
               throw Http2Exception.streamError(this.streamId, Http2Error.PROTOCOL_ERROR, "Invalid HTTP/2 header '%s' encountered in translation to HTTP/1.x", var2);
            }

            if (HttpHeaderNames.COOKIE.equals(var2)) {
               String var5 = this.output.get((CharSequence)HttpHeaderNames.COOKIE);
               this.output.set((CharSequence)HttpHeaderNames.COOKIE, (Object)(var5 != null ? var5 + "; " + var3 : var3));
            } else {
               this.output.add((CharSequence)var2, (Object)var3);
            }
         }

      }

      static {
         RESPONSE_HEADER_TRANSLATIONS.add(Http2Headers.PseudoHeaderName.AUTHORITY.value(), HttpHeaderNames.HOST);
         RESPONSE_HEADER_TRANSLATIONS.add(Http2Headers.PseudoHeaderName.SCHEME.value(), HttpConversionUtil.ExtensionHeaderNames.SCHEME.text());
         REQUEST_HEADER_TRANSLATIONS.add(RESPONSE_HEADER_TRANSLATIONS);
         RESPONSE_HEADER_TRANSLATIONS.add(Http2Headers.PseudoHeaderName.PATH.value(), HttpConversionUtil.ExtensionHeaderNames.PATH.text());
      }
   }

   public static enum ExtensionHeaderNames {
      STREAM_ID("x-http2-stream-id"),
      SCHEME("x-http2-scheme"),
      PATH("x-http2-path"),
      STREAM_PROMISE_ID("x-http2-stream-promise-id"),
      STREAM_DEPENDENCY_ID("x-http2-stream-dependency-id"),
      STREAM_WEIGHT("x-http2-stream-weight");

      private final AsciiString text;

      private ExtensionHeaderNames(String var3) {
         this.text = AsciiString.cached(var3);
      }

      public AsciiString text() {
         return this.text;
      }
   }
}
