package io.netty.handler.codec.http;

public class HttpResponseDecoder extends HttpObjectDecoder {
   private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");

   public HttpResponseDecoder() {
      super();
   }

   public HttpResponseDecoder(int var1, int var2, int var3) {
      super(var1, var2, var3, true);
   }

   public HttpResponseDecoder(int var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3, true, var4);
   }

   public HttpResponseDecoder(int var1, int var2, int var3, boolean var4, int var5) {
      super(var1, var2, var3, true, var4, var5);
   }

   protected HttpMessage createMessage(String[] var1) {
      return new DefaultHttpResponse(HttpVersion.valueOf(var1[0]), HttpResponseStatus.valueOf(Integer.parseInt(var1[1]), var1[2]), this.validateHeaders);
   }

   protected HttpMessage createInvalidMessage() {
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, UNKNOWN_STATUS, this.validateHeaders);
   }

   protected boolean isDecodingRequest() {
      return false;
   }
}
