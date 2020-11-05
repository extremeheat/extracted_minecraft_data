package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BucketItem extends Item {
   private final Fluid content;

   public BucketItem(Fluid var1, Item.Properties var2) {
      super(var2);
      this.content = var1;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      BlockHitResult var5 = getPlayerPOVHitResult(var1, var2, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
      if (var5.getType() == HitResult.Type.MISS) {
         return InteractionResultHolder.pass(var4);
      } else if (var5.getType() != HitResult.Type.BLOCK) {
         return InteractionResultHolder.pass(var4);
      } else {
         BlockHitResult var6 = (BlockHitResult)var5;
         BlockPos var7 = var6.getBlockPos();
         Direction var8 = var6.getDirection();
         BlockPos var9 = var7.relative(var8);
         if (var1.mayInteract(var2, var7) && var2.mayUseItemAt(var9, var8, var4)) {
            BlockState var10;
            if (this.content == Fluids.EMPTY) {
               var10 = var1.getBlockState(var7);
               if (var10.getBlock() instanceof BucketPickup) {
                  Fluid var13 = ((BucketPickup)var10.getBlock()).takeLiquid(var1, var7, var10);
                  if (var13 != Fluids.EMPTY) {
                     var2.awardStat(Stats.ITEM_USED.get(this));
                     var2.playSound(var13.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, 1.0F, 1.0F);
                     ItemStack var12 = ItemUtils.createFilledResult(var4, var2, new ItemStack(var13.getBucket()));
                     if (!var1.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)var2, new ItemStack(var13.getBucket()));
                     }

                     return InteractionResultHolder.sidedSuccess(var12, var1.isClientSide());
                  }
               }

               return InteractionResultHolder.fail(var4);
            } else {
               var10 = var1.getBlockState(var7);
               BlockPos var11 = var10.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? var7 : var9;
               if (this.emptyBucket(var2, var1, var11, var6)) {
                  this.checkExtraContent(var1, var4, var11);
                  if (var2 instanceof ServerPlayer) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var2, var11, var4);
                  }

                  var2.awardStat(Stats.ITEM_USED.get(this));
                  return InteractionResultHolder.sidedSuccess(this.getEmptySuccessItem(var4, var2), var1.isClientSide());
               } else {
                  return InteractionResultHolder.fail(var4);
               }
            }
         } else {
            return InteractionResultHolder.fail(var4);
         }
      }
   }

   protected ItemStack getEmptySuccessItem(ItemStack var1, Player var2) {
      return !var2.abilities.instabuild ? new ItemStack(Items.BUCKET) : var1;
   }

   public void checkExtraContent(Level var1, ItemStack var2, BlockPos var3) {
   }

   public boolean emptyBucket(@Nullable Player var1, Level var2, BlockPos var3, @Nullable BlockHitResult var4) {
      if (!(this.content instanceof FlowingFluid)) {
         return false;
      } else {
         BlockState var5 = var2.getBlockState(var3);
         Block var6 = var5.getBlock();
         Material var7 = var5.getMaterial();
         boolean var8 = var5.canBeReplaced(this.content);
         boolean var9 = var5.isAir() || var8 || var6 instanceof LiquidBlockContainer && ((LiquidBlockContainer)var6).canPlaceLiquid(var2, var3, var5, this.content);
         if (!var9) {
            return var4 != null && this.emptyBucket(var1, var2, var4.getBlockPos().relative(var4.getDirection()), (BlockHitResult)null);
         } else if (var2.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
            int var10 = var3.getX();
            int var11 = var3.getY();
            int var12 = var3.getZ();
            var2.playSound(var1, var3, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var2.random.nextFloat() - var2.random.nextFloat()) * 0.8F);

            for(int var13 = 0; var13 < 8; ++var13) {
               var2.addParticle(ParticleTypes.LARGE_SMOKE, (double)var10 + Math.random(), (double)var11 + Math.random(), (double)var12 + Math.random(), 0.0D, 0.0D, 0.0D);
            }

            return true;
         } else if (var6 instanceof LiquidBlockContainer && this.content == Fluids.WATER) {
            ((LiquidBlockContainer)var6).placeLiquid(var2, var3, var5, ((FlowingFluid)this.content).getSource(false));
            this.playEmptySound(var1, var2, var3);
            return true;
         } else {
            if (!var2.isClientSide && var8 && !var7.isLiquid()) {
               var2.destroyBlock(var3, true);
            }

            if (!var2.setBlock(var3, this.content.defaultFluidState().createLegacyBlock(), 11) && !var5.getFluidState().isSource()) {
               return false;
            } else {
               this.playEmptySound(var1, var2, var3);
               return true;
            }
         }
      }
   }

   protected void playEmptySound(@Nullable Player var1, LevelAccessor var2, BlockPos var3) {
      SoundEvent var4 = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
      var2.playSound(var1, var3, var4, SoundSource.BLOCKS, 1.0F, 1.0F);
   }
}
