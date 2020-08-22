package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AnvilBlock extends FallingBlock {
   public static final DirectionProperty FACING;
   private static final VoxelShape BASE;
   private static final VoxelShape X_LEG1;
   private static final VoxelShape X_LEG2;
   private static final VoxelShape X_TOP;
   private static final VoxelShape Z_LEG1;
   private static final VoxelShape Z_LEG2;
   private static final VoxelShape Z_TOP;
   private static final VoxelShape X_AXIS_AABB;
   private static final VoxelShape Z_AXIS_AABB;
   private static final TranslatableComponent CONTAINER_TITLE;

   public AnvilBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getClockWise());
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var4.openMenu(var1.getMenuProvider(var2, var3));
         var4.awardStat(Stats.INTERACT_WITH_ANVIL);
         return InteractionResult.SUCCESS;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return new SimpleMenuProvider((var2x, var3x, var4) -> {
         return new AnvilMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3));
      }, CONTAINER_TITLE);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction var5 = (Direction)var1.getValue(FACING);
      return var5.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
   }

   protected void falling(FallingBlockEntity var1) {
      var1.setHurtsEntities(true);
   }

   public void onLand(Level var1, BlockPos var2, BlockState var3, BlockState var4) {
      var1.levelEvent(1031, var2, 0);
   }

   public void onBroken(Level var1, BlockPos var2) {
      var1.levelEvent(1029, var2, 0);
   }

   @Nullable
   public static BlockState damage(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 == Blocks.ANVIL) {
         return (BlockState)Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(FACING, var0.getValue(FACING));
      } else {
         return var1 == Blocks.CHIPPED_ANVIL ? (BlockState)Blocks.DAMAGED_ANVIL.defaultBlockState().setValue(FACING, var0.getValue(FACING)) : null;
      }
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(FACING);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
      X_LEG1 = Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
      X_LEG2 = Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
      X_TOP = Block.box(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
      Z_LEG1 = Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
      Z_LEG2 = Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
      Z_TOP = Block.box(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
      X_AXIS_AABB = Shapes.or(BASE, X_LEG1, X_LEG2, X_TOP);
      Z_AXIS_AABB = Shapes.or(BASE, Z_LEG1, Z_LEG2, Z_TOP);
      CONTAINER_TITLE = new TranslatableComponent("container.repair", new Object[0]);
   }
}
