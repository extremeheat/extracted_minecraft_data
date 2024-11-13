package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MultifaceBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<MultifaceBlock> CODEC = simpleCodec(MultifaceBlock::new);
   public static final BooleanProperty WATERLOGGED;
   private static final float AABB_OFFSET = 1.0F;
   private static final VoxelShape UP_AABB;
   private static final VoxelShape DOWN_AABB;
   private static final VoxelShape WEST_AABB;
   private static final VoxelShape EAST_AABB;
   private static final VoxelShape NORTH_AABB;
   private static final VoxelShape SOUTH_AABB;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
   private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION;
   protected static final Direction[] DIRECTIONS;
   private final ImmutableMap<BlockState, VoxelShape> shapesCache;
   private final boolean canRotate;
   private final boolean canMirrorX;
   private final boolean canMirrorZ;

   protected MapCodec<? extends MultifaceBlock> codec() {
      return CODEC;
   }

   public MultifaceBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(getDefaultMultifaceState(this.stateDefinition));
      this.shapesCache = this.getShapeForEachState(MultifaceBlock::calculateMultifaceShape);
      this.canRotate = Direction.Plane.HORIZONTAL.stream().allMatch(this::isFaceSupported);
      this.canMirrorX = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.X).filter(this::isFaceSupported).count() % 2L == 0L;
      this.canMirrorZ = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(this::isFaceSupported).count() % 2L == 0L;
   }

   public static Set<Direction> availableFaces(BlockState var0) {
      if (!(var0.getBlock() instanceof MultifaceBlock)) {
         return Set.of();
      } else {
         EnumSet var1 = EnumSet.noneOf(Direction.class);

         for(Direction var5 : Direction.values()) {
            if (hasFace(var0, var5)) {
               var1.add(var5);
            }
         }

         return var1;
      }
   }

   public static Set<Direction> unpack(byte var0) {
      EnumSet var1 = EnumSet.noneOf(Direction.class);

      for(Direction var5 : Direction.values()) {
         if ((var0 & (byte)(1 << var5.ordinal())) > 0) {
            var1.add(var5);
         }
      }

      return var1;
   }

   public static byte pack(Collection<Direction> var0) {
      byte var1 = 0;

      for(Direction var3 : var0) {
         var1 = (byte)(var1 | 1 << var3.ordinal());
      }

      return var1;
   }

   protected boolean isFaceSupported(Direction var1) {
      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      for(Direction var5 : DIRECTIONS) {
         if (this.isFaceSupported(var5)) {
            var1.add(getFaceProperty(var5));
         }
      }

      var1.add(WATERLOGGED);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      if (!hasAnyFace(var1)) {
         return this.getFluidState(var1).createLegacyBlock();
      } else {
         return hasFace(var1, var5) && !canAttachTo(var2, var5, var6, var7) ? removeFace(var1, getFaceProperty(var5)) : var1;
      }
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapesCache.get(var1);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      boolean var4 = false;

      for(Direction var8 : DIRECTIONS) {
         if (hasFace(var1, var8)) {
            if (!canAttachTo(var2, var3, var8)) {
               return false;
            }

            var4 = true;
         }
      }

      return var4;
   }

   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return !var2.getItemInHand().is(this.asItem()) || hasAnyVacantFace(var1);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      return (BlockState)Arrays.stream(var1.getNearestLookingDirections()).map((var4x) -> this.getStateForPlacement(var4, var2, var3, var4x)).filter(Objects::nonNull).findFirst().orElse((Object)null);
   }

   public boolean isValidStateForPlacement(BlockGetter var1, BlockState var2, BlockPos var3, Direction var4) {
      if (this.isFaceSupported(var4) && (!var2.is(this) || !hasFace(var2, var4))) {
         BlockPos var5 = var3.relative(var4);
         return canAttachTo(var1, var4, var5, var1.getBlockState(var5));
      } else {
         return false;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (!this.isValidStateForPlacement(var2, var1, var3, var4)) {
         return null;
      } else {
         BlockState var5;
         if (var1.is(this)) {
            var5 = var1;
         } else if (var1.getFluidState().isSourceOfType(Fluids.WATER)) {
            var5 = (BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
         } else {
            var5 = this.defaultBlockState();
         }

         return (BlockState)var5.setValue(getFaceProperty(var4), true);
      }
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      if (!this.canRotate) {
         return var1;
      } else {
         Objects.requireNonNull(var2);
         return this.mapDirections(var1, var2::rotate);
      }
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      if (var2 == Mirror.FRONT_BACK && !this.canMirrorX) {
         return var1;
      } else if (var2 == Mirror.LEFT_RIGHT && !this.canMirrorZ) {
         return var1;
      } else {
         Objects.requireNonNull(var2);
         return this.mapDirections(var1, var2::mirror);
      }
   }

   private BlockState mapDirections(BlockState var1, Function<Direction, Direction> var2) {
      BlockState var3 = var1;

      for(Direction var7 : DIRECTIONS) {
         if (this.isFaceSupported(var7)) {
            var3 = (BlockState)var3.setValue(getFaceProperty((Direction)var2.apply(var7)), (Boolean)var1.getValue(getFaceProperty(var7)));
         }
      }

      return var3;
   }

   public static boolean hasFace(BlockState var0, Direction var1) {
      BooleanProperty var2 = getFaceProperty(var1);
      return (Boolean)var0.getValueOrElse(var2, false);
   }

   public static boolean canAttachTo(BlockGetter var0, BlockPos var1, Direction var2) {
      BlockPos var3 = var1.relative(var2);
      BlockState var4 = var0.getBlockState(var3);
      return canAttachTo(var0, var2, var3, var4);
   }

   public static boolean canAttachTo(BlockGetter var0, Direction var1, BlockPos var2, BlockState var3) {
      return Block.isFaceFull(var3.getBlockSupportShape(var0, var2), var1.getOpposite()) || Block.isFaceFull(var3.getCollisionShape(var0, var2), var1.getOpposite());
   }

   private static BlockState removeFace(BlockState var0, BooleanProperty var1) {
      BlockState var2 = (BlockState)var0.setValue(var1, false);
      return hasAnyFace(var2) ? var2 : var0.getFluidState().createLegacyBlock();
   }

   public static BooleanProperty getFaceProperty(Direction var0) {
      return (BooleanProperty)PROPERTY_BY_DIRECTION.get(var0);
   }

   private static BlockState getDefaultMultifaceState(StateDefinition<Block, BlockState> var0) {
      BlockState var1 = (BlockState)((BlockState)var0.any()).setValue(WATERLOGGED, false);

      for(BooleanProperty var3 : PROPERTY_BY_DIRECTION.values()) {
         var1 = (BlockState)var1.trySetValue(var3, false);
      }

      return var1;
   }

   private static VoxelShape calculateMultifaceShape(BlockState var0) {
      VoxelShape var1 = Shapes.empty();

      for(Direction var5 : DIRECTIONS) {
         if (hasFace(var0, var5)) {
            var1 = Shapes.or(var1, (VoxelShape)SHAPE_BY_DIRECTION.get(var5));
         }
      }

      return var1.isEmpty() ? Shapes.block() : var1;
   }

   protected static boolean hasAnyFace(BlockState var0) {
      for(Direction var4 : DIRECTIONS) {
         if (hasFace(var0, var4)) {
            return true;
         }
      }

      return false;
   }

   private static boolean hasAnyVacantFace(BlockState var0) {
      for(Direction var4 : DIRECTIONS) {
         if (!hasFace(var0, var4)) {
            return true;
         }
      }

      return false;
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
      DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
      WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
      EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
      NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
      SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
      PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
      SHAPE_BY_DIRECTION = (Map)Util.make(Maps.newEnumMap(Direction.class), (var0) -> {
         var0.put(Direction.NORTH, NORTH_AABB);
         var0.put(Direction.EAST, EAST_AABB);
         var0.put(Direction.SOUTH, SOUTH_AABB);
         var0.put(Direction.WEST, WEST_AABB);
         var0.put(Direction.UP, UP_AABB);
         var0.put(Direction.DOWN, DOWN_AABB);
      });
      DIRECTIONS = Direction.values();
   }
}
