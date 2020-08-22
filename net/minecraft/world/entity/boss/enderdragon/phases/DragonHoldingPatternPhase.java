package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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
   private static final TargetingConditions NEW_TARGET_TARGETING = (new TargetingConditions()).range(64.0D);
   private Path currentPath;
   private Vec3 targetLocation;
   private boolean clockwise;

   public DragonHoldingPatternPhase(EnderDragon var1) {
      super(var1);
   }

   public EnderDragonPhase getPhase() {
      return EnderDragonPhase.HOLDING_PATTERN;
   }

   public void doServerTick() {
      double var1 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (var1 < 100.0D || var1 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
         this.findNewTarget();
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

   private void findNewTarget() {
      int var2;
      if (this.currentPath != null && this.currentPath.isDone()) {
         BlockPos var1 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
         var2 = this.dragon.getDragonFight() == null ? 0 : this.dragon.getDragonFight().getCrystalsAlive();
         if (this.dragon.getRandom().nextInt(var2 + 3) == 0) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING_APPROACH);
            return;
         }

         double var3 = 64.0D;
         Player var5 = this.dragon.level.getNearestPlayer(NEW_TARGET_TARGETING, (double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
         if (var5 != null) {
            var3 = var1.distSqr(var5.position(), true) / 512.0D;
         }

         if (var5 != null && !var5.abilities.invulnerable && (this.dragon.getRandom().nextInt(Mth.abs((int)var3) + 2) == 0 || this.dragon.getRandom().nextInt(var2 + 2) == 0)) {
            this.strafePlayer(var5);
            return;
         }
      }

      if (this.currentPath == null || this.currentPath.isDone()) {
         int var6 = this.dragon.findClosestNode();
         var2 = var6;
         if (this.dragon.getRandom().nextInt(8) == 0) {
            this.clockwise = !this.clockwise;
            var2 = var6 + 6;
         }

         if (this.clockwise) {
            ++var2;
         } else {
            --var2;
         }

         if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() >= 0) {
            var2 %= 12;
            if (var2 < 0) {
               var2 += 12;
            }
         } else {
            var2 -= 12;
            var2 &= 7;
            var2 += 12;
         }

         this.currentPath = this.dragon.findPath(var6, var2, (Node)null);
         if (this.currentPath != null) {
            this.currentPath.next();
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
         Vec3 var1 = this.currentPath.currentPos();
         this.currentPath.next();
         double var2 = var1.x;
         double var4 = var1.z;

         double var6;
         do {
            var6 = var1.y + (double)(this.dragon.getRandom().nextFloat() * 20.0F);
         } while(var6 < var1.y);

         this.targetLocation = new Vec3(var2, var6, var4);
      }

   }

   public void onCrystalDestroyed(EndCrystal var1, BlockPos var2, DamageSource var3, @Nullable Player var4) {
      if (var4 != null && !var4.abilities.invulnerable) {
         this.strafePlayer(var4);
      }

   }
}
