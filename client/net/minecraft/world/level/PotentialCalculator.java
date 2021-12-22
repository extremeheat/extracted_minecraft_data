package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;

public class PotentialCalculator {
   private final List<PotentialCalculator.PointCharge> charges = Lists.newArrayList();

   public PotentialCalculator() {
      super();
   }

   public void addCharge(BlockPos var1, double var2) {
      if (var2 != 0.0D) {
         this.charges.add(new PotentialCalculator.PointCharge(var1, var2));
      }

   }

   public double getPotentialEnergyChange(BlockPos var1, double var2) {
      if (var2 == 0.0D) {
         return 0.0D;
      } else {
         double var4 = 0.0D;

         PotentialCalculator.PointCharge var7;
         for(Iterator var6 = this.charges.iterator(); var6.hasNext(); var4 += var7.getPotentialChange(var1)) {
            var7 = (PotentialCalculator.PointCharge)var6.next();
         }

         return var4 * var2;
      }
   }

   private static class PointCharge {
      private final BlockPos pos;
      private final double charge;

      public PointCharge(BlockPos var1, double var2) {
         super();
         this.pos = var1;
         this.charge = var2;
      }

      public double getPotentialChange(BlockPos var1) {
         double var2 = this.pos.distSqr(var1);
         return var2 == 0.0D ? 1.0D / 0.0 : this.charge / Math.sqrt(var2);
      }
   }
}
