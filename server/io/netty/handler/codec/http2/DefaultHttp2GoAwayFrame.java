package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;

public final class DefaultHttp2GoAwayFrame extends DefaultByteBufHolder implements Http2GoAwayFrame {
   private final long errorCode;
   private final int lastStreamId;
   private int extraStreamIds;

   public DefaultHttp2GoAwayFrame(Http2Error var1) {
      this(var1.code());
   }

   public DefaultHttp2GoAwayFrame(long var1) {
      this(var1, Unpooled.EMPTY_BUFFER);
   }

   public DefaultHttp2GoAwayFrame(Http2Error var1, ByteBuf var2) {
      this(var1.code(), var2);
   }

   public DefaultHttp2GoAwayFrame(long var1, ByteBuf var3) {
      this(-1, var1, var3);
   }

   DefaultHttp2GoAwayFrame(int var1, long var2, ByteBuf var4) {
      super(var4);
      this.errorCode = var2;
      this.lastStreamId = var1;
   }

   public String name() {
      return "GOAWAY";
   }

   public long errorCode() {
      return this.errorCode;
   }

   public int extraStreamIds() {
      return this.extraStreamIds;
   }

   public Http2GoAwayFrame setExtraStreamIds(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("extraStreamIds must be non-negative");
      } else {
         this.extraStreamIds = var1;
         return this;
      }
   }

   public int lastStreamId() {
      return this.lastStreamId;
   }

   public Http2GoAwayFrame copy() {
      return new DefaultHttp2GoAwayFrame(this.lastStreamId, this.errorCode, this.content().copy());
   }

   public Http2GoAwayFrame duplicate() {
      return (Http2GoAwayFrame)super.duplicate();
   }

   public Http2GoAwayFrame retainedDuplicate() {
      return (Http2GoAwayFrame)super.retainedDuplicate();
   }

   public Http2GoAwayFrame replace(ByteBuf var1) {
      return (new DefaultHttp2GoAwayFrame(this.errorCode, var1)).setExtraStreamIds(this.extraStreamIds);
   }

   public Http2GoAwayFrame retain() {
      super.retain();
      return this;
   }

   public Http2GoAwayFrame retain(int var1) {
      super.retain(var1);
      return this;
   }

   public Http2GoAwayFrame touch() {
      super.touch();
      return this;
   }

   public Http2GoAwayFrame touch(Object var1) {
      super.touch(var1);
      return this;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttp2GoAwayFrame)) {
         return false;
      } else {
         DefaultHttp2GoAwayFrame var2 = (DefaultHttp2GoAwayFrame)var1;
         return this.errorCode == var2.errorCode && this.extraStreamIds == var2.extraStreamIds && super.equals(var2);
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = var1 * 31 + (int)(this.errorCode ^ this.errorCode >>> 32);
      var1 = var1 * 31 + this.extraStreamIds;
      return var1;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(errorCode=" + this.errorCode + ", content=" + this.content() + ", extraStreamIds=" + this.extraStreamIds + ", lastStreamId=" + this.lastStreamId + ')';
   }
}
