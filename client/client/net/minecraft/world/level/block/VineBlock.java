package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VineBlock extends Block {
   public static final MapCodec<VineBlock> CODEC = simpleCodec(VineBlock::new);
   public static final BooleanProperty UP = PipeBlock.UP;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION
      .entrySet()
      .stream()
      .filter(var0 -> var0.getKey() != Direction.DOWN)
      .collect(Util.toMap());
   protected static final float AABB_OFFSET = 1.0F;
   private static final VoxelShape UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
   private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
   private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
   private final Map<BlockState, VoxelShape> shapesCache;

   @Override
   public MapCodec<VineBlock> codec() {
      return CODEC;
   }

   public VineBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(UP, Boolean.valueOf(false))
            .setValue(NORTH, Boolean.valueOf(false))
            .setValue(EAST, Boolean.valueOf(false))
            .setValue(SOUTH, Boolean.valueOf(false))
            .setValue(WEST, Boolean.valueOf(false))
      );
      this.shapesCache = ImmutableMap.copyOf(
         this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), VineBlock::calculateShape))
      );
   }

   private static VoxelShape calculateShape(BlockState var0) {
      VoxelShape var1 = Shapes.empty();
      if (var0.getValue(UP)) {
         var1 = UP_AABB;
      }

      if (var0.getValue(NORTH)) {
         var1 = Shapes.or(var1, NORTH_AABB);
      }

      if (var0.getValue(SOUTH)) {
         var1 = Shapes.or(var1, SOUTH_AABB);
      }

      if (var0.getValue(EAST)) {
         var1 = Shapes.or(var1, EAST_AABB);
      }

      if (var0.getValue(WEST)) {
         var1 = Shapes.or(var1, WEST_AABB);
      }

      return var1.isEmpty() ? Shapes.block() : var1;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.shapesCache.get(var1);
   }

   @Override
   protected boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return this.hasFaces(this.getUpdatedState(var1, var2, var3));
   }

   private boolean hasFaces(BlockState var1) {
      return this.countFaces(var1) > 0;
   }

   private int countFaces(BlockState var1) {
      int var2 = 0;

      for (BooleanProperty var4 : PROPERTY_BY_DIRECTION.values()) {
         if (var1.getValue(var4)) {
            var2++;
         }
      }

      return var2;
   }

   private boolean canSupportAtFace(BlockGetter var1, BlockPos var2, Direction var3) {
      if (var3 == Direction.DOWN) {
         return false;
      } else {
         BlockPos var4 = var2.relative(var3);
         if (isAcceptableNeighbour(var1, var4, var3)) {
            return true;
         } else if (var3.getAxis() == Direction.Axis.Y) {
            return false;
         } else {
            BooleanProperty var5 = PROPERTY_BY_DIRECTION.get(var3);
            BlockState var6 = var1.getBlockState(var2.above());
            return var6.is(this) && var6.getValue(var5);
         }
      }
   }

   public static boolean isAcceptableNeighbour(BlockGetter var0, BlockPos var1, Direction var2) {
      return MultifaceBlock.canAttachTo(var0, var2, var1, var0.getBlockState(var1));
   }

   private BlockState getUpdatedState(BlockState var1, BlockGetter var2, BlockPos var3) {
      BlockPos var4 = var3.above();
      if (var1.getValue(UP)) {
         var1 = var1.setValue(UP, Boolean.valueOf(isAcceptableNeighbour(var2, var4, Direction.DOWN)));
      }

      BlockState var5 = null;

      for (Direction var7 : Direction.Plane.HORIZONTAL) {
         BooleanProperty var8 = getPropertyForFace(var7);
         if (var1.getValue(var8)) {
            boolean var9 = this.canSupportAtFace(var2, var3, var7);
            if (!var9) {
               if (var5 == null) {
                  var5 = var2.getBlockState(var4);
               }

               var9 = var5.is(this) && var5.getValue(var8);
            }

            var1 = var1.setValue(var8, Boolean.valueOf(var9));
         }
      }

      return var1;
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.DOWN) {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         BlockState var7 = this.getUpdatedState(var1, var4, var5);
         return !this.hasFaces(var7) ? Blocks.AIR.defaultBlockState() : var7;
      }
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.getGameRules().getBoolean(GameRules.RULE_DO_VINES_SPREAD)) {
         if (var4.nextInt(4) == 0) {
            Direction var5 = Direction.getRandom(var4);
            BlockPos var6 = var3.above();
            if (var5.getAxis().isHorizontal() && !var1.getValue(getPropertyForFace(var5))) {
               if (this.canSpread(var2, var3)) {
                  BlockPos var17 = var3.relative(var5);
                  BlockState var19 = var2.getBlockState(var17);
                  if (var19.isAir()) {
                     Direction var21 = var5.getClockWise();
                     Direction var22 = var5.getCounterClockWise();
                     boolean var11 = var1.getValue(getPropertyForFace(var21));
                     boolean var12 = var1.getValue(getPropertyForFace(var22));
                     BlockPos var13 = var17.relative(var21);
                     BlockPos var14 = var17.relative(var22);
                     if (var11 && isAcceptableNeighbour(var2, var13, var21)) {
                        var2.setBlock(var17, this.defaultBlockState().setValue(getPropertyForFace(var21), Boolean.valueOf(true)), 2);
                     } else if (var12 && isAcceptableNeighbour(var2, var14, var22)) {
                        var2.setBlock(var17, this.defaultBlockState().setValue(getPropertyForFace(var22), Boolean.valueOf(true)), 2);
                     } else {
                        Direction var15 = var5.getOpposite();
                        if (var11 && var2.isEmptyBlock(var13) && isAcceptableNeighbour(var2, var3.relative(var21), var15)) {
                           var2.setBlock(var13, this.defaultBlockState().setValue(getPropertyForFace(var15), Boolean.valueOf(true)), 2);
                        } else if (var12 && var2.isEmptyBlock(var14) && isAcceptableNeighbour(var2, var3.relative(var22), var15)) {
                           var2.setBlock(var14, this.defaultBlockState().setValue(getPropertyForFace(var15), Boolean.valueOf(true)), 2);
                        } else if ((double)var4.nextFloat() < 0.05 && isAcceptableNeighbour(var2, var17.above(), Direction.UP)) {
                           var2.setBlock(var17, this.defaultBlockState().setValue(UP, Boolean.valueOf(true)), 2);
                        }
                     }
                  } else if (isAcceptableNeighbour(var2, var17, var5)) {
                     var2.setBlock(var3, var1.setValue(getPropertyForFace(var5), Boolean.valueOf(true)), 2);
                  }
               }
            } else {
               if (var5 == Direction.UP && var3.getY() < var2.getMaxBuildHeight() - 1) {
                  if (this.canSupportAtFace(var2, var3, var5)) {
                     var2.setBlock(var3, var1.setValue(UP, Boolean.valueOf(true)), 2);
                     return;
                  }

                  if (var2.isEmptyBlock(var6)) {
                     if (!this.canSpread(var2, var3)) {
                        return;
                     }

                     BlockState var16 = var1;

                     for (Direction var20 : Direction.Plane.HORIZONTAL) {
                        if (var4.nextBoolean() || !isAcceptableNeighbour(var2, var6.relative(var20), var20)) {
                           var16 = var16.setValue(getPropertyForFace(var20), Boolean.valueOf(false));
                        }
                     }

                     if (this.hasHorizontalConnection(var16)) {
                        var2.setBlock(var6, var16, 2);
                     }

                     return;
                  }
               }

               if (var3.getY() > var2.getMinBuildHeight()) {
                  BlockPos var7 = var3.below();
                  BlockState var8 = var2.getBlockState(var7);
                  if (var8.isAir() || var8.is(this)) {
                     BlockState var9 = var8.isAir() ? this.defaultBlockState() : var8;
                     BlockState var10 = this.copyRandomFaces(var1, var9, var4);
                     if (var9 != var10 && this.hasHorizontalConnection(var10)) {
                        var2.setBlock(var7, var10, 2);
                     }
                  }
               }
            }
         }
      }
   }

   private BlockState copyRandomFaces(BlockState var1, BlockState var2, RandomSource var3) {
      for (Direction var5 : Direction.Plane.HORIZONTAL) {
         if (var3.nextBoolean()) {
            BooleanProperty var6 = getPropertyForFace(var5);
            if (var1.getValue(var6)) {
               var2 = var2.setValue(var6, Boolean.valueOf(true));
            }
         }
      }

      return var2;
   }

   private boolean hasHorizontalConnection(BlockState var1) {
      return var1.getValue(NORTH) || var1.getValue(EAST) || var1.getValue(SOUTH) || var1.getValue(WEST);
   }

   private boolean canSpread(BlockGetter var1, BlockPos var2) {
      byte var3 = 4;
      Iterable var4 = BlockPos.betweenClosed(var2.getX() - 4, var2.getY() - 1, var2.getZ() - 4, var2.getX() + 4, var2.getY() + 1, var2.getZ() + 4);
      int var5 = 5;

      for (BlockPos var7 : var4) {
         if (var1.getBlockState(var7).is(this)) {
            if (--var5 <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      BlockState var3 = var2.getLevel().getBlockState(var2.getClickedPos());
      return var3.is(this) ? this.countFaces(var3) < PROPERTY_BY_DIRECTION.size() : super.canBeReplaced(var1, var2);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      boolean var3 = var2.is(this);
      BlockState var4 = var3 ? var2 : this.defaultBlockState();

      for (Direction var8 : var1.getNearestLookingDirections()) {
         if (var8 != Direction.DOWN) {
            BooleanProperty var9 = getPropertyForFace(var8);
            boolean var10 = var3 && var2.getValue(var9);
            if (!var10 && this.canSupportAtFace(var1.getLevel(), var1.getClickedPos(), var8)) {
               return var4.setValue(var9, Boolean.valueOf(true));
            }
         }
      }

      return var3 ? var4 : null;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(UP, NORTH, EAST, SOUTH, WEST);
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
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
      switch (var2) {
         case LEFT_RIGHT:
            return var1.setValue(NORTH, var1.getValue(SOUTH)).setValue(SOUTH, var1.getValue(NORTH));
         case FRONT_BACK:
            return var1.setValue(EAST, var1.getValue(WEST)).setValue(WEST, var1.getValue(EAST));
         default:
            return super.mirror(var1, var2);
      }
   }

   public static BooleanProperty getPropertyForFace(Direction var0) {
      return PROPERTY_BY_DIRECTION.get(var0);
   }
}
