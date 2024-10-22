package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<LightBlock> CODEC = simpleCodec(LightBlock::new);
   public static final int MAX_LEVEL = 15;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final ToIntFunction<BlockState> LIGHT_EMISSION = var0 -> var0.getValue(LEVEL);

   @Override
   public MapCodec<LightBlock> codec() {
      return CODEC;
   }

   public LightBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(15)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LEVEL, WATERLOGGED);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var2.isClientSide && var4.canUseGameMasterBlocks()) {
         var2.setBlock(var3, var1.cycle(LEVEL), 2);
         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.CONSUME;
      }
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return var4.isHoldingItem(Items.LIGHT) ? Shapes.block() : Shapes.empty();
   }

   @Override
   protected boolean propagatesSkylightDown(BlockState var1) {
      return var1.getFluidState().isEmpty();
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   @Override
   protected float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return 1.0F;
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return setLightOnStack(super.getCloneItemStack(var1, var2, var3), var3.getValue(LEVEL));
   }

   public static ItemStack setLightOnStack(ItemStack var0, int var1) {
      if (var1 != 15) {
         var0.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(LEVEL, var1));
      }

      return var0;
   }
}
