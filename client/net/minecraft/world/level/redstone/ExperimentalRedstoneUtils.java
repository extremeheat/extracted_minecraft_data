package net.minecraft.world.level.redstone;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;

public class ExperimentalRedstoneUtils {
   public ExperimentalRedstoneUtils() {
      super();
   }

   @Nullable
   public static Orientation initialOrientation(Level var0, @Nullable Direction var1, @Nullable Direction var2) {
      if (var0.enabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
         Orientation var3 = Orientation.random(var0.random).withSideBias(Orientation.SideBias.LEFT);
         if (var2 != null) {
            var3 = var3.withUp(var2);
         }

         if (var1 != null) {
            var3 = var3.withFront(var1);
         }

         return var3;
      } else {
         return null;
      }
   }

   @Nullable
   public static Orientation withFront(@Nullable Orientation var0, Direction var1) {
      return var0 == null ? null : var0.withFront(var1);
   }
}
