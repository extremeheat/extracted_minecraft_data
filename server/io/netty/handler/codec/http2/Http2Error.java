package io.netty.handler.codec.http2;

public enum Http2Error {
   NO_ERROR(0L),
   PROTOCOL_ERROR(1L),
   INTERNAL_ERROR(2L),
   FLOW_CONTROL_ERROR(3L),
   SETTINGS_TIMEOUT(4L),
   STREAM_CLOSED(5L),
   FRAME_SIZE_ERROR(6L),
   REFUSED_STREAM(7L),
   CANCEL(8L),
   COMPRESSION_ERROR(9L),
   CONNECT_ERROR(10L),
   ENHANCE_YOUR_CALM(11L),
   INADEQUATE_SECURITY(12L),
   HTTP_1_1_REQUIRED(13L);

   private final long code;
   private static final Http2Error[] INT_TO_ENUM_MAP;

   private Http2Error(long var3) {
      this.code = var3;
   }

   public long code() {
      return this.code;
   }

   public static Http2Error valueOf(long var0) {
      return var0 < (long)INT_TO_ENUM_MAP.length && var0 >= 0L ? INT_TO_ENUM_MAP[(int)var0] : null;
   }

   static {
      Http2Error[] var0 = values();
      Http2Error[] var1 = new Http2Error[var0.length];
      Http2Error[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Http2Error var5 = var2[var4];
         var1[(int)var5.code()] = var5;
      }

      INT_TO_ENUM_MAP = var1;
   }
}
