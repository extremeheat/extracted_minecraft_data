package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.regex.Pattern;

public class RtspDecoder extends HttpObjectDecoder {
   private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");
   private boolean isDecodingRequest;
   private static final Pattern versionPattern = Pattern.compile("RTSP/\\d\\.\\d");
   public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
   public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
   public static final int DEFAULT_MAX_CONTENT_LENGTH = 8192;

   public RtspDecoder() {
      this(4096, 8192, 8192);
   }

   public RtspDecoder(int var1, int var2, int var3) {
      super(var1, var2, var3 * 2, false);
   }

   public RtspDecoder(int var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3 * 2, false, var4);
   }

   protected HttpMessage createMessage(String[] var1) throws Exception {
      if (versionPattern.matcher(var1[0]).matches()) {
         this.isDecodingRequest = false;
         return new DefaultHttpResponse(RtspVersions.valueOf(var1[0]), new HttpResponseStatus(Integer.parseInt(var1[1]), var1[2]), this.validateHeaders);
      } else {
         this.isDecodingRequest = true;
         return new DefaultHttpRequest(RtspVersions.valueOf(var1[2]), RtspMethods.valueOf(var1[0]), var1[1], this.validateHeaders);
      }
   }

   protected boolean isContentAlwaysEmpty(HttpMessage var1) {
      return super.isContentAlwaysEmpty(var1) || !var1.headers().contains((CharSequence)RtspHeaderNames.CONTENT_LENGTH);
   }

   protected HttpMessage createInvalidMessage() {
      return (HttpMessage)(this.isDecodingRequest ? new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS, "/bad-request", this.validateHeaders) : new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, UNKNOWN_STATUS, this.validateHeaders));
   }

   protected boolean isDecodingRequest() {
      return this.isDecodingRequest;
   }
}
