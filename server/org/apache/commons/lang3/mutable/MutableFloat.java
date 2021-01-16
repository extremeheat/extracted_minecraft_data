package org.apache.commons.lang3.mutable;

public class MutableFloat extends Number implements Comparable<MutableFloat>, Mutable<Number> {
   private static final long serialVersionUID = 5787169186L;
   private float value;

   public MutableFloat() {
      super();
   }

   public MutableFloat(float var1) {
      super();
      this.value = var1;
   }

   public MutableFloat(Number var1) {
      super();
      this.value = var1.floatValue();
   }

   public MutableFloat(String var1) throws NumberFormatException {
      super();
      this.value = Float.parseFloat(var1);
   }

   public Float getValue() {
      return this.value;
   }

   public void setValue(float var1) {
      this.value = var1;
   }

   public void setValue(Number var1) {
      this.value = var1.floatValue();
   }

   public boolean isNaN() {
      return Float.isNaN(this.value);
   }

   public boolean isInfinite() {
      return Float.isInfinite(this.value);
   }

   public void increment() {
      ++this.value;
   }

   public float getAndIncrement() {
      float var1 = (float)(this.value++);
      return var1;
   }

   public float incrementAndGet() {
      ++this.value;
      return this.value;
   }

   public void decrement() {
      --this.value;
   }

   public float getAndDecrement() {
      float var1 = (float)(this.value--);
      return var1;
   }

   public float decrementAndGet() {
      --this.value;
      return this.value;
   }

   public void add(float var1) {
      this.value += var1;
   }

   public void add(Number var1) {
      this.value += var1.floatValue();
   }

   public void subtract(float var1) {
      this.value -= var1;
   }

   public void subtract(Number var1) {
      this.value -= var1.floatValue();
   }

   public float addAndGet(float var1) {
      this.value += var1;
      return this.value;
   }

   public float addAndGet(Number var1) {
      this.value += var1.floatValue();
      return this.value;
   }

   public float getAndAdd(float var1) {
      float var2 = this.value;
      this.value += var1;
      return var2;
   }

   public float getAndAdd(Number var1) {
      float var2 = this.value;
      this.value += var1.floatValue();
      return var2;
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public Float toFloat() {
      return this.floatValue();
   }

   public boolean equals(Object var1) {
      return var1 instanceof MutableFloat && Float.floatToIntBits(((MutableFloat)var1).value) == Float.floatToIntBits(this.value);
   }

   public int hashCode() {
      return Float.floatToIntBits(this.value);
   }

   public int compareTo(MutableFloat var1) {
      return Float.compare(this.value, var1.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
