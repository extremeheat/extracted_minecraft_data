package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class DragonSittingScanningPhase extends AbstractDragonSittingPhase {
   private static final TargetingConditions CHARGE_TARGETING = (new TargetingConditions()).range(150.0D);
   private final TargetingConditions scanTargeting;
   private int scanningTime;

   public DragonSittingScanningPhase(EnderDragon var1) {
      super(var1);
      this.scanTargeting = (new TargetingConditions()).range(20.0D).selector((var1x) -> {
         return Math.abs(var1x.getY() - var1.getY()) <= 10.0D;
      });
   }

   public void doServerTick() {
      ++this.scanningTime;
      Player var1 = this.dragon.level.getNearestPlayer(this.scanTargeting, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (var1 != null) {
         if (this.scanningTime > 25) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_ATTACKING);
         } else {
            Vec3 var2 = (new Vec3(var1.getX() - this.dragon.getX(), 0.0D, var1.getZ() - this.dragon.getZ())).normalize();
            Vec3 var3 = (new Vec3((double)Mth.sin(this.dragon.yRot * 0.017453292F), 0.0D, (double)(-Mth.cos(this.dragon.yRot * 0.017453292F)))).normalize();
            float var4 = (float)var3.dot(var2);
            float var5 = (float)(Math.acos((double)var4) * 57.2957763671875D) + 0.5F;
            if (var5 < 0.0F || var5 > 10.0F) {
               double var6 = var1.getX() - this.dragon.head.getX();
               double var8 = var1.getZ() - this.dragon.head.getZ();
               double var10 = Mth.clamp(Mth.wrapDegrees(180.0D - Mth.atan2(var6, var8) * 57.2957763671875D - (double)this.dragon.yRot), -100.0D, 100.0D);
               EnderDragon var10000 = this.dragon;
               var10000.yRotA *= 0.8F;
               float var12 = Mth.sqrt(var6 * var6 + var8 * var8) + 1.0F;
               float var13 = var12;
               if (var12 > 40.0F) {
                  var12 = 40.0F;
               }

               var10000 = this.dragon;
               var10000.yRotA = (float)((double)var10000.yRotA + var10 * (double)(0.7F / var12 / var13));
               var10000 = this.dragon;
               var10000.yRot += this.dragon.yRotA;
            }
         }
      } else if (this.scanningTime >= 100) {
         var1 = this.dragon.level.getNearestPlayer(CHARGE_TARGETING, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
         if (var1 != null) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
            ((DragonChargePlayerPhase)this.dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER)).setTarget(new Vec3(var1.getX(), var1.getY(), var1.getZ()));
         }
      }

   }

   public void begin() {
      this.scanningTime = 0;
   }

   public EnderDragonPhase getPhase() {
      return EnderDragonPhase.SITTING_SCANNING;
   }
}
