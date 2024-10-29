package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.DragonFlightHistory;
import net.minecraft.world.phys.Vec3;

public class EnderDragonRenderState extends EntityRenderState {
   public float flapTime;
   public float deathTime;
   public boolean hasRedOverlay;
   @Nullable
   public Vec3 beamOffset;
   public boolean isLandingOrTakingOff;
   public boolean isSitting;
   public double distanceToEgg;
   public float partialTicks;
   public final DragonFlightHistory flightHistory = new DragonFlightHistory();

   public EnderDragonRenderState() {
      super();
   }

   public DragonFlightHistory.Sample getHistoricalPos(int var1) {
      return this.flightHistory.get(var1, this.partialTicks);
   }

   public float getHeadPartYOffset(int var1, DragonFlightHistory.Sample var2, DragonFlightHistory.Sample var3) {
      double var4;
      if (this.isLandingOrTakingOff) {
         var4 = (double)var1 / Math.max(this.distanceToEgg / 4.0, 1.0);
      } else if (this.isSitting) {
         var4 = (double)var1;
      } else if (var1 == 6) {
         var4 = 0.0;
      } else {
         var4 = var3.y() - var2.y();
      }

      return (float)var4;
   }
}
