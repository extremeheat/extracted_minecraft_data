package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;

public class PotentialCalculator {
   private final List<PointCharge> charges = Lists.newArrayList();

   public PotentialCalculator() {
      super();
   }

   public void addCharge(BlockPos var1, double var2) {
      if (var2 != 0.0) {
         this.charges.add(new PointCharge(var1, var2));
      }

   }

   public double getPotentialEnergyChange(BlockPos var1, double var2) {
      if (var2 == 0.0) {
         return 0.0;
      } else {
         double var4 = 0.0;

         for(PointCharge var7 : this.charges) {
            var4 += var7.getPotentialChange(var1);
         }

         return var4 * var2;
      }
   }

   static class PointCharge {
      private final BlockPos pos;
      private final double charge;

      public PointCharge(BlockPos var1, double var2) {
         super();
         this.pos = var1;
         this.charge = var2;
      }

      public double getPotentialChange(BlockPos var1) {
         double var2 = this.pos.distSqr(var1);
         return var2 == 0.0 ? 1.0 / 0.0 : this.charge / Math.sqrt(var2);
      }
   }
}
