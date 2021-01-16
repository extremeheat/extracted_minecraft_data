package io.netty.channel.udt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

/** @deprecated */
@Deprecated
public final class UdtMessage extends DefaultByteBufHolder {
   public UdtMessage(ByteBuf var1) {
      super(var1);
   }

   public UdtMessage copy() {
      return (UdtMessage)super.copy();
   }

   public UdtMessage duplicate() {
      return (UdtMessage)super.duplicate();
   }

   public UdtMessage retainedDuplicate() {
      return (UdtMessage)super.retainedDuplicate();
   }

   public UdtMessage replace(ByteBuf var1) {
      return new UdtMessage(var1);
   }

   public UdtMessage retain() {
      super.retain();
      return this;
   }

   public UdtMessage retain(int var1) {
      super.retain(var1);
      return this;
   }

   public UdtMessage touch() {
      super.touch();
      return this;
   }

   public UdtMessage touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
