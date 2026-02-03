package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCounted;

public record HiddenByteBuf(ByteBuf contents) implements ReferenceCounted {
   public HiddenByteBuf(final ByteBuf contents) {
      super();
      this.contents = ByteBufUtil.ensureAccessible(contents);
   }

   public static Object pack(final Object msg) {
      if (msg instanceof ByteBuf buf) {
         return new HiddenByteBuf(buf);
      } else {
         return msg;
      }
   }

   public static Object unpack(final Object msg) {
      if (msg instanceof HiddenByteBuf buf) {
         return ByteBufUtil.ensureAccessible(buf.contents);
      } else {
         return msg;
      }
   }

   public int refCnt() {
      return this.contents.refCnt();
   }

   public HiddenByteBuf retain() {
      this.contents.retain();
      return this;
   }

   public HiddenByteBuf retain(final int increment) {
      this.contents.retain(increment);
      return this;
   }

   public HiddenByteBuf touch() {
      this.contents.touch();
      return this;
   }

   public HiddenByteBuf touch(final Object hint) {
      this.contents.touch(hint);
      return this;
   }

   public boolean release() {
      return this.contents.release();
   }

   public boolean release(final int decrement) {
      return this.contents.release(decrement);
   }
}
