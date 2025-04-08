package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class DragonLandingApproachPhase extends AbstractDragonPhaseInstance {
   private static final TargetingConditions NEAR_EGG_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight();
   @Nullable
   private Path currentPath;
   @Nullable
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

   public void doServerTick(ServerLevel var1) {
      double var2 = this.targetLocation == null ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (var2 < 100.0 || var2 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
         this.findNewTarget(var1);
      }

   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget(ServerLevel var1) {
      if (this.currentPath == null || this.currentPath.isDone()) {
         int var2 = this.dragon.findClosestNode();
         BlockPos var3 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.dragon.getFightOrigin()));
         Player var4 = var1.getNearestPlayer(NEAR_EGG_TARGETING, this.dragon, (double)var3.getX(), (double)var3.getY(), (double)var3.getZ());
         int var5;
         if (var4 != null) {
            Vec3 var6 = (new Vec3(var4.getX(), 0.0, var4.getZ())).normalize();
            var5 = this.dragon.findClosestNode(-var6.x * 40.0, 105.0, -var6.z * 40.0);
         } else {
            var5 = this.dragon.findClosestNode(40.0, (double)var3.getY(), 0.0);
         }

         Node var7 = new Node(var3.getX(), var3.getY(), var3.getZ());
         this.currentPath = this.dragon.findPath(var2, var5, var7);
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
         double var2 = (double)((Vec3i)var1).getX();
         double var4 = (double)((Vec3i)var1).getZ();

         double var6;
         do {
            var6 = (double)((float)((Vec3i)var1).getY() + this.dragon.getRandom().nextFloat() * 20.0F);
         } while(var6 < (double)((Vec3i)var1).getY());

         this.targetLocation = new Vec3(var2, var6, var4);
      }

   }
}
