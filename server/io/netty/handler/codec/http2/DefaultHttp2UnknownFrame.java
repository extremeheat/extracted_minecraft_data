package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;

public final class DefaultHttp2UnknownFrame extends DefaultByteBufHolder implements Http2UnknownFrame {
   private final byte frameType;
   private final Http2Flags flags;
   private Http2FrameStream stream;

   public DefaultHttp2UnknownFrame(byte var1, Http2Flags var2) {
      this(var1, var2, Unpooled.EMPTY_BUFFER);
   }

   public DefaultHttp2UnknownFrame(byte var1, Http2Flags var2, ByteBuf var3) {
      super(var3);
      this.frameType = var1;
      this.flags = var2;
   }

   public Http2FrameStream stream() {
      return this.stream;
   }

   public DefaultHttp2UnknownFrame stream(Http2FrameStream var1) {
      this.stream = var1;
      return this;
   }

   public byte frameType() {
      return this.frameType;
   }

   public Http2Flags flags() {
      return this.flags;
   }

   public String name() {
      return "UNKNOWN";
   }

   public DefaultHttp2UnknownFrame copy() {
      return this.replace(this.content().copy());
   }

   public DefaultHttp2UnknownFrame duplicate() {
      return this.replace(this.content().duplicate());
   }

   public DefaultHttp2UnknownFrame retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public DefaultHttp2UnknownFrame replace(ByteBuf var1) {
      return (new DefaultHttp2UnknownFrame(this.frameType, this.flags, var1)).stream(this.stream());
   }

   public DefaultHttp2UnknownFrame retain() {
      super.retain();
      return this;
   }

   public DefaultHttp2UnknownFrame retain(int var1) {
      super.retain(var1);
      return this;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(frameType=" + this.frameType() + ", stream=" + this.stream() + ", flags=" + this.flags() + ", content=" + this.contentToString() + ')';
   }

   public DefaultHttp2UnknownFrame touch() {
      super.touch();
      return this;
   }

   public DefaultHttp2UnknownFrame touch(Object var1) {
      super.touch(var1);
      return this;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttp2UnknownFrame)) {
         return false;
      } else {
         DefaultHttp2UnknownFrame var2 = (DefaultHttp2UnknownFrame)var1;
         return super.equals(var2) && this.flags().equals(var2.flags()) && this.frameType() == var2.frameType() && this.stream() == null && var2.stream() == null || this.stream().equals(var2.stream());
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = var1 * 31 + this.frameType();
      var1 = var1 * 31 + this.flags().hashCode();
      if (this.stream() != null) {
         var1 = var1 * 31 + this.stream().hashCode();
      }

      return var1;
   }
}
