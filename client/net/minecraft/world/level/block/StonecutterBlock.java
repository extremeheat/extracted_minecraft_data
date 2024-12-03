package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StonecutterBlock extends Block {
   public static final MapCodec<StonecutterBlock> CODEC = simpleCodec(StonecutterBlock::new);
   private static final Component CONTAINER_TITLE = Component.translatable("container.stonecutter");
   public static final EnumProperty<Direction> FACING;
   protected static final VoxelShape SHAPE;

   public MapCodec<StonecutterBlock> codec() {
      return CODEC;
   }

   public StonecutterBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var2.isClientSide) {
         var4.openMenu(var1.getMenuProvider(var2, var3));
         var4.awardStat(Stats.INTERACT_WITH_STONECUTTER);
      }

      return InteractionResult.SUCCESS;
   }

   @Nullable
   protected MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return new SimpleMenuProvider((var2x, var3x, var4) -> new StonecutterMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3)), CONTAINER_TITLE);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
   }
}
