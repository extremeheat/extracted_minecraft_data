package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
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
   public static final BooleanProperty UP;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
   protected static final float AABB_OFFSET = 1.0F;
   private static final VoxelShape UP_AABB;
   private static final VoxelShape WEST_AABB;
   private static final VoxelShape EAST_AABB;
   private static final VoxelShape NORTH_AABB;
   private static final VoxelShape SOUTH_AABB;
   private final Map<BlockState, VoxelShape> shapesCache;

   public MapCodec<VineBlock> codec() {
      return CODEC;
   }

   public VineBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, false)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false));
      this.shapesCache = ImmutableMap.copyOf((Map)this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), VineBlock::calculateShape)));
   }

   private static VoxelShape calculateShape(BlockState var0) {
      VoxelShape var1 = Shapes.empty();
      if ((Boolean)var0.getValue(UP)) {
         var1 = UP_AABB;
      }

      if ((Boolean)var0.getValue(NORTH)) {
         var1 = Shapes.or(var1, NORTH_AABB);
      }

      if ((Boolean)var0.getValue(SOUTH)) {
         var1 = Shapes.or(var1, SOUTH_AABB);
      }

      if ((Boolean)var0.getValue(EAST)) {
         var1 = Shapes.or(var1, EAST_AABB);
      }

      if ((Boolean)var0.getValue(WEST)) {
         var1 = Shapes.or(var1, WEST_AABB);
      }

      return var1.isEmpty() ? Shapes.block() : var1;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapesCache.get(var1);
   }

   protected boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return this.hasFaces(this.getUpdatedState(var1, var2, var3));
   }

   private boolean hasFaces(BlockState var1) {
      return this.countFaces(var1) > 0;
   }

   private int countFaces(BlockState var1) {
      int var2 = 0;
      Iterator var3 = PROPERTY_BY_DIRECTION.values().iterator();

      while(var3.hasNext()) {
         BooleanProperty var4 = (BooleanProperty)var3.next();
         if ((Boolean)var1.getValue(var4)) {
            ++var2;
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
            BooleanProperty var5 = (BooleanProperty)PROPERTY_BY_DIRECTION.get(var3);
            BlockState var6 = var1.getBlockState(var2.above());
            return var6.is(this) && (Boolean)var6.getValue(var5);
         }
      }
   }

   public static boolean isAcceptableNeighbour(BlockGetter var0, BlockPos var1, Direction var2) {
      return MultifaceBlock.canAttachTo(var0, var2, var1, var0.getBlockState(var1));
   }

   private BlockState getUpdatedState(BlockState var1, BlockGetter var2, BlockPos var3) {
      BlockPos var4 = var3.above();
      if ((Boolean)var1.getValue(UP)) {
         var1 = (BlockState)var1.setValue(UP, isAcceptableNeighbour(var2, var4, Direction.DOWN));
      }

      BlockState var5 = null;
      Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

      while(true) {
         Direction var7;
         BooleanProperty var8;
         do {
            if (!var6.hasNext()) {
               return var1;
            }

            var7 = (Direction)var6.next();
            var8 = getPropertyForFace(var7);
         } while(!(Boolean)var1.getValue(var8));

         boolean var9 = this.canSupportAtFace(var2, var3, var7);
         if (!var9) {
            if (var5 == null) {
               var5 = var2.getBlockState(var4);
            }

            var9 = var5.is(this) && (Boolean)var5.getValue(var8);
         }

         var1 = (BlockState)var1.setValue(var8, var9);
      }
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.DOWN) {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         BlockState var7 = this.getUpdatedState(var1, var4, var5);
         return !this.hasFaces(var7) ? Blocks.AIR.defaultBlockState() : var7;
      }
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.getGameRules().getBoolean(GameRules.RULE_DO_VINES_SPREAD)) {
         if (var4.nextInt(4) == 0) {
            Direction var5 = Direction.getRandom(var4);
            BlockPos var6 = var3.above();
            BlockPos var7;
            BlockState var8;
            Direction var18;
            if (var5.getAxis().isHorizontal() && !(Boolean)var1.getValue(getPropertyForFace(var5))) {
               if (this.canSpread(var2, var3)) {
                  var7 = var3.relative(var5);
                  var8 = var2.getBlockState(var7);
                  if (var8.isAir()) {
                     var18 = var5.getClockWise();
                     Direction var19 = var5.getCounterClockWise();
                     boolean var11 = (Boolean)var1.getValue(getPropertyForFace(var18));
                     boolean var12 = (Boolean)var1.getValue(getPropertyForFace(var19));
                     BlockPos var13 = var7.relative(var18);
                     BlockPos var14 = var7.relative(var19);
                     if (var11 && isAcceptableNeighbour(var2, var13, var18)) {
                        var2.setBlock(var7, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var18), true), 2);
                     } else if (var12 && isAcceptableNeighbour(var2, var14, var19)) {
                        var2.setBlock(var7, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var19), true), 2);
                     } else {
                        Direction var15 = var5.getOpposite();
                        if (var11 && var2.isEmptyBlock(var13) && isAcceptableNeighbour(var2, var3.relative(var18), var15)) {
                           var2.setBlock(var13, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var15), true), 2);
                        } else if (var12 && var2.isEmptyBlock(var14) && isAcceptableNeighbour(var2, var3.relative(var19), var15)) {
                           var2.setBlock(var14, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var15), true), 2);
                        } else if ((double)var4.nextFloat() < 0.05 && isAcceptableNeighbour(var2, var7.above(), Direction.UP)) {
                           var2.setBlock(var7, (BlockState)this.defaultBlockState().setValue(UP, true), 2);
                        }
                     }
                  } else if (isAcceptableNeighbour(var2, var7, var5)) {
                     var2.setBlock(var3, (BlockState)var1.setValue(getPropertyForFace(var5), true), 2);
                  }

               }
            } else {
               if (var5 == Direction.UP && var3.getY() < var2.getMaxBuildHeight() - 1) {
                  if (this.canSupportAtFace(var2, var3, var5)) {
                     var2.setBlock(var3, (BlockState)var1.setValue(UP, true), 2);
                     return;
                  }

                  if (var2.isEmptyBlock(var6)) {
                     if (!this.canSpread(var2, var3)) {
                        return;
                     }

                     BlockState var16 = var1;
                     Iterator var17 = Direction.Plane.HORIZONTAL.iterator();

                     while(true) {
                        do {
                           if (!var17.hasNext()) {
                              if (this.hasHorizontalConnection(var16)) {
                                 var2.setBlock(var6, var16, 2);
                              }

                              return;
                           }

                           var18 = (Direction)var17.next();
                        } while(!var4.nextBoolean() && isAcceptableNeighbour(var2, var6.relative(var18), var18));

                        var16 = (BlockState)var16.setValue(getPropertyForFace(var18), false);
                     }
                  }
               }

               if (var3.getY() > var2.getMinBuildHeight()) {
                  var7 = var3.below();
                  var8 = var2.getBlockState(var7);
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
      Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         Direction var5 = (Direction)var4.next();
         if (var3.nextBoolean()) {
            BooleanProperty var6 = getPropertyForFace(var5);
            if ((Boolean)var1.getValue(var6)) {
               var2 = (BlockState)var2.setValue(var6, true);
            }
         }
      }

      return var2;
   }

   private boolean hasHorizontalConnection(BlockState var1) {
      return (Boolean)var1.getValue(NORTH) || (Boolean)var1.getValue(EAST) || (Boolean)var1.getValue(SOUTH) || (Boolean)var1.getValue(WEST);
   }

   private boolean canSpread(BlockGetter var1, BlockPos var2) {
      boolean var3 = true;
      Iterable var4 = BlockPos.betweenClosed(var2.getX() - 4, var2.getY() - 1, var2.getZ() - 4, var2.getX() + 4, var2.getY() + 1, var2.getZ() + 4);
      int var5 = 5;
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         BlockPos var7 = (BlockPos)var6.next();
         if (var1.getBlockState(var7).is(this)) {
            --var5;
            if (var5 <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      BlockState var3 = var2.getLevel().getBlockState(var2.getClickedPos());
      if (var3.is(this)) {
         return this.countFaces(var3) < PROPERTY_BY_DIRECTION.size();
      } else {
         return super.canBeReplaced(var1, var2);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      boolean var3 = var2.is(this);
      BlockState var4 = var3 ? var2 : this.defaultBlockState();
      Direction[] var5 = var1.getNearestLookingDirections();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         if (var8 != Direction.DOWN) {
            BooleanProperty var9 = getPropertyForFace(var8);
            boolean var10 = var3 && (Boolean)var2.getValue(var9);
            if (!var10 && this.canSupportAtFace(var1.getLevel(), var1.getClickedPos(), var8)) {
               return (BlockState)var4.setValue(var9, true);
            }
         }
      }

      return var3 ? var4 : null;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(UP, NORTH, EAST, SOUTH, WEST);
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case CLOCKWISE_180 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(SOUTH))).setValue(EAST, (Boolean)var1.getValue(WEST))).setValue(SOUTH, (Boolean)var1.getValue(NORTH))).setValue(WEST, (Boolean)var1.getValue(EAST));
         }
         case COUNTERCLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(EAST))).setValue(EAST, (Boolean)var1.getValue(SOUTH))).setValue(SOUTH, (Boolean)var1.getValue(WEST))).setValue(WEST, (Boolean)var1.getValue(NORTH));
         }
         case CLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(WEST))).setValue(EAST, (Boolean)var1.getValue(NORTH))).setValue(SOUTH, (Boolean)var1.getValue(EAST))).setValue(WEST, (Boolean)var1.getValue(SOUTH));
         }
         default -> {
            return var1;
         }
      }
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      switch (var2) {
         case LEFT_RIGHT -> {
            return (BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(SOUTH))).setValue(SOUTH, (Boolean)var1.getValue(NORTH));
         }
         case FRONT_BACK -> {
            return (BlockState)((BlockState)var1.setValue(EAST, (Boolean)var1.getValue(WEST))).setValue(WEST, (Boolean)var1.getValue(EAST));
         }
         default -> {
            return super.mirror(var1, var2);
         }
      }
   }

   public static BooleanProperty getPropertyForFace(Direction var0) {
      return (BooleanProperty)PROPERTY_BY_DIRECTION.get(var0);
   }

   static {
      UP = PipeBlock.UP;
      NORTH = PipeBlock.NORTH;
      EAST = PipeBlock.EAST;
      SOUTH = PipeBlock.SOUTH;
      WEST = PipeBlock.WEST;
      PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((var0) -> {
         return var0.getKey() != Direction.DOWN;
      }).collect(Util.toMap());
      UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
      WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
      EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
      NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
      SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
   }
}
