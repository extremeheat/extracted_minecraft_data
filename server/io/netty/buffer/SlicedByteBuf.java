package io.netty.buffer;

/** @deprecated */
@Deprecated
public class SlicedByteBuf extends AbstractUnpooledSlicedByteBuf {
   private int length;

   public SlicedByteBuf(ByteBuf var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   final void initLength(int var1) {
      this.length = var1;
   }

   final int length() {
      return this.length;
   }

   public int capacity() {
      return this.length;
   }
}
