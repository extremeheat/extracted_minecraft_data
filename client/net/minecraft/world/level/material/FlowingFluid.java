package net.minecraft.world.level.material;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<BlockStatePairKey>> OCCLUSION_CACHE;
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

      for(Direction var10 : Direction.Plane.HORIZONTAL) {
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
         for(Direction var18 : Direction.Plane.HORIZONTAL) {
            var8.setWithOffset(var2, (Direction)var18);
            if (this.isSolidFace(var1, var8, var18) || this.isSolidFace(var1, var8.above(), var18)) {
               var16 = var16.normalize().add(0.0, -6.0, 0.0);
               break;
            }
         }
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

   protected void spread(ServerLevel var1, BlockPos var2, BlockState var3, FluidState var4) {
      if (!var4.isEmpty()) {
         BlockPos var5 = var2.below();
         BlockState var6 = var1.getBlockState(var5);
         FluidState var7 = var6.getFluidState();
         if (this.canMaybePassThrough(var1, var2, var3, Direction.DOWN, var5, var6, var7)) {
            FluidState var8 = this.getNewLiquid(var1, var5, var6);
            Fluid var9 = var8.getType();
            if (var7.canBeReplacedWith(var1, var5, var9, Direction.DOWN) && canHoldSpecificFluid(var1, var5, var6, var9)) {
               this.spreadTo(var1, var5, var6, Direction.DOWN, var8);
               if (this.sourceNeighborCount(var1, var2) >= 3) {
                  this.spreadToSides(var1, var2, var4, var3);
               }

               return;
            }
         }

         if (var4.isSource() || !this.isWaterHole(var1, var2, var3, var5, var6)) {
            this.spreadToSides(var1, var2, var4, var3);
         }

      }
   }

   private void spreadToSides(ServerLevel var1, BlockPos var2, FluidState var3, BlockState var4) {
      int var5 = var3.getAmount() - this.getDropOff(var1);
      if ((Boolean)var3.getValue(FALLING)) {
         var5 = 7;
      }

      if (var5 > 0) {
         Map var6 = this.getSpread(var1, var2, var4);

         for(Map.Entry var8 : var6.entrySet()) {
            Direction var9 = (Direction)var8.getKey();
            FluidState var10 = (FluidState)var8.getValue();
            BlockPos var11 = var2.relative(var9);
            this.spreadTo(var1, var11, var1.getBlockState(var11), var9, var10);
         }

      }
   }

   protected FluidState getNewLiquid(ServerLevel var1, BlockPos var2, BlockState var3) {
      int var4 = 0;
      int var5 = 0;
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(Direction var8 : Direction.Plane.HORIZONTAL) {
         BlockPos.MutableBlockPos var9 = var6.setWithOffset(var2, (Direction)var8);
         BlockState var10 = var1.getBlockState(var9);
         FluidState var11 = var10.getFluidState();
         if (var11.getType().isSame(this) && canPassThroughWall(var8, var1, var2, var3, var9, var10)) {
            if (var11.isSource()) {
               ++var5;
            }

            var4 = Math.max(var4, var11.getAmount());
         }
      }

      if (var5 >= 2 && this.canConvertToSource(var1)) {
         BlockState var12 = var1.getBlockState(var6.setWithOffset(var2, (Direction)Direction.DOWN));
         FluidState var14 = var12.getFluidState();
         if (var12.isSolid() || this.isSourceBlockOfThisType(var14)) {
            return this.getSource(false);
         }
      }

      BlockPos.MutableBlockPos var13 = var6.setWithOffset(var2, (Direction)Direction.UP);
      BlockState var15 = var1.getBlockState(var13);
      FluidState var16 = var15.getFluidState();
      if (!var16.isEmpty() && var16.getType().isSame(this) && canPassThroughWall(Direction.UP, var1, var2, var3, var13, var15)) {
         return this.getFlowing(8, true);
      } else {
         int var17 = var4 - this.getDropOff(var1);
         if (var17 <= 0) {
            return Fluids.EMPTY.defaultFluidState();
         } else {
            return this.getFlowing(var17, false);
         }
      }
   }

   private static boolean canPassThroughWall(Direction var0, BlockGetter var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5) {
      VoxelShape var6 = var5.getCollisionShape(var1, var4);
      if (var6 == Shapes.block()) {
         return false;
      } else {
         VoxelShape var7 = var3.getCollisionShape(var1, var2);
         if (var7 == Shapes.block()) {
            return false;
         } else if (var7 == Shapes.empty() && var6 == Shapes.empty()) {
            return true;
         } else {
            Object2ByteLinkedOpenHashMap var8;
            if (!var3.getBlock().hasDynamicShape() && !var5.getBlock().hasDynamicShape()) {
               var8 = (Object2ByteLinkedOpenHashMap)OCCLUSION_CACHE.get();
            } else {
               var8 = null;
            }

            BlockStatePairKey var9;
            if (var8 != null) {
               var9 = new BlockStatePairKey(var3, var5, var0);
               byte var10 = var8.getAndMoveToFirst(var9);
               if (var10 != 127) {
                  return var10 != 0;
               }
            } else {
               var9 = null;
            }

            boolean var11 = !Shapes.mergedFaceOccludes(var7, var6, var0);
            if (var8 != null) {
               if (var8.size() == 200) {
                  var8.removeLastByte();
               }

               var8.putAndMoveToFirst(var9, (byte)(var11 ? 1 : 0));
            }

            return var11;
         }
      }
   }

   public abstract Fluid getFlowing();

   public FluidState getFlowing(int var1, boolean var2) {
      return (FluidState)((FluidState)this.getFlowing().defaultFluidState().setValue(LEVEL, var1)).setValue(FALLING, var2);
   }

   public abstract Fluid getSource();

   public FluidState getSource(boolean var1) {
      return (FluidState)this.getSource().defaultFluidState().setValue(FALLING, var1);
   }

   protected abstract boolean canConvertToSource(ServerLevel var1);

   protected void spreadTo(LevelAccessor var1, BlockPos var2, BlockState var3, Direction var4, FluidState var5) {
      Block var7 = var3.getBlock();
      if (var7 instanceof LiquidBlockContainer var6) {
         var6.placeLiquid(var1, var2, var3, var5);
      } else {
         if (!var3.isAir()) {
            this.beforeDestroyingBlock(var1, var2, var3);
         }

         var1.setBlock(var2, var5.createLegacyBlock(), 3);
      }

   }

   protected abstract void beforeDestroyingBlock(LevelAccessor var1, BlockPos var2, BlockState var3);

   protected int getSlopeDistance(LevelReader var1, BlockPos var2, int var3, Direction var4, BlockState var5, SpreadContext var6) {
      int var7 = 1000;

      for(Direction var9 : Direction.Plane.HORIZONTAL) {
         if (var9 != var4) {
            BlockPos var10 = var2.relative(var9);
            BlockState var11 = var6.getBlockState(var10);
            FluidState var12 = var11.getFluidState();
            if (this.canPassThrough(var1, this.getFlowing(), var2, var5, var9, var10, var11, var12)) {
               if (var6.isHole(var10)) {
                  return var3;
               }

               if (var3 < this.getSlopeFindDistance(var1)) {
                  int var13 = this.getSlopeDistance(var1, var10, var3 + 1, var9.getOpposite(), var11, var6);
                  if (var13 < var7) {
                     var7 = var13;
                  }
               }
            }
         }
      }

      return var7;
   }

   boolean isWaterHole(BlockGetter var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5) {
      if (!canPassThroughWall(Direction.DOWN, var1, var2, var3, var4, var5)) {
         return false;
      } else {
         return var5.getFluidState().getType().isSame(this) ? true : canHoldFluid(var1, var4, var5, this.getFlowing());
      }
   }

   private boolean canPassThrough(BlockGetter var1, Fluid var2, BlockPos var3, BlockState var4, Direction var5, BlockPos var6, BlockState var7, FluidState var8) {
      return this.canMaybePassThrough(var1, var3, var4, var5, var6, var7, var8) && canHoldSpecificFluid(var1, var6, var7, var2);
   }

   private boolean canMaybePassThrough(BlockGetter var1, BlockPos var2, BlockState var3, Direction var4, BlockPos var5, BlockState var6, FluidState var7) {
      return !this.isSourceBlockOfThisType(var7) && canHoldAnyFluid(var6) && canPassThroughWall(var4, var1, var2, var3, var5, var6);
   }

   private boolean isSourceBlockOfThisType(FluidState var1) {
      return var1.getType().isSame(this) && var1.isSource();
   }

   protected abstract int getSlopeFindDistance(LevelReader var1);

   private int sourceNeighborCount(LevelReader var1, BlockPos var2) {
      int var3 = 0;

      for(Direction var5 : Direction.Plane.HORIZONTAL) {
         BlockPos var6 = var2.relative(var5);
         FluidState var7 = var1.getFluidState(var6);
         if (this.isSourceBlockOfThisType(var7)) {
            ++var3;
         }
      }

      return var3;
   }

   protected Map<Direction, FluidState> getSpread(ServerLevel var1, BlockPos var2, BlockState var3) {
      int var4 = 1000;
      EnumMap var5 = Maps.newEnumMap(Direction.class);
      SpreadContext var6 = null;

      for(Direction var8 : Direction.Plane.HORIZONTAL) {
         BlockPos var9 = var2.relative(var8);
         BlockState var10 = var1.getBlockState(var9);
         FluidState var11 = var10.getFluidState();
         if (this.canMaybePassThrough(var1, var2, var3, var8, var9, var10, var11)) {
            FluidState var12 = this.getNewLiquid(var1, var9, var10);
            if (canHoldSpecificFluid(var1, var9, var10, var12.getType())) {
               if (var6 == null) {
                  var6 = new SpreadContext(var1, var2);
               }

               int var13;
               if (var6.isHole(var9)) {
                  var13 = 0;
               } else {
                  var13 = this.getSlopeDistance(var1, var9, 1, var8.getOpposite(), var10, var6);
               }

               if (var13 < var4) {
                  var5.clear();
               }

               if (var13 <= var4) {
                  if (var11.canBeReplacedWith(var1, var9, var12.getType(), var8)) {
                     var5.put(var8, var12);
                  }

                  var4 = var13;
               }
            }
         }
      }

      return var5;
   }

   private static boolean canHoldAnyFluid(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 instanceof LiquidBlockContainer) {
         return true;
      } else if (var0.blocksMotion()) {
         return false;
      } else {
         return !(var1 instanceof DoorBlock) && !var0.is(BlockTags.SIGNS) && !var0.is(Blocks.LADDER) && !var0.is(Blocks.SUGAR_CANE) && !var0.is(Blocks.BUBBLE_COLUMN) && !var0.is(Blocks.NETHER_PORTAL) && !var0.is(Blocks.END_PORTAL) && !var0.is(Blocks.END_GATEWAY) && !var0.is(Blocks.STRUCTURE_VOID);
      }
   }

   private static boolean canHoldFluid(BlockGetter var0, BlockPos var1, BlockState var2, Fluid var3) {
      return canHoldAnyFluid(var2) && canHoldSpecificFluid(var0, var1, var2, var3);
   }

   private static boolean canHoldSpecificFluid(BlockGetter var0, BlockPos var1, BlockState var2, Fluid var3) {
      Block var4 = var2.getBlock();
      if (var4 instanceof LiquidBlockContainer var5) {
         return var5.canPlaceLiquid((Player)null, var0, var1, var2, var3);
      } else {
         return true;
      }
   }

   protected abstract int getDropOff(LevelReader var1);

   protected int getSpreadDelay(Level var1, BlockPos var2, FluidState var3, FluidState var4) {
      return this.getTickDelay(var1);
   }

   public void tick(ServerLevel var1, BlockPos var2, BlockState var3, FluidState var4) {
      if (!var4.isSource()) {
         FluidState var5 = this.getNewLiquid(var1, var2, var1.getBlockState(var2));
         int var6 = this.getSpreadDelay(var1, var2, var4, var5);
         if (var5.isEmpty()) {
            var4 = var5;
            var3 = Blocks.AIR.defaultBlockState();
            var1.setBlock(var2, var3, 3);
         } else if (!var5.equals(var4)) {
            var4 = var5;
            var3 = var5.createLegacyBlock();
            var1.setBlock(var2, var3, 3);
            var1.scheduleTick(var2, var5.getType(), var6);
         }
      }

      this.spread(var1, var2, var3, var4);
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
      return var1.getAmount() == 9 && hasSameAbove(var1, var2, var3) ? Shapes.block() : (VoxelShape)this.shapes.computeIfAbsent(var1, (var2x) -> Shapes.box(0.0, 0.0, 0.0, 1.0, (double)var2x.getHeight(var2, var3), 1.0));
   }

   static {
      FALLING = BlockStateProperties.FALLING;
      LEVEL = BlockStateProperties.LEVEL_FLOWING;
      OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
         Object2ByteLinkedOpenHashMap var0 = new Object2ByteLinkedOpenHashMap<BlockStatePairKey>(200) {
            protected void rehash(int var1) {
            }
         };
         var0.defaultReturnValue((byte)127);
         return var0;
      });
   }

   static record BlockStatePairKey(BlockState first, BlockState second, Direction direction) {
      BlockStatePairKey(BlockState var1, BlockState var2, Direction var3) {
         super();
         this.first = var1;
         this.second = var2;
         this.direction = var3;
      }

      public boolean equals(Object var1) {
         boolean var10000;
         if (var1 instanceof BlockStatePairKey var2) {
            if (this.first == var2.first && this.second == var2.second && this.direction == var2.direction) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }

      public int hashCode() {
         int var1 = System.identityHashCode(this.first);
         var1 = 31 * var1 + System.identityHashCode(this.second);
         var1 = 31 * var1 + this.direction.hashCode();
         return var1;
      }
   }

   protected class SpreadContext {
      private final BlockGetter level;
      private final BlockPos origin;
      private final Short2ObjectMap<BlockState> stateCache = new Short2ObjectOpenHashMap();
      private final Short2BooleanMap holeCache = new Short2BooleanOpenHashMap();

      SpreadContext(final BlockGetter var2, final BlockPos var3) {
         super();
         this.level = var2;
         this.origin = var3;
      }

      public BlockState getBlockState(BlockPos var1) {
         return this.getBlockState(var1, this.getCacheKey(var1));
      }

      private BlockState getBlockState(BlockPos var1, short var2) {
         return (BlockState)this.stateCache.computeIfAbsent(var2, (var2x) -> this.level.getBlockState(var1));
      }

      public boolean isHole(BlockPos var1) {
         return this.holeCache.computeIfAbsent(this.getCacheKey(var1), (var2) -> {
            BlockState var3 = this.getBlockState(var1, var2);
            BlockPos var4 = var1.below();
            BlockState var5 = this.level.getBlockState(var4);
            return FlowingFluid.this.isWaterHole(this.level, var1, var3, var4, var5);
         });
      }

      private short getCacheKey(BlockPos var1) {
         int var2 = var1.getX() - this.origin.getX();
         int var3 = var1.getZ() - this.origin.getZ();
         return (short)((var2 + 128 & 255) << 8 | var3 + 128 & 255);
      }
   }
}
