package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class MossyCarpetBlock extends Block implements BonemealableBlock {
   public static final MapCodec<MossyCarpetBlock> CODEC = simpleCodec(MossyCarpetBlock::new);
   public static final BooleanProperty BASE;
   public static final EnumProperty<WallSide> NORTH;
   public static final EnumProperty<WallSide> EAST;
   public static final EnumProperty<WallSide> SOUTH;
   public static final EnumProperty<WallSide> WEST;
   public static final Map<Direction, EnumProperty<WallSide>> PROPERTY_BY_DIRECTION;
   private final Function<BlockState, VoxelShape> shapes;

   public MapCodec<MossyCarpetBlock> codec() {
      return CODEC;
   }

   public MossyCarpetBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(BASE, true)).setValue(NORTH, WallSide.NONE)).setValue(EAST, WallSide.NONE)).setValue(SOUTH, WallSide.NONE)).setValue(WEST, WallSide.NONE));
      this.shapes = this.makeShapes();
   }

   public Function<BlockState, VoxelShape> makeShapes() {
      Map<Direction, VoxelShape> low = Shapes.rotateHorizontal(Block.boxZ(16.0, 0.0, 10.0, 0.0, 1.0));
      Map<Direction, VoxelShape> tall = Shapes.rotateAll(Block.boxZ(16.0, 0.0, 1.0));
      return this.getShapeForEachState((state) -> {
         VoxelShape shape = (Boolean)state.getValue(BASE) ? (VoxelShape)tall.get(Direction.DOWN) : Shapes.empty();

         for(Map.Entry<Direction, EnumProperty<WallSide>> entry : PROPERTY_BY_DIRECTION.entrySet()) {
            switch ((WallSide)state.getValue((Property)entry.getValue())) {
               case NONE:
               default:
                  break;
               case LOW:
                  shape = Shapes.or(shape, (VoxelShape)low.get(entry.getKey()));
                  break;
               case TALL:
                  shape = Shapes.or(shape, (VoxelShape)tall.get(entry.getKey()));
            }
         }

         return shape.isEmpty() ? Shapes.block() : shape;
      });
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (Boolean)state.getValue(BASE) ? (VoxelShape)this.shapes.apply(this.defaultBlockState()) : Shapes.empty();
   }

   protected boolean propagatesSkylightDown(final BlockState state) {
      return true;
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockState belowState = level.getBlockState(pos.below());
      if ((Boolean)state.getValue(BASE)) {
         return !belowState.isAir();
      } else {
         return belowState.is(this) && (Boolean)belowState.getValue(BASE);
      }
   }

   private static boolean hasFaces(final BlockState blockState) {
      if ((Boolean)blockState.getValue(BASE)) {
         return true;
      } else {
         for(EnumProperty<WallSide> property : PROPERTY_BY_DIRECTION.values()) {
            if (blockState.getValue(property) != WallSide.NONE) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean canSupportAtFace(final BlockGetter level, final BlockPos pos, final Direction direction) {
      return direction == Direction.UP ? false : MultifaceBlock.canAttachTo(level, pos, direction);
   }

   private static BlockState getUpdatedState(BlockState state, final BlockGetter level, final BlockPos pos, boolean createSides) {
      BlockState aboveState = null;
      BlockState belowState = null;
      createSides |= (Boolean)state.getValue(BASE);

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         EnumProperty<WallSide> property = getPropertyForFace(direction);
         WallSide side = canSupportAtFace(level, pos, direction) ? (createSides ? WallSide.LOW : (WallSide)state.getValue(property)) : WallSide.NONE;
         if (side == WallSide.LOW) {
            if (aboveState == null) {
               aboveState = level.getBlockState(pos.above());
            }

            if (aboveState.is(Blocks.PALE_MOSS_CARPET) && aboveState.getValue(property) != WallSide.NONE && !(Boolean)aboveState.getValue(BASE)) {
               side = WallSide.TALL;
            }

            if (!(Boolean)state.getValue(BASE)) {
               if (belowState == null) {
                  belowState = level.getBlockState(pos.below());
               }

               if (belowState.is(Blocks.PALE_MOSS_CARPET) && belowState.getValue(property) == WallSide.NONE) {
                  side = WallSide.NONE;
               }
            }
         }

         state = (BlockState)state.setValue(property, side);
      }

      return state;
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      return getUpdatedState(this.defaultBlockState(), context.getLevel(), context.getClickedPos(), true);
   }

   public static void placeAt(final LevelAccessor level, final BlockPos pos, final RandomSource random, final @Block.UpdateFlags int updateType) {
      BlockState simpleCarpetLayer = Blocks.PALE_MOSS_CARPET.defaultBlockState();
      BlockState adjustedCarpetLayer = getUpdatedState(simpleCarpetLayer, level, pos, true);
      level.setBlock(pos, adjustedCarpetLayer, updateType);
      Objects.requireNonNull(random);
      BlockState state = createTopperWithSideChance(level, pos, random::nextBoolean);
      if (!state.isAir()) {
         level.setBlock(pos.above(), state, updateType);
         BlockState updateBottomCarpet = getUpdatedState(adjustedCarpetLayer, level, pos, true);
         level.setBlock(pos, updateBottomCarpet, updateType);
      }

   }

   public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
      if (!level.isClientSide()) {
         RandomSource random = level.getRandom();
         Objects.requireNonNull(random);
         BlockState topper = createTopperWithSideChance(level, pos, random::nextBoolean);
         if (!topper.isAir()) {
            level.setBlockAndUpdate(pos.above(), topper);
         }

      }
   }

   private static BlockState createTopperWithSideChance(final BlockGetter level, final BlockPos pos, final BooleanSupplier sideSurvivalTest) {
      BlockPos above = pos.above();
      BlockState abovePreviousState = level.getBlockState(above);
      boolean isMossyCarpetAbove = abovePreviousState.is(Blocks.PALE_MOSS_CARPET);
      if ((!isMossyCarpetAbove || !(Boolean)abovePreviousState.getValue(BASE)) && (isMossyCarpetAbove || abovePreviousState.canBeReplaced())) {
         BlockState noCarpetBaseState = (BlockState)Blocks.PALE_MOSS_CARPET.defaultBlockState().setValue(BASE, false);
         BlockState aboveState = getUpdatedState(noCarpetBaseState, level, pos.above(), true);

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            EnumProperty<WallSide> property = getPropertyForFace(direction);
            if (aboveState.getValue(property) != WallSide.NONE && !sideSurvivalTest.getAsBoolean()) {
               aboveState = (BlockState)aboveState.setValue(property, WallSide.NONE);
            }
         }

         if (hasFaces(aboveState) && aboveState != abovePreviousState) {
            return aboveState;
         } else {
            return Blocks.AIR.defaultBlockState();
         }
      } else {
         return Blocks.AIR.defaultBlockState();
      }
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         BlockState blockState = getUpdatedState(state, level, pos, false);
         return !hasFaces(blockState) ? Blocks.AIR.defaultBlockState() : blockState;
      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(BASE, NORTH, EAST, SOUTH, WEST);
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      BlockState var10000;
      switch (rotation) {
         case CLOCKWISE_180 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (WallSide)state.getValue(SOUTH))).setValue(EAST, (WallSide)state.getValue(WEST))).setValue(SOUTH, (WallSide)state.getValue(NORTH))).setValue(WEST, (WallSide)state.getValue(EAST));
         case COUNTERCLOCKWISE_90 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (WallSide)state.getValue(EAST))).setValue(EAST, (WallSide)state.getValue(SOUTH))).setValue(SOUTH, (WallSide)state.getValue(WEST))).setValue(WEST, (WallSide)state.getValue(NORTH));
         case CLOCKWISE_90 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (WallSide)state.getValue(WEST))).setValue(EAST, (WallSide)state.getValue(NORTH))).setValue(SOUTH, (WallSide)state.getValue(EAST))).setValue(WEST, (WallSide)state.getValue(SOUTH));
         default -> var10000 = state;
      }

      return var10000;
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      BlockState var10000;
      switch (mirror) {
         case LEFT_RIGHT -> var10000 = (BlockState)((BlockState)state.setValue(NORTH, (WallSide)state.getValue(SOUTH))).setValue(SOUTH, (WallSide)state.getValue(NORTH));
         case FRONT_BACK -> var10000 = (BlockState)((BlockState)state.setValue(EAST, (WallSide)state.getValue(WEST))).setValue(WEST, (WallSide)state.getValue(EAST));
         default -> var10000 = super.mirror(state, mirror);
      }

      return var10000;
   }

   public static @Nullable EnumProperty<WallSide> getPropertyForFace(final Direction direction) {
      return (EnumProperty)PROPERTY_BY_DIRECTION.get(direction);
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
      return (Boolean)state.getValue(BASE) && !createTopperWithSideChance(level, pos, () -> true).isAir();
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
      BlockState topper = createTopperWithSideChance(level, pos, () -> true);
      if (!topper.isAir()) {
         level.setBlockAndUpdate(pos.above(), topper);
      }

   }

   static {
      BASE = BlockStateProperties.BOTTOM;
      NORTH = BlockStateProperties.NORTH_WALL;
      EAST = BlockStateProperties.EAST_WALL;
      SOUTH = BlockStateProperties.SOUTH_WALL;
      WEST = BlockStateProperties.WEST_WALL;
      PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST)));
   }
}
