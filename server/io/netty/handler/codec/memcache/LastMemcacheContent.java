package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;

public interface LastMemcacheContent extends MemcacheContent {
   LastMemcacheContent EMPTY_LAST_CONTENT = new LastMemcacheContent() {
      public LastMemcacheContent copy() {
         return EMPTY_LAST_CONTENT;
      }

      public LastMemcacheContent duplicate() {
         return this;
      }

      public LastMemcacheContent retainedDuplicate() {
         return this;
      }

      public LastMemcacheContent replace(ByteBuf var1) {
         return new DefaultLastMemcacheContent(var1);
      }

      public LastMemcacheContent retain(int var1) {
         return this;
      }

      public LastMemcacheContent retain() {
         return this;
      }

      public LastMemcacheContent touch() {
         return this;
      }

      public LastMemcacheContent touch(Object var1) {
         return this;
      }

      public ByteBuf content() {
         return Unpooled.EMPTY_BUFFER;
      }

      public DecoderResult decoderResult() {
         return DecoderResult.SUCCESS;
      }

      public void setDecoderResult(DecoderResult var1) {
         throw new UnsupportedOperationException("read only");
      }

      public int refCnt() {
         return 1;
      }

      public boolean release() {
         return false;
      }

      public boolean release(int var1) {
         return false;
      }
   };

   LastMemcacheContent copy();

   LastMemcacheContent duplicate();

   LastMemcacheContent retainedDuplicate();

   LastMemcacheContent replace(ByteBuf var1);

   LastMemcacheContent retain(int var1);

   LastMemcacheContent retain();

   LastMemcacheContent touch();

   LastMemcacheContent touch(Object var1);
}
