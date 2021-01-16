package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public final class DefaultHttp2DataFrame extends AbstractHttp2StreamFrame implements Http2DataFrame {
   private final ByteBuf content;
   private final boolean endStream;
   private final int padding;
   private final int initialFlowControlledBytes;

   public DefaultHttp2DataFrame(ByteBuf var1) {
      this(var1, false);
   }

   public DefaultHttp2DataFrame(boolean var1) {
      this(Unpooled.EMPTY_BUFFER, var1);
   }

   public DefaultHttp2DataFrame(ByteBuf var1, boolean var2) {
      this(var1, var2, 0);
   }

   public DefaultHttp2DataFrame(ByteBuf var1, boolean var2, int var3) {
      super();
      this.content = (ByteBuf)ObjectUtil.checkNotNull(var1, "content");
      this.endStream = var2;
      Http2CodecUtil.verifyPadding(var3);
      this.padding = var3;
      if ((long)this.content().readableBytes() + (long)var3 > 2147483647L) {
         throw new IllegalArgumentException("content + padding must be <= Integer.MAX_VALUE");
      } else {
         this.initialFlowControlledBytes = this.content().readableBytes() + var3;
      }
   }

   public DefaultHttp2DataFrame stream(Http2FrameStream var1) {
      super.stream(var1);
      return this;
   }

   public String name() {
      return "DATA";
   }

   public boolean isEndStream() {
      return this.endStream;
   }

   public int padding() {
      return this.padding;
   }

   public ByteBuf content() {
      if (this.content.refCnt() <= 0) {
         throw new IllegalReferenceCountException(this.content.refCnt());
      } else {
         return this.content;
      }
   }

   public int initialFlowControlledBytes() {
      return this.initialFlowControlledBytes;
   }

   public DefaultHttp2DataFrame copy() {
      return this.replace(this.content().copy());
   }

   public DefaultHttp2DataFrame duplicate() {
      return this.replace(this.content().duplicate());
   }

   public DefaultHttp2DataFrame retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public DefaultHttp2DataFrame replace(ByteBuf var1) {
      return new DefaultHttp2DataFrame(var1, this.endStream, this.padding);
   }

   public int refCnt() {
      return this.content.refCnt();
   }

   public boolean release() {
      return this.content.release();
   }

   public boolean release(int var1) {
      return this.content.release(var1);
   }

   public DefaultHttp2DataFrame retain() {
      this.content.retain();
      return this;
   }

   public DefaultHttp2DataFrame retain(int var1) {
      this.content.retain(var1);
      return this;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(stream=" + this.stream() + ", content=" + this.content + ", endStream=" + this.endStream + ", padding=" + this.padding + ')';
   }

   public DefaultHttp2DataFrame touch() {
      this.content.touch();
      return this;
   }

   public DefaultHttp2DataFrame touch(Object var1) {
      this.content.touch(var1);
      return this;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttp2DataFrame)) {
         return false;
      } else {
         DefaultHttp2DataFrame var2 = (DefaultHttp2DataFrame)var1;
         return super.equals(var2) && this.content.equals(var2.content()) && this.endStream == var2.endStream && this.padding == var2.padding;
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = var1 * 31 + this.content.hashCode();
      var1 = var1 * 31 + (this.endStream ? 0 : 1);
      var1 = var1 * 31 + this.padding;
      return var1;
   }
}
