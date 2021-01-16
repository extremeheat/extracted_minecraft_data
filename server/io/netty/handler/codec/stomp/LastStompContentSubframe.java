package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;

public interface LastStompContentSubframe extends StompContentSubframe {
   LastStompContentSubframe EMPTY_LAST_CONTENT = new LastStompContentSubframe() {
      public ByteBuf content() {
         return Unpooled.EMPTY_BUFFER;
      }

      public LastStompContentSubframe copy() {
         return EMPTY_LAST_CONTENT;
      }

      public LastStompContentSubframe duplicate() {
         return this;
      }

      public LastStompContentSubframe retainedDuplicate() {
         return this;
      }

      public LastStompContentSubframe replace(ByteBuf var1) {
         return new DefaultLastStompContentSubframe(var1);
      }

      public LastStompContentSubframe retain() {
         return this;
      }

      public LastStompContentSubframe retain(int var1) {
         return this;
      }

      public LastStompContentSubframe touch() {
         return this;
      }

      public LastStompContentSubframe touch(Object var1) {
         return this;
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

      public DecoderResult decoderResult() {
         return DecoderResult.SUCCESS;
      }

      public void setDecoderResult(DecoderResult var1) {
         throw new UnsupportedOperationException("read only");
      }
   };

   LastStompContentSubframe copy();

   LastStompContentSubframe duplicate();

   LastStompContentSubframe retainedDuplicate();

   LastStompContentSubframe replace(ByteBuf var1);

   LastStompContentSubframe retain();

   LastStompContentSubframe retain(int var1);

   LastStompContentSubframe touch();

   LastStompContentSubframe touch(Object var1);
}
