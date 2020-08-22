package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VineBlock extends Block {
   public static final BooleanProperty UP;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final Map PROPERTY_BY_DIRECTION;
   protected static final VoxelShape UP_AABB;
   protected static final VoxelShape EAST_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape NORTH_AABB;

   public VineBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, false)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      VoxelShape var5 = Shapes.empty();
      if ((Boolean)var1.getValue(UP)) {
         var5 = Shapes.or(var5, UP_AABB);
      }

      if ((Boolean)var1.getValue(NORTH)) {
         var5 = Shapes.or(var5, SOUTH_AABB);
      }

      if ((Boolean)var1.getValue(EAST)) {
         var5 = Shapes.or(var5, WEST_AABB);
      }

      if ((Boolean)var1.getValue(SOUTH)) {
         var5 = Shapes.or(var5, NORTH_AABB);
      }

      if ((Boolean)var1.getValue(WEST)) {
         var5 = Shapes.or(var5, EAST_AABB);
      }

      return var5;
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
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
            return var6.getBlock() == this && (Boolean)var6.getValue(var5);
         }
      }
   }

   public static boolean isAcceptableNeighbour(BlockGetter var0, BlockPos var1, Direction var2) {
      BlockState var3 = var0.getBlockState(var1);
      return Block.isFaceFull(var3.getCollisionShape(var0, var1), var2.getOpposite());
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

            var9 = var5.getBlock() == this && (Boolean)var5.getValue(var8);
         }

         var1 = (BlockState)var1.setValue(var8, var9);
      }
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.DOWN) {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         BlockState var7 = this.getUpdatedState(var1, var4, var5);
         return !this.hasFaces(var7) ? Blocks.AIR.defaultBlockState() : var7;
      }
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      BlockState var5 = this.getUpdatedState(var1, var2, var3);
      if (var5 != var1) {
         if (this.hasFaces(var5)) {
            var2.setBlock(var3, var5, 2);
         } else {
            dropResources(var1, var2, var3);
            var2.removeBlock(var3, false);
         }

      } else if (var2.random.nextInt(4) == 0) {
         Direction var6 = Direction.getRandomFace(var4);
         BlockPos var7 = var3.above();
         BlockPos var8;
         BlockState var9;
         Direction var19;
         if (var6.getAxis().isHorizontal() && !(Boolean)var1.getValue(getPropertyForFace(var6))) {
            if (this.canSpread(var2, var3)) {
               var8 = var3.relative(var6);
               var9 = var2.getBlockState(var8);
               if (var9.isAir()) {
                  var19 = var6.getClockWise();
                  Direction var20 = var6.getCounterClockWise();
                  boolean var12 = (Boolean)var1.getValue(getPropertyForFace(var19));
                  boolean var13 = (Boolean)var1.getValue(getPropertyForFace(var20));
                  BlockPos var14 = var8.relative(var19);
                  BlockPos var15 = var8.relative(var20);
                  if (var12 && isAcceptableNeighbour(var2, var14, var19)) {
                     var2.setBlock(var8, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var19), true), 2);
                  } else if (var13 && isAcceptableNeighbour(var2, var15, var20)) {
                     var2.setBlock(var8, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var20), true), 2);
                  } else {
                     Direction var16 = var6.getOpposite();
                     if (var12 && var2.isEmptyBlock(var14) && isAcceptableNeighbour(var2, var3.relative(var19), var16)) {
                        var2.setBlock(var14, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var16), true), 2);
                     } else if (var13 && var2.isEmptyBlock(var15) && isAcceptableNeighbour(var2, var3.relative(var20), var16)) {
                        var2.setBlock(var15, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(var16), true), 2);
                     } else if ((double)var2.random.nextFloat() < 0.05D && isAcceptableNeighbour(var2, var8.above(), Direction.UP)) {
                        var2.setBlock(var8, (BlockState)this.defaultBlockState().setValue(UP, true), 2);
                     }
                  }
               } else if (isAcceptableNeighbour(var2, var8, var6)) {
                  var2.setBlock(var3, (BlockState)var1.setValue(getPropertyForFace(var6), true), 2);
               }

            }
         } else {
            if (var6 == Direction.UP && var3.getY() < 255) {
               if (this.canSupportAtFace(var2, var3, var6)) {
                  var2.setBlock(var3, (BlockState)var1.setValue(UP, true), 2);
                  return;
               }

               if (var2.isEmptyBlock(var7)) {
                  if (!this.canSpread(var2, var3)) {
                     return;
                  }

                  BlockState var17 = var1;
                  Iterator var18 = Direction.Plane.HORIZONTAL.iterator();

                  while(true) {
                     do {
                        if (!var18.hasNext()) {
                           if (this.hasHorizontalConnection(var17)) {
                              var2.setBlock(var7, var17, 2);
                           }

                           return;
                        }

                        var19 = (Direction)var18.next();
                     } while(!var4.nextBoolean() && isAcceptableNeighbour(var2, var7.relative(var19), Direction.UP));

                     var17 = (BlockState)var17.setValue(getPropertyForFace(var19), false);
                  }
               }
            }

            if (var3.getY() > 0) {
               var8 = var3.below();
               var9 = var2.getBlockState(var8);
               if (var9.isAir() || var9.getBlock() == this) {
                  BlockState var10 = var9.isAir() ? this.defaultBlockState() : var9;
                  BlockState var11 = this.copyRandomFaces(var1, var10, var4);
                  if (var10 != var11 && this.hasHorizontalConnection(var11)) {
                     var2.setBlock(var8, var11, 2);
                  }
               }
            }

         }
      }
   }

   private BlockState copyRandomFaces(BlockState var1, BlockState var2, Random var3) {
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
         if (var1.getBlockState(var7).getBlock() == this) {
            --var5;
            if (var5 <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      BlockState var3 = var2.getLevel().getBlockState(var2.getClickedPos());
      if (var3.getBlock() == this) {
         return this.countFaces(var3) < PROPERTY_BY_DIRECTION.size();
      } else {
         return super.canBeReplaced(var1, var2);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      boolean var3 = var2.getBlock() == this;
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

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(UP, NORTH, EAST, SOUTH, WEST);
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
      UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
      WEST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
      NORTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   }
}
