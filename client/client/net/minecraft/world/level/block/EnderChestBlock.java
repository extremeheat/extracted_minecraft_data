package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

public class EnderChestBlock extends AbstractChestBlock<EnderChestBlockEntity> implements SimpleWaterloggedBlock {
   public static final MapCodec<EnderChestBlock> CODEC = simpleCodec(EnderChestBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
   private static final Component CONTAINER_TITLE = Component.translatable("container.enderchest");

   @Override
   public MapCodec<EnderChestBlock> codec() {
      return CODEC;
   }

   protected EnderChestBlock(BlockBehaviour.Properties var1) {
      super(var1, () -> BlockEntityType.ENDER_CHEST);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   @Override
   public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState var1, Level var2, BlockPos var3, boolean var4) {
      return DoubleBlockCombiner.Combiner::acceptNone;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return this.defaultBlockState()
         .setValue(FACING, var1.getHorizontalDirection().getOpposite())
         .setValue(WATERLOGGED, Boolean.valueOf(var2.getType() == Fluids.WATER));
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      PlayerEnderChestContainer var6 = var4.getEnderChestInventory();
      BlockEntity var7 = var2.getBlockEntity(var3);
      if (var6 != null && var7 instanceof EnderChestBlockEntity) {
         BlockPos var8 = var3.above();
         if (var2.getBlockState(var8).isRedstoneConductor(var2, var8)) {
            return InteractionResult.sidedSuccess(var2.isClientSide);
         } else if (var2.isClientSide) {
            return InteractionResult.SUCCESS;
         } else {
            EnderChestBlockEntity var9 = (EnderChestBlockEntity)var7;
            var6.setActiveChest(var9);
            var4.openMenu(new SimpleMenuProvider((var1x, var2x, var3x) -> ChestMenu.threeRows(var1x, var2x, var6), CONTAINER_TITLE));
            var4.awardStat(Stats.OPEN_ENDERCHEST);
            PiglinAi.angerNearbyPiglins(var4, true);
            return InteractionResult.CONSUME;
         }
      } else {
         return InteractionResult.sidedSuccess(var2.isClientSide);
      }
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new EnderChestBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? createTickerHelper(var3, BlockEntityType.ENDER_CHEST, EnderChestBlockEntity::lidAnimateTick) : null;
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      for (int var5 = 0; var5 < 3; var5++) {
         int var6 = var4.nextInt(2) * 2 - 1;
         int var7 = var4.nextInt(2) * 2 - 1;
         double var8 = (double)var3.getX() + 0.5 + 0.25 * (double)var6;
         double var10 = (double)((float)var3.getY() + var4.nextFloat());
         double var12 = (double)var3.getZ() + 0.5 + 0.25 * (double)var7;
         double var14 = (double)(var4.nextFloat() * (float)var6);
         double var16 = ((double)var4.nextFloat() - 0.5) * 0.125;
         double var18 = (double)(var4.nextFloat() * (float)var7);
         var2.addParticle(ParticleTypes.PORTAL, var8, var10, var12, var14, var16, var18);
      }
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
      var1.add(FACING, WATERLOGGED);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof EnderChestBlockEntity) {
         ((EnderChestBlockEntity)var5).recheckOpen();
      }
   }
}
