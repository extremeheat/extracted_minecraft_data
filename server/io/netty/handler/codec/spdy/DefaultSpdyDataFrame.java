package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyDataFrame extends DefaultSpdyStreamFrame implements SpdyDataFrame {
   private final ByteBuf data;

   public DefaultSpdyDataFrame(int var1) {
      this(var1, Unpooled.buffer(0));
   }

   public DefaultSpdyDataFrame(int var1, ByteBuf var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("data");
      } else {
         this.data = validate(var2);
      }
   }

   private static ByteBuf validate(ByteBuf var0) {
      if (var0.readableBytes() > 16777215) {
         throw new IllegalArgumentException("data payload cannot exceed 16777215 bytes");
      } else {
         return var0;
      }
   }

   public SpdyDataFrame setStreamId(int var1) {
      super.setStreamId(var1);
      return this;
   }

   public SpdyDataFrame setLast(boolean var1) {
      super.setLast(var1);
      return this;
   }

   public ByteBuf content() {
      if (this.data.refCnt() <= 0) {
         throw new IllegalReferenceCountException(this.data.refCnt());
      } else {
         return this.data;
      }
   }

   public SpdyDataFrame copy() {
      return this.replace(this.content().copy());
   }

   public SpdyDataFrame duplicate() {
      return this.replace(this.content().duplicate());
   }

   public SpdyDataFrame retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public SpdyDataFrame replace(ByteBuf var1) {
      DefaultSpdyDataFrame var2 = new DefaultSpdyDataFrame(this.streamId(), var1);
      var2.setLast(this.isLast());
      return var2;
   }

   public int refCnt() {
      return this.data.refCnt();
   }

   public SpdyDataFrame retain() {
      this.data.retain();
      return this;
   }

   public SpdyDataFrame retain(int var1) {
      this.data.retain(var1);
      return this;
   }

   public SpdyDataFrame touch() {
      this.data.touch();
      return this;
   }

   public SpdyDataFrame touch(Object var1) {
      this.data.touch(var1);
      return this;
   }

   public boolean release() {
      return this.data.release();
   }

   public boolean release(int var1) {
      return this.data.release(var1);
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder()).append(StringUtil.simpleClassName((Object)this)).append("(last: ").append(this.isLast()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(this.streamId()).append(StringUtil.NEWLINE).append("--> Size = ");
      if (this.refCnt() == 0) {
         var1.append("(freed)");
      } else {
         var1.append(this.content().readableBytes());
      }

      return var1.toString();
   }
}
