package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class DragonSittingScanningPhase extends AbstractDragonSittingPhase {
   private static final int SITTING_SCANNING_IDLE_TICKS = 100;
   private static final int SITTING_ATTACK_Y_VIEW_RANGE = 10;
   private static final int SITTING_ATTACK_VIEW_RANGE = 20;
   private static final int SITTING_CHARGE_VIEW_RANGE = 150;
   private static final TargetingConditions CHARGE_TARGETING = TargetingConditions.forCombat().range(150.0);
   private final TargetingConditions scanTargeting;
   private int scanningTime;

   public DragonSittingScanningPhase(EnderDragon var1) {
      super(var1);
      this.scanTargeting = TargetingConditions.forCombat().range(20.0).selector((var1x, var2) -> {
         return Math.abs(var1x.getY() - var1.getY()) <= 10.0;
      });
   }

   public void doServerTick(ServerLevel var1) {
      ++this.scanningTime;
      Player var2 = var1.getNearestPlayer(this.scanTargeting, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (var2 != null) {
         if (this.scanningTime > 25) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_ATTACKING);
         } else {
            Vec3 var3 = (new Vec3(((LivingEntity)var2).getX() - this.dragon.getX(), 0.0, ((LivingEntity)var2).getZ() - this.dragon.getZ())).normalize();
            Vec3 var4 = (new Vec3((double)Mth.sin(this.dragon.getYRot() * 0.017453292F), 0.0, (double)(-Mth.cos(this.dragon.getYRot() * 0.017453292F)))).normalize();
            float var5 = (float)var4.dot(var3);
            float var6 = (float)(Math.acos((double)var5) * 57.2957763671875) + 0.5F;
            if (var6 < 0.0F || var6 > 10.0F) {
               double var7 = ((LivingEntity)var2).getX() - this.dragon.head.getX();
               double var9 = ((LivingEntity)var2).getZ() - this.dragon.head.getZ();
               double var11 = Mth.clamp(Mth.wrapDegrees(180.0 - Mth.atan2(var7, var9) * 57.2957763671875 - (double)this.dragon.getYRot()), -100.0, 100.0);
               EnderDragon var10000 = this.dragon;
               var10000.yRotA *= 0.8F;
               float var13 = (float)Math.sqrt(var7 * var7 + var9 * var9) + 1.0F;
               float var14 = var13;
               if (var13 > 40.0F) {
                  var13 = 40.0F;
               }

               var10000 = this.dragon;
               var10000.yRotA += (float)var11 * (0.7F / var13 / var14);
               this.dragon.setYRot(this.dragon.getYRot() + this.dragon.yRotA);
            }
         }
      } else if (this.scanningTime >= 100) {
         var2 = var1.getNearestPlayer(CHARGE_TARGETING, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
         if (var2 != null) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
            ((DragonChargePlayerPhase)this.dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER)).setTarget(new Vec3(((LivingEntity)var2).getX(), ((LivingEntity)var2).getY(), ((LivingEntity)var2).getZ()));
         }
      }

   }

   public void begin() {
      this.scanningTime = 0;
   }

   public EnderDragonPhase<DragonSittingScanningPhase> getPhase() {
      return EnderDragonPhase.SITTING_SCANNING;
   }
}
