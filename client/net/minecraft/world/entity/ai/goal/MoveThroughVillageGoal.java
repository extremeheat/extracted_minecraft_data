package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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

   @Override
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
               Vec3 var3 = LandRandomPos.getPos(
                  this.mob,
                  15,
                  7,
                  var3x -> {
                     if (!var1.isVillage(var3x)) {
                        return -1.0 / 0.0;
                     } else {
                        Optional var4xx = var1.getPoiManager()
                           .find(var0 -> var0.is(PoiTypeTags.VILLAGE), this::hasNotVisited, var3x, 10, PoiManager.Occupancy.IS_OCCUPIED);
                        return var4xx.<Double>map(var1xx -> -var1xx.distSqr(var2)).orElse(-1.0 / 0.0);
                     }
                  }
               );
               if (var3 == null) {
                  return false;
               } else {
                  Optional var4 = var1.getPoiManager()
                     .find(var0 -> var0.is(PoiTypeTags.VILLAGE), this::hasNotVisited, BlockPos.containing(var3), 10, PoiManager.Occupancy.IS_OCCUPIED);
                  if (var4.isEmpty()) {
                     return false;
                  } else {
                     this.poiPos = ((BlockPos)var4.get()).immutable();
                     GroundPathNavigation var5 = (GroundPathNavigation)this.mob.getNavigation();
                     boolean var6 = var5.canOpenDoors();
                     var5.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                     this.path = var5.createPath(this.poiPos, 0);
                     var5.setCanOpenDoors(var6);
                     if (this.path == null) {
                        Vec3 var7 = DefaultRandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf(this.poiPos), 1.5707963705062866);
                        if (var7 == null) {
                           return false;
                        }

                        var5.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                        this.path = this.mob.getNavigation().createPath(var7.x, var7.y, var7.z, 0);
                        var5.setCanOpenDoors(var6);
                        if (this.path == null) {
                           return false;
                        }
                     }

                     for(int var10 = 0; var10 < this.path.getNodeCount(); ++var10) {
                        Node var8 = this.path.getNode(var10);
                        BlockPos var9 = new BlockPos(var8.x, var8.y + 1, var8.z);
                        if (DoorBlock.isWoodenDoor(this.mob.level(), var9)) {
                           this.path = this.mob.getNavigation().createPath((double)var8.x, (double)var8.y, (double)var8.z, 0);
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

   @Override
   public boolean canContinueToUse() {
      if (this.mob.getNavigation().isDone()) {
         return false;
      } else {
         return !this.poiPos.closerToCenterThan(this.mob.position(), (double)(this.mob.getBbWidth() + (float)this.distanceToPoi));
      }
   }

   @Override
   public void start() {
      this.mob.getNavigation().moveTo(this.path, this.speedModifier);
   }

   @Override
   public void stop() {
      if (this.mob.getNavigation().isDone() || this.poiPos.closerToCenterThan(this.mob.position(), (double)this.distanceToPoi)) {
         this.visited.add(this.poiPos);
      }
   }

   private boolean hasNotVisited(BlockPos var1) {
      for(BlockPos var3 : this.visited) {
         if (Objects.equals(var1, var3)) {
            return false;
         }
      }

      return true;
   }

   private void updateVisited() {
      if (this.visited.size() > 15) {
         this.visited.remove(0);
      }
   }
}
