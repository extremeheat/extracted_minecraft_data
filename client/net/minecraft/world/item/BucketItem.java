package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BucketItem extends Item implements DispensibleContainerItem {
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
         BlockPos var6 = var5.getBlockPos();
         Direction var7 = var5.getDirection();
         BlockPos var8 = var6.relative(var7);
         if (var1.mayInteract(var2, var6) && var2.mayUseItemAt(var8, var7, var4)) {
            BlockState var9;
            ItemStack var11;
            if (this.content == Fluids.EMPTY) {
               var9 = var1.getBlockState(var6);
               Block var14 = var9.getBlock();
               if (var14 instanceof BucketPickup) {
                  BucketPickup var13 = (BucketPickup)var14;
                  var11 = var13.pickupBlock(var2, var1, var6, var9);
                  if (!var11.isEmpty()) {
                     var2.awardStat(Stats.ITEM_USED.get(this));
                     var13.getPickupSound().ifPresent((var1x) -> {
                        var2.playSound(var1x, 1.0F, 1.0F);
                     });
                     var1.gameEvent(var2, GameEvent.FLUID_PICKUP, var6);
                     ItemStack var12 = ItemUtils.createFilledResult(var4, var2, var11);
                     if (!var1.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)var2, var11);
                     }

                     return InteractionResultHolder.sidedSuccess(var12, var1.isClientSide());
                  }
               }

               return InteractionResultHolder.fail(var4);
            } else {
               var9 = var1.getBlockState(var6);
               BlockPos var10 = var9.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? var6 : var8;
               if (this.emptyContents(var2, var1, var10, var5)) {
                  this.checkExtraContent(var2, var1, var4, var10);
                  if (var2 instanceof ServerPlayer) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var2, var10, var4);
                  }

                  var2.awardStat(Stats.ITEM_USED.get(this));
                  var11 = ItemUtils.createFilledResult(var4, var2, getEmptySuccessItem(var4, var2));
                  return InteractionResultHolder.sidedSuccess(var11, var1.isClientSide());
               } else {
                  return InteractionResultHolder.fail(var4);
               }
            }
         } else {
            return InteractionResultHolder.fail(var4);
         }
      }
   }

   public static ItemStack getEmptySuccessItem(ItemStack var0, Player var1) {
      return !var1.hasInfiniteMaterials() ? new ItemStack(Items.BUCKET) : var0;
   }

   public void checkExtraContent(@Nullable Player var1, Level var2, ItemStack var3, BlockPos var4) {
   }

   public boolean emptyContents(@Nullable Player var1, Level var2, BlockPos var3, @Nullable BlockHitResult var4) {
      Fluid var6 = this.content;
      if (!(var6 instanceof FlowingFluid var5)) {
         return false;
      } else {
         Block var7;
         boolean var8;
         LiquidBlockContainer var10;
         BlockState var14;
         boolean var10000;
         label82: {
            var14 = var2.getBlockState(var3);
            var7 = var14.getBlock();
            var8 = var14.canBeReplaced(this.content);
            if (!var14.isAir() && !var8) {
               label80: {
                  if (var7 instanceof LiquidBlockContainer) {
                     var10 = (LiquidBlockContainer)var7;
                     if (var10.canPlaceLiquid(var1, var2, var3, var14, this.content)) {
                        break label80;
                     }
                  }

                  var10000 = false;
                  break label82;
               }
            }

            var10000 = true;
         }

         boolean var9 = var10000;
         if (!var9) {
            return var4 != null && this.emptyContents(var1, var2, var4.getBlockPos().relative(var4.getDirection()), (BlockHitResult)null);
         } else if (var2.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
            int var15 = var3.getX();
            int var11 = var3.getY();
            int var12 = var3.getZ();
            var2.playSound(var1, var3, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var2.random.nextFloat() - var2.random.nextFloat()) * 0.8F);

            for(int var13 = 0; var13 < 8; ++var13) {
               var2.addParticle(ParticleTypes.LARGE_SMOKE, (double)var15 + Math.random(), (double)var11 + Math.random(), (double)var12 + Math.random(), 0.0, 0.0, 0.0);
            }

            return true;
         } else {
            if (var7 instanceof LiquidBlockContainer) {
               var10 = (LiquidBlockContainer)var7;
               if (this.content == Fluids.WATER) {
                  var10.placeLiquid(var2, var3, var14, var5.getSource(false));
                  this.playEmptySound(var1, var2, var3);
                  return true;
               }
            }

            if (!var2.isClientSide && var8 && !var14.liquid()) {
               var2.destroyBlock(var3, true);
            }

            if (!var2.setBlock(var3, this.content.defaultFluidState().createLegacyBlock(), 11) && !var14.getFluidState().isSource()) {
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
      var2.gameEvent((Entity)var1, (Holder)GameEvent.FLUID_PLACE, (BlockPos)var3);
   }
}
