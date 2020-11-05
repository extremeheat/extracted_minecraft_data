package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class DragonLandingApproachPhase extends AbstractDragonPhaseInstance {
   private static final TargetingConditions NEAR_EGG_TARGETING = (new TargetingConditions()).range(128.0D);
   private Path currentPath;
   private Vec3 targetLocation;

   public DragonLandingApproachPhase(EnderDragon var1) {
      super(var1);
   }

   public EnderDragonPhase<DragonLandingApproachPhase> getPhase() {
      return EnderDragonPhase.LANDING_APPROACH;
   }

   public void begin() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   public void doServerTick() {
      double var1 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (var1 < 100.0D || var1 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
         this.findNewTarget();
      }

   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      if (this.currentPath == null || this.currentPath.isDone()) {
         int var1 = this.dragon.findClosestNode();
         BlockPos var2 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         Player var3 = this.dragon.level.getNearestPlayer(NEAR_EGG_TARGETING, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
         int var4;
         if (var3 != null) {
            Vec3 var5 = (new Vec3(var3.getX(), 0.0D, var3.getZ())).normalize();
            var4 = this.dragon.findClosestNode(-var5.x * 40.0D, 105.0D, -var5.z * 40.0D);
         } else {
            var4 = this.dragon.findClosestNode(40.0D, (double)var2.getY(), 0.0D);
         }

         Node var6 = new Node(var2.getX(), var2.getY(), var2.getZ());
         this.currentPath = this.dragon.findPath(var1, var4, var6);
         if (this.currentPath != null) {
            this.currentPath.advance();
         }
      }

      this.navigateToNextPathNode();
      if (this.currentPath != null && this.currentPath.isDone()) {
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING);
      }

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
         } while(var6 < (double)var1.getY());

         this.targetLocation = new Vec3(var2, var6, var4);
      }

   }
}
