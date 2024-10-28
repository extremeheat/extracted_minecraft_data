package net.minecraft.world.level.material;

import java.util.Optional;
import javax.annotation.Nullable;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

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

   public void animateTick(Level var1, BlockPos var2, FluidState var3, RandomSource var4) {
      if (!var3.isSource() && !(Boolean)var3.getValue(FALLING)) {
         if (var4.nextInt(64) == 0) {
            var1.playLocalSound((double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, var4.nextFloat() * 0.25F + 0.75F, var4.nextFloat() + 0.5F, false);
         }
      } else if (var4.nextInt(10) == 0) {
         var1.addParticle(ParticleTypes.UNDERWATER, (double)var2.getX() + var4.nextDouble(), (double)var2.getY() + var4.nextDouble(), (double)var2.getZ() + var4.nextDouble(), 0.0, 0.0, 0.0);
      }

   }

   @Nullable
   public ParticleOptions getDripParticle() {
      return ParticleTypes.DRIPPING_WATER;
   }

   protected boolean canConvertToSource(ServerLevel var1) {
      return var1.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
   }

   protected void beforeDestroyingBlock(LevelAccessor var1, BlockPos var2, BlockState var3) {
      BlockEntity var4 = var3.hasBlockEntity() ? var1.getBlockEntity(var2) : null;
      Block.dropResources(var3, var1, var2, var4);
   }

   public int getSlopeFindDistance(LevelReader var1) {
      return 4;
   }

   public BlockState createLegacyBlock(FluidState var1) {
      return (BlockState)Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(var1));
   }

   public boolean isSame(Fluid var1) {
      return var1 == Fluids.WATER || var1 == Fluids.FLOWING_WATER;
   }

   public int getDropOff(LevelReader var1) {
      return 1;
   }

   public int getTickDelay(LevelReader var1) {
      return 5;
   }

   public boolean canBeReplacedWith(FluidState var1, BlockGetter var2, BlockPos var3, Fluid var4, Direction var5) {
      return var5 == Direction.DOWN && !var4.is(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public Optional<SoundEvent> getPickupSound() {
      return Optional.of(SoundEvents.BUCKET_FILL);
   }

   public static class Flowing extends WaterFluid {
      public Flowing() {
         super();
      }

      protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> var1) {
         super.createFluidStateDefinition(var1);
         var1.add(LEVEL);
      }

      public int getAmount(FluidState var1) {
         return (Integer)var1.getValue(LEVEL);
      }

      public boolean isSource(FluidState var1) {
         return false;
      }
   }

   public static class Source extends WaterFluid {
      public Source() {
         super();
      }

      public int getAmount(FluidState var1) {
         return 8;
      }

      public boolean isSource(FluidState var1) {
         return true;
      }
   }
}
