package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
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
   public static final VoxelShape FLOOR_NORTH_SOUTH_LEFT_POST = Block.box(2.0D, 0.0D, 6.0D, 4.0D, 7.0D, 10.0D);
   public static final VoxelShape FLOOR_NORTH_SOUTH_RIGHT_POST = Block.box(12.0D, 0.0D, 6.0D, 14.0D, 7.0D, 10.0D);
   public static final VoxelShape FLOOR_NORTH_SOUTH_LEFT_PIVOT = Block.box(2.0D, 7.0D, 5.0D, 4.0D, 13.0D, 11.0D);
   public static final VoxelShape FLOOR_NORTH_SOUTH_RIGHT_PIVOT = Block.box(12.0D, 7.0D, 5.0D, 14.0D, 13.0D, 11.0D);
   public static final VoxelShape FLOOR_NORTH_SOUTH_LEFT_LEG;
   public static final VoxelShape FLOOR_NORTH_SOUTH_RIGHT_LEG;
   public static final VoxelShape FLOOR_NORTH_SOUTH_ALL_LEGS;
   public static final VoxelShape FLOOR_NORTH_SOUTH_GRINDSTONE;
   public static final VoxelShape FLOOR_EAST_WEST_LEFT_POST;
   public static final VoxelShape FLOOR_EAST_WEST_RIGHT_POST;
   public static final VoxelShape FLOOR_EAST_WEST_LEFT_PIVOT;
   public static final VoxelShape FLOOR_EAST_WEST_RIGHT_PIVOT;
   public static final VoxelShape FLOOR_EAST_WEST_LEFT_LEG;
   public static final VoxelShape FLOOR_EAST_WEST_RIGHT_LEG;
   public static final VoxelShape FLOOR_EAST_WEST_ALL_LEGS;
   public static final VoxelShape FLOOR_EAST_WEST_GRINDSTONE;
   public static final VoxelShape WALL_SOUTH_LEFT_POST;
   public static final VoxelShape WALL_SOUTH_RIGHT_POST;
   public static final VoxelShape WALL_SOUTH_LEFT_PIVOT;
   public static final VoxelShape WALL_SOUTH_RIGHT_PIVOT;
   public static final VoxelShape WALL_SOUTH_LEFT_LEG;
   public static final VoxelShape WALL_SOUTH_RIGHT_LEG;
   public static final VoxelShape WALL_SOUTH_ALL_LEGS;
   public static final VoxelShape WALL_SOUTH_GRINDSTONE;
   public static final VoxelShape WALL_NORTH_LEFT_POST;
   public static final VoxelShape WALL_NORTH_RIGHT_POST;
   public static final VoxelShape WALL_NORTH_LEFT_PIVOT;
   public static final VoxelShape WALL_NORTH_RIGHT_PIVOT;
   public static final VoxelShape WALL_NORTH_LEFT_LEG;
   public static final VoxelShape WALL_NORTH_RIGHT_LEG;
   public static final VoxelShape WALL_NORTH_ALL_LEGS;
   public static final VoxelShape WALL_NORTH_GRINDSTONE;
   public static final VoxelShape WALL_WEST_LEFT_POST;
   public static final VoxelShape WALL_WEST_RIGHT_POST;
   public static final VoxelShape WALL_WEST_LEFT_PIVOT;
   public static final VoxelShape WALL_WEST_RIGHT_PIVOT;
   public static final VoxelShape WALL_WEST_LEFT_LEG;
   public static final VoxelShape WALL_WEST_RIGHT_LEG;
   public static final VoxelShape WALL_WEST_ALL_LEGS;
   public static final VoxelShape WALL_WEST_GRINDSTONE;
   public static final VoxelShape WALL_EAST_LEFT_POST;
   public static final VoxelShape WALL_EAST_RIGHT_POST;
   public static final VoxelShape WALL_EAST_LEFT_PIVOT;
   public static final VoxelShape WALL_EAST_RIGHT_PIVOT;
   public static final VoxelShape WALL_EAST_LEFT_LEG;
   public static final VoxelShape WALL_EAST_RIGHT_LEG;
   public static final VoxelShape WALL_EAST_ALL_LEGS;
   public static final VoxelShape WALL_EAST_GRINDSTONE;
   public static final VoxelShape CEILING_NORTH_SOUTH_LEFT_POST;
   public static final VoxelShape CEILING_NORTH_SOUTH_RIGHT_POST;
   public static final VoxelShape CEILING_NORTH_SOUTH_LEFT_PIVOT;
   public static final VoxelShape CEILING_NORTH_SOUTH_RIGHT_PIVOT;
   public static final VoxelShape CEILING_NORTH_SOUTH_LEFT_LEG;
   public static final VoxelShape CEILING_NORTH_SOUTH_RIGHT_LEG;
   public static final VoxelShape CEILING_NORTH_SOUTH_ALL_LEGS;
   public static final VoxelShape CEILING_NORTH_SOUTH_GRINDSTONE;
   public static final VoxelShape CEILING_EAST_WEST_LEFT_POST;
   public static final VoxelShape CEILING_EAST_WEST_RIGHT_POST;
   public static final VoxelShape CEILING_EAST_WEST_LEFT_PIVOT;
   public static final VoxelShape CEILING_EAST_WEST_RIGHT_PIVOT;
   public static final VoxelShape CEILING_EAST_WEST_LEFT_LEG;
   public static final VoxelShape CEILING_EAST_WEST_RIGHT_LEG;
   public static final VoxelShape CEILING_EAST_WEST_ALL_LEGS;
   public static final VoxelShape CEILING_EAST_WEST_GRINDSTONE;
   private static final Component CONTAINER_TITLE;

   protected GrindstoneBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(FACE, AttachFace.WALL));
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   private VoxelShape getVoxelShape(BlockState var1) {
      Direction var2 = (Direction)var1.getValue(FACING);
      switch((AttachFace)var1.getValue(FACE)) {
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

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return true;
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var4.openMenu(var1.getMenuProvider(var2, var3));
         var4.awardStat(Stats.INTERACT_WITH_GRINDSTONE);
         return InteractionResult.CONSUME;
      }
   }

   public MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return new SimpleMenuProvider((var2x, var3x, var4) -> {
         return new GrindstoneMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3));
      }, CONTAINER_TITLE);
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, FACE);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FLOOR_NORTH_SOUTH_LEFT_LEG = Shapes.or(FLOOR_NORTH_SOUTH_LEFT_POST, FLOOR_NORTH_SOUTH_LEFT_PIVOT);
      FLOOR_NORTH_SOUTH_RIGHT_LEG = Shapes.or(FLOOR_NORTH_SOUTH_RIGHT_POST, FLOOR_NORTH_SOUTH_RIGHT_PIVOT);
      FLOOR_NORTH_SOUTH_ALL_LEGS = Shapes.or(FLOOR_NORTH_SOUTH_LEFT_LEG, FLOOR_NORTH_SOUTH_RIGHT_LEG);
      FLOOR_NORTH_SOUTH_GRINDSTONE = Shapes.or(FLOOR_NORTH_SOUTH_ALL_LEGS, Block.box(4.0D, 4.0D, 2.0D, 12.0D, 16.0D, 14.0D));
      FLOOR_EAST_WEST_LEFT_POST = Block.box(6.0D, 0.0D, 2.0D, 10.0D, 7.0D, 4.0D);
      FLOOR_EAST_WEST_RIGHT_POST = Block.box(6.0D, 0.0D, 12.0D, 10.0D, 7.0D, 14.0D);
      FLOOR_EAST_WEST_LEFT_PIVOT = Block.box(5.0D, 7.0D, 2.0D, 11.0D, 13.0D, 4.0D);
      FLOOR_EAST_WEST_RIGHT_PIVOT = Block.box(5.0D, 7.0D, 12.0D, 11.0D, 13.0D, 14.0D);
      FLOOR_EAST_WEST_LEFT_LEG = Shapes.or(FLOOR_EAST_WEST_LEFT_POST, FLOOR_EAST_WEST_LEFT_PIVOT);
      FLOOR_EAST_WEST_RIGHT_LEG = Shapes.or(FLOOR_EAST_WEST_RIGHT_POST, FLOOR_EAST_WEST_RIGHT_PIVOT);
      FLOOR_EAST_WEST_ALL_LEGS = Shapes.or(FLOOR_EAST_WEST_LEFT_LEG, FLOOR_EAST_WEST_RIGHT_LEG);
      FLOOR_EAST_WEST_GRINDSTONE = Shapes.or(FLOOR_EAST_WEST_ALL_LEGS, Block.box(2.0D, 4.0D, 4.0D, 14.0D, 16.0D, 12.0D));
      WALL_SOUTH_LEFT_POST = Block.box(2.0D, 6.0D, 0.0D, 4.0D, 10.0D, 7.0D);
      WALL_SOUTH_RIGHT_POST = Block.box(12.0D, 6.0D, 0.0D, 14.0D, 10.0D, 7.0D);
      WALL_SOUTH_LEFT_PIVOT = Block.box(2.0D, 5.0D, 7.0D, 4.0D, 11.0D, 13.0D);
      WALL_SOUTH_RIGHT_PIVOT = Block.box(12.0D, 5.0D, 7.0D, 14.0D, 11.0D, 13.0D);
      WALL_SOUTH_LEFT_LEG = Shapes.or(WALL_SOUTH_LEFT_POST, WALL_SOUTH_LEFT_PIVOT);
      WALL_SOUTH_RIGHT_LEG = Shapes.or(WALL_SOUTH_RIGHT_POST, WALL_SOUTH_RIGHT_PIVOT);
      WALL_SOUTH_ALL_LEGS = Shapes.or(WALL_SOUTH_LEFT_LEG, WALL_SOUTH_RIGHT_LEG);
      WALL_SOUTH_GRINDSTONE = Shapes.or(WALL_SOUTH_ALL_LEGS, Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 16.0D));
      WALL_NORTH_LEFT_POST = Block.box(2.0D, 6.0D, 7.0D, 4.0D, 10.0D, 16.0D);
      WALL_NORTH_RIGHT_POST = Block.box(12.0D, 6.0D, 7.0D, 14.0D, 10.0D, 16.0D);
      WALL_NORTH_LEFT_PIVOT = Block.box(2.0D, 5.0D, 3.0D, 4.0D, 11.0D, 9.0D);
      WALL_NORTH_RIGHT_PIVOT = Block.box(12.0D, 5.0D, 3.0D, 14.0D, 11.0D, 9.0D);
      WALL_NORTH_LEFT_LEG = Shapes.or(WALL_NORTH_LEFT_POST, WALL_NORTH_LEFT_PIVOT);
      WALL_NORTH_RIGHT_LEG = Shapes.or(WALL_NORTH_RIGHT_POST, WALL_NORTH_RIGHT_PIVOT);
      WALL_NORTH_ALL_LEGS = Shapes.or(WALL_NORTH_LEFT_LEG, WALL_NORTH_RIGHT_LEG);
      WALL_NORTH_GRINDSTONE = Shapes.or(WALL_NORTH_ALL_LEGS, Block.box(4.0D, 2.0D, 0.0D, 12.0D, 14.0D, 12.0D));
      WALL_WEST_LEFT_POST = Block.box(7.0D, 6.0D, 2.0D, 16.0D, 10.0D, 4.0D);
      WALL_WEST_RIGHT_POST = Block.box(7.0D, 6.0D, 12.0D, 16.0D, 10.0D, 14.0D);
      WALL_WEST_LEFT_PIVOT = Block.box(3.0D, 5.0D, 2.0D, 9.0D, 11.0D, 4.0D);
      WALL_WEST_RIGHT_PIVOT = Block.box(3.0D, 5.0D, 12.0D, 9.0D, 11.0D, 14.0D);
      WALL_WEST_LEFT_LEG = Shapes.or(WALL_WEST_LEFT_POST, WALL_WEST_LEFT_PIVOT);
      WALL_WEST_RIGHT_LEG = Shapes.or(WALL_WEST_RIGHT_POST, WALL_WEST_RIGHT_PIVOT);
      WALL_WEST_ALL_LEGS = Shapes.or(WALL_WEST_LEFT_LEG, WALL_WEST_RIGHT_LEG);
      WALL_WEST_GRINDSTONE = Shapes.or(WALL_WEST_ALL_LEGS, Block.box(0.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D));
      WALL_EAST_LEFT_POST = Block.box(0.0D, 6.0D, 2.0D, 9.0D, 10.0D, 4.0D);
      WALL_EAST_RIGHT_POST = Block.box(0.0D, 6.0D, 12.0D, 9.0D, 10.0D, 14.0D);
      WALL_EAST_LEFT_PIVOT = Block.box(7.0D, 5.0D, 2.0D, 13.0D, 11.0D, 4.0D);
      WALL_EAST_RIGHT_PIVOT = Block.box(7.0D, 5.0D, 12.0D, 13.0D, 11.0D, 14.0D);
      WALL_EAST_LEFT_LEG = Shapes.or(WALL_EAST_LEFT_POST, WALL_EAST_LEFT_PIVOT);
      WALL_EAST_RIGHT_LEG = Shapes.or(WALL_EAST_RIGHT_POST, WALL_EAST_RIGHT_PIVOT);
      WALL_EAST_ALL_LEGS = Shapes.or(WALL_EAST_LEFT_LEG, WALL_EAST_RIGHT_LEG);
      WALL_EAST_GRINDSTONE = Shapes.or(WALL_EAST_ALL_LEGS, Block.box(4.0D, 2.0D, 4.0D, 16.0D, 14.0D, 12.0D));
      CEILING_NORTH_SOUTH_LEFT_POST = Block.box(2.0D, 9.0D, 6.0D, 4.0D, 16.0D, 10.0D);
      CEILING_NORTH_SOUTH_RIGHT_POST = Block.box(12.0D, 9.0D, 6.0D, 14.0D, 16.0D, 10.0D);
      CEILING_NORTH_SOUTH_LEFT_PIVOT = Block.box(2.0D, 3.0D, 5.0D, 4.0D, 9.0D, 11.0D);
      CEILING_NORTH_SOUTH_RIGHT_PIVOT = Block.box(12.0D, 3.0D, 5.0D, 14.0D, 9.0D, 11.0D);
      CEILING_NORTH_SOUTH_LEFT_LEG = Shapes.or(CEILING_NORTH_SOUTH_LEFT_POST, CEILING_NORTH_SOUTH_LEFT_PIVOT);
      CEILING_NORTH_SOUTH_RIGHT_LEG = Shapes.or(CEILING_NORTH_SOUTH_RIGHT_POST, CEILING_NORTH_SOUTH_RIGHT_PIVOT);
      CEILING_NORTH_SOUTH_ALL_LEGS = Shapes.or(CEILING_NORTH_SOUTH_LEFT_LEG, CEILING_NORTH_SOUTH_RIGHT_LEG);
      CEILING_NORTH_SOUTH_GRINDSTONE = Shapes.or(CEILING_NORTH_SOUTH_ALL_LEGS, Block.box(4.0D, 0.0D, 2.0D, 12.0D, 12.0D, 14.0D));
      CEILING_EAST_WEST_LEFT_POST = Block.box(6.0D, 9.0D, 2.0D, 10.0D, 16.0D, 4.0D);
      CEILING_EAST_WEST_RIGHT_POST = Block.box(6.0D, 9.0D, 12.0D, 10.0D, 16.0D, 14.0D);
      CEILING_EAST_WEST_LEFT_PIVOT = Block.box(5.0D, 3.0D, 2.0D, 11.0D, 9.0D, 4.0D);
      CEILING_EAST_WEST_RIGHT_PIVOT = Block.box(5.0D, 3.0D, 12.0D, 11.0D, 9.0D, 14.0D);
      CEILING_EAST_WEST_LEFT_LEG = Shapes.or(CEILING_EAST_WEST_LEFT_POST, CEILING_EAST_WEST_LEFT_PIVOT);
      CEILING_EAST_WEST_RIGHT_LEG = Shapes.or(CEILING_EAST_WEST_RIGHT_POST, CEILING_EAST_WEST_RIGHT_PIVOT);
      CEILING_EAST_WEST_ALL_LEGS = Shapes.or(CEILING_EAST_WEST_LEFT_LEG, CEILING_EAST_WEST_RIGHT_LEG);
      CEILING_EAST_WEST_GRINDSTONE = Shapes.or(CEILING_EAST_WEST_ALL_LEGS, Block.box(2.0D, 0.0D, 4.0D, 14.0D, 12.0D, 12.0D));
      CONTAINER_TITLE = new TranslatableComponent("container.grindstone_title");
   }
}
