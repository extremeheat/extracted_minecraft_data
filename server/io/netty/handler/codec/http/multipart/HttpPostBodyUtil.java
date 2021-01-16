package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;

final class HttpPostBodyUtil {
   public static final int chunkSize = 8096;
   public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
   public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/plain";

   private HttpPostBodyUtil() {
      super();
   }

   static int findNonWhitespace(String var0, int var1) {
      int var2;
      for(var2 = var1; var2 < var0.length() && Character.isWhitespace(var0.charAt(var2)); ++var2) {
      }

      return var2;
   }

   static int findEndOfString(String var0) {
      int var1;
      for(var1 = var0.length(); var1 > 0 && Character.isWhitespace(var0.charAt(var1 - 1)); --var1) {
      }

      return var1;
   }

   static class SeekAheadOptimize {
      byte[] bytes;
      int readerIndex;
      int pos;
      int origPos;
      int limit;
      ByteBuf buffer;

      SeekAheadOptimize(ByteBuf var1) {
         super();
         if (!var1.hasArray()) {
            throw new IllegalArgumentException("buffer hasn't backing byte array");
         } else {
            this.buffer = var1;
            this.bytes = var1.array();
            this.readerIndex = var1.readerIndex();
            this.origPos = this.pos = var1.arrayOffset() + this.readerIndex;
            this.limit = var1.arrayOffset() + var1.writerIndex();
         }
      }

      void setReadPosition(int var1) {
         this.pos -= var1;
         this.readerIndex = this.getReadPosition(this.pos);
         this.buffer.readerIndex(this.readerIndex);
      }

      int getReadPosition(int var1) {
         return var1 - this.origPos + this.readerIndex;
      }
   }

   public static enum TransferEncodingMechanism {
      BIT7("7bit"),
      BIT8("8bit"),
      BINARY("binary");

      private final String value;

      private TransferEncodingMechanism(String var3) {
         this.value = var3;
      }

      public String value() {
         return this.value;
      }

      public String toString() {
         return this.value;
      }
   }
}
