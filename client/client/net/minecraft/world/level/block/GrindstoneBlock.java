package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrindstoneBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final MapCodec<GrindstoneBlock> CODEC = simpleCodec(GrindstoneBlock::new);
   public static final VoxelShape FLOOR_NORTH_SOUTH_LEFT_POST = Block.box(2.0, 0.0, 6.0, 4.0, 7.0, 10.0);
   public static final VoxelShape FLOOR_NORTH_SOUTH_RIGHT_POST = Block.box(12.0, 0.0, 6.0, 14.0, 7.0, 10.0);
   public static final VoxelShape FLOOR_NORTH_SOUTH_LEFT_PIVOT = Block.box(2.0, 7.0, 5.0, 4.0, 13.0, 11.0);
   public static final VoxelShape FLOOR_NORTH_SOUTH_RIGHT_PIVOT = Block.box(12.0, 7.0, 5.0, 14.0, 13.0, 11.0);
   public static final VoxelShape FLOOR_NORTH_SOUTH_LEFT_LEG = Shapes.or(FLOOR_NORTH_SOUTH_LEFT_POST, FLOOR_NORTH_SOUTH_LEFT_PIVOT);
   public static final VoxelShape FLOOR_NORTH_SOUTH_RIGHT_LEG = Shapes.or(FLOOR_NORTH_SOUTH_RIGHT_POST, FLOOR_NORTH_SOUTH_RIGHT_PIVOT);
   public static final VoxelShape FLOOR_NORTH_SOUTH_ALL_LEGS = Shapes.or(FLOOR_NORTH_SOUTH_LEFT_LEG, FLOOR_NORTH_SOUTH_RIGHT_LEG);
   public static final VoxelShape FLOOR_NORTH_SOUTH_GRINDSTONE = Shapes.or(FLOOR_NORTH_SOUTH_ALL_LEGS, Block.box(4.0, 4.0, 2.0, 12.0, 16.0, 14.0));
   public static final VoxelShape FLOOR_EAST_WEST_LEFT_POST = Block.box(6.0, 0.0, 2.0, 10.0, 7.0, 4.0);
   public static final VoxelShape FLOOR_EAST_WEST_RIGHT_POST = Block.box(6.0, 0.0, 12.0, 10.0, 7.0, 14.0);
   public static final VoxelShape FLOOR_EAST_WEST_LEFT_PIVOT = Block.box(5.0, 7.0, 2.0, 11.0, 13.0, 4.0);
   public static final VoxelShape FLOOR_EAST_WEST_RIGHT_PIVOT = Block.box(5.0, 7.0, 12.0, 11.0, 13.0, 14.0);
   public static final VoxelShape FLOOR_EAST_WEST_LEFT_LEG = Shapes.or(FLOOR_EAST_WEST_LEFT_POST, FLOOR_EAST_WEST_LEFT_PIVOT);
   public static final VoxelShape FLOOR_EAST_WEST_RIGHT_LEG = Shapes.or(FLOOR_EAST_WEST_RIGHT_POST, FLOOR_EAST_WEST_RIGHT_PIVOT);
   public static final VoxelShape FLOOR_EAST_WEST_ALL_LEGS = Shapes.or(FLOOR_EAST_WEST_LEFT_LEG, FLOOR_EAST_WEST_RIGHT_LEG);
   public static final VoxelShape FLOOR_EAST_WEST_GRINDSTONE = Shapes.or(FLOOR_EAST_WEST_ALL_LEGS, Block.box(2.0, 4.0, 4.0, 14.0, 16.0, 12.0));
   public static final VoxelShape WALL_SOUTH_LEFT_POST = Block.box(2.0, 6.0, 0.0, 4.0, 10.0, 7.0);
   public static final VoxelShape WALL_SOUTH_RIGHT_POST = Block.box(12.0, 6.0, 0.0, 14.0, 10.0, 7.0);
   public static final VoxelShape WALL_SOUTH_LEFT_PIVOT = Block.box(2.0, 5.0, 7.0, 4.0, 11.0, 13.0);
   public static final VoxelShape WALL_SOUTH_RIGHT_PIVOT = Block.box(12.0, 5.0, 7.0, 14.0, 11.0, 13.0);
   public static final VoxelShape WALL_SOUTH_LEFT_LEG = Shapes.or(WALL_SOUTH_LEFT_POST, WALL_SOUTH_LEFT_PIVOT);
   public static final VoxelShape WALL_SOUTH_RIGHT_LEG = Shapes.or(WALL_SOUTH_RIGHT_POST, WALL_SOUTH_RIGHT_PIVOT);
   public static final VoxelShape WALL_SOUTH_ALL_LEGS = Shapes.or(WALL_SOUTH_LEFT_LEG, WALL_SOUTH_RIGHT_LEG);
   public static final VoxelShape WALL_SOUTH_GRINDSTONE = Shapes.or(WALL_SOUTH_ALL_LEGS, Block.box(4.0, 2.0, 4.0, 12.0, 14.0, 16.0));
   public static final VoxelShape WALL_NORTH_LEFT_POST = Block.box(2.0, 6.0, 7.0, 4.0, 10.0, 16.0);
   public static final VoxelShape WALL_NORTH_RIGHT_POST = Block.box(12.0, 6.0, 7.0, 14.0, 10.0, 16.0);
   public static final VoxelShape WALL_NORTH_LEFT_PIVOT = Block.box(2.0, 5.0, 3.0, 4.0, 11.0, 9.0);
   public static final VoxelShape WALL_NORTH_RIGHT_PIVOT = Block.box(12.0, 5.0, 3.0, 14.0, 11.0, 9.0);
   public static final VoxelShape WALL_NORTH_LEFT_LEG = Shapes.or(WALL_NORTH_LEFT_POST, WALL_NORTH_LEFT_PIVOT);
   public static final VoxelShape WALL_NORTH_RIGHT_LEG = Shapes.or(WALL_NORTH_RIGHT_POST, WALL_NORTH_RIGHT_PIVOT);
   public static final VoxelShape WALL_NORTH_ALL_LEGS = Shapes.or(WALL_NORTH_LEFT_LEG, WALL_NORTH_RIGHT_LEG);
   public static final VoxelShape WALL_NORTH_GRINDSTONE = Shapes.or(WALL_NORTH_ALL_LEGS, Block.box(4.0, 2.0, 0.0, 12.0, 14.0, 12.0));
   public static final VoxelShape WALL_WEST_LEFT_POST = Block.box(7.0, 6.0, 2.0, 16.0, 10.0, 4.0);
   public static final VoxelShape WALL_WEST_RIGHT_POST = Block.box(7.0, 6.0, 12.0, 16.0, 10.0, 14.0);
   public static final VoxelShape WALL_WEST_LEFT_PIVOT = Block.box(3.0, 5.0, 2.0, 9.0, 11.0, 4.0);
   public static final VoxelShape WALL_WEST_RIGHT_PIVOT = Block.box(3.0, 5.0, 12.0, 9.0, 11.0, 14.0);
   public static final VoxelShape WALL_WEST_LEFT_LEG = Shapes.or(WALL_WEST_LEFT_POST, WALL_WEST_LEFT_PIVOT);
   public static final VoxelShape WALL_WEST_RIGHT_LEG = Shapes.or(WALL_WEST_RIGHT_POST, WALL_WEST_RIGHT_PIVOT);
   public static final VoxelShape WALL_WEST_ALL_LEGS = Shapes.or(WALL_WEST_LEFT_LEG, WALL_WEST_RIGHT_LEG);
   public static final VoxelShape WALL_WEST_GRINDSTONE = Shapes.or(WALL_WEST_ALL_LEGS, Block.box(0.0, 2.0, 4.0, 12.0, 14.0, 12.0));
   public static final VoxelShape WALL_EAST_LEFT_POST = Block.box(0.0, 6.0, 2.0, 9.0, 10.0, 4.0);
   public static final VoxelShape WALL_EAST_RIGHT_POST = Block.box(0.0, 6.0, 12.0, 9.0, 10.0, 14.0);
   public static final VoxelShape WALL_EAST_LEFT_PIVOT = Block.box(7.0, 5.0, 2.0, 13.0, 11.0, 4.0);
   public static final VoxelShape WALL_EAST_RIGHT_PIVOT = Block.box(7.0, 5.0, 12.0, 13.0, 11.0, 14.0);
   public static final VoxelShape WALL_EAST_LEFT_LEG = Shapes.or(WALL_EAST_LEFT_POST, WALL_EAST_LEFT_PIVOT);
   public static final VoxelShape WALL_EAST_RIGHT_LEG = Shapes.or(WALL_EAST_RIGHT_POST, WALL_EAST_RIGHT_PIVOT);
   public static final VoxelShape WALL_EAST_ALL_LEGS = Shapes.or(WALL_EAST_LEFT_LEG, WALL_EAST_RIGHT_LEG);
   public static final VoxelShape WALL_EAST_GRINDSTONE = Shapes.or(WALL_EAST_ALL_LEGS, Block.box(4.0, 2.0, 4.0, 16.0, 14.0, 12.0));
   public static final VoxelShape CEILING_NORTH_SOUTH_LEFT_POST = Block.box(2.0, 9.0, 6.0, 4.0, 16.0, 10.0);
   public static final VoxelShape CEILING_NORTH_SOUTH_RIGHT_POST = Block.box(12.0, 9.0, 6.0, 14.0, 16.0, 10.0);
   public static final VoxelShape CEILING_NORTH_SOUTH_LEFT_PIVOT = Block.box(2.0, 3.0, 5.0, 4.0, 9.0, 11.0);
   public static final VoxelShape CEILING_NORTH_SOUTH_RIGHT_PIVOT = Block.box(12.0, 3.0, 5.0, 14.0, 9.0, 11.0);
   public static final VoxelShape CEILING_NORTH_SOUTH_LEFT_LEG = Shapes.or(CEILING_NORTH_SOUTH_LEFT_POST, CEILING_NORTH_SOUTH_LEFT_PIVOT);
   public static final VoxelShape CEILING_NORTH_SOUTH_RIGHT_LEG = Shapes.or(CEILING_NORTH_SOUTH_RIGHT_POST, CEILING_NORTH_SOUTH_RIGHT_PIVOT);
   public static final VoxelShape CEILING_NORTH_SOUTH_ALL_LEGS = Shapes.or(CEILING_NORTH_SOUTH_LEFT_LEG, CEILING_NORTH_SOUTH_RIGHT_LEG);
   public static final VoxelShape CEILING_NORTH_SOUTH_GRINDSTONE = Shapes.or(CEILING_NORTH_SOUTH_ALL_LEGS, Block.box(4.0, 0.0, 2.0, 12.0, 12.0, 14.0));
   public static final VoxelShape CEILING_EAST_WEST_LEFT_POST = Block.box(6.0, 9.0, 2.0, 10.0, 16.0, 4.0);
   public static final VoxelShape CEILING_EAST_WEST_RIGHT_POST = Block.box(6.0, 9.0, 12.0, 10.0, 16.0, 14.0);
   public static final VoxelShape CEILING_EAST_WEST_LEFT_PIVOT = Block.box(5.0, 3.0, 2.0, 11.0, 9.0, 4.0);
   public static final VoxelShape CEILING_EAST_WEST_RIGHT_PIVOT = Block.box(5.0, 3.0, 12.0, 11.0, 9.0, 14.0);
   public static final VoxelShape CEILING_EAST_WEST_LEFT_LEG = Shapes.or(CEILING_EAST_WEST_LEFT_POST, CEILING_EAST_WEST_LEFT_PIVOT);
   public static final VoxelShape CEILING_EAST_WEST_RIGHT_LEG = Shapes.or(CEILING_EAST_WEST_RIGHT_POST, CEILING_EAST_WEST_RIGHT_PIVOT);
   public static final VoxelShape CEILING_EAST_WEST_ALL_LEGS = Shapes.or(CEILING_EAST_WEST_LEFT_LEG, CEILING_EAST_WEST_RIGHT_LEG);
   public static final VoxelShape CEILING_EAST_WEST_GRINDSTONE = Shapes.or(CEILING_EAST_WEST_ALL_LEGS, Block.box(2.0, 0.0, 4.0, 14.0, 12.0, 12.0));
   private static final Component CONTAINER_TITLE = Component.translatable("container.grindstone_title");

   @Override
   public MapCodec<GrindstoneBlock> codec() {
      return CODEC;
   }

   protected GrindstoneBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   private VoxelShape getVoxelShape(BlockState var1) {
      Direction var2 = var1.getValue(FACING);
      switch ((AttachFace)var1.getValue(FACE)) {
         case FLOOR:
            if (var2 != Direction.NORTH && var2 != Direction.SOUTH) {
               return FLOOR_EAST_WEST_GRINDSTONE;
            }

            return FLOOR_NORTH_SOUTH_GRINDSTONE;
         case WALL:
            if (var2 == Direction.NORTH) {
               return WALL_NORTH_GRINDSTONE;
            } else if (var2 == Direction.SOUTH) {
               return WALL_SOUTH_GRINDSTONE;
            } else {
               if (var2 == Direction.EAST) {
                  return WALL_EAST_GRINDSTONE;
               }

               return WALL_WEST_GRINDSTONE;
            }
         case CEILING:
            if (var2 != Direction.NORTH && var2 != Direction.SOUTH) {
               return CEILING_EAST_WEST_GRINDSTONE;
            }

            return CEILING_NORTH_SOUTH_GRINDSTONE;
         default:
            return FLOOR_EAST_WEST_GRINDSTONE;
      }
   }

   @Override
   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return true;
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var4.openMenu(var1.getMenuProvider(var2, var3));
         var4.awardStat(Stats.INTERACT_WITH_GRINDSTONE);
         return InteractionResult.CONSUME;
      }
   }

   @Override
   protected MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return new SimpleMenuProvider((var2x, var3x, var4) -> new GrindstoneMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3)), CONTAINER_TITLE);
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, FACE);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
