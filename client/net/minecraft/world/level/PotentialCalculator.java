package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;

public class PotentialCalculator {
   private final List<PointCharge> charges = Lists.newArrayList();

   public PotentialCalculator() {
      super();
   }

   public void addCharge(final BlockPos pos, final double charge) {
      if (charge != 0.0) {
         this.charges.add(new PointCharge(pos, charge));
      }

   }

   public double getPotentialEnergyChange(final BlockPos pos, final double charge) {
      if (charge == 0.0) {
         return 0.0;
      } else {
         double potentialChange = 0.0;

         for(PointCharge point : this.charges) {
            potentialChange += point.getPotentialChange(pos);
         }

         return potentialChange * charge;
      }
   }

   private static class PointCharge {
      private final BlockPos pos;
      private final double charge;

      public PointCharge(final BlockPos pos, final double charge) {
         super();
         this.pos = pos;
         this.charge = charge;
      }

      public double getPotentialChange(final BlockPos pos) {
         double distSqr = this.pos.distSqr(pos);
         return distSqr == 0.0 ? 1.0 / 0.0 : this.charge / Math.sqrt(distSqr);
      }
   }
}
