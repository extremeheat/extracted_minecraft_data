package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.redstone.DefaultRedstoneWireEvaluator;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.ExperimentalRedstoneWireEvaluator;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.redstone.RedstoneWireEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedStoneWireBlock extends Block {
   public static final MapCodec<RedStoneWireBlock> CODEC = simpleCodec(RedStoneWireBlock::new);
   public static final EnumProperty<RedstoneSide> NORTH;
   public static final EnumProperty<RedstoneSide> EAST;
   public static final EnumProperty<RedstoneSide> SOUTH;
   public static final EnumProperty<RedstoneSide> WEST;
   public static final IntegerProperty POWER;
   public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION;
   protected static final int H = 1;
   protected static final int W = 3;
   protected static final int E = 13;
   protected static final int N = 3;
   protected static final int S = 13;
   private static final VoxelShape SHAPE_DOT;
   private static final Map<Direction, VoxelShape> SHAPES_FLOOR;
   private static final Map<Direction, VoxelShape> SHAPES_UP;
   private static final Map<BlockState, VoxelShape> SHAPES_CACHE;
   private static final int[] COLORS;
   private static final float PARTICLE_DENSITY = 0.2F;
   private final BlockState crossState;
   private final RedstoneWireEvaluator evaluator = new DefaultRedstoneWireEvaluator(this);
   private boolean shouldSignal = true;

   public MapCodec<RedStoneWireBlock> codec() {
      return CODEC;
   }

   public RedStoneWireBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, RedstoneSide.NONE)).setValue(EAST, RedstoneSide.NONE)).setValue(SOUTH, RedstoneSide.NONE)).setValue(WEST, RedstoneSide.NONE)).setValue(POWER, 0));
      this.crossState = (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE)).setValue(EAST, RedstoneSide.SIDE)).setValue(SOUTH, RedstoneSide.SIDE)).setValue(WEST, RedstoneSide.SIDE);
      UnmodifiableIterator var2 = this.getStateDefinition().getPossibleStates().iterator();

      while(var2.hasNext()) {
         BlockState var3 = (BlockState)var2.next();
         if ((Integer)var3.getValue(POWER) == 0) {
            SHAPES_CACHE.put(var3, this.calculateShape(var3));
         }
      }

   }

   private VoxelShape calculateShape(BlockState var1) {
      VoxelShape var2 = SHAPE_DOT;

      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         RedstoneSide var5 = (RedstoneSide)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(var4));
         if (var5 == RedstoneSide.SIDE) {
            var2 = Shapes.or(var2, (VoxelShape)SHAPES_FLOOR.get(var4));
         } else if (var5 == RedstoneSide.UP) {
            var2 = Shapes.or(var2, (VoxelShape)SHAPES_UP.get(var4));
         }
      }

      return var2;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)SHAPES_CACHE.get(var1.setValue(POWER, 0));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.getConnectionState(var1.getLevel(), this.crossState, var1.getClickedPos());
   }

   private BlockState getConnectionState(BlockGetter var1, BlockState var2, BlockPos var3) {
      boolean var4 = isDot(var2);
      var2 = this.getMissingConnections(var1, (BlockState)this.defaultBlockState().setValue(POWER, (Integer)var2.getValue(POWER)), var3);
      if (var4 && isDot(var2)) {
         return var2;
      } else {
         boolean var5 = ((RedstoneSide)var2.getValue(NORTH)).isConnected();
         boolean var6 = ((RedstoneSide)var2.getValue(SOUTH)).isConnected();
         boolean var7 = ((RedstoneSide)var2.getValue(EAST)).isConnected();
         boolean var8 = ((RedstoneSide)var2.getValue(WEST)).isConnected();
         boolean var9 = !var5 && !var6;
         boolean var10 = !var7 && !var8;
         if (!var8 && var9) {
            var2 = (BlockState)var2.setValue(WEST, RedstoneSide.SIDE);
         }

         if (!var7 && var9) {
            var2 = (BlockState)var2.setValue(EAST, RedstoneSide.SIDE);
         }

         if (!var5 && var10) {
            var2 = (BlockState)var2.setValue(NORTH, RedstoneSide.SIDE);
         }

         if (!var6 && var10) {
            var2 = (BlockState)var2.setValue(SOUTH, RedstoneSide.SIDE);
         }

         return var2;
      }
   }

   private BlockState getMissingConnections(BlockGetter var1, BlockState var2, BlockPos var3) {
      boolean var4 = !var1.getBlockState(var3.above()).isRedstoneConductor(var1, var3);

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         if (!((RedstoneSide)var2.getValue((Property)PROPERTY_BY_DIRECTION.get(var6))).isConnected()) {
            RedstoneSide var7 = this.getConnectingSide(var1, var3, var6, var4);
            var2 = (BlockState)var2.setValue((Property)PROPERTY_BY_DIRECTION.get(var6), var7);
         }
      }

      return var2;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if (var5 == Direction.DOWN) {
         return !this.canSurviveOn(var2, var6, var7) ? Blocks.AIR.defaultBlockState() : var1;
      } else if (var5 == Direction.UP) {
         return this.getConnectionState(var2, var1, var4);
      } else {
         RedstoneSide var9 = this.getConnectingSide(var2, var4, var5);
         return var9.isConnected() == ((RedstoneSide)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(var5))).isConnected() && !isCross(var1) ? (BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var5), var9) : this.getConnectionState(var2, (BlockState)((BlockState)this.crossState.setValue(POWER, (Integer)var1.getValue(POWER))).setValue((Property)PROPERTY_BY_DIRECTION.get(var5), var9), var4);
      }
   }

   private static boolean isCross(BlockState var0) {
      return ((RedstoneSide)var0.getValue(NORTH)).isConnected() && ((RedstoneSide)var0.getValue(SOUTH)).isConnected() && ((RedstoneSide)var0.getValue(EAST)).isConnected() && ((RedstoneSide)var0.getValue(WEST)).isConnected();
   }

   private static boolean isDot(BlockState var0) {
      return !((RedstoneSide)var0.getValue(NORTH)).isConnected() && !((RedstoneSide)var0.getValue(SOUTH)).isConnected() && !((RedstoneSide)var0.getValue(EAST)).isConnected() && !((RedstoneSide)var0.getValue(WEST)).isConnected();
   }

   protected void updateIndirectNeighbourShapes(BlockState var1, LevelAccessor var2, BlockPos var3, int var4, int var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(Direction var8 : Direction.Plane.HORIZONTAL) {
         RedstoneSide var9 = (RedstoneSide)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(var8));
         if (var9 != RedstoneSide.NONE && !var2.getBlockState(var6.setWithOffset(var3, (Direction)var8)).is(this)) {
            var6.move(Direction.DOWN);
            BlockState var10 = var2.getBlockState(var6);
            if (var10.is(this)) {
               BlockPos var11 = var6.relative(var8.getOpposite());
               var2.neighborShapeChanged(var8.getOpposite(), var6, var11, var2.getBlockState(var11), var4, var5);
            }

            var6.setWithOffset(var3, (Direction)var8).move(Direction.UP);
            BlockState var13 = var2.getBlockState(var6);
            if (var13.is(this)) {
               BlockPos var12 = var6.relative(var8.getOpposite());
               var2.neighborShapeChanged(var8.getOpposite(), var6, var12, var2.getBlockState(var12), var4, var5);
            }
         }
      }

   }

   private RedstoneSide getConnectingSide(BlockGetter var1, BlockPos var2, Direction var3) {
      return this.getConnectingSide(var1, var2, var3, !var1.getBlockState(var2.above()).isRedstoneConductor(var1, var2));
   }

   private RedstoneSide getConnectingSide(BlockGetter var1, BlockPos var2, Direction var3, boolean var4) {
      BlockPos var5 = var2.relative(var3);
      BlockState var6 = var1.getBlockState(var5);
      if (var4) {
         boolean var7 = var6.getBlock() instanceof TrapDoorBlock || this.canSurviveOn(var1, var5, var6);
         if (var7 && shouldConnectTo(var1.getBlockState(var5.above()))) {
            if (var6.isFaceSturdy(var1, var5, var3.getOpposite())) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !shouldConnectTo(var6, var3) && (var6.isRedstoneConductor(var1, var5) || !shouldConnectTo(var1.getBlockState(var5.below()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      return this.canSurviveOn(var2, var4, var5);
   }

   private boolean canSurviveOn(BlockGetter var1, BlockPos var2, BlockState var3) {
      return var3.isFaceSturdy(var1, var2, Direction.UP) || var3.is(Blocks.HOPPER);
   }

   private void updatePowerStrength(Level var1, BlockPos var2, BlockState var3, @Nullable Orientation var4, boolean var5) {
      if (useExperimentalEvaluator(var1)) {
         (new ExperimentalRedstoneWireEvaluator(this)).updatePowerStrength(var1, var2, var3, var4, var5);
      } else {
         this.evaluator.updatePowerStrength(var1, var2, var3, var4, var5);
      }

   }

   public int getBlockSignal(Level var1, BlockPos var2) {
      this.shouldSignal = false;
      int var3 = var1.getBestNeighborSignal(var2);
      this.shouldSignal = true;
      return var3;
   }

   private void checkCornerChangeAt(Level var1, BlockPos var2) {
      if (var1.getBlockState(var2).is(this)) {
         var1.updateNeighborsAt(var2, this);

         for(Direction var6 : Direction.values()) {
            var1.updateNeighborsAt(var2.relative(var6), this);
         }

      }
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock()) && !var2.isClientSide) {
         this.updatePowerStrength(var2, var3, var1, (Orientation)null, true);

         for(Direction var7 : Direction.Plane.VERTICAL) {
            var2.updateNeighborsAt(var3.relative(var7), this);
         }

         this.updateNeighborsOfNeighboringWires(var2, var3);
      }
   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         super.onRemove(var1, var2, var3, var4, var5);
         if (!var2.isClientSide) {
            for(Direction var9 : Direction.values()) {
               var2.updateNeighborsAt(var3.relative(var9), this);
            }

            this.updatePowerStrength(var2, var3, var1, (Orientation)null, false);
            this.updateNeighborsOfNeighboringWires(var2, var3);
         }
      }
   }

   private void updateNeighborsOfNeighboringWires(Level var1, BlockPos var2) {
      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         this.checkCornerChangeAt(var1, var2.relative(var4));
      }

      for(Direction var7 : Direction.Plane.HORIZONTAL) {
         BlockPos var5 = var2.relative(var7);
         if (var1.getBlockState(var5).isRedstoneConductor(var1, var5)) {
            this.checkCornerChangeAt(var1, var5.above());
         } else {
            this.checkCornerChangeAt(var1, var5.below());
         }
      }

   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if (!var2.isClientSide) {
         if (var4 != this || !useExperimentalEvaluator(var2)) {
            if (var1.canSurvive(var2, var3)) {
               this.updatePowerStrength(var2, var3, var1, var5, false);
            } else {
               dropResources(var1, var2, var3);
               var2.removeBlock(var3, false);
            }

         }
      }
   }

   private static boolean useExperimentalEvaluator(Level var0) {
      return var0.enabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS);
   }

   protected int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return !this.shouldSignal ? 0 : var1.getSignal(var2, var3, var4);
   }

   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (this.shouldSignal && var4 != Direction.DOWN) {
         int var5 = (Integer)var1.getValue(POWER);
         if (var5 == 0) {
            return 0;
         } else {
            return var4 != Direction.UP && !((RedstoneSide)this.getConnectionState(var2, var1, var3).getValue((Property)PROPERTY_BY_DIRECTION.get(var4.getOpposite()))).isConnected() ? 0 : var5;
         }
      } else {
         return 0;
      }
   }

   protected static boolean shouldConnectTo(BlockState var0) {
      return shouldConnectTo(var0, (Direction)null);
   }

   protected static boolean shouldConnectTo(BlockState var0, @Nullable Direction var1) {
      if (var0.is(Blocks.REDSTONE_WIRE)) {
         return true;
      } else if (var0.is(Blocks.REPEATER)) {
         Direction var2 = (Direction)var0.getValue(RepeaterBlock.FACING);
         return var2 == var1 || var2.getOpposite() == var1;
      } else if (var0.is(Blocks.OBSERVER)) {
         return var1 == var0.getValue(ObserverBlock.FACING);
      } else {
         return var0.isSignalSource() && var1 != null;
      }
   }

   protected boolean isSignalSource(BlockState var1) {
      return this.shouldSignal;
   }

   public static int getColorForPower(int var0) {
      return COLORS[var0];
   }

   private static void spawnParticlesAlongLine(Level var0, RandomSource var1, BlockPos var2, int var3, Direction var4, Direction var5, float var6, float var7) {
      float var8 = var7 - var6;
      if (!(var1.nextFloat() >= 0.2F * var8)) {
         float var9 = 0.4375F;
         float var10 = var6 + var8 * var1.nextFloat();
         double var11 = 0.5 + (double)(0.4375F * (float)var4.getStepX()) + (double)(var10 * (float)var5.getStepX());
         double var13 = 0.5 + (double)(0.4375F * (float)var4.getStepY()) + (double)(var10 * (float)var5.getStepY());
         double var15 = 0.5 + (double)(0.4375F * (float)var4.getStepZ()) + (double)(var10 * (float)var5.getStepZ());
         var0.addParticle(new DustParticleOptions(var3, 1.0F), (double)var2.getX() + var11, (double)var2.getY() + var13, (double)var2.getZ() + var15, 0.0, 0.0, 0.0);
      }
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      int var5 = (Integer)var1.getValue(POWER);
      if (var5 != 0) {
         for(Direction var7 : Direction.Plane.HORIZONTAL) {
            RedstoneSide var8 = (RedstoneSide)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(var7));
            switch (var8) {
               case UP:
                  spawnParticlesAlongLine(var2, var4, var3, COLORS[var5], var7, Direction.UP, -0.5F, 0.5F);
               case SIDE:
                  spawnParticlesAlongLine(var2, var4, var3, COLORS[var5], Direction.DOWN, var7, 0.0F, 0.5F);
                  break;
               case NONE:
               default:
                  spawnParticlesAlongLine(var2, var4, var3, COLORS[var5], Direction.DOWN, var7, 0.0F, 0.3F);
            }
         }

      }
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case CLOCKWISE_180 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (RedstoneSide)var1.getValue(SOUTH))).setValue(EAST, (RedstoneSide)var1.getValue(WEST))).setValue(SOUTH, (RedstoneSide)var1.getValue(NORTH))).setValue(WEST, (RedstoneSide)var1.getValue(EAST));
         }
         case COUNTERCLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (RedstoneSide)var1.getValue(EAST))).setValue(EAST, (RedstoneSide)var1.getValue(SOUTH))).setValue(SOUTH, (RedstoneSide)var1.getValue(WEST))).setValue(WEST, (RedstoneSide)var1.getValue(NORTH));
         }
         case CLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (RedstoneSide)var1.getValue(WEST))).setValue(EAST, (RedstoneSide)var1.getValue(NORTH))).setValue(SOUTH, (RedstoneSide)var1.getValue(EAST))).setValue(WEST, (RedstoneSide)var1.getValue(SOUTH));
         }
         default -> {
            return var1;
         }
      }
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      switch (var2) {
         case LEFT_RIGHT -> {
            return (BlockState)((BlockState)var1.setValue(NORTH, (RedstoneSide)var1.getValue(SOUTH))).setValue(SOUTH, (RedstoneSide)var1.getValue(NORTH));
         }
         case FRONT_BACK -> {
            return (BlockState)((BlockState)var1.setValue(EAST, (RedstoneSide)var1.getValue(WEST))).setValue(WEST, (RedstoneSide)var1.getValue(EAST));
         }
         default -> {
            return super.mirror(var1, var2);
         }
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, SOUTH, WEST, POWER);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var4.getAbilities().mayBuild) {
         return InteractionResult.PASS;
      } else {
         if (isCross(var1) || isDot(var1)) {
            BlockState var6 = isCross(var1) ? this.defaultBlockState() : this.crossState;
            var6 = (BlockState)var6.setValue(POWER, (Integer)var1.getValue(POWER));
            var6 = this.getConnectionState(var2, var6, var3);
            if (var6 != var1) {
               var2.setBlock(var3, var6, 3);
               this.updatesOnShapeChange(var2, var3, var1, var6);
               return InteractionResult.SUCCESS;
            }
         }

         return InteractionResult.PASS;
      }
   }

   private void updatesOnShapeChange(Level var1, BlockPos var2, BlockState var3, BlockState var4) {
      Orientation var5 = ExperimentalRedstoneUtils.initialOrientation(var1, (Direction)null, Direction.UP);

      for(Direction var7 : Direction.Plane.HORIZONTAL) {
         BlockPos var8 = var2.relative(var7);
         if (((RedstoneSide)var3.getValue((Property)PROPERTY_BY_DIRECTION.get(var7))).isConnected() != ((RedstoneSide)var4.getValue((Property)PROPERTY_BY_DIRECTION.get(var7))).isConnected() && var1.getBlockState(var8).isRedstoneConductor(var1, var8)) {
            var1.updateNeighborsAtExceptFromFacing(var8, var4.getBlock(), var7.getOpposite(), ExperimentalRedstoneUtils.withFront(var5, var7));
         }
      }

   }

   static {
      NORTH = BlockStateProperties.NORTH_REDSTONE;
      EAST = BlockStateProperties.EAST_REDSTONE;
      SOUTH = BlockStateProperties.SOUTH_REDSTONE;
      WEST = BlockStateProperties.WEST_REDSTONE;
      POWER = BlockStateProperties.POWER;
      PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
      SHAPE_DOT = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
      SHAPES_FLOOR = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Direction.SOUTH, Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Direction.EAST, Block.box(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Direction.WEST, Block.box(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
      SHAPES_UP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Shapes.or((VoxelShape)SHAPES_FLOOR.get(Direction.NORTH), Block.box(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)), Direction.SOUTH, Shapes.or((VoxelShape)SHAPES_FLOOR.get(Direction.SOUTH), Block.box(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)), Direction.EAST, Shapes.or((VoxelShape)SHAPES_FLOOR.get(Direction.EAST), Block.box(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)), Direction.WEST, Shapes.or((VoxelShape)SHAPES_FLOOR.get(Direction.WEST), Block.box(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));
      SHAPES_CACHE = Maps.newHashMap();
      COLORS = (int[])Util.make(new int[16], (var0) -> {
         for(int var1 = 0; var1 <= 15; ++var1) {
            float var2 = (float)var1 / 15.0F;
            float var3 = var2 * 0.6F + (var2 > 0.0F ? 0.4F : 0.3F);
            float var4 = Mth.clamp(var2 * var2 * 0.7F - 0.5F, 0.0F, 1.0F);
            float var5 = Mth.clamp(var2 * var2 * 0.6F - 0.7F, 0.0F, 1.0F);
            var0[var1] = ARGB.colorFromFloat(1.0F, var3, var4, var5);
         }

      });
   }
}
