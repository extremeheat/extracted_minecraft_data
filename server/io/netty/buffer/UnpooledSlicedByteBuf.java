package io.netty.buffer;

class UnpooledSlicedByteBuf extends AbstractUnpooledSlicedByteBuf {
   UnpooledSlicedByteBuf(AbstractByteBuf var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public int capacity() {
      return this.maxCapacity();
   }

   public AbstractByteBuf unwrap() {
      return (AbstractByteBuf)super.unwrap();
   }

   protected byte _getByte(int var1) {
      return this.unwrap()._getByte(this.idx(var1));
   }

   protected short _getShort(int var1) {
      return this.unwrap()._getShort(this.idx(var1));
   }

   protected short _getShortLE(int var1) {
      return this.unwrap()._getShortLE(this.idx(var1));
   }

   protected int _getUnsignedMedium(int var1) {
      return this.unwrap()._getUnsignedMedium(this.idx(var1));
   }

   protected int _getUnsignedMediumLE(int var1) {
      return this.unwrap()._getUnsignedMediumLE(this.idx(var1));
   }

   protected int _getInt(int var1) {
      return this.unwrap()._getInt(this.idx(var1));
   }

   protected int _getIntLE(int var1) {
      return this.unwrap()._getIntLE(this.idx(var1));
   }

   protected long _getLong(int var1) {
      return this.unwrap()._getLong(this.idx(var1));
   }

   protected long _getLongLE(int var1) {
      return this.unwrap()._getLongLE(this.idx(var1));
   }

   protected void _setByte(int var1, int var2) {
      this.unwrap()._setByte(this.idx(var1), var2);
   }

   protected void _setShort(int var1, int var2) {
      this.unwrap()._setShort(this.idx(var1), var2);
   }

   protected void _setShortLE(int var1, int var2) {
      this.unwrap()._setShortLE(this.idx(var1), var2);
   }

   protected void _setMedium(int var1, int var2) {
      this.unwrap()._setMedium(this.idx(var1), var2);
   }

   protected void _setMediumLE(int var1, int var2) {
      this.unwrap()._setMediumLE(this.idx(var1), var2);
   }

   protected void _setInt(int var1, int var2) {
      this.unwrap()._setInt(this.idx(var1), var2);
   }

   protected void _setIntLE(int var1, int var2) {
      this.unwrap()._setIntLE(this.idx(var1), var2);
   }

   protected void _setLong(int var1, long var2) {
      this.unwrap()._setLong(this.idx(var1), var2);
   }

   protected void _setLongLE(int var1, long var2) {
      this.unwrap()._setLongLE(this.idx(var1), var2);
   }
}
