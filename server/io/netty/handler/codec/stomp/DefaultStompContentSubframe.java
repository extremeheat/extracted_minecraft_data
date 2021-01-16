package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.handler.codec.DecoderResult;

public class DefaultStompContentSubframe extends DefaultByteBufHolder implements StompContentSubframe {
   private DecoderResult decoderResult;

   public DefaultStompContentSubframe(ByteBuf var1) {
      super(var1);
      this.decoderResult = DecoderResult.SUCCESS;
   }

   public StompContentSubframe copy() {
      return (StompContentSubframe)super.copy();
   }

   public StompContentSubframe duplicate() {
      return (StompContentSubframe)super.duplicate();
   }

   public StompContentSubframe retainedDuplicate() {
      return (StompContentSubframe)super.retainedDuplicate();
   }

   public StompContentSubframe replace(ByteBuf var1) {
      return new DefaultStompContentSubframe(var1);
   }

   public StompContentSubframe retain() {
      super.retain();
      return this;
   }

   public StompContentSubframe retain(int var1) {
      super.retain(var1);
      return this;
   }

   public StompContentSubframe touch() {
      super.touch();
      return this;
   }

   public StompContentSubframe touch(Object var1) {
      super.touch(var1);
      return this;
   }

   public DecoderResult decoderResult() {
      return this.decoderResult;
   }

   public void setDecoderResult(DecoderResult var1) {
      this.decoderResult = var1;
   }

   public String toString() {
      return "DefaultStompContent{decoderResult=" + this.decoderResult + '}';
   }
}
