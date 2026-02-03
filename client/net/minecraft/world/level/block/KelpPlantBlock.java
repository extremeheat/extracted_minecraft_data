package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import org.jspecify.annotations.Nullable;

public class KelpPlantBlock extends GrowingPlantBodyBlock implements LiquidBlockContainer {
   public static final MapCodec<KelpPlantBlock> CODEC = simpleCodec(KelpPlantBlock::new);

   public MapCodec<KelpPlantBlock> codec() {
      return CODEC;
   }

   protected KelpPlantBlock(final BlockBehaviour.Properties properties) {
      super(properties, Direction.UP, Shapes.block(), true);
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock)Blocks.KELP;
   }

   protected FluidState getFluidState(final BlockState state) {
      return Fluids.WATER.getSource(false);
   }

   protected boolean canAttachTo(final BlockState state) {
      return this.getHeadBlock().canAttachTo(state);
   }

   public boolean canPlaceLiquid(final @Nullable LivingEntity user, final BlockGetter level, final BlockPos pos, final BlockState state, final Fluid type) {
      return false;
   }

   public boolean placeLiquid(final LevelAccessor level, final BlockPos pos, final BlockState state, final FluidState fluidState) {
      return false;
   }
}
