package net.minecraft.nbt;

import java.util.Optional;

public sealed interface NumericTag extends PrimitiveTag permits ByteTag, ShortTag, IntTag, LongTag, FloatTag, DoubleTag {
   byte byteValue();

   short shortValue();

   int intValue();

   long longValue();

   float floatValue();

   double doubleValue();

   Number box();

   default Optional<Number> asNumber() {
      return Optional.of(this.box());
   }

   default Optional<Byte> asByte() {
      return Optional.of(this.byteValue());
   }

   default Optional<Short> asShort() {
      return Optional.of(this.shortValue());
   }

   default Optional<Integer> asInt() {
      return Optional.of(this.intValue());
   }

   default Optional<Long> asLong() {
      return Optional.of(this.longValue());
   }

   default Optional<Float> asFloat() {
      return Optional.of(this.floatValue());
   }

   default Optional<Double> asDouble() {
      return Optional.of(this.doubleValue());
   }

   default Optional<Boolean> asBoolean() {
      return Optional.of(this.byteValue() != 0);
   }
}
