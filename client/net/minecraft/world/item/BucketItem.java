package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
      HitResult var5 = getPlayerPOVHitResult(var1, var2, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
      if (var5.getType() == HitResult.Type.MISS) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else if (var5.getType() != HitResult.Type.BLOCK) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else {
         BlockHitResult var6 = (BlockHitResult)var5;
         BlockPos var7 = var6.getBlockPos();
         if (var1.mayInteract(var2, var7) && var2.mayUseItemAt(var7, var6.getDirection(), var4)) {
            BlockState var8;
            if (this.content == Fluids.EMPTY) {
               var8 = var1.getBlockState(var7);
               if (var8.getBlock() instanceof BucketPickup) {
                  Fluid var11 = ((BucketPickup)var8.getBlock()).takeLiquid(var1, var7, var8);
                  if (var11 != Fluids.EMPTY) {
                     var2.awardStat(Stats.ITEM_USED.get(this));
                     var2.playSound(var11.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, 1.0F, 1.0F);
                     ItemStack var10 = this.createResultItem(var4, var2, var11.getBucket());
                     if (!var1.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)var2, new ItemStack(var11.getBucket()));
                     }

                     return new InteractionResultHolder(InteractionResult.SUCCESS, var10);
                  }
               }

               return new InteractionResultHolder(InteractionResult.FAIL, var4);
            } else {
               var8 = var1.getBlockState(var7);
               BlockPos var9 = var8.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? var7 : var6.getBlockPos().relative(var6.getDirection());
               if (this.emptyBucket(var2, var1, var9, var6)) {
                  this.checkExtraContent(var1, var4, var9);
                  if (var2 instanceof ServerPlayer) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var2, var9, var4);
                  }

                  var2.awardStat(Stats.ITEM_USED.get(this));
                  return new InteractionResultHolder(InteractionResult.SUCCESS, this.getEmptySuccessItem(var4, var2));
               } else {
                  return new InteractionResultHolder(InteractionResult.FAIL, var4);
               }
            }
         } else {
            return new InteractionResultHolder(InteractionResult.FAIL, var4);
         }
      }
   }

   protected ItemStack getEmptySuccessItem(ItemStack var1, Player var2) {
      return !var2.abilities.instabuild ? new ItemStack(Items.BUCKET) : var1;
   }

   public void checkExtraContent(Level var1, ItemStack var2, BlockPos var3) {
   }

   private ItemStack createResultItem(ItemStack var1, Player var2, Item var3) {
      if (var2.abilities.instabuild) {
         return var1;
      } else {
         var1.shrink(1);
         if (var1.isEmpty()) {
            return new ItemStack(var3);
         } else {
            if (!var2.inventory.add(new ItemStack(var3))) {
               var2.drop(new ItemStack(var3), false);
            }

            return var1;
         }
      }
   }

   public boolean emptyBucket(@Nullable Player var1, Level var2, BlockPos var3, @Nullable BlockHitResult var4) {
      if (!(this.content instanceof FlowingFluid)) {
         return false;
      } else {
         BlockState var5 = var2.getBlockState(var3);
         Material var6 = var5.getMaterial();
         boolean var7 = !var6.isSolid();
         boolean var8 = var6.isReplaceable();
         if (var2.isEmptyBlock(var3) || var7 || var8 || var5.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer)var5.getBlock()).canPlaceLiquid(var2, var3, var5, this.content)) {
            if (var2.dimension.isUltraWarm() && this.content.is(FluidTags.WATER)) {
               int var9 = var3.getX();
               int var10 = var3.getY();
               int var11 = var3.getZ();
               var2.playSound(var1, var3, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var2.random.nextFloat() - var2.random.nextFloat()) * 0.8F);

               for(int var12 = 0; var12 < 8; ++var12) {
                  var2.addParticle(ParticleTypes.LARGE_SMOKE, (double)var9 + Math.random(), (double)var10 + Math.random(), (double)var11 + Math.random(), 0.0D, 0.0D, 0.0D);
               }
            } else if (var5.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER) {
               if (((LiquidBlockContainer)var5.getBlock()).placeLiquid(var2, var3, var5, ((FlowingFluid)this.content).getSource(false))) {
                  this.playEmptySound(var1, var2, var3);
               }
            } else {
               if (!var2.isClientSide && (var7 || var8) && !var6.isLiquid()) {
                  var2.destroyBlock(var3, true);
               }

               this.playEmptySound(var1, var2, var3);
               var2.setBlock(var3, this.content.defaultFluidState().createLegacyBlock(), 11);
            }

            return true;
         } else {
            return var4 == null ? false : this.emptyBucket(var1, var2, var4.getBlockPos().relative(var4.getDirection()), (BlockHitResult)null);
         }
      }
   }

   protected void playEmptySound(@Nullable Player var1, LevelAccessor var2, BlockPos var3) {
      SoundEvent var4 = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
      var2.playSound(var1, var3, var4, SoundSource.BLOCKS, 1.0F, 1.0F);
   }
}
