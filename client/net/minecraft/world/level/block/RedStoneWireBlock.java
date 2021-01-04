package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedStoneWireBlock extends Block {
   public static final EnumProperty<RedstoneSide> NORTH;
   public static final EnumProperty<RedstoneSide> EAST;
   public static final EnumProperty<RedstoneSide> SOUTH;
   public static final EnumProperty<RedstoneSide> WEST;
   public static final IntegerProperty POWER;
   public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION;
   protected static final VoxelShape[] SHAPE_BY_INDEX;
   private boolean shouldSignal = true;
   private final Set<BlockPos> toUpdate = Sets.newHashSet();

   public RedStoneWireBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, RedstoneSide.NONE)).setValue(EAST, RedstoneSide.NONE)).setValue(SOUTH, RedstoneSide.NONE)).setValue(WEST, RedstoneSide.NONE)).setValue(POWER, 0));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_INDEX[getAABBIndex(var1)];
   }

   private static int getAABBIndex(BlockState var0) {
      int var1 = 0;
      boolean var2 = var0.getValue(NORTH) != RedstoneSide.NONE;
      boolean var3 = var0.getValue(EAST) != RedstoneSide.NONE;
      boolean var4 = var0.getValue(SOUTH) != RedstoneSide.NONE;
      boolean var5 = var0.getValue(WEST) != RedstoneSide.NONE;
      if (var2 || var4 && !var2 && !var3 && !var5) {
         var1 |= 1 << Direction.NORTH.get2DDataValue();
      }

      if (var3 || var5 && !var2 && !var3 && !var4) {
         var1 |= 1 << Direction.EAST.get2DDataValue();
      }

      if (var4 || var2 && !var3 && !var4 && !var5) {
         var1 |= 1 << Direction.SOUTH.get2DDataValue();
      }

      if (var5 || var3 && !var2 && !var4 && !var5) {
         var1 |= 1 << Direction.WEST.get2DDataValue();
      }

      return var1;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(WEST, this.getConnectingSide(var2, var3, Direction.WEST))).setValue(EAST, this.getConnectingSide(var2, var3, Direction.EAST))).setValue(NORTH, this.getConnectingSide(var2, var3, Direction.NORTH))).setValue(SOUTH, this.getConnectingSide(var2, var3, Direction.SOUTH));
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.DOWN) {
         return var1;
      } else {
         return var2 == Direction.UP ? (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(WEST, this.getConnectingSide(var4, var5, Direction.WEST))).setValue(EAST, this.getConnectingSide(var4, var5, Direction.EAST))).setValue(NORTH, this.getConnectingSide(var4, var5, Direction.NORTH))).setValue(SOUTH, this.getConnectingSide(var4, var5, Direction.SOUTH)) : (BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var2), this.getConnectingSide(var4, var5, var2));
      }
   }

   public void updateIndirectNeighbourShapes(BlockState var1, LevelAccessor var2, BlockPos var3, int var4) {
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var6 = null;

      try {
         Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

         while(var7.hasNext()) {
            Direction var8 = (Direction)var7.next();
            RedstoneSide var9 = (RedstoneSide)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(var8));
            if (var9 != RedstoneSide.NONE && var2.getBlockState(var5.set((Vec3i)var3).move(var8)).getBlock() != this) {
               var5.move(Direction.DOWN);
               BlockState var10 = var2.getBlockState(var5);
               if (var10.getBlock() != Blocks.OBSERVER) {
                  BlockPos var11 = var5.relative(var8.getOpposite());
                  BlockState var12 = var10.updateShape(var8.getOpposite(), var2.getBlockState(var11), var2, var5, var11);
                  updateOrDestroy(var10, var12, var2, var5, var4);
               }

               var5.set((Vec3i)var3).move(var8).move(Direction.UP);
               BlockState var23 = var2.getBlockState(var5);
               if (var23.getBlock() != Blocks.OBSERVER) {
                  BlockPos var24 = var5.relative(var8.getOpposite());
                  BlockState var13 = var23.updateShape(var8.getOpposite(), var2.getBlockState(var24), var2, var5, var24);
                  updateOrDestroy(var23, var13, var2, var5, var4);
               }
            }
         }
      } catch (Throwable var21) {
         var6 = var21;
         throw var21;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var20) {
                  var6.addSuppressed(var20);
               }
            } else {
               var5.close();
            }
         }

      }

   }

   private RedstoneSide getConnectingSide(BlockGetter var1, BlockPos var2, Direction var3) {
      BlockPos var4 = var2.relative(var3);
      BlockState var5 = var1.getBlockState(var4);
      BlockPos var6 = var2.above();
      BlockState var7 = var1.getBlockState(var6);
      if (!var7.isRedstoneConductor(var1, var6)) {
         boolean var8 = var5.isFaceSturdy(var1, var4, Direction.UP) || var5.getBlock() == Blocks.HOPPER;
         if (var8 && shouldConnectTo(var1.getBlockState(var4.above()))) {
            if (var5.isCollisionShapeFullBlock(var1, var4)) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !shouldConnectTo(var5, var3) && (var5.isRedstoneConductor(var1, var4) || !shouldConnectTo(var1.getBlockState(var4.below()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      return var5.isFaceSturdy(var2, var4, Direction.UP) || var5.getBlock() == Blocks.HOPPER;
   }

   private BlockState updatePowerStrength(Level var1, BlockPos var2, BlockState var3) {
      var3 = this.updatePowerStrengthImpl(var1, var2, var3);
      ArrayList var4 = Lists.newArrayList(this.toUpdate);
      this.toUpdate.clear();
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         BlockPos var6 = (BlockPos)var5.next();
         var1.updateNeighborsAt(var6, this);
      }

      return var3;
   }

   private BlockState updatePowerStrengthImpl(Level var1, BlockPos var2, BlockState var3) {
      BlockState var4 = var3;
      int var5 = (Integer)var3.getValue(POWER);
      this.shouldSignal = false;
      int var6 = var1.getBestNeighborSignal(var2);
      this.shouldSignal = true;
      int var7 = 0;
      if (var6 < 15) {
         Iterator var8 = Direction.Plane.HORIZONTAL.iterator();

         label43:
         while(true) {
            while(true) {
               if (!var8.hasNext()) {
                  break label43;
               }

               Direction var9 = (Direction)var8.next();
               BlockPos var10 = var2.relative(var9);
               BlockState var11 = var1.getBlockState(var10);
               var7 = this.checkTarget(var7, var11);
               BlockPos var12 = var2.above();
               if (var11.isRedstoneConductor(var1, var10) && !var1.getBlockState(var12).isRedstoneConductor(var1, var12)) {
                  var7 = this.checkTarget(var7, var1.getBlockState(var10.above()));
               } else if (!var11.isRedstoneConductor(var1, var10)) {
                  var7 = this.checkTarget(var7, var1.getBlockState(var10.below()));
               }
            }
         }
      }

      int var13 = var7 - 1;
      if (var6 > var13) {
         var13 = var6;
      }

      if (var5 != var13) {
         var3 = (BlockState)var3.setValue(POWER, var13);
         if (var1.getBlockState(var2) == var4) {
            var1.setBlock(var2, var3, 2);
         }

         this.toUpdate.add(var2);
         Direction[] var14 = Direction.values();
         int var15 = var14.length;

         for(int var16 = 0; var16 < var15; ++var16) {
            Direction var17 = var14[var16];
            this.toUpdate.add(var2.relative(var17));
         }
      }

      return var3;
   }

   private void checkCornerChangeAt(Level var1, BlockPos var2) {
      if (var1.getBlockState(var2).getBlock() == this) {
         var1.updateNeighborsAt(var2, this);
         Direction[] var3 = Direction.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Direction var6 = var3[var5];
            var1.updateNeighborsAt(var2.relative(var6), this);
         }

      }
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var4.getBlock() != var1.getBlock() && !var2.isClientSide) {
         this.updatePowerStrength(var2, var3, var1);
         Iterator var6 = Direction.Plane.VERTICAL.iterator();

         Direction var7;
         while(var6.hasNext()) {
            var7 = (Direction)var6.next();
            var2.updateNeighborsAt(var3.relative(var7), this);
         }

         var6 = Direction.Plane.HORIZONTAL.iterator();

         while(var6.hasNext()) {
            var7 = (Direction)var6.next();
            this.checkCornerChangeAt(var2, var3.relative(var7));
         }

         var6 = Direction.Plane.HORIZONTAL.iterator();

         while(var6.hasNext()) {
            var7 = (Direction)var6.next();
            BlockPos var8 = var3.relative(var7);
            if (var2.getBlockState(var8).isRedstoneConductor(var2, var8)) {
               this.checkCornerChangeAt(var2, var8.above());
            } else {
               this.checkCornerChangeAt(var2, var8.below());
            }
         }

      }
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && var1.getBlock() != var4.getBlock()) {
         super.onRemove(var1, var2, var3, var4, var5);
         if (!var2.isClientSide) {
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Direction var9 = var6[var8];
               var2.updateNeighborsAt(var3.relative(var9), this);
            }

            this.updatePowerStrength(var2, var3, var1);
            Iterator var10 = Direction.Plane.HORIZONTAL.iterator();

            Direction var11;
            while(var10.hasNext()) {
               var11 = (Direction)var10.next();
               this.checkCornerChangeAt(var2, var3.relative(var11));
            }

            var10 = Direction.Plane.HORIZONTAL.iterator();

            while(var10.hasNext()) {
               var11 = (Direction)var10.next();
               BlockPos var12 = var3.relative(var11);
               if (var2.getBlockState(var12).isRedstoneConductor(var2, var12)) {
                  this.checkCornerChangeAt(var2, var12.above());
               } else {
                  this.checkCornerChangeAt(var2, var12.below());
               }
            }

         }
      }
   }

   private int checkTarget(int var1, BlockState var2) {
      if (var2.getBlock() != this) {
         return var1;
      } else {
         int var3 = (Integer)var2.getValue(POWER);
         return var3 > var1 ? var3 : var1;
      }
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         if (var1.canSurvive(var2, var3)) {
            this.updatePowerStrength(var2, var3, var1);
         } else {
            dropResources(var1, var2, var3);
            var2.removeBlock(var3, false);
         }

      }
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return !this.shouldSignal ? 0 : var1.getSignal(var2, var3, var4);
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (!this.shouldSignal) {
         return 0;
      } else {
         int var5 = (Integer)var1.getValue(POWER);
         if (var5 == 0) {
            return 0;
         } else if (var4 == Direction.UP) {
            return var5;
         } else {
            EnumSet var6 = EnumSet.noneOf(Direction.class);
            Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               Direction var8 = (Direction)var7.next();
               if (this.isPowerSourceAt(var2, var3, var8)) {
                  var6.add(var8);
               }
            }

            if (var4.getAxis().isHorizontal() && var6.isEmpty()) {
               return var5;
            } else if (var6.contains(var4) && !var6.contains(var4.getCounterClockWise()) && !var6.contains(var4.getClockWise())) {
               return var5;
            } else {
               return 0;
            }
         }
      }
   }

   private boolean isPowerSourceAt(BlockGetter var1, BlockPos var2, Direction var3) {
      BlockPos var4 = var2.relative(var3);
      BlockState var5 = var1.getBlockState(var4);
      boolean var6 = var5.isRedstoneConductor(var1, var4);
      BlockPos var7 = var2.above();
      boolean var8 = var1.getBlockState(var7).isRedstoneConductor(var1, var7);
      if (!var8 && var6 && shouldConnectTo(var1, var4.above())) {
         return true;
      } else if (shouldConnectTo(var5, var3)) {
         return true;
      } else if (var5.getBlock() == Blocks.REPEATER && (Boolean)var5.getValue(DiodeBlock.POWERED) && var5.getValue(DiodeBlock.FACING) == var3) {
         return true;
      } else {
         return !var6 && shouldConnectTo(var1, var4.below());
      }
   }

   protected static boolean shouldConnectTo(BlockGetter var0, BlockPos var1) {
      return shouldConnectTo(var0.getBlockState(var1));
   }

   protected static boolean shouldConnectTo(BlockState var0) {
      return shouldConnectTo((BlockState)var0, (Direction)null);
   }

   protected static boolean shouldConnectTo(BlockState var0, @Nullable Direction var1) {
      Block var2 = var0.getBlock();
      if (var2 == Blocks.REDSTONE_WIRE) {
         return true;
      } else if (var0.getBlock() == Blocks.REPEATER) {
         Direction var3 = (Direction)var0.getValue(RepeaterBlock.FACING);
         return var3 == var1 || var3.getOpposite() == var1;
      } else if (Blocks.OBSERVER == var0.getBlock()) {
         return var1 == var0.getValue(ObserverBlock.FACING);
      } else {
         return var0.isSignalSource() && var1 != null;
      }
   }

   public boolean isSignalSource(BlockState var1) {
      return this.shouldSignal;
   }

   public static int getColorForData(int var0) {
      float var1 = (float)var0 / 15.0F;
      float var2 = var1 * 0.6F + 0.4F;
      if (var0 == 0) {
         var2 = 0.3F;
      }

      float var3 = var1 * var1 * 0.7F - 0.5F;
      float var4 = var1 * var1 * 0.6F - 0.7F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var4 < 0.0F) {
         var4 = 0.0F;
      }

      int var5 = Mth.clamp((int)(var2 * 255.0F), 0, 255);
      int var6 = Mth.clamp((int)(var3 * 255.0F), 0, 255);
      int var7 = Mth.clamp((int)(var4 * 255.0F), 0, 255);
      return -16777216 | var5 << 16 | var6 << 8 | var7;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      int var5 = (Integer)var1.getValue(POWER);
      if (var5 != 0) {
         double var6 = (double)var3.getX() + 0.5D + ((double)var4.nextFloat() - 0.5D) * 0.2D;
         double var8 = (double)((float)var3.getY() + 0.0625F);
         double var10 = (double)var3.getZ() + 0.5D + ((double)var4.nextFloat() - 0.5D) * 0.2D;
         float var12 = (float)var5 / 15.0F;
         float var13 = var12 * 0.6F + 0.4F;
         float var14 = Math.max(0.0F, var12 * var12 * 0.7F - 0.5F);
         float var15 = Math.max(0.0F, var12 * var12 * 0.6F - 0.7F);
         var2.addParticle(new DustParticleOptions(var13, var14, var15, 1.0F), var6, var8, var10, 0.0D, 0.0D, 0.0D);
      }
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(SOUTH))).setValue(EAST, var1.getValue(WEST))).setValue(SOUTH, var1.getValue(NORTH))).setValue(WEST, var1.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(EAST))).setValue(EAST, var1.getValue(SOUTH))).setValue(SOUTH, var1.getValue(WEST))).setValue(WEST, var1.getValue(NORTH));
      case CLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(WEST))).setValue(EAST, var1.getValue(NORTH))).setValue(SOUTH, var1.getValue(EAST))).setValue(WEST, var1.getValue(SOUTH));
      default:
         return var1;
      }
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      switch(var2) {
      case LEFT_RIGHT:
         return (BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(SOUTH))).setValue(SOUTH, var1.getValue(NORTH));
      case FRONT_BACK:
         return (BlockState)((BlockState)var1.setValue(EAST, var1.getValue(WEST))).setValue(WEST, var1.getValue(EAST));
      default:
         return super.mirror(var1, var2);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, SOUTH, WEST, POWER);
   }

   static {
      NORTH = BlockStateProperties.NORTH_REDSTONE;
      EAST = BlockStateProperties.EAST_REDSTONE;
      SOUTH = BlockStateProperties.SOUTH_REDSTONE;
      WEST = BlockStateProperties.WEST_REDSTONE;
      POWER = BlockStateProperties.POWER;
      PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
      SHAPE_BY_INDEX = new VoxelShape[]{Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.box(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
   }
}
