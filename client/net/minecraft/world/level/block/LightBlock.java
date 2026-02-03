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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<LightBlock> CODEC = simpleCodec(LightBlock::new);
   public static final int MAX_LEVEL = 15;
   public static final IntegerProperty LEVEL;
   public static final BooleanProperty WATERLOGGED;
   public static final ToIntFunction<BlockState> LIGHT_EMISSION;

   public MapCodec<LightBlock> codec() {
      return CODEC;
   }

   public LightBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 15)).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(LEVEL, WATERLOGGED);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!level.isClientSide() && player.canUseGameMasterBlocks()) {
         level.setBlock(pos, (BlockState)state.cycle(LEVEL), 2);
         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.CONSUME;
      }
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return context.isHoldingItem(Items.LIGHT) ? Shapes.block() : Shapes.empty();
   }

   protected boolean propagatesSkylightDown(final BlockState state) {
      return state.getFluidState().isEmpty();
   }

   protected RenderShape getRenderShape(final BlockState state) {
      return RenderShape.INVISIBLE;
   }

   protected float getShadeBrightness(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return 1.0F;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction direction, final BlockPos neighbourPos, final BlockState neighbour, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighbour, random);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return setLightOnStack(super.getCloneItemStack(level, pos, state, includeData), (Integer)state.getValue(LEVEL));
   }

   public static ItemStack setLightOnStack(final ItemStack result, final int lightLevel) {
      result.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(LEVEL, lightLevel));
      return result;
   }

   static {
      LEVEL = BlockStateProperties.LEVEL;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      LIGHT_EMISSION = (state) -> (Integer)state.getValue(LEVEL);
   }
}
