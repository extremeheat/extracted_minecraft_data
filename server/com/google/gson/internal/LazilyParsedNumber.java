package com.google.gson.internal;

import java.io.ObjectStreamException;
import java.math.BigDecimal;

public final class LazilyParsedNumber extends Number {
   private final String value;

   public LazilyParsedNumber(String var1) {
      super();
      this.value = var1;
   }

   public int intValue() {
      try {
         return Integer.parseInt(this.value);
      } catch (NumberFormatException var4) {
         try {
            return (int)Long.parseLong(this.value);
         } catch (NumberFormatException var3) {
            return (new BigDecimal(this.value)).intValue();
         }
      }
   }

   public long longValue() {
      try {
         return Long.parseLong(this.value);
      } catch (NumberFormatException var2) {
         return (new BigDecimal(this.value)).longValue();
      }
   }

   public float floatValue() {
      return Float.parseFloat(this.value);
   }

   public double doubleValue() {
      return Double.parseDouble(this.value);
   }

   public String toString() {
      return this.value;
   }

   private Object writeReplace() throws ObjectStreamException {
      return new BigDecimal(this.value);
   }

   public int hashCode() {
      return this.value.hashCode();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LazilyParsedNumber)) {
         return false;
      } else {
         LazilyParsedNumber var2 = (LazilyParsedNumber)var1;
         return this.value == var2.value || this.value.equals(var2.value);
      }
   }
}
