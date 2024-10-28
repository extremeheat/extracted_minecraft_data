package net.minecraft.world.level.material;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class FlowingFluid extends Fluid {
   public static final BooleanProperty FALLING;
   public static final IntegerProperty LEVEL;
   private static final int CACHE_SIZE = 200;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE;
   private final Map<FluidState, VoxelShape> shapes = Maps.newIdentityHashMap();

   public FlowingFluid() {
      super();
   }

   protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> var1) {
      var1.add(FALLING);
   }

   public Vec3 getFlow(BlockGetter var1, BlockPos var2, FluidState var3) {
      double var4 = 0.0;
      double var6 = 0.0;
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();
      Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

      while(var9.hasNext()) {
         Direction var10 = (Direction)var9.next();
         var8.setWithOffset(var2, (Direction)var10);
         FluidState var11 = var1.getFluidState(var8);
         if (this.affectsFlow(var11)) {
            float var12 = var11.getOwnHeight();
            float var13 = 0.0F;
            if (var12 == 0.0F) {
               if (!var1.getBlockState(var8).blocksMotion()) {
                  BlockPos var14 = var8.below();
                  FluidState var15 = var1.getFluidState(var14);
                  if (this.affectsFlow(var15)) {
                     var12 = var15.getOwnHeight();
                     if (var12 > 0.0F) {
                        var13 = var3.getOwnHeight() - (var12 - 0.8888889F);
                     }
                  }
               }
            } else if (var12 > 0.0F) {
               var13 = var3.getOwnHeight() - var12;
            }

            if (var13 != 0.0F) {
               var4 += (double)((float)var10.getStepX() * var13);
               var6 += (double)((float)var10.getStepZ() * var13);
            }
         }
      }

      Vec3 var16 = new Vec3(var4, 0.0, var6);
      if ((Boolean)var3.getValue(FALLING)) {
         Iterator var17 = Direction.Plane.HORIZONTAL.iterator();

         Direction var18;
         do {
            if (!var17.hasNext()) {
               return var16.normalize();
            }

            var18 = (Direction)var17.next();
            var8.setWithOffset(var2, (Direction)var18);
         } while(!this.isSolidFace(var1, var8, var18) && !this.isSolidFace(var1, var8.above(), var18));

         var16 = var16.normalize().add(0.0, -6.0, 0.0);
      }

      return var16.normalize();
   }

   private boolean affectsFlow(FluidState var1) {
      return var1.isEmpty() || var1.getType().isSame(this);
   }

   protected boolean isSolidFace(BlockGetter var1, BlockPos var2, Direction var3) {
      BlockState var4 = var1.getBlockState(var2);
      FluidState var5 = var1.getFluidState(var2);
      if (var5.getType().isSame(this)) {
         return false;
      } else if (var3 == Direction.UP) {
         return true;
      } else {
         return var4.getBlock() instanceof IceBlock ? false : var4.isFaceSturdy(var1, var2, var3);
      }
   }

   protected void spread(Level var1, BlockPos var2, FluidState var3) {
      if (!var3.isEmpty()) {
         BlockState var4 = var1.getBlockState(var2);
         BlockPos var5 = var2.below();
         BlockState var6 = var1.getBlockState(var5);
         FluidState var7 = this.getNewLiquid(var1, var5, var6);
         if (this.canSpreadTo(var1, var2, var4, Direction.DOWN, var5, var6, var1.getFluidState(var5), var7.getType())) {
            this.spreadTo(var1, var5, var6, Direction.DOWN, var7);
            if (this.sourceNeighborCount(var1, var2) >= 3) {
               this.spreadToSides(var1, var2, var3, var4);
            }
         } else if (var3.isSource() || !this.isWaterHole(var1, var7.getType(), var2, var4, var5, var6)) {
            this.spreadToSides(var1, var2, var3, var4);
         }

      }
   }

   private void spreadToSides(Level var1, BlockPos var2, FluidState var3, BlockState var4) {
      int var5 = var3.getAmount() - this.getDropOff(var1);
      if ((Boolean)var3.getValue(FALLING)) {
         var5 = 7;
      }

      if (var5 > 0) {
         Map var6 = this.getSpread(var1, var2, var4);
         Iterator var7 = var6.entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry var8 = (Map.Entry)var7.next();
            Direction var9 = (Direction)var8.getKey();
            FluidState var10 = (FluidState)var8.getValue();
            BlockPos var11 = var2.relative(var9);
            BlockState var12 = var1.getBlockState(var11);
            if (this.canSpreadTo(var1, var2, var4, var9, var11, var12, var1.getFluidState(var11), var10.getType())) {
               this.spreadTo(var1, var11, var12, var9, var10);
            }
         }

      }
   }

   protected FluidState getNewLiquid(Level var1, BlockPos var2, BlockState var3) {
      int var4 = 0;
      int var5 = 0;
      Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

      while(var6.hasNext()) {
         Direction var7 = (Direction)var6.next();
         BlockPos var8 = var2.relative(var7);
         BlockState var9 = var1.getBlockState(var8);
         FluidState var10 = var9.getFluidState();
         if (var10.getType().isSame(this) && this.canPassThroughWall(var7, var1, var2, var3, var8, var9)) {
            if (var10.isSource()) {
               ++var5;
            }

            var4 = Math.max(var4, var10.getAmount());
         }
      }

      if (this.canConvertToSource(var1) && var5 >= 2) {
         BlockState var11 = var1.getBlockState(var2.below());
         FluidState var13 = var11.getFluidState();
         if (var11.isSolid() || this.isSourceBlockOfThisType(var13)) {
            return this.getSource(false);
         }
      }

      BlockPos var12 = var2.above();
      BlockState var14 = var1.getBlockState(var12);
      FluidState var15 = var14.getFluidState();
      if (!var15.isEmpty() && var15.getType().isSame(this) && this.canPassThroughWall(Direction.UP, var1, var2, var3, var12, var14)) {
         return this.getFlowing(8, true);
      } else {
         int var16 = var4 - this.getDropOff(var1);
         if (var16 <= 0) {
            return Fluids.EMPTY.defaultFluidState();
         } else {
            return this.getFlowing(var16, false);
         }
      }
   }

   private boolean canPassThroughWall(Direction var1, BlockGetter var2, BlockPos var3, BlockState var4, BlockPos var5, BlockState var6) {
      Object2ByteLinkedOpenHashMap var7;
      if (!var4.getBlock().hasDynamicShape() && !var6.getBlock().hasDynamicShape()) {
         var7 = (Object2ByteLinkedOpenHashMap)OCCLUSION_CACHE.get();
      } else {
         var7 = null;
      }

      Block.BlockStatePairKey var8;
      if (var7 != null) {
         var8 = new Block.BlockStatePairKey(var4, var6, var1);
         byte var9 = var7.getAndMoveToFirst(var8);
         if (var9 != 127) {
            return var9 != 0;
         }
      } else {
         var8 = null;
      }

      VoxelShape var12 = var4.getCollisionShape(var2, var3);
      VoxelShape var10 = var6.getCollisionShape(var2, var5);
      boolean var11 = !Shapes.mergedFaceOccludes(var12, var10, var1);
      if (var7 != null) {
         if (var7.size() == 200) {
            var7.removeLastByte();
         }

         var7.putAndMoveToFirst(var8, (byte)(var11 ? 1 : 0));
      }

      return var11;
   }

   public abstract Fluid getFlowing();

   public FluidState getFlowing(int var1, boolean var2) {
      return (FluidState)((FluidState)this.getFlowing().defaultFluidState().setValue(LEVEL, var1)).setValue(FALLING, var2);
   }

   public abstract Fluid getSource();

   public FluidState getSource(boolean var1) {
      return (FluidState)this.getSource().defaultFluidState().setValue(FALLING, var1);
   }

   protected abstract boolean canConvertToSource(Level var1);

   protected void spreadTo(LevelAccessor var1, BlockPos var2, BlockState var3, Direction var4, FluidState var5) {
      if (var3.getBlock() instanceof LiquidBlockContainer) {
         ((LiquidBlockContainer)var3.getBlock()).placeLiquid(var1, var2, var3, var5);
      } else {
         if (!var3.isAir()) {
            this.beforeDestroyingBlock(var1, var2, var3);
         }

         var1.setBlock(var2, var5.createLegacyBlock(), 3);
      }

   }

   protected abstract void beforeDestroyingBlock(LevelAccessor var1, BlockPos var2, BlockState var3);

   private static short getCacheKey(BlockPos var0, BlockPos var1) {
      int var2 = var1.getX() - var0.getX();
      int var3 = var1.getZ() - var0.getZ();
      return (short)((var2 + 128 & 255) << 8 | var3 + 128 & 255);
   }

   protected int getSlopeDistance(LevelReader var1, BlockPos var2, int var3, Direction var4, BlockState var5, BlockPos var6, Short2ObjectMap<Pair<BlockState, FluidState>> var7, Short2BooleanMap var8) {
      int var9 = 1000;
      Iterator var10 = Direction.Plane.HORIZONTAL.iterator();

      while(var10.hasNext()) {
         Direction var11 = (Direction)var10.next();
         if (var11 != var4) {
            BlockPos var12 = var2.relative(var11);
            short var13 = getCacheKey(var6, var12);
            Pair var14 = (Pair)var7.computeIfAbsent(var13, (var2x) -> {
               BlockState var3 = var1.getBlockState(var12);
               return Pair.of(var3, var3.getFluidState());
            });
            BlockState var15 = (BlockState)var14.getFirst();
            FluidState var16 = (FluidState)var14.getSecond();
            if (this.canPassThrough(var1, this.getFlowing(), var2, var5, var11, var12, var15, var16)) {
               boolean var17 = var8.computeIfAbsent(var13, (var4x) -> {
                  BlockPos var5 = var12.below();
                  BlockState var6 = var1.getBlockState(var5);
                  return this.isWaterHole(var1, this.getFlowing(), var12, var15, var5, var6);
               });
               if (var17) {
                  return var3;
               }

               if (var3 < this.getSlopeFindDistance(var1)) {
                  int var18 = this.getSlopeDistance(var1, var12, var3 + 1, var11.getOpposite(), var15, var6, var7, var8);
                  if (var18 < var9) {
                     var9 = var18;
                  }
               }
            }
         }
      }

      return var9;
   }

   private boolean isWaterHole(BlockGetter var1, Fluid var2, BlockPos var3, BlockState var4, BlockPos var5, BlockState var6) {
      if (!this.canPassThroughWall(Direction.DOWN, var1, var3, var4, var5, var6)) {
         return false;
      } else {
         return var6.getFluidState().getType().isSame(this) ? true : this.canHoldFluid(var1, var5, var6, var2);
      }
   }

   private boolean canPassThrough(BlockGetter var1, Fluid var2, BlockPos var3, BlockState var4, Direction var5, BlockPos var6, BlockState var7, FluidState var8) {
      return !this.isSourceBlockOfThisType(var8) && this.canPassThroughWall(var5, var1, var3, var4, var6, var7) && this.canHoldFluid(var1, var6, var7, var2);
   }

   private boolean isSourceBlockOfThisType(FluidState var1) {
      return var1.getType().isSame(this) && var1.isSource();
   }

   protected abstract int getSlopeFindDistance(LevelReader var1);

   private int sourceNeighborCount(LevelReader var1, BlockPos var2) {
      int var3 = 0;
      Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         Direction var5 = (Direction)var4.next();
         BlockPos var6 = var2.relative(var5);
         FluidState var7 = var1.getFluidState(var6);
         if (this.isSourceBlockOfThisType(var7)) {
            ++var3;
         }
      }

      return var3;
   }

   protected Map<Direction, FluidState> getSpread(Level var1, BlockPos var2, BlockState var3) {
      int var4 = 1000;
      EnumMap var5 = Maps.newEnumMap(Direction.class);
      Short2ObjectOpenHashMap var6 = new Short2ObjectOpenHashMap();
      Short2BooleanOpenHashMap var7 = new Short2BooleanOpenHashMap();
      Iterator var8 = Direction.Plane.HORIZONTAL.iterator();

      while(var8.hasNext()) {
         Direction var9 = (Direction)var8.next();
         BlockPos var10 = var2.relative(var9);
         short var11 = getCacheKey(var2, var10);
         Pair var12 = (Pair)var6.computeIfAbsent(var11, (var2x) -> {
            BlockState var3 = var1.getBlockState(var10);
            return Pair.of(var3, var3.getFluidState());
         });
         BlockState var13 = (BlockState)var12.getFirst();
         FluidState var14 = (FluidState)var12.getSecond();
         FluidState var15 = this.getNewLiquid(var1, var10, var13);
         if (this.canPassThrough(var1, var15.getType(), var2, var3, var9, var10, var13, var14)) {
            BlockPos var17 = var10.below();
            boolean var18 = var7.computeIfAbsent(var11, (var5x) -> {
               BlockState var6 = var1.getBlockState(var17);
               return this.isWaterHole(var1, this.getFlowing(), var10, var13, var17, var6);
            });
            int var16;
            if (var18) {
               var16 = 0;
            } else {
               var16 = this.getSlopeDistance(var1, var10, 1, var9.getOpposite(), var13, var2, var6, var7);
            }

            if (var16 < var4) {
               var5.clear();
            }

            if (var16 <= var4) {
               var5.put(var9, var15);
               var4 = var16;
            }
         }
      }

      return var5;
   }

   private boolean canHoldFluid(BlockGetter var1, BlockPos var2, BlockState var3, Fluid var4) {
      Block var5 = var3.getBlock();
      if (var5 instanceof LiquidBlockContainer var6) {
         return var6.canPlaceLiquid((Player)null, var1, var2, var3, var4);
      } else if (!(var5 instanceof DoorBlock) && !var3.is(BlockTags.SIGNS) && !var3.is(Blocks.LADDER) && !var3.is(Blocks.SUGAR_CANE) && !var3.is(Blocks.BUBBLE_COLUMN)) {
         if (!var3.is(Blocks.NETHER_PORTAL) && !var3.is(Blocks.END_PORTAL) && !var3.is(Blocks.END_GATEWAY) && !var3.is(Blocks.STRUCTURE_VOID)) {
            return !var3.blocksMotion();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean canSpreadTo(BlockGetter var1, BlockPos var2, BlockState var3, Direction var4, BlockPos var5, BlockState var6, FluidState var7, Fluid var8) {
      return var7.canBeReplacedWith(var1, var5, var8, var4) && this.canPassThroughWall(var4, var1, var2, var3, var5, var6) && this.canHoldFluid(var1, var5, var6, var8);
   }

   protected abstract int getDropOff(LevelReader var1);

   protected int getSpreadDelay(Level var1, BlockPos var2, FluidState var3, FluidState var4) {
      return this.getTickDelay(var1);
   }

   public void tick(Level var1, BlockPos var2, FluidState var3) {
      if (!var3.isSource()) {
         FluidState var4 = this.getNewLiquid(var1, var2, var1.getBlockState(var2));
         int var5 = this.getSpreadDelay(var1, var2, var3, var4);
         if (var4.isEmpty()) {
            var3 = var4;
            var1.setBlock(var2, Blocks.AIR.defaultBlockState(), 3);
         } else if (!var4.equals(var3)) {
            var3 = var4;
            BlockState var6 = var4.createLegacyBlock();
            var1.setBlock(var2, var6, 2);
            var1.scheduleTick(var2, var4.getType(), var5);
            var1.updateNeighborsAt(var2, var6.getBlock());
         }
      }

      this.spread(var1, var2, var3);
   }

   protected static int getLegacyLevel(FluidState var0) {
      return var0.isSource() ? 0 : 8 - Math.min(var0.getAmount(), 8) + ((Boolean)var0.getValue(FALLING) ? 8 : 0);
   }

   private static boolean hasSameAbove(FluidState var0, BlockGetter var1, BlockPos var2) {
      return var0.getType().isSame(var1.getFluidState(var2.above()).getType());
   }

   public float getHeight(FluidState var1, BlockGetter var2, BlockPos var3) {
      return hasSameAbove(var1, var2, var3) ? 1.0F : var1.getOwnHeight();
   }

   public float getOwnHeight(FluidState var1) {
      return (float)var1.getAmount() / 9.0F;
   }

   public abstract int getAmount(FluidState var1);

   public VoxelShape getShape(FluidState var1, BlockGetter var2, BlockPos var3) {
      return var1.getAmount() == 9 && hasSameAbove(var1, var2, var3) ? Shapes.block() : (VoxelShape)this.shapes.computeIfAbsent(var1, (var2x) -> {
         return Shapes.box(0.0, 0.0, 0.0, 1.0, (double)var2x.getHeight(var2, var3), 1.0);
      });
   }

   static {
      FALLING = BlockStateProperties.FALLING;
      LEVEL = BlockStateProperties.LEVEL_FLOWING;
      OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
         Object2ByteLinkedOpenHashMap var0 = new Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>(200) {
            protected void rehash(int var1) {
            }
         };
         var0.defaultReturnValue((byte)127);
         return var0;
      });
   }
}
