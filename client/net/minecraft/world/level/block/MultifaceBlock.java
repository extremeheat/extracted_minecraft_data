package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MultifaceBlock extends Block {
   private static final float AABB_OFFSET = 1.0F;
   private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
   private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
   private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION;
   protected static final Direction[] DIRECTIONS;
   private final ImmutableMap<BlockState, VoxelShape> shapesCache;
   private final boolean canRotate;
   private final boolean canMirrorX;
   private final boolean canMirrorZ;

   public MultifaceBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(getDefaultMultifaceState(this.stateDefinition));
      this.shapesCache = this.getShapeForEachState(MultifaceBlock::calculateMultifaceShape);
      this.canRotate = Direction.Plane.HORIZONTAL.stream().allMatch(this::isFaceSupported);
      this.canMirrorX = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.field_500).filter(this::isFaceSupported).count() % 2L == 0L;
      this.canMirrorZ = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.field_502).filter(this::isFaceSupported).count() % 2L == 0L;
   }

   protected boolean isFaceSupported(Direction var1) {
      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      Direction[] var2 = DIRECTIONS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction var5 = var2[var4];
         if (this.isFaceSupported(var5)) {
            var1.add(getFaceProperty(var5));
         }
      }

   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!hasAnyFace(var1)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         return hasFace(var1, var2) && !canAttachTo(var4, var2, var6, var3) ? removeFace(var1, getFaceProperty(var2)) : var1;
      }
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapesCache.get(var1);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      boolean var4 = false;
      Direction[] var5 = DIRECTIONS;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         if (hasFace(var1, var8)) {
            BlockPos var9 = var3.relative(var8);
            if (!canAttachTo(var2, var8, var9, var2.getBlockState(var9))) {
               return false;
            }

            var4 = true;
         }
      }

      return var4;
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return hasAnyVacantFace(var1);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      return (BlockState)Arrays.stream(var1.getNearestLookingDirections()).map((var4x) -> {
         return this.getStateForPlacement(var4, var2, var3, var4x);
      }).filter(Objects::nonNull).findFirst().orElse((Object)null);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (!this.isFaceSupported(var4)) {
         return null;
      } else {
         BlockState var5;
         if (var1.is(this)) {
            if (hasFace(var1, var4)) {
               return null;
            }

            var5 = var1;
         } else if (this.isWaterloggable() && var1.getFluidState().isSourceOfType(Fluids.WATER)) {
            var5 = (BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
         } else {
            var5 = this.defaultBlockState();
         }

         BlockPos var6 = var3.relative(var4);
         return canAttachTo(var2, var4, var6, var2.getBlockState(var6)) ? (BlockState)var5.setValue(getFaceProperty(var4), true) : null;
      }
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      if (!this.canRotate) {
         return var1;
      } else {
         Objects.requireNonNull(var2);
         return this.mapDirections(var1, var2::rotate);
      }
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
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
      Direction[] var4 = DIRECTIONS;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         if (this.isFaceSupported(var7)) {
            var3 = (BlockState)var3.setValue(getFaceProperty((Direction)var2.apply(var7)), (Boolean)var1.getValue(getFaceProperty(var7)));
         }
      }

      return var3;
   }

   public boolean spreadFromRandomFaceTowardRandomDirection(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      ArrayList var5 = Lists.newArrayList(DIRECTIONS);
      Collections.shuffle(var5);
      return var5.stream().filter((var1x) -> {
         return hasFace(var1, var1x);
      }).anyMatch((var5x) -> {
         return this.spreadFromFaceTowardRandomDirection(var1, var2, var3, var5x, var4, false);
      });
   }

   public boolean spreadFromFaceTowardRandomDirection(BlockState var1, LevelAccessor var2, BlockPos var3, Direction var4, Random var5, boolean var6) {
      List var7 = Arrays.asList(DIRECTIONS);
      Collections.shuffle(var7, var5);
      return var7.stream().anyMatch((var6x) -> {
         return this.spreadFromFaceTowardDirection(var1, var2, var3, var4, var6x, var6);
      });
   }

   public boolean spreadFromFaceTowardDirection(BlockState var1, LevelAccessor var2, BlockPos var3, Direction var4, Direction var5, boolean var6) {
      Optional var7 = this.getSpreadFromFaceTowardDirection(var1, var2, var3, var4, var5);
      if (var7.isPresent()) {
         Pair var8 = (Pair)var7.get();
         return this.spreadToFace(var2, (BlockPos)var8.getFirst(), (Direction)var8.getSecond(), var6);
      } else {
         return false;
      }
   }

   protected boolean canSpread(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return Stream.of(DIRECTIONS).anyMatch((var5) -> {
         return this.getSpreadFromFaceTowardDirection(var1, var2, var3, var4, var5).isPresent();
      });
   }

   private Optional<Pair<BlockPos, Direction>> getSpreadFromFaceTowardDirection(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4, Direction var5) {
      if (var5.getAxis() != var4.getAxis() && hasFace(var1, var4) && !hasFace(var1, var5)) {
         if (this.canSpreadToFace(var2, var3, var5)) {
            return Optional.of(Pair.of(var3, var5));
         } else {
            BlockPos var6 = var3.relative(var5);
            if (this.canSpreadToFace(var2, var6, var4)) {
               return Optional.of(Pair.of(var6, var4));
            } else {
               BlockPos var7 = var6.relative(var4);
               Direction var8 = var5.getOpposite();
               return this.canSpreadToFace(var2, var7, var8) ? Optional.of(Pair.of(var7, var8)) : Optional.empty();
            }
         }
      } else {
         return Optional.empty();
      }
   }

   private boolean canSpreadToFace(BlockGetter var1, BlockPos var2, Direction var3) {
      BlockState var4 = var1.getBlockState(var2);
      if (!this.canSpreadInto(var4)) {
         return false;
      } else {
         BlockState var5 = this.getStateForPlacement(var4, var1, var2, var3);
         return var5 != null;
      }
   }

   private boolean spreadToFace(LevelAccessor var1, BlockPos var2, Direction var3, boolean var4) {
      BlockState var5 = var1.getBlockState(var2);
      BlockState var6 = this.getStateForPlacement(var5, var1, var2, var3);
      if (var6 != null) {
         if (var4) {
            var1.getChunk(var2).markPosForPostprocessing(var2);
         }

         return var1.setBlock(var2, var6, 2);
      } else {
         return false;
      }
   }

   private boolean canSpreadInto(BlockState var1) {
      return var1.isAir() || var1.is(this) || var1.is(Blocks.WATER) && var1.getFluidState().isSource();
   }

   private static boolean hasFace(BlockState var0, Direction var1) {
      BooleanProperty var2 = getFaceProperty(var1);
      return var0.hasProperty(var2) && (Boolean)var0.getValue(var2);
   }

   private static boolean canAttachTo(BlockGetter var0, Direction var1, BlockPos var2, BlockState var3) {
      return Block.isFaceFull(var3.getCollisionShape(var0, var2), var1.getOpposite());
   }

   private boolean isWaterloggable() {
      return this.stateDefinition.getProperties().contains(BlockStateProperties.WATERLOGGED);
   }

   private static BlockState removeFace(BlockState var0, BooleanProperty var1) {
      BlockState var2 = (BlockState)var0.setValue(var1, false);
      return hasAnyFace(var2) ? var2 : Blocks.AIR.defaultBlockState();
   }

   public static BooleanProperty getFaceProperty(Direction var0) {
      return (BooleanProperty)PROPERTY_BY_DIRECTION.get(var0);
   }

   private static BlockState getDefaultMultifaceState(StateDefinition<Block, BlockState> var0) {
      BlockState var1 = (BlockState)var0.any();
      Iterator var2 = PROPERTY_BY_DIRECTION.values().iterator();

      while(var2.hasNext()) {
         BooleanProperty var3 = (BooleanProperty)var2.next();
         if (var1.hasProperty(var3)) {
            var1 = (BlockState)var1.setValue(var3, false);
         }
      }

      return var1;
   }

   private static VoxelShape calculateMultifaceShape(BlockState var0) {
      VoxelShape var1 = Shapes.empty();
      Direction[] var2 = DIRECTIONS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction var5 = var2[var4];
         if (hasFace(var0, var5)) {
            var1 = Shapes.method_31(var1, (VoxelShape)SHAPE_BY_DIRECTION.get(var5));
         }
      }

      return var1.isEmpty() ? Shapes.block() : var1;
   }

   protected static boolean hasAnyFace(BlockState var0) {
      return Arrays.stream(DIRECTIONS).anyMatch((var1) -> {
         return hasFace(var0, var1);
      });
   }

   private static boolean hasAnyVacantFace(BlockState var0) {
      return Arrays.stream(DIRECTIONS).anyMatch((var1) -> {
         return !hasFace(var0, var1);
      });
   }

   static {
      PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
      SHAPE_BY_DIRECTION = (Map)Util.make(Maps.newEnumMap(Direction.class), (var0) -> {
         var0.put(Direction.NORTH, NORTH_AABB);
         var0.put(Direction.EAST, EAST_AABB);
         var0.put(Direction.SOUTH, SOUTH_AABB);
         var0.put(Direction.WEST, WEST_AABB);
         var0.put(Direction.field_526, UP_AABB);
         var0.put(Direction.DOWN, DOWN_AABB);
      });
      DIRECTIONS = Direction.values();
   }
}
