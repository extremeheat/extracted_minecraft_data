package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnderChestBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape SHAPE;
   public static final TranslatableComponent CONTAINER_TITLE;

   protected EnderChestBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public boolean hasCustomBreakingProgress(BlockState var1) {
      return true;
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite())).setValue(WATERLOGGED, var2.getType() == Fluids.WATER);
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      PlayerEnderChestContainer var7 = var4.getEnderChestInventory();
      BlockEntity var8 = var2.getBlockEntity(var3);
      if (var7 != null && var8 instanceof EnderChestBlockEntity) {
         BlockPos var9 = var3.above();
         if (var2.getBlockState(var9).isRedstoneConductor(var2, var9)) {
            return true;
         } else if (var2.isClientSide) {
            return true;
         } else {
            EnderChestBlockEntity var10 = (EnderChestBlockEntity)var8;
            var7.setActiveChest(var10);
            var4.openMenu(new SimpleMenuProvider((var1x, var2x, var3x) -> {
               return ChestMenu.threeRows(var1x, var2x, var7);
            }, CONTAINER_TITLE));
            var4.awardStat(Stats.OPEN_ENDERCHEST);
            return true;
         }
      } else {
         return true;
      }
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new EnderChestBlockEntity();
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      for(int var5 = 0; var5 < 3; ++var5) {
         int var6 = var4.nextInt(2) * 2 - 1;
         int var7 = var4.nextInt(2) * 2 - 1;
         double var8 = (double)var3.getX() + 0.5D + 0.25D * (double)var6;
         double var10 = (double)((float)var3.getY() + var4.nextFloat());
         double var12 = (double)var3.getZ() + 0.5D + 0.25D * (double)var7;
         double var14 = (double)(var4.nextFloat() * (float)var6);
         double var16 = ((double)var4.nextFloat() - 0.5D) * 0.125D;
         double var18 = (double)(var4.nextFloat() * (float)var7);
         var2.addParticle(ParticleTypes.PORTAL, var8, var10, var12, var14, var16, var18);
      }

   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, WATERLOGGED);
   }

   public FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
      CONTAINER_TITLE = new TranslatableComponent("container.enderchest", new Object[0]);
   }
}
