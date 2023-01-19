package net.minecraft.world.level.material;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class LavaFluid extends FlowingFluid {
   public static final float MIN_LEVEL_CUTOFF = 0.44444445F;

   public LavaFluid() {
      super();
   }

   @Override
   public Fluid getFlowing() {
      return Fluids.FLOWING_LAVA;
   }

   @Override
   public Fluid getSource() {
      return Fluids.LAVA;
   }

   @Override
   public Item getBucket() {
      return Items.LAVA_BUCKET;
   }

   @Override
   public void animateTick(Level var1, BlockPos var2, FluidState var3, RandomSource var4) {
      BlockPos var5 = var2.above();
      if (var1.getBlockState(var5).isAir() && !var1.getBlockState(var5).isSolidRender(var1, var5)) {
         if (var4.nextInt(100) == 0) {
            double var6 = (double)var2.getX() + var4.nextDouble();
            double var8 = (double)var2.getY() + 1.0;
            double var10 = (double)var2.getZ() + var4.nextDouble();
            var1.addParticle(ParticleTypes.LAVA, var6, var8, var10, 0.0, 0.0, 0.0);
            var1.playLocalSound(
               var6, var8, var10, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false
            );
         }

         if (var4.nextInt(200) == 0) {
            var1.playLocalSound(
               (double)var2.getX(),
               (double)var2.getY(),
               (double)var2.getZ(),
               SoundEvents.LAVA_AMBIENT,
               SoundSource.BLOCKS,
               0.2F + var4.nextFloat() * 0.2F,
               0.9F + var4.nextFloat() * 0.15F,
               false
            );
         }
      }
   }

   @Override
   public void randomTick(Level var1, BlockPos var2, FluidState var3, RandomSource var4) {
      if (var1.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         int var5 = var4.nextInt(3);
         if (var5 > 0) {
            BlockPos var6 = var2;

            for(int var7 = 0; var7 < var5; ++var7) {
               var6 = var6.offset(var4.nextInt(3) - 1, 1, var4.nextInt(3) - 1);
               if (!var1.isLoaded(var6)) {
                  return;
               }

               BlockState var8 = var1.getBlockState(var6);
               if (var8.isAir()) {
                  if (this.hasFlammableNeighbours(var1, var6)) {
                     var1.setBlockAndUpdate(var6, BaseFireBlock.getState(var1, var6));
                     return;
                  }
               } else if (var8.getMaterial().blocksMotion()) {
                  return;
               }
            }
         } else {
            for(int var9 = 0; var9 < 3; ++var9) {
               BlockPos var10 = var2.offset(var4.nextInt(3) - 1, 0, var4.nextInt(3) - 1);
               if (!var1.isLoaded(var10)) {
                  return;
               }

               if (var1.isEmptyBlock(var10.above()) && this.isFlammable(var1, var10)) {
                  var1.setBlockAndUpdate(var10.above(), BaseFireBlock.getState(var1, var10));
               }
            }
         }
      }
   }

   private boolean hasFlammableNeighbours(LevelReader var1, BlockPos var2) {
      for(Direction var6 : Direction.values()) {
         if (this.isFlammable(var1, var2.relative(var6))) {
            return true;
         }
      }

      return false;
   }

   private boolean isFlammable(LevelReader var1, BlockPos var2) {
      return var2.getY() >= var1.getMinBuildHeight() && var2.getY() < var1.getMaxBuildHeight() && !var1.hasChunkAt(var2)
         ? false
         : var1.getBlockState(var2).getMaterial().isFlammable();
   }

   @Nullable
   @Override
   public ParticleOptions getDripParticle() {
      return ParticleTypes.DRIPPING_LAVA;
   }

   @Override
   protected void beforeDestroyingBlock(LevelAccessor var1, BlockPos var2, BlockState var3) {
      this.fizz(var1, var2);
   }

   @Override
   public int getSlopeFindDistance(LevelReader var1) {
      return var1.dimensionType().ultraWarm() ? 4 : 2;
   }

   @Override
   public BlockState createLegacyBlock(FluidState var1) {
      return Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(var1)));
   }

   @Override
   public boolean isSame(Fluid var1) {
      return var1 == Fluids.LAVA || var1 == Fluids.FLOWING_LAVA;
   }

   @Override
   public int getDropOff(LevelReader var1) {
      return var1.dimensionType().ultraWarm() ? 1 : 2;
   }

   @Override
   public boolean canBeReplacedWith(FluidState var1, BlockGetter var2, BlockPos var3, Fluid var4, Direction var5) {
      return var1.getHeight(var2, var3) >= 0.44444445F && var4.is(FluidTags.WATER);
   }

   @Override
   public int getTickDelay(LevelReader var1) {
      return var1.dimensionType().ultraWarm() ? 10 : 30;
   }

   @Override
   public int getSpreadDelay(Level var1, BlockPos var2, FluidState var3, FluidState var4) {
      int var5 = this.getTickDelay(var1);
      if (!var3.isEmpty()
         && !var4.isEmpty()
         && !var3.getValue(FALLING)
         && !var4.getValue(FALLING)
         && var4.getHeight(var1, var2) > var3.getHeight(var1, var2)
         && var1.getRandom().nextInt(4) != 0) {
         var5 *= 4;
      }

      return var5;
   }

   private void fizz(LevelAccessor var1, BlockPos var2) {
      var1.levelEvent(1501, var2, 0);
   }

   @Override
   protected boolean canConvertToSource() {
      return false;
   }

   @Override
   protected void spreadTo(LevelAccessor var1, BlockPos var2, BlockState var3, Direction var4, FluidState var5) {
      if (var4 == Direction.DOWN) {
         FluidState var6 = var1.getFluidState(var2);
         if (this.is(FluidTags.LAVA) && var6.is(FluidTags.WATER)) {
            if (var3.getBlock() instanceof LiquidBlock) {
               var1.setBlock(var2, Blocks.STONE.defaultBlockState(), 3);
            }

            this.fizz(var1, var2);
            return;
         }
      }

      super.spreadTo(var1, var2, var3, var4, var5);
   }

   @Override
   protected boolean isRandomlyTicking() {
      return true;
   }

   @Override
   protected float getExplosionResistance() {
      return 100.0F;
   }

   @Override
   public Optional<SoundEvent> getPickupSound() {
      return Optional.of(SoundEvents.BUCKET_FILL_LAVA);
   }

   public static class Flowing extends LavaFluid {
      public Flowing() {
         super();
      }

      @Override
      protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> var1) {
         super.createFluidStateDefinition(var1);
         var1.add(LEVEL);
      }

      @Override
      public int getAmount(FluidState var1) {
         return var1.getValue(LEVEL);
      }

      @Override
      public boolean isSource(FluidState var1) {
         return false;
      }
   }

   public static class Source extends LavaFluid {
      public Source() {
         super();
      }

      @Override
      public int getAmount(FluidState var1) {
         return 8;
      }

      @Override
      public boolean isSource(FluidState var1) {
         return true;
      }
   }
}
