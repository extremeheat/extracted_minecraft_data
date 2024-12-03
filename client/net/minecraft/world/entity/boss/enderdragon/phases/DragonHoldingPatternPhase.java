package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.Node;
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

   public EnderDragonPhase<DragonHoldingPatternPhase> getPhase() {
      return EnderDragonPhase.HOLDING_PATTERN;
   }

   public void doServerTick(ServerLevel var1) {
      double var2 = this.targetLocation == null ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (var2 < 100.0 || var2 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
         this.findNewTarget(var1);
      }

   }

   public void begin() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget(ServerLevel var1) {
      if (this.currentPath != null && this.currentPath.isDone()) {
         BlockPos var2 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.dragon.getFightOrigin()));
         int var3 = this.dragon.getDragonFight() == null ? 0 : this.dragon.getDragonFight().getCrystalsAlive();
         if (this.dragon.getRandom().nextInt(var3 + 3) == 0) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING_APPROACH);
            return;
         }

         Player var6 = var1.getNearestPlayer(NEW_TARGET_TARGETING, this.dragon, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
         double var4;
         if (var6 != null) {
            var4 = var2.distToCenterSqr(var6.position()) / 512.0;
         } else {
            var4 = 64.0;
         }

         if (var6 != null && (this.dragon.getRandom().nextInt((int)(var4 + 2.0)) == 0 || this.dragon.getRandom().nextInt(var3 + 2) == 0)) {
            this.strafePlayer(var6);
            return;
         }
      }

      if (this.currentPath == null || this.currentPath.isDone()) {
         int var7 = this.dragon.findClosestNode();
         int var8 = var7;
         if (this.dragon.getRandom().nextInt(8) == 0) {
            this.clockwise = !this.clockwise;
            var8 = var7 + 6;
         }

         if (this.clockwise) {
            ++var8;
         } else {
            --var8;
         }

         if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() >= 0) {
            var8 %= 12;
            if (var8 < 0) {
               var8 += 12;
            }
         } else {
            var8 -= 12;
            var8 &= 7;
            var8 += 12;
         }

         this.currentPath = this.dragon.findPath(var7, var8, (Node)null);
         if (this.currentPath != null) {
            this.currentPath.advance();
         }
      }

      this.navigateToNextPathNode();
   }

   private void strafePlayer(Player var1) {
      this.dragon.getPhaseManager().setPhase(EnderDragonPhase.STRAFE_PLAYER);
      ((DragonStrafePlayerPhase)this.dragon.getPhaseManager().getPhase(EnderDragonPhase.STRAFE_PLAYER)).setTarget(var1);
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isDone()) {
         BlockPos var1 = this.currentPath.getNextNodePos();
         this.currentPath.advance();
         double var2 = (double)((Vec3i)var1).getX();
         double var4 = (double)((Vec3i)var1).getZ();

         double var6;
         do {
            var6 = (double)((float)((Vec3i)var1).getY() + this.dragon.getRandom().nextFloat() * 20.0F);
         } while(var6 < (double)((Vec3i)var1).getY());

         this.targetLocation = new Vec3(var2, var6, var4);
      }

   }

   public void onCrystalDestroyed(EndCrystal var1, BlockPos var2, DamageSource var3, @Nullable Player var4) {
      if (var4 != null && this.dragon.canAttack(var4)) {
         this.strafePlayer(var4);
      }

   }
}
