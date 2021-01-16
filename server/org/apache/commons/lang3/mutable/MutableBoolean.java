package org.apache.commons.lang3.mutable;

import java.io.Serializable;
import org.apache.commons.lang3.BooleanUtils;

public class MutableBoolean implements Mutable<Boolean>, Serializable, Comparable<MutableBoolean> {
   private static final long serialVersionUID = -4830728138360036487L;
   private boolean value;

   public MutableBoolean() {
      super();
   }

   public MutableBoolean(boolean var1) {
      super();
      this.value = var1;
   }

   public MutableBoolean(Boolean var1) {
      super();
      this.value = var1;
   }

   public Boolean getValue() {
      return this.value;
   }

   public void setValue(boolean var1) {
      this.value = var1;
   }

   public void setFalse() {
      this.value = false;
   }

   public void setTrue() {
      this.value = true;
   }

   public void setValue(Boolean var1) {
      this.value = var1;
   }

   public boolean isTrue() {
      return this.value;
   }

   public boolean isFalse() {
      return !this.value;
   }

   public boolean booleanValue() {
      return this.value;
   }

   public Boolean toBoolean() {
      return this.booleanValue();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof MutableBoolean) {
         return this.value == ((MutableBoolean)var1).booleanValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
   }

   public int compareTo(MutableBoolean var1) {
      return BooleanUtils.compare(this.value, var1.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
