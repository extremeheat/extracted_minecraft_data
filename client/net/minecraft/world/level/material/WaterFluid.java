package net.minecraft.world.level.material;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;

public abstract class WaterFluid extends FlowingFluid {
   public WaterFluid() {
      super();
   }

   public Fluid getFlowing() {
      return Fluids.FLOWING_WATER;
   }

   public Fluid getSource() {
      return Fluids.WATER;
   }

   public Item getBucket() {
      return Items.WATER_BUCKET;
   }

   public void animateTick(final Level level, final BlockPos pos, final FluidState fluidState, final RandomSource random) {
      if (!fluidState.isSource() && !(Boolean)fluidState.getValue(FALLING)) {
         if (random.nextInt(64) == 0) {
            level.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.WATER_AMBIENT, SoundSource.AMBIENT, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
         }
      } else if (random.nextInt(10) == 0) {
         level.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
      }

   }

   public @Nullable ParticleOptions getDripParticle() {
      return ParticleTypes.DRIPPING_WATER;
   }

   protected boolean canConvertToSource(final ServerLevel level) {
      return (Boolean)level.getGameRules().get(GameRules.WATER_SOURCE_CONVERSION);
   }

   protected void beforeDestroyingBlock(final LevelAccessor level, final BlockPos pos, final BlockState state) {
      BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
      Block.dropResources(state, level, pos, blockEntity);
   }

   protected void entityInside(final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier) {
      effectApplier.apply(InsideBlockEffectType.EXTINGUISH);
   }

   public int getSlopeFindDistance(final LevelReader level) {
      return 4;
   }

   public BlockState createLegacyBlock(final FluidState fluidState) {
      return (BlockState)Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
   }

   public boolean isSame(final Fluid other) {
      return other == Fluids.WATER || other == Fluids.FLOWING_WATER;
   }

   public int getDropOff(final LevelReader level) {
      return 1;
   }

   public int getTickDelay(final LevelReader level) {
      return 5;
   }

   public boolean canBeReplacedWith(final FluidState state, final BlockGetter level, final BlockPos pos, final Fluid other, final Direction direction) {
      return direction == Direction.DOWN && !other.is(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public Optional<SoundEvent> getPickupSound() {
      return Optional.of(SoundEvents.BUCKET_FILL);
   }

   public static class Source extends WaterFluid {
      public Source() {
         super();
      }

      public int getAmount(final FluidState fluidState) {
         return 8;
      }

      public boolean isSource(final FluidState fluidState) {
         return true;
      }
   }

   public static class Flowing extends WaterFluid {
      public Flowing() {
         super();
      }

      protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> builder) {
         super.createFluidStateDefinition(builder);
         builder.add(LEVEL);
      }

      public int getAmount(final FluidState fluidState) {
         return (Integer)fluidState.getValue(LEVEL);
      }

      public boolean isSource(final FluidState fluidState) {
         return false;
      }
   }
}
