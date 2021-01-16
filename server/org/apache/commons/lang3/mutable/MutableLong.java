package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableLong extends Number implements Comparable<MutableLong>, Mutable<Number> {
   private static final long serialVersionUID = 62986528375L;
   private long value;

   public MutableLong() {
      super();
   }

   public MutableLong(long var1) {
      super();
      this.value = var1;
   }

   public MutableLong(Number var1) {
      super();
      this.value = var1.longValue();
   }

   public MutableLong(String var1) throws NumberFormatException {
      super();
      this.value = Long.parseLong(var1);
   }

   public Long getValue() {
      return this.value;
   }

   public void setValue(long var1) {
      this.value = var1;
   }

   public void setValue(Number var1) {
      this.value = var1.longValue();
   }

   public void increment() {
      ++this.value;
   }

   public long getAndIncrement() {
      long var1 = (long)(this.value++);
      return var1;
   }

   public long incrementAndGet() {
      ++this.value;
      return this.value;
   }

   public void decrement() {
      --this.value;
   }

   public long getAndDecrement() {
      long var1 = (long)(this.value--);
      return var1;
   }

   public long decrementAndGet() {
      --this.value;
      return this.value;
   }

   public void add(long var1) {
      this.value += var1;
   }

   public void add(Number var1) {
      this.value += var1.longValue();
   }

   public void subtract(long var1) {
      this.value -= var1;
   }

   public void subtract(Number var1) {
      this.value -= var1.longValue();
   }

   public long addAndGet(long var1) {
      this.value += var1;
      return this.value;
   }

   public long addAndGet(Number var1) {
      this.value += var1.longValue();
      return this.value;
   }

   public long getAndAdd(long var1) {
      long var3 = this.value;
      this.value += var1;
      return var3;
   }

   public long getAndAdd(Number var1) {
      long var2 = this.value;
      this.value += var1.longValue();
      return var2;
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public Long toLong() {
      return this.longValue();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof MutableLong) {
         return this.value == ((MutableLong)var1).longValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (int)(this.value ^ this.value >>> 32);
   }

   public int compareTo(MutableLong var1) {
      return NumberUtils.compare(this.value, var1.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
