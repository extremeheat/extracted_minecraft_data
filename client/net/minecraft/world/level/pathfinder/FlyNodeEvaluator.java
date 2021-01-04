package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class FlyNodeEvaluator extends WalkNodeEvaluator {
   public FlyNodeEvaluator() {
      super();
   }

   public void prepare(LevelReader var1, Mob var2) {
      super.prepare(var1, var2);
      this.oldWaterCost = var2.getPathfindingMalus(BlockPathTypes.WATER);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
      super.done();
   }

   public Node getStart() {
      int var1;
      if (this.canFloat() && this.mob.isInWater()) {
         var1 = Mth.floor(this.mob.getBoundingBox().minY);
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos(this.mob.x, (double)var1, this.mob.z);

         for(Block var3 = this.level.getBlockState(var2).getBlock(); var3 == Blocks.WATER; var3 = this.level.getBlockState(var2).getBlock()) {
            ++var1;
            var2.set(this.mob.x, (double)var1, this.mob.z);
         }
      } else {
         var1 = Mth.floor(this.mob.getBoundingBox().minY + 0.5D);
      }

      BlockPos var8 = new BlockPos(this.mob);
      BlockPathTypes var9 = this.getBlockPathType(this.mob, var8.getX(), var1, var8.getZ());
      if (this.mob.getPathfindingMalus(var9) < 0.0F) {
         HashSet var4 = Sets.newHashSet();
         var4.add(new BlockPos(this.mob.getBoundingBox().minX, (double)var1, this.mob.getBoundingBox().minZ));
         var4.add(new BlockPos(this.mob.getBoundingBox().minX, (double)var1, this.mob.getBoundingBox().maxZ));
         var4.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)var1, this.mob.getBoundingBox().minZ));
         var4.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)var1, this.mob.getBoundingBox().maxZ));
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            BlockPos var6 = (BlockPos)var5.next();
            BlockPathTypes var7 = this.getBlockPathType(this.mob, var6);
            if (this.mob.getPathfindingMalus(var7) >= 0.0F) {
               return super.getNode(var6.getX(), var6.getY(), var6.getZ());
            }
         }
      }

      return super.getNode(var8.getX(), var1, var8.getZ());
   }

   public Target getGoal(double var1, double var3, double var5) {
      return new Target(super.getNode(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5)));
   }

   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = 0;
      Node var4 = this.getNode(var2.x, var2.y, var2.z + 1);
      Node var5 = this.getNode(var2.x - 1, var2.y, var2.z);
      Node var6 = this.getNode(var2.x + 1, var2.y, var2.z);
      Node var7 = this.getNode(var2.x, var2.y, var2.z - 1);
      Node var8 = this.getNode(var2.x, var2.y + 1, var2.z);
      Node var9 = this.getNode(var2.x, var2.y - 1, var2.z);
      if (var4 != null && !var4.closed) {
         var1[var3++] = var4;
      }

      if (var5 != null && !var5.closed) {
         var1[var3++] = var5;
      }

      if (var6 != null && !var6.closed) {
         var1[var3++] = var6;
      }

      if (var7 != null && !var7.closed) {
         var1[var3++] = var7;
      }

      if (var8 != null && !var8.closed) {
         var1[var3++] = var8;
      }

      if (var9 != null && !var9.closed) {
         var1[var3++] = var9;
      }

      boolean var10 = var7 == null || var7.costMalus != 0.0F;
      boolean var11 = var4 == null || var4.costMalus != 0.0F;
      boolean var12 = var6 == null || var6.costMalus != 0.0F;
      boolean var13 = var5 == null || var5.costMalus != 0.0F;
      boolean var14 = var8 == null || var8.costMalus != 0.0F;
      boolean var15 = var9 == null || var9.costMalus != 0.0F;
      Node var16;
      if (var10 && var13) {
         var16 = this.getNode(var2.x - 1, var2.y, var2.z - 1);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var10 && var12) {
         var16 = this.getNode(var2.x + 1, var2.y, var2.z - 1);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var11 && var13) {
         var16 = this.getNode(var2.x - 1, var2.y, var2.z + 1);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var11 && var12) {
         var16 = this.getNode(var2.x + 1, var2.y, var2.z + 1);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var10 && var14) {
         var16 = this.getNode(var2.x, var2.y + 1, var2.z - 1);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var11 && var14) {
         var16 = this.getNode(var2.x, var2.y + 1, var2.z + 1);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var12 && var14) {
         var16 = this.getNode(var2.x + 1, var2.y + 1, var2.z);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var13 && var14) {
         var16 = this.getNode(var2.x - 1, var2.y + 1, var2.z);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var10 && var15) {
         var16 = this.getNode(var2.x, var2.y - 1, var2.z - 1);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var11 && var15) {
         var16 = this.getNode(var2.x, var2.y - 1, var2.z + 1);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var12 && var15) {
         var16 = this.getNode(var2.x + 1, var2.y - 1, var2.z);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      if (var13 && var15) {
         var16 = this.getNode(var2.x - 1, var2.y - 1, var2.z);
         if (var16 != null && !var16.closed) {
            var1[var3++] = var16;
         }
      }

      return var3;
   }

   @Nullable
   protected Node getNode(int var1, int var2, int var3) {
      Node var4 = null;
      BlockPathTypes var5 = this.getBlockPathType(this.mob, var1, var2, var3);
      float var6 = this.mob.getPathfindingMalus(var5);
      if (var6 >= 0.0F) {
         var4 = super.getNode(var1, var2, var3);
         var4.type = var5;
         var4.costMalus = Math.max(var4.costMalus, var6);
         if (var5 == BlockPathTypes.WALKABLE) {
            ++var4.costMalus;
         }
      }

      return var5 != BlockPathTypes.OPEN && var5 != BlockPathTypes.WALKABLE ? var4 : var4;
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4, Mob var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      EnumSet var11 = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes var12 = BlockPathTypes.BLOCKED;
      BlockPos var13 = new BlockPos(var5);
      var12 = this.getBlockPathTypes(var1, var2, var3, var4, var6, var7, var8, var9, var10, var11, var12, var13);
      if (var11.contains(BlockPathTypes.FENCE)) {
         return BlockPathTypes.FENCE;
      } else {
         BlockPathTypes var14 = BlockPathTypes.BLOCKED;
         Iterator var15 = var11.iterator();

         while(var15.hasNext()) {
            BlockPathTypes var16 = (BlockPathTypes)var15.next();
            if (var5.getPathfindingMalus(var16) < 0.0F) {
               return var16;
            }

            if (var5.getPathfindingMalus(var16) >= var5.getPathfindingMalus(var14)) {
               var14 = var16;
            }
         }

         if (var12 == BlockPathTypes.OPEN && var5.getPathfindingMalus(var14) == 0.0F) {
            return BlockPathTypes.OPEN;
         } else {
            return var14;
         }
      }
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
      BlockPathTypes var5 = this.getBlockPathTypeRaw(var1, var2, var3, var4);
      if (var5 == BlockPathTypes.OPEN && var3 >= 1) {
         Block var6 = var1.getBlockState(new BlockPos(var2, var3 - 1, var4)).getBlock();
         BlockPathTypes var7 = this.getBlockPathTypeRaw(var1, var2, var3 - 1, var4);
         if (var7 != BlockPathTypes.DAMAGE_FIRE && var6 != Blocks.MAGMA_BLOCK && var7 != BlockPathTypes.LAVA && var6 != Blocks.CAMPFIRE) {
            if (var7 == BlockPathTypes.DAMAGE_CACTUS) {
               var5 = BlockPathTypes.DAMAGE_CACTUS;
            } else if (var7 == BlockPathTypes.DAMAGE_OTHER) {
               var5 = BlockPathTypes.DAMAGE_OTHER;
            } else {
               var5 = var7 != BlockPathTypes.WALKABLE && var7 != BlockPathTypes.OPEN && var7 != BlockPathTypes.WATER ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
            }
         } else {
            var5 = BlockPathTypes.DAMAGE_FIRE;
         }
      }

      var5 = this.checkNeighbourBlocks(var1, var2, var3, var4, var5);
      return var5;
   }

   private BlockPathTypes getBlockPathType(Mob var1, BlockPos var2) {
      return this.getBlockPathType(var1, var2.getX(), var2.getY(), var2.getZ());
   }

   private BlockPathTypes getBlockPathType(Mob var1, int var2, int var3, int var4) {
      return this.getBlockPathType(this.level, var2, var3, var4, var1, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
   }
}
