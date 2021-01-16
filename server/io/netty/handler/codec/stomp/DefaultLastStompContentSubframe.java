package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;

public class DefaultLastStompContentSubframe extends DefaultStompContentSubframe implements LastStompContentSubframe {
   public DefaultLastStompContentSubframe(ByteBuf var1) {
      super(var1);
   }

   public LastStompContentSubframe copy() {
      return (LastStompContentSubframe)super.copy();
   }

   public LastStompContentSubframe duplicate() {
      return (LastStompContentSubframe)super.duplicate();
   }

   public LastStompContentSubframe retainedDuplicate() {
      return (LastStompContentSubframe)super.retainedDuplicate();
   }

   public LastStompContentSubframe replace(ByteBuf var1) {
      return new DefaultLastStompContentSubframe(var1);
   }

   public DefaultLastStompContentSubframe retain() {
      super.retain();
      return this;
   }

   public LastStompContentSubframe retain(int var1) {
      super.retain(var1);
      return this;
   }

   public LastStompContentSubframe touch() {
      super.touch();
      return this;
   }

   public LastStompContentSubframe touch(Object var1) {
      super.touch(var1);
      return this;
   }

   public String toString() {
      return "DefaultLastStompContent{decoderResult=" + this.decoderResult() + '}';
   }
}
