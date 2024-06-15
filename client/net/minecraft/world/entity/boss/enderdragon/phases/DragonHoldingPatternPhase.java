package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class DragonHoldingPatternPhase extends AbstractDragonPhaseInstance {
   private static final TargetingConditions NEW_TARGET_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight();
   @Nullable
   private Path currentPath;
   @Nullable
   private Vec3 targetLocation;
   private boolean clockwise;

   public DragonHoldingPatternPhase(EnderDragon var1) {
      super(var1);
   }

   @Override
   public EnderDragonPhase<DragonHoldingPatternPhase> getPhase() {
      return EnderDragonPhase.HOLDING_PATTERN;
   }

   @Override
   public void doServerTick() {
      double var1 = this.targetLocation == null ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (var1 < 100.0 || var1 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
         this.findNewTarget();
      }
   }

   @Override
   public void begin() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   @Nullable
   @Override
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      if (this.currentPath != null && this.currentPath.isDone()) {
         BlockPos var1 = this.dragon
            .level()
            .getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.getLocation(this.dragon.getFightOrigin())));
         int var2 = this.dragon.getDragonFight() == null ? 0 : this.dragon.getDragonFight().getCrystalsAlive();
         if (this.dragon.getRandom().nextInt(var2 + 3) == 0) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING_APPROACH);
            return;
         }

         Player var5 = this.dragon.level().getNearestPlayer(NEW_TARGET_TARGETING, this.dragon, (double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
         double var3;
         if (var5 != null) {
            var3 = var1.distToCenterSqr(var5.position()) / 512.0;
         } else {
            var3 = 64.0;
         }

         if (var5 != null && (this.dragon.getRandom().nextInt((int)(var3 + 2.0)) == 0 || this.dragon.getRandom().nextInt(var2 + 2) == 0)) {
            this.strafePlayer(var5);
            return;
         }
      }

      if (this.currentPath == null || this.currentPath.isDone()) {
         int var6 = this.dragon.findClosestNode();
         int var7 = var6;
         if (this.dragon.getRandom().nextInt(8) == 0) {
            this.clockwise = !this.clockwise;
            var7 = var6 + 6;
         }

         if (this.clockwise) {
            var7++;
         } else {
            var7--;
         }

         if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() >= 0) {
            var7 %= 12;
            if (var7 < 0) {
               var7 += 12;
            }
         } else {
            var7 -= 12;
            var7 &= 7;
            var7 += 12;
         }

         this.currentPath = this.dragon.findPath(var6, var7, null);
         if (this.currentPath != null) {
            this.currentPath.advance();
         }
      }

      this.navigateToNextPathNode();
   }

   private void strafePlayer(Player var1) {
      this.dragon.getPhaseManager().setPhase(EnderDragonPhase.STRAFE_PLAYER);
      this.dragon.getPhaseManager().getPhase(EnderDragonPhase.STRAFE_PLAYER).setTarget(var1);
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isDone()) {
         BlockPos var1 = this.currentPath.getNextNodePos();
         this.currentPath.advance();
         double var2 = (double)var1.getX();
         double var4 = (double)var1.getZ();

         double var6;
         do {
            var6 = (double)((float)var1.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
         } while (var6 < (double)var1.getY());

         this.targetLocation = new Vec3(var2, var6, var4);
      }
   }

   @Override
   public void onCrystalDestroyed(EndCrystal var1, BlockPos var2, DamageSource var3, @Nullable Player var4) {
      if (var4 != null && this.dragon.canAttack(var4)) {
         this.strafePlayer(var4);
      }
   }
}
