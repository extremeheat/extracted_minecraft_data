package net.minecraft.world.level.pathfinder;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FlyNodeEvaluator extends WalkNodeEvaluator {
   private final Long2ObjectMap<BlockPathTypes> pathTypeByPosCache = new Long2ObjectOpenHashMap();

   public FlyNodeEvaluator() {
      super();
   }

   public void prepare(PathNavigationRegion var1, Mob var2) {
      super.prepare(var1, var2);
      this.pathTypeByPosCache.clear();
      this.oldWaterCost = var2.getPathfindingMalus(BlockPathTypes.WATER);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
      this.pathTypeByPosCache.clear();
      super.done();
   }

   public Node getStart() {
      int var1;
      if (this.canFloat() && this.mob.isInWater()) {
         var1 = this.mob.getBlockY();
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos(this.mob.getX(), (double)var1, this.mob.getZ());

         for(BlockState var3 = this.level.getBlockState(var2); var3.is(Blocks.WATER); var3 = this.level.getBlockState(var2)) {
            ++var1;
            var2.set(this.mob.getX(), (double)var1, this.mob.getZ());
         }
      } else {
         var1 = Mth.floor(this.mob.getY() + 0.5D);
      }

      BlockPos var8 = this.mob.blockPosition();
      BlockPathTypes var9 = this.getCachedBlockPathType(var8.getX(), var1, var8.getZ());
      if (this.mob.getPathfindingMalus(var9) < 0.0F) {
         ImmutableSet var4 = ImmutableSet.of(new BlockPos(this.mob.getBoundingBox().minX, (double)var1, this.mob.getBoundingBox().minZ), new BlockPos(this.mob.getBoundingBox().minX, (double)var1, this.mob.getBoundingBox().maxZ), new BlockPos(this.mob.getBoundingBox().maxX, (double)var1, this.mob.getBoundingBox().minZ), new BlockPos(this.mob.getBoundingBox().maxX, (double)var1, this.mob.getBoundingBox().maxZ));
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            BlockPos var6 = (BlockPos)var5.next();
            BlockPathTypes var7 = this.getCachedBlockPathType(var8.getX(), var1, var8.getZ());
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
      Node var4 = this.getNode(var2.field_333, var2.field_334, var2.field_335 + 1);
      if (this.isOpen(var4)) {
         var1[var3++] = var4;
      }

      Node var5 = this.getNode(var2.field_333 - 1, var2.field_334, var2.field_335);
      if (this.isOpen(var5)) {
         var1[var3++] = var5;
      }

      Node var6 = this.getNode(var2.field_333 + 1, var2.field_334, var2.field_335);
      if (this.isOpen(var6)) {
         var1[var3++] = var6;
      }

      Node var7 = this.getNode(var2.field_333, var2.field_334, var2.field_335 - 1);
      if (this.isOpen(var7)) {
         var1[var3++] = var7;
      }

      Node var8 = this.getNode(var2.field_333, var2.field_334 + 1, var2.field_335);
      if (this.isOpen(var8)) {
         var1[var3++] = var8;
      }

      Node var9 = this.getNode(var2.field_333, var2.field_334 - 1, var2.field_335);
      if (this.isOpen(var9)) {
         var1[var3++] = var9;
      }

      Node var10 = this.getNode(var2.field_333, var2.field_334 + 1, var2.field_335 + 1);
      if (this.isOpen(var10) && this.hasMalus(var4) && this.hasMalus(var8)) {
         var1[var3++] = var10;
      }

      Node var11 = this.getNode(var2.field_333 - 1, var2.field_334 + 1, var2.field_335);
      if (this.isOpen(var11) && this.hasMalus(var5) && this.hasMalus(var8)) {
         var1[var3++] = var11;
      }

      Node var12 = this.getNode(var2.field_333 + 1, var2.field_334 + 1, var2.field_335);
      if (this.isOpen(var12) && this.hasMalus(var6) && this.hasMalus(var8)) {
         var1[var3++] = var12;
      }

      Node var13 = this.getNode(var2.field_333, var2.field_334 + 1, var2.field_335 - 1);
      if (this.isOpen(var13) && this.hasMalus(var7) && this.hasMalus(var8)) {
         var1[var3++] = var13;
      }

      Node var14 = this.getNode(var2.field_333, var2.field_334 - 1, var2.field_335 + 1);
      if (this.isOpen(var14) && this.hasMalus(var4) && this.hasMalus(var9)) {
         var1[var3++] = var14;
      }

      Node var15 = this.getNode(var2.field_333 - 1, var2.field_334 - 1, var2.field_335);
      if (this.isOpen(var15) && this.hasMalus(var5) && this.hasMalus(var9)) {
         var1[var3++] = var15;
      }

      Node var16 = this.getNode(var2.field_333 + 1, var2.field_334 - 1, var2.field_335);
      if (this.isOpen(var16) && this.hasMalus(var6) && this.hasMalus(var9)) {
         var1[var3++] = var16;
      }

      Node var17 = this.getNode(var2.field_333, var2.field_334 - 1, var2.field_335 - 1);
      if (this.isOpen(var17) && this.hasMalus(var7) && this.hasMalus(var9)) {
         var1[var3++] = var17;
      }

      Node var18 = this.getNode(var2.field_333 + 1, var2.field_334, var2.field_335 - 1);
      if (this.isOpen(var18) && this.hasMalus(var7) && this.hasMalus(var6)) {
         var1[var3++] = var18;
      }

      Node var19 = this.getNode(var2.field_333 + 1, var2.field_334, var2.field_335 + 1);
      if (this.isOpen(var19) && this.hasMalus(var4) && this.hasMalus(var6)) {
         var1[var3++] = var19;
      }

      Node var20 = this.getNode(var2.field_333 - 1, var2.field_334, var2.field_335 - 1);
      if (this.isOpen(var20) && this.hasMalus(var7) && this.hasMalus(var5)) {
         var1[var3++] = var20;
      }

      Node var21 = this.getNode(var2.field_333 - 1, var2.field_334, var2.field_335 + 1);
      if (this.isOpen(var21) && this.hasMalus(var4) && this.hasMalus(var5)) {
         var1[var3++] = var21;
      }

      Node var22 = this.getNode(var2.field_333 + 1, var2.field_334 + 1, var2.field_335 - 1);
      if (this.isOpen(var22) && this.hasMalus(var18) && this.hasMalus(var7) && this.hasMalus(var6) && this.hasMalus(var8) && this.hasMalus(var13) && this.hasMalus(var12)) {
         var1[var3++] = var22;
      }

      Node var23 = this.getNode(var2.field_333 + 1, var2.field_334 + 1, var2.field_335 + 1);
      if (this.isOpen(var23) && this.hasMalus(var19) && this.hasMalus(var4) && this.hasMalus(var6) && this.hasMalus(var8) && this.hasMalus(var10) && this.hasMalus(var12)) {
         var1[var3++] = var23;
      }

      Node var24 = this.getNode(var2.field_333 - 1, var2.field_334 + 1, var2.field_335 - 1);
      if (this.isOpen(var24) && this.hasMalus(var20) && this.hasMalus(var7) && this.hasMalus(var5) && this.hasMalus(var8) && this.hasMalus(var13) && this.hasMalus(var11)) {
         var1[var3++] = var24;
      }

      Node var25 = this.getNode(var2.field_333 - 1, var2.field_334 + 1, var2.field_335 + 1);
      if (this.isOpen(var25) && this.hasMalus(var21) && this.hasMalus(var4) && this.hasMalus(var5) && this.hasMalus(var8) && this.hasMalus(var10) && this.hasMalus(var11)) {
         var1[var3++] = var25;
      }

      Node var26 = this.getNode(var2.field_333 + 1, var2.field_334 - 1, var2.field_335 - 1);
      if (this.isOpen(var26) && this.hasMalus(var18) && this.hasMalus(var7) && this.hasMalus(var6) && this.hasMalus(var9) && this.hasMalus(var17) && this.hasMalus(var16)) {
         var1[var3++] = var26;
      }

      Node var27 = this.getNode(var2.field_333 + 1, var2.field_334 - 1, var2.field_335 + 1);
      if (this.isOpen(var27) && this.hasMalus(var19) && this.hasMalus(var4) && this.hasMalus(var6) && this.hasMalus(var9) && this.hasMalus(var14) && this.hasMalus(var16)) {
         var1[var3++] = var27;
      }

      Node var28 = this.getNode(var2.field_333 - 1, var2.field_334 - 1, var2.field_335 - 1);
      if (this.isOpen(var28) && this.hasMalus(var20) && this.hasMalus(var7) && this.hasMalus(var5) && this.hasMalus(var9) && this.hasMalus(var17) && this.hasMalus(var15)) {
         var1[var3++] = var28;
      }

      Node var29 = this.getNode(var2.field_333 - 1, var2.field_334 - 1, var2.field_335 + 1);
      if (this.isOpen(var29) && this.hasMalus(var21) && this.hasMalus(var4) && this.hasMalus(var5) && this.hasMalus(var9) && this.hasMalus(var14) && this.hasMalus(var15)) {
         var1[var3++] = var29;
      }

      return var3;
   }

   private boolean hasMalus(@Nullable Node var1) {
      return var1 != null && var1.costMalus >= 0.0F;
   }

   private boolean isOpen(@Nullable Node var1) {
      return var1 != null && !var1.closed;
   }

   @Nullable
   protected Node getNode(int var1, int var2, int var3) {
      Node var4 = null;
      BlockPathTypes var5 = this.getCachedBlockPathType(var1, var2, var3);
      float var6 = this.mob.getPathfindingMalus(var5);
      if (var6 >= 0.0F) {
         var4 = super.getNode(var1, var2, var3);
         var4.type = var5;
         var4.costMalus = Math.max(var4.costMalus, var6);
         if (var5 == BlockPathTypes.WALKABLE) {
            ++var4.costMalus;
         }
      }

      return var4;
   }

   private BlockPathTypes getCachedBlockPathType(int var1, int var2, int var3) {
      return (BlockPathTypes)this.pathTypeByPosCache.computeIfAbsent(BlockPos.asLong(var1, var2, var3), (var4) -> {
         return this.getBlockPathType(this.level, var1, var2, var3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
      });
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4, Mob var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      EnumSet var11 = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes var12 = BlockPathTypes.BLOCKED;
      BlockPos var13 = var5.blockPosition();
      var12 = super.getBlockPathTypes(var1, var2, var3, var4, var6, var7, var8, var9, var10, var11, var12, var13);
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
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
      BlockPathTypes var6 = getBlockPathTypeRaw(var1, var5.set(var2, var3, var4));
      if (var6 == BlockPathTypes.OPEN && var3 >= var1.getMinBuildHeight() + 1) {
         BlockPathTypes var7 = getBlockPathTypeRaw(var1, var5.set(var2, var3 - 1, var4));
         if (var7 != BlockPathTypes.DAMAGE_FIRE && var7 != BlockPathTypes.LAVA) {
            if (var7 == BlockPathTypes.DAMAGE_CACTUS) {
               var6 = BlockPathTypes.DAMAGE_CACTUS;
            } else if (var7 == BlockPathTypes.DAMAGE_OTHER) {
               var6 = BlockPathTypes.DAMAGE_OTHER;
            } else if (var7 == BlockPathTypes.COCOA) {
               var6 = BlockPathTypes.COCOA;
            } else if (var7 == BlockPathTypes.FENCE) {
               var6 = BlockPathTypes.FENCE;
            } else {
               var6 = var7 != BlockPathTypes.WALKABLE && var7 != BlockPathTypes.OPEN && var7 != BlockPathTypes.WATER ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
            }
         } else {
            var6 = BlockPathTypes.DAMAGE_FIRE;
         }
      }

      if (var6 == BlockPathTypes.WALKABLE || var6 == BlockPathTypes.OPEN) {
         var6 = checkNeighbourBlocks(var1, var5.set(var2, var3, var4), var6);
      }

      return var6;
   }
}
