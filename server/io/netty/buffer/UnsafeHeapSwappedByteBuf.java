package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;

final class UnsafeHeapSwappedByteBuf extends AbstractUnsafeSwappedByteBuf {
   UnsafeHeapSwappedByteBuf(AbstractByteBuf var1) {
      super(var1);
   }

   private static int idx(ByteBuf var0, int var1) {
      return var0.arrayOffset() + var1;
   }

   protected long _getLong(AbstractByteBuf var1, int var2) {
      return PlatformDependent.getLong(var1.array(), idx(var1, var2));
   }

   protected int _getInt(AbstractByteBuf var1, int var2) {
      return PlatformDependent.getInt(var1.array(), idx(var1, var2));
   }

   protected short _getShort(AbstractByteBuf var1, int var2) {
      return PlatformDependent.getShort(var1.array(), idx(var1, var2));
   }

   protected void _setShort(AbstractByteBuf var1, int var2, short var3) {
      PlatformDependent.putShort(var1.array(), idx(var1, var2), var3);
   }

   protected void _setInt(AbstractByteBuf var1, int var2, int var3) {
      PlatformDependent.putInt(var1.array(), idx(var1, var2), var3);
   }

   protected void _setLong(AbstractByteBuf var1, int var2, long var3) {
      PlatformDependent.putLong(var1.array(), idx(var1, var2), var3);
   }
}
