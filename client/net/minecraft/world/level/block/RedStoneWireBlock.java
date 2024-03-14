package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.MapCodec;
import java.util.HashSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedStoneWireBlock extends Block {
   public static final MapCodec<RedStoneWireBlock> CODEC = simpleCodec(RedStoneWireBlock::new);
   public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
   public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
   public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
   public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap(
      ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST)
   );
   protected static final int H = 1;
   protected static final int W = 3;
   protected static final int E = 13;
   protected static final int N = 3;
   protected static final int S = 13;
   private static final VoxelShape SHAPE_DOT = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
   private static final Map<Direction, VoxelShape> SHAPES_FLOOR = Maps.newEnumMap(
      ImmutableMap.of(
         Direction.NORTH,
         Block.box(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
         Direction.SOUTH,
         Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
         Direction.EAST,
         Block.box(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
         Direction.WEST,
         Block.box(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)
      )
   );
   private static final Map<Direction, VoxelShape> SHAPES_UP = Maps.newEnumMap(
      ImmutableMap.of(
         Direction.NORTH,
         Shapes.or(SHAPES_FLOOR.get(Direction.NORTH), Block.box(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)),
         Direction.SOUTH,
         Shapes.or(SHAPES_FLOOR.get(Direction.SOUTH), Block.box(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)),
         Direction.EAST,
         Shapes.or(SHAPES_FLOOR.get(Direction.EAST), Block.box(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)),
         Direction.WEST,
         Shapes.or(SHAPES_FLOOR.get(Direction.WEST), Block.box(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))
      )
   );
   private static final Map<BlockState, VoxelShape> SHAPES_CACHE = Maps.newHashMap();
   private static final Vec3[] COLORS = Util.make(new Vec3[16], var0 -> {
      for(int var1 = 0; var1 <= 15; ++var1) {
         float var2 = (float)var1 / 15.0F;
         float var3 = var2 * 0.6F + (var2 > 0.0F ? 0.4F : 0.3F);
         float var4 = Mth.clamp(var2 * var2 * 0.7F - 0.5F, 0.0F, 1.0F);
         float var5 = Mth.clamp(var2 * var2 * 0.6F - 0.7F, 0.0F, 1.0F);
         var0[var1] = new Vec3((double)var3, (double)var4, (double)var5);
      }
   });
   private static final float PARTICLE_DENSITY = 0.2F;
   private final BlockState crossState;
   private boolean shouldSignal = true;

   @Override
   public MapCodec<RedStoneWireBlock> codec() {
      return CODEC;
   }

   public RedStoneWireBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, RedstoneSide.NONE)
            .setValue(EAST, RedstoneSide.NONE)
            .setValue(SOUTH, RedstoneSide.NONE)
            .setValue(WEST, RedstoneSide.NONE)
            .setValue(POWER, Integer.valueOf(0))
      );
      this.crossState = this.defaultBlockState()
         .setValue(NORTH, RedstoneSide.SIDE)
         .setValue(EAST, RedstoneSide.SIDE)
         .setValue(SOUTH, RedstoneSide.SIDE)
         .setValue(WEST, RedstoneSide.SIDE);
      UnmodifiableIterator var2 = this.getStateDefinition().getPossibleStates().iterator();

      while(var2.hasNext()) {
         BlockState var3 = (BlockState)var2.next();
         if (var3.getValue(POWER) == 0) {
            SHAPES_CACHE.put(var3, this.calculateShape(var3));
         }
      }
   }

   private VoxelShape calculateShape(BlockState var1) {
      VoxelShape var2 = SHAPE_DOT;

      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         RedstoneSide var5 = var1.getValue(PROPERTY_BY_DIRECTION.get(var4));
         if (var5 == RedstoneSide.SIDE) {
            var2 = Shapes.or(var2, SHAPES_FLOOR.get(var4));
         } else if (var5 == RedstoneSide.UP) {
            var2 = Shapes.or(var2, SHAPES_UP.get(var4));
         }
      }

      return var2;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPES_CACHE.get(var1.setValue(POWER, Integer.valueOf(0)));
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.getConnectionState(var1.getLevel(), this.crossState, var1.getClickedPos());
   }

   private BlockState getConnectionState(BlockGetter var1, BlockState var2, BlockPos var3) {
      boolean var4 = isDot(var2);
      var2 = this.getMissingConnections(var1, this.defaultBlockState().setValue(POWER, var2.getValue(POWER)), var3);
      if (var4 && isDot(var2)) {
         return var2;
      } else {
         boolean var5 = var2.getValue(NORTH).isConnected();
         boolean var6 = var2.getValue(SOUTH).isConnected();
         boolean var7 = var2.getValue(EAST).isConnected();
         boolean var8 = var2.getValue(WEST).isConnected();
         boolean var9 = !var5 && !var6;
         boolean var10 = !var7 && !var8;
         if (!var8 && var9) {
            var2 = var2.setValue(WEST, RedstoneSide.SIDE);
         }

         if (!var7 && var9) {
            var2 = var2.setValue(EAST, RedstoneSide.SIDE);
         }

         if (!var5 && var10) {
            var2 = var2.setValue(NORTH, RedstoneSide.SIDE);
         }

         if (!var6 && var10) {
            var2 = var2.setValue(SOUTH, RedstoneSide.SIDE);
         }

         return var2;
      }
   }

   private BlockState getMissingConnections(BlockGetter var1, BlockState var2, BlockPos var3) {
      boolean var4 = !var1.getBlockState(var3.above()).isRedstoneConductor(var1, var3);

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         if (!var2.getValue(PROPERTY_BY_DIRECTION.get(var6)).isConnected()) {
            RedstoneSide var7 = this.getConnectingSide(var1, var3, var6, var4);
            var2 = var2.setValue(PROPERTY_BY_DIRECTION.get(var6), var7);
         }
      }

      return var2;
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.DOWN) {
         return !this.canSurviveOn(var4, var6, var3) ? Blocks.AIR.defaultBlockState() : var1;
      } else if (var2 == Direction.UP) {
         return this.getConnectionState(var4, var1, var5);
      } else {
         RedstoneSide var7 = this.getConnectingSide(var4, var5, var2);
         return var7.isConnected() == var1.getValue(PROPERTY_BY_DIRECTION.get(var2)).isConnected() && !isCross(var1)
            ? var1.setValue(PROPERTY_BY_DIRECTION.get(var2), var7)
            : this.getConnectionState(var4, this.crossState.setValue(POWER, var1.getValue(POWER)).setValue(PROPERTY_BY_DIRECTION.get(var2), var7), var5);
      }
   }

   private static boolean isCross(BlockState var0) {
      return var0.getValue(NORTH).isConnected() && var0.getValue(SOUTH).isConnected() && var0.getValue(EAST).isConnected() && var0.getValue(WEST).isConnected();
   }

   private static boolean isDot(BlockState var0) {
      return !var0.getValue(NORTH).isConnected()
         && !var0.getValue(SOUTH).isConnected()
         && !var0.getValue(EAST).isConnected()
         && !var0.getValue(WEST).isConnected();
   }

   @Override
   protected void updateIndirectNeighbourShapes(BlockState var1, LevelAccessor var2, BlockPos var3, int var4, int var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(Direction var8 : Direction.Plane.HORIZONTAL) {
         RedstoneSide var9 = var1.getValue(PROPERTY_BY_DIRECTION.get(var8));
         if (var9 != RedstoneSide.NONE && !var2.getBlockState(var6.setWithOffset(var3, var8)).is(this)) {
            var6.move(Direction.DOWN);
            BlockState var10 = var2.getBlockState(var6);
            if (var10.is(this)) {
               BlockPos var11 = var6.relative(var8.getOpposite());
               var2.neighborShapeChanged(var8.getOpposite(), var2.getBlockState(var11), var6, var11, var4, var5);
            }

            var6.setWithOffset(var3, var8).move(Direction.UP);
            BlockState var13 = var2.getBlockState(var6);
            if (var13.is(this)) {
               BlockPos var12 = var6.relative(var8.getOpposite());
               var2.neighborShapeChanged(var8.getOpposite(), var2.getBlockState(var12), var6, var12, var4, var5);
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

      return !shouldConnectTo(var6, var3) && (var6.isRedstoneConductor(var1, var5) || !shouldConnectTo(var1.getBlockState(var5.below())))
         ? RedstoneSide.NONE
         : RedstoneSide.SIDE;
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      return this.canSurviveOn(var2, var4, var5);
   }

   private boolean canSurviveOn(BlockGetter var1, BlockPos var2, BlockState var3) {
      return var3.isFaceSturdy(var1, var2, Direction.UP) || var3.is(Blocks.HOPPER);
   }

   private void updatePowerStrength(Level var1, BlockPos var2, BlockState var3) {
      int var4 = this.calculateTargetStrength(var1, var2);
      if (var3.getValue(POWER) != var4) {
         if (var1.getBlockState(var2) == var3) {
            var1.setBlock(var2, var3.setValue(POWER, Integer.valueOf(var4)), 2);
         }

         HashSet var5 = Sets.newHashSet();
         var5.add(var2);

         for(Direction var9 : Direction.values()) {
            var5.add(var2.relative(var9));
         }

         for(BlockPos var11 : var5) {
            var1.updateNeighborsAt(var11, this);
         }
      }
   }

   private int calculateTargetStrength(Level var1, BlockPos var2) {
      this.shouldSignal = false;
      int var3 = var1.getBestNeighborSignal(var2);
      this.shouldSignal = true;
      int var4 = 0;
      if (var3 < 15) {
         for(Direction var6 : Direction.Plane.HORIZONTAL) {
            BlockPos var7 = var2.relative(var6);
            BlockState var8 = var1.getBlockState(var7);
            var4 = Math.max(var4, this.getWireSignal(var8));
            BlockPos var9 = var2.above();
            if (var8.isRedstoneConductor(var1, var7) && !var1.getBlockState(var9).isRedstoneConductor(var1, var9)) {
               var4 = Math.max(var4, this.getWireSignal(var1.getBlockState(var7.above())));
            } else if (!var8.isRedstoneConductor(var1, var7)) {
               var4 = Math.max(var4, this.getWireSignal(var1.getBlockState(var7.below())));
            }
         }
      }

      return Math.max(var3, var4 - 1);
   }

   private int getWireSignal(BlockState var1) {
      return var1.is(this) ? var1.getValue(POWER) : 0;
   }

   private void checkCornerChangeAt(Level var1, BlockPos var2) {
      if (var1.getBlockState(var2).is(this)) {
         var1.updateNeighborsAt(var2, this);

         for(Direction var6 : Direction.values()) {
            var1.updateNeighborsAt(var2.relative(var6), this);
         }
      }
   }

   @Override
   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock()) && !var2.isClientSide) {
         this.updatePowerStrength(var2, var3, var1);

         for(Direction var7 : Direction.Plane.VERTICAL) {
            var2.updateNeighborsAt(var3.relative(var7), this);
         }

         this.updateNeighborsOfNeighboringWires(var2, var3);
      }
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         super.onRemove(var1, var2, var3, var4, var5);
         if (!var2.isClientSide) {
            for(Direction var9 : Direction.values()) {
               var2.updateNeighborsAt(var3.relative(var9), this);
            }

            this.updatePowerStrength(var2, var3, var1);
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

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         if (var1.canSurvive(var2, var3)) {
            this.updatePowerStrength(var2, var3, var1);
         } else {
            dropResources(var1, var2, var3);
            var2.removeBlock(var3, false);
         }
      }
   }

   @Override
   protected int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return !this.shouldSignal ? 0 : var1.getSignal(var2, var3, var4);
   }

   @Override
   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (this.shouldSignal && var4 != Direction.DOWN) {
         int var5 = var1.getValue(POWER);
         if (var5 == 0) {
            return 0;
         } else {
            return var4 != Direction.UP && !this.getConnectionState(var2, var1, var3).getValue(PROPERTY_BY_DIRECTION.get(var4.getOpposite())).isConnected()
               ? 0
               : var5;
         }
      } else {
         return 0;
      }
   }

   protected static boolean shouldConnectTo(BlockState var0) {
      return shouldConnectTo(var0, null);
   }

   protected static boolean shouldConnectTo(BlockState var0, @Nullable Direction var1) {
      if (var0.is(Blocks.REDSTONE_WIRE)) {
         return true;
      } else if (var0.is(Blocks.REPEATER)) {
         Direction var2 = var0.getValue(RepeaterBlock.FACING);
         return var2 == var1 || var2.getOpposite() == var1;
      } else if (var0.is(Blocks.OBSERVER)) {
         return var1 == var0.getValue(ObserverBlock.FACING);
      } else {
         return var0.isSignalSource() && var1 != null;
      }
   }

   @Override
   protected boolean isSignalSource(BlockState var1) {
      return this.shouldSignal;
   }

   public static int getColorForPower(int var0) {
      Vec3 var1 = COLORS[var0];
      return Mth.color((float)var1.x(), (float)var1.y(), (float)var1.z());
   }

   private void spawnParticlesAlongLine(Level var1, RandomSource var2, BlockPos var3, Vec3 var4, Direction var5, Direction var6, float var7, float var8) {
      float var9 = var8 - var7;
      if (!(var2.nextFloat() >= 0.2F * var9)) {
         float var10 = 0.4375F;
         float var11 = var7 + var9 * var2.nextFloat();
         double var12 = 0.5 + (double)(0.4375F * (float)var5.getStepX()) + (double)(var11 * (float)var6.getStepX());
         double var14 = 0.5 + (double)(0.4375F * (float)var5.getStepY()) + (double)(var11 * (float)var6.getStepY());
         double var16 = 0.5 + (double)(0.4375F * (float)var5.getStepZ()) + (double)(var11 * (float)var6.getStepZ());
         var1.addParticle(
            new DustParticleOptions(var4.toVector3f(), 1.0F),
            (double)var3.getX() + var12,
            (double)var3.getY() + var14,
            (double)var3.getZ() + var16,
            0.0,
            0.0,
            0.0
         );
      }
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      int var5 = var1.getValue(POWER);
      if (var5 != 0) {
         for(Direction var7 : Direction.Plane.HORIZONTAL) {
            RedstoneSide var8 = var1.getValue(PROPERTY_BY_DIRECTION.get(var7));
            switch(var8) {
               case UP:
                  this.spawnParticlesAlongLine(var2, var4, var3, COLORS[var5], var7, Direction.UP, -0.5F, 0.5F);
               case SIDE:
                  this.spawnParticlesAlongLine(var2, var4, var3, COLORS[var5], Direction.DOWN, var7, 0.0F, 0.5F);
                  break;
               case NONE:
               default:
                  this.spawnParticlesAlongLine(var2, var4, var3, COLORS[var5], Direction.DOWN, var7, 0.0F, 0.3F);
            }
         }
      }
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch(var2) {
         case CLOCKWISE_180:
            return var1.setValue(NORTH, var1.getValue(SOUTH))
               .setValue(EAST, var1.getValue(WEST))
               .setValue(SOUTH, var1.getValue(NORTH))
               .setValue(WEST, var1.getValue(EAST));
         case COUNTERCLOCKWISE_90:
            return var1.setValue(NORTH, var1.getValue(EAST))
               .setValue(EAST, var1.getValue(SOUTH))
               .setValue(SOUTH, var1.getValue(WEST))
               .setValue(WEST, var1.getValue(NORTH));
         case CLOCKWISE_90:
            return var1.setValue(NORTH, var1.getValue(WEST))
               .setValue(EAST, var1.getValue(NORTH))
               .setValue(SOUTH, var1.getValue(EAST))
               .setValue(WEST, var1.getValue(SOUTH));
         default:
            return var1;
      }
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      switch(var2) {
         case LEFT_RIGHT:
            return var1.setValue(NORTH, var1.getValue(SOUTH)).setValue(SOUTH, var1.getValue(NORTH));
         case FRONT_BACK:
            return var1.setValue(EAST, var1.getValue(WEST)).setValue(WEST, var1.getValue(EAST));
         default:
            return super.mirror(var1, var2);
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, SOUTH, WEST, POWER);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var4.getAbilities().mayBuild) {
         return InteractionResult.PASS;
      } else {
         if (isCross(var1) || isDot(var1)) {
            BlockState var6 = isCross(var1) ? this.defaultBlockState() : this.crossState;
            var6 = var6.setValue(POWER, var1.getValue(POWER));
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
      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         BlockPos var7 = var2.relative(var6);
         if (var3.getValue(PROPERTY_BY_DIRECTION.get(var6)).isConnected() != var4.getValue(PROPERTY_BY_DIRECTION.get(var6)).isConnected()
            && var1.getBlockState(var7).isRedstoneConductor(var1, var7)) {
            var1.updateNeighborsAtExceptFromFacing(var7, var4.getBlock(), var6.getOpposite());
         }
      }
   }
}
