package io.netty.buffer;

class UnpooledDuplicatedByteBuf extends DuplicatedByteBuf {
   UnpooledDuplicatedByteBuf(AbstractByteBuf var1) {
      super(var1);
   }

   public AbstractByteBuf unwrap() {
      return (AbstractByteBuf)super.unwrap();
   }

   protected byte _getByte(int var1) {
      return this.unwrap()._getByte(var1);
   }

   protected short _getShort(int var1) {
      return this.unwrap()._getShort(var1);
   }

   protected short _getShortLE(int var1) {
      return this.unwrap()._getShortLE(var1);
   }

   protected int _getUnsignedMedium(int var1) {
      return this.unwrap()._getUnsignedMedium(var1);
   }

   protected int _getUnsignedMediumLE(int var1) {
      return this.unwrap()._getUnsignedMediumLE(var1);
   }

   protected int _getInt(int var1) {
      return this.unwrap()._getInt(var1);
   }

   protected int _getIntLE(int var1) {
      return this.unwrap()._getIntLE(var1);
   }

   protected long _getLong(int var1) {
      return this.unwrap()._getLong(var1);
   }

   protected long _getLongLE(int var1) {
      return this.unwrap()._getLongLE(var1);
   }

   protected void _setByte(int var1, int var2) {
      this.unwrap()._setByte(var1, var2);
   }

   protected void _setShort(int var1, int var2) {
      this.unwrap()._setShort(var1, var2);
   }

   protected void _setShortLE(int var1, int var2) {
      this.unwrap()._setShortLE(var1, var2);
   }

   protected void _setMedium(int var1, int var2) {
      this.unwrap()._setMedium(var1, var2);
   }

   protected void _setMediumLE(int var1, int var2) {
      this.unwrap()._setMediumLE(var1, var2);
   }

   protected void _setInt(int var1, int var2) {
      this.unwrap()._setInt(var1, var2);
   }

   protected void _setIntLE(int var1, int var2) {
      this.unwrap()._setIntLE(var1, var2);
   }

   protected void _setLong(int var1, long var2) {
      this.unwrap()._setLong(var1, var2);
   }

   protected void _setLongLE(int var1, long var2) {
      this.unwrap()._setLongLE(var1, var2);
   }
}
