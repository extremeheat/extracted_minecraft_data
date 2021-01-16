package io.netty.handler.codec;

public final class UnsupportedValueConverter<V> implements ValueConverter<V> {
   private static final UnsupportedValueConverter INSTANCE = new UnsupportedValueConverter();

   private UnsupportedValueConverter() {
      super();
   }

   public static <V> UnsupportedValueConverter<V> instance() {
      return INSTANCE;
   }

   public V convertObject(Object var1) {
      throw new UnsupportedOperationException();
   }

   public V convertBoolean(boolean var1) {
      throw new UnsupportedOperationException();
   }

   public boolean convertToBoolean(V var1) {
      throw new UnsupportedOperationException();
   }

   public V convertByte(byte var1) {
      throw new UnsupportedOperationException();
   }

   public byte convertToByte(V var1) {
      throw new UnsupportedOperationException();
   }

   public V convertChar(char var1) {
      throw new UnsupportedOperationException();
   }

   public char convertToChar(V var1) {
      throw new UnsupportedOperationException();
   }

   public V convertShort(short var1) {
      throw new UnsupportedOperationException();
   }

   public short convertToShort(V var1) {
      throw new UnsupportedOperationException();
   }

   public V convertInt(int var1) {
      throw new UnsupportedOperationException();
   }

   public int convertToInt(V var1) {
      throw new UnsupportedOperationException();
   }

   public V convertLong(long var1) {
      throw new UnsupportedOperationException();
   }

   public long convertToLong(V var1) {
      throw new UnsupportedOperationException();
   }

   public V convertTimeMillis(long var1) {
      throw new UnsupportedOperationException();
   }

   public long convertToTimeMillis(V var1) {
      throw new UnsupportedOperationException();
   }

   public V convertFloat(float var1) {
      throw new UnsupportedOperationException();
   }

   public float convertToFloat(V var1) {
      throw new UnsupportedOperationException();
   }

   public V convertDouble(double var1) {
      throw new UnsupportedOperationException();
   }

   public double convertToDouble(V var1) {
      throw new UnsupportedOperationException();
   }
}
