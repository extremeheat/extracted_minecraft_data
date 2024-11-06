package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MoveThroughVillageGoal extends Goal {
   protected final PathfinderMob mob;
   private final double speedModifier;
   @Nullable
   private Path path;
   private BlockPos poiPos;
   private final boolean onlyAtNight;
   private final List<BlockPos> visited = Lists.newArrayList();
   private final int distanceToPoi;
   private final BooleanSupplier canDealWithDoors;

   public MoveThroughVillageGoal(PathfinderMob var1, double var2, boolean var4, int var5, BooleanSupplier var6) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.onlyAtNight = var4;
      this.distanceToPoi = var5;
      this.canDealWithDoors = var6;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      if (!GoalUtils.hasGroundPathNavigation(var1)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   public boolean canUse() {
      if (!GoalUtils.hasGroundPathNavigation(this.mob)) {
         return false;
      } else {
         this.updateVisited();
         if (this.onlyAtNight && this.mob.level().isDay()) {
            return false;
         } else {
            ServerLevel var1 = (ServerLevel)this.mob.level();
            BlockPos var2 = this.mob.blockPosition();
            if (!var1.isCloseToVillage(var2, 6)) {
               return false;
            } else {
               Vec3 var3 = LandRandomPos.getPos(this.mob, 15, 7, (var3x) -> {
                  if (!var1.isVillage(var3x)) {
                     return -1.0 / 0.0;
                  } else {
                     Optional var4 = var1.getPoiManager().find((var0) -> {
                        return var0.is(PoiTypeTags.VILLAGE);
                     }, this::hasNotVisited, var3x, 10, PoiManager.Occupancy.IS_OCCUPIED);
                     return (Double)var4.map((var1x) -> {
                        return -var1x.distSqr(var2);
                     }).orElse(-1.0 / 0.0);
                  }
               });
               if (var3 == null) {
                  return false;
               } else {
                  Optional var4 = var1.getPoiManager().find((var0) -> {
                     return var0.is(PoiTypeTags.VILLAGE);
                  }, this::hasNotVisited, BlockPos.containing(var3), 10, PoiManager.Occupancy.IS_OCCUPIED);
                  if (var4.isEmpty()) {
                     return false;
                  } else {
                     this.poiPos = ((BlockPos)var4.get()).immutable();
                     GroundPathNavigation var5 = (GroundPathNavigation)this.mob.getNavigation();
                     var5.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                     this.path = var5.createPath((BlockPos)this.poiPos, 0);
                     var5.setCanOpenDoors(true);
                     if (this.path == null) {
                        Vec3 var6 = DefaultRandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf(this.poiPos), 1.5707963705062866);
                        if (var6 == null) {
                           return false;
                        }

                        var5.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                        this.path = this.mob.getNavigation().createPath(var6.x, var6.y, var6.z, 0);
                        var5.setCanOpenDoors(true);
                        if (this.path == null) {
                           return false;
                        }
                     }

                     for(int var9 = 0; var9 < this.path.getNodeCount(); ++var9) {
                        Node var7 = this.path.getNode(var9);
                        BlockPos var8 = new BlockPos(var7.x, var7.y + 1, var7.z);
                        if (DoorBlock.isWoodenDoor(this.mob.level(), var8)) {
                           this.path = this.mob.getNavigation().createPath((double)var7.x, (double)var7.y, (double)var7.z, 0);
                           break;
                        }
                     }

                     return this.path != null;
                  }
               }
            }
         }
      }
   }

   public boolean canContinueToUse() {
      if (this.mob.getNavigation().isDone()) {
         return false;
      } else {
         return !this.poiPos.closerToCenterThan(this.mob.position(), (double)(this.mob.getBbWidth() + (float)this.distanceToPoi));
      }
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.path, this.speedModifier);
   }

   public void stop() {
      if (this.mob.getNavigation().isDone() || this.poiPos.closerToCenterThan(this.mob.position(), (double)this.distanceToPoi)) {
         this.visited.add(this.poiPos);
      }

   }

   private boolean hasNotVisited(BlockPos var1) {
      Iterator var2 = this.visited.iterator();

      BlockPos var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (BlockPos)var2.next();
      } while(!Objects.equals(var1, var3));

      return false;
   }

   private void updateVisited() {
      if (this.visited.size() > 15) {
         this.visited.remove(0);
      }

   }
}
