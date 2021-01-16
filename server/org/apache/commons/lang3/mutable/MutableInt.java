package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableInt extends Number implements Comparable<MutableInt>, Mutable<Number> {
   private static final long serialVersionUID = 512176391864L;
   private int value;

   public MutableInt() {
      super();
   }

   public MutableInt(int var1) {
      super();
      this.value = var1;
   }

   public MutableInt(Number var1) {
      super();
      this.value = var1.intValue();
   }

   public MutableInt(String var1) throws NumberFormatException {
      super();
      this.value = Integer.parseInt(var1);
   }

   public Integer getValue() {
      return this.value;
   }

   public void setValue(int var1) {
      this.value = var1;
   }

   public void setValue(Number var1) {
      this.value = var1.intValue();
   }

   public void increment() {
      ++this.value;
   }

   public int getAndIncrement() {
      int var1 = this.value++;
      return var1;
   }

   public int incrementAndGet() {
      ++this.value;
      return this.value;
   }

   public void decrement() {
      --this.value;
   }

   public int getAndDecrement() {
      int var1 = this.value--;
      return var1;
   }

   public int decrementAndGet() {
      --this.value;
      return this.value;
   }

   public void add(int var1) {
      this.value += var1;
   }

   public void add(Number var1) {
      this.value += var1.intValue();
   }

   public void subtract(int var1) {
      this.value -= var1;
   }

   public void subtract(Number var1) {
      this.value -= var1.intValue();
   }

   public int addAndGet(int var1) {
      this.value += var1;
      return this.value;
   }

   public int addAndGet(Number var1) {
      this.value += var1.intValue();
      return this.value;
   }

   public int getAndAdd(int var1) {
      int var2 = this.value;
      this.value += var1;
      return var2;
   }

   public int getAndAdd(Number var1) {
      int var2 = this.value;
      this.value += var1.intValue();
      return var2;
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

   public Integer toInteger() {
      return this.intValue();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof MutableInt) {
         return this.value == ((MutableInt)var1).intValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableInt var1) {
      return NumberUtils.compare(this.value, var1.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
