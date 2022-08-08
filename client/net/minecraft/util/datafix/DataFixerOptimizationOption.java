package net.minecraft.util.datafix;

public enum DataFixerOptimizationOption {
   UNINITIALIZED_UNOPTIMIZED,
   UNINITIALIZED_OPTIMIZED,
   INITIALIZED_UNOPTIMIZED,
   INITIALIZED_OPTIMIZED;

   private DataFixerOptimizationOption() {
   }

   // $FF: synthetic method
   private static DataFixerOptimizationOption[] $values() {
      return new DataFixerOptimizationOption[]{UNINITIALIZED_UNOPTIMIZED, UNINITIALIZED_OPTIMIZED, INITIALIZED_UNOPTIMIZED, INITIALIZED_OPTIMIZED};
   }
}
