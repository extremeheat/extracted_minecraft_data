package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCounted;

public record HiddenByteBuf(ByteBuf contents) implements ReferenceCounted {
   public HiddenByteBuf(final ByteBuf var1) {
      super();
      this.contents = ByteBufUtil.ensureAccessible(var1);
   }

   public static Object pack(Object var0) {
      if (var0 instanceof ByteBuf var1) {
         return new HiddenByteBuf(var1);
      } else {
         return var0;
      }
   }

   public static Object unpack(Object var0) {
      if (var0 instanceof HiddenByteBuf var1) {
         return ByteBufUtil.ensureAccessible(var1.contents);
      } else {
         return var0;
      }
   }

   public int refCnt() {
      return this.contents.refCnt();
   }

   public HiddenByteBuf retain() {
      this.contents.retain();
      return this;
   }

   public HiddenByteBuf retain(int var1) {
      this.contents.retain(var1);
      return this;
   }

   public HiddenByteBuf touch() {
      this.contents.touch();
      return this;
   }

   public HiddenByteBuf touch(Object var1) {
      this.contents.touch(var1);
      return this;
   }

   public boolean release() {
      return this.contents.release();
   }

   public boolean release(int var1) {
      return this.contents.release(var1);
   }

   // $FF: synthetic method
   public ReferenceCounted touch(final Object var1) {
      return this.touch(var1);
   }

   // $FF: synthetic method
   public ReferenceCounted touch() {
      return this.touch();
   }

   // $FF: synthetic method
   public ReferenceCounted retain(final int var1) {
      return this.retain(var1);
   }

   // $FF: synthetic method
   public ReferenceCounted retain() {
      return this.retain();
   }
}
