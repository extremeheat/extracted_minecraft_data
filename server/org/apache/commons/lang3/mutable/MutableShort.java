package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableShort extends Number implements Comparable<MutableShort>, Mutable<Number> {
   private static final long serialVersionUID = -2135791679L;
   private short value;

   public MutableShort() {
      super();
   }

   public MutableShort(short var1) {
      super();
      this.value = var1;
   }

   public MutableShort(Number var1) {
      super();
      this.value = var1.shortValue();
   }

   public MutableShort(String var1) throws NumberFormatException {
      super();
      this.value = Short.parseShort(var1);
   }

   public Short getValue() {
      return this.value;
   }

   public void setValue(short var1) {
      this.value = var1;
   }

   public void setValue(Number var1) {
      this.value = var1.shortValue();
   }

   public void increment() {
      ++this.value;
   }

   public short getAndIncrement() {
      short var1 = this.value++;
      return var1;
   }

   public short incrementAndGet() {
      ++this.value;
      return this.value;
   }

   public void decrement() {
      --this.value;
   }

   public short getAndDecrement() {
      short var1 = this.value--;
      return var1;
   }

   public short decrementAndGet() {
      --this.value;
      return this.value;
   }

   public void add(short var1) {
      this.value += var1;
   }

   public void add(Number var1) {
      this.value += var1.shortValue();
   }

   public void subtract(short var1) {
      this.value -= var1;
   }

   public void subtract(Number var1) {
      this.value -= var1.shortValue();
   }

   public short addAndGet(short var1) {
      this.value += var1;
      return this.value;
   }

   public short addAndGet(Number var1) {
      this.value += var1.shortValue();
      return this.value;
   }

   public short getAndAdd(short var1) {
      short var2 = this.value;
      this.value += var1;
      return var2;
   }

   public short getAndAdd(Number var1) {
      short var2 = this.value;
      this.value += var1.shortValue();
      return var2;
   }

   public short shortValue() {
      return this.value;
   }

   public int intValue() {
      return this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public Short toShort() {
      return this.shortValue();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof MutableShort) {
         return this.value == ((MutableShort)var1).shortValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableShort var1) {
      return NumberUtils.compare(this.value, var1.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
