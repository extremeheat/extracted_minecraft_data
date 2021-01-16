package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class DragonTakeoffPhase extends AbstractDragonPhaseInstance {
   private boolean firstTick;
   private Path currentPath;
   private Vec3 targetLocation;

   public DragonTakeoffPhase(EnderDragon var1) {
      super(var1);
   }

   public void doServerTick() {
      if (!this.firstTick && this.currentPath != null) {
         BlockPos var1 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         if (!var1.closerThan(this.dragon.position(), 10.0D)) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
         }
      } else {
         this.firstTick = false;
         this.findNewTarget();
      }

   }

   public void begin() {
      this.firstTick = true;
      this.currentPath = null;
      this.targetLocation = null;
   }

   private void findNewTarget() {
      int var1 = this.dragon.findClosestNode();
      Vec3 var2 = this.dragon.getHeadLookVector(1.0F);
      int var3 = this.dragon.findClosestNode(-var2.x * 40.0D, 105.0D, -var2.z * 40.0D);
      if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() > 0) {
         var3 %= 12;
         if (var3 < 0) {
            var3 += 12;
         }
      } else {
         var3 -= 12;
         var3 &= 7;
         var3 += 12;
      }

      this.currentPath = this.dragon.findPath(var1, var3, (Node)null);
      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null) {
         this.currentPath.advance();
         if (!this.currentPath.isDone()) {
            BlockPos var1 = this.currentPath.getNextNodePos();
            this.currentPath.advance();

            double var2;
            do {
               var2 = (double)((float)var1.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
            } while(var2 < (double)var1.getY());

            this.targetLocation = new Vec3((double)var1.getX(), var2, (double)var1.getZ());
         }
      }

   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   public EnderDragonPhase<DragonTakeoffPhase> getPhase() {
      return EnderDragonPhase.TAKEOFF;
   }
}
