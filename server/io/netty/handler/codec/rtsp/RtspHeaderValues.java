package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;

public final class RtspHeaderValues {
   public static final AsciiString APPEND = AsciiString.cached("append");
   public static final AsciiString AVP = AsciiString.cached("AVP");
   public static final AsciiString BYTES;
   public static final AsciiString CHARSET;
   public static final AsciiString CLIENT_PORT;
   public static final AsciiString CLOCK;
   public static final AsciiString CLOSE;
   public static final AsciiString COMPRESS;
   public static final AsciiString CONTINUE;
   public static final AsciiString DEFLATE;
   public static final AsciiString DESTINATION;
   public static final AsciiString GZIP;
   public static final AsciiString IDENTITY;
   public static final AsciiString INTERLEAVED;
   public static final AsciiString KEEP_ALIVE;
   public static final AsciiString LAYERS;
   public static final AsciiString MAX_AGE;
   public static final AsciiString MAX_STALE;
   public static final AsciiString MIN_FRESH;
   public static final AsciiString MODE;
   public static final AsciiString MULTICAST;
   public static final AsciiString MUST_REVALIDATE;
   public static final AsciiString NONE;
   public static final AsciiString NO_CACHE;
   public static final AsciiString NO_TRANSFORM;
   public static final AsciiString ONLY_IF_CACHED;
   public static final AsciiString PORT;
   public static final AsciiString PRIVATE;
   public static final AsciiString PROXY_REVALIDATE;
   public static final AsciiString PUBLIC;
   public static final AsciiString RTP;
   public static final AsciiString RTPTIME;
   public static final AsciiString SEQ;
   public static final AsciiString SERVER_PORT;
   public static final AsciiString SSRC;
   public static final AsciiString TCP;
   public static final AsciiString TIME;
   public static final AsciiString TIMEOUT;
   public static final AsciiString TTL;
   public static final AsciiString UDP;
   public static final AsciiString UNICAST;
   public static final AsciiString URL;

   private RtspHeaderValues() {
      super();
   }

   static {
      BYTES = HttpHeaderValues.BYTES;
      CHARSET = HttpHeaderValues.CHARSET;
      CLIENT_PORT = AsciiString.cached("client_port");
      CLOCK = AsciiString.cached("clock");
      CLOSE = HttpHeaderValues.CLOSE;
      COMPRESS = HttpHeaderValues.COMPRESS;
      CONTINUE = HttpHeaderValues.CONTINUE;
      DEFLATE = HttpHeaderValues.DEFLATE;
      DESTINATION = AsciiString.cached("destination");
      GZIP = HttpHeaderValues.GZIP;
      IDENTITY = HttpHeaderValues.IDENTITY;
      INTERLEAVED = AsciiString.cached("interleaved");
      KEEP_ALIVE = HttpHeaderValues.KEEP_ALIVE;
      LAYERS = AsciiString.cached("layers");
      MAX_AGE = HttpHeaderValues.MAX_AGE;
      MAX_STALE = HttpHeaderValues.MAX_STALE;
      MIN_FRESH = HttpHeaderValues.MIN_FRESH;
      MODE = AsciiString.cached("mode");
      MULTICAST = AsciiString.cached("multicast");
      MUST_REVALIDATE = HttpHeaderValues.MUST_REVALIDATE;
      NONE = HttpHeaderValues.NONE;
      NO_CACHE = HttpHeaderValues.NO_CACHE;
      NO_TRANSFORM = HttpHeaderValues.NO_TRANSFORM;
      ONLY_IF_CACHED = HttpHeaderValues.ONLY_IF_CACHED;
      PORT = AsciiString.cached("port");
      PRIVATE = HttpHeaderValues.PRIVATE;
      PROXY_REVALIDATE = HttpHeaderValues.PROXY_REVALIDATE;
      PUBLIC = HttpHeaderValues.PUBLIC;
      RTP = AsciiString.cached("RTP");
      RTPTIME = AsciiString.cached("rtptime");
      SEQ = AsciiString.cached("seq");
      SERVER_PORT = AsciiString.cached("server_port");
      SSRC = AsciiString.cached("ssrc");
      TCP = AsciiString.cached("TCP");
      TIME = AsciiString.cached("time");
      TIMEOUT = AsciiString.cached("timeout");
      TTL = AsciiString.cached("ttl");
      UDP = AsciiString.cached("UDP");
      UNICAST = AsciiString.cached("unicast");
      URL = AsciiString.cached("url");
   }
}
