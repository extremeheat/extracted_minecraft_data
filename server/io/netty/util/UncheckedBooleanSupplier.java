package io.netty.util;

public interface UncheckedBooleanSupplier extends BooleanSupplier {
   UncheckedBooleanSupplier FALSE_SUPPLIER = new UncheckedBooleanSupplier() {
      public boolean get() {
         return false;
      }
   };
   UncheckedBooleanSupplier TRUE_SUPPLIER = new UncheckedBooleanSupplier() {
      public boolean get() {
         return true;
      }
   };

   boolean get();
}
