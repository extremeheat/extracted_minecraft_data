package net.minecraft.init;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class Fluids {
   private static final Set<Fluid> field_207214_f;
   public static final Fluid field_204541_a;
   public static final FlowingFluid field_207212_b;
   public static final FlowingFluid field_204546_a;
   public static final FlowingFluid field_207213_d;
   public static final FlowingFluid field_204547_b;

   private static Fluid func_207211_a(String var0) {
      Fluid var1 = (Fluid)IRegistry.field_212619_h.func_82594_a(new ResourceLocation(var0));
      if (!field_207214_f.add(var1)) {
         throw new IllegalStateException("Invalid Fluid requested: " + var0);
      } else {
         return var1;
      }
   }

   static {
      if (!Bootstrap.func_179869_a()) {
         throw new RuntimeException("Accessed Fluids before Bootstrap!");
      } else {
         field_207214_f = Sets.newHashSet(new Fluid[]{(Fluid)null});
         field_204541_a = func_207211_a("empty");
         field_207212_b = (FlowingFluid)func_207211_a("flowing_water");
         field_204546_a = (FlowingFluid)func_207211_a("water");
         field_207213_d = (FlowingFluid)func_207211_a("flowing_lava");
         field_204547_b = (FlowingFluid)func_207211_a("lava");
         field_207214_f.clear();
      }
   }
}
