package net.minecraft.util.math;

import java.util.function.Predicate;
import net.minecraft.fluid.IFluidState;

public enum RayTraceFluidMode {
   NEVER((var0) -> {
      return false;
   }),
   SOURCE_ONLY(IFluidState::func_206889_d),
   ALWAYS((var0) -> {
      return !var0.func_206888_e();
   });

   public final Predicate<IFluidState> field_209544_d;

   private RayTraceFluidMode(Predicate<IFluidState> var3) {
      this.field_209544_d = var3;
   }
}
