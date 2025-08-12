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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      BlockHitResult var5 = getPlayerPOVHitResult(var1, var2, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
      if (var5.getType() == HitResult.Type.MISS) {
         return InteractionResult.PASS;
      } else if (var5.getType() != HitResult.Type.BLOCK) {
         return InteractionResult.PASS;
      } else {
         BlockPos var6 = var5.getBlockPos();
         Direction var7 = var5.getDirection();
         BlockPos var8 = var6.relative(var7);
         if (var1.mayInteract(var2, var6) && var2.mayUseItemAt(var8, var7, var4)) {
            if (this.content == Fluids.EMPTY) {
               BlockState var13 = var1.getBlockState(var6);
               Block var15 = var13.getBlock();
               if (var15 instanceof BucketPickup) {
                  BucketPickup var14 = (BucketPickup)var15;
                  ItemStack var16 = var14.pickupBlock(var2, var1, var6, var13);
                  if (!var16.isEmpty()) {
                     var2.awardStat(Stats.ITEM_USED.get(this));
                     var14.getPickupSound().ifPresent((var1x) -> var2.playSound(var1x, 1.0F, 1.0F));
                     var1.gameEvent(var2, GameEvent.FLUID_PICKUP, var6);
                     ItemStack var12 = ItemUtils.createFilledResult(var4, var2, var16);
                     if (!var1.isClientSide()) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)var2, var16);
                     }

                     return InteractionResult.SUCCESS.heldItemTransformedTo(var12);
                  }
               }

               return InteractionResult.FAIL;
            } else {
               BlockState var9 = var1.getBlockState(var6);
               BlockPos var10 = var9.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? var6 : var8;
               if (this.emptyContents(var2, var1, var10, var5)) {
                  this.checkExtraContent(var2, var1, var4, var10);
                  if (var2 instanceof ServerPlayer) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var2, var10, var4);
                  }

                  var2.awardStat(Stats.ITEM_USED.get(this));
                  ItemStack var11 = ItemUtils.createFilledResult(var4, var2, getEmptySuccessItem(var4, var2));
                  return InteractionResult.SUCCESS.heldItemTransformedTo(var11);
               } else {
                  return InteractionResult.FAIL;
               }
            }
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   public static ItemStack getEmptySuccessItem(ItemStack var0, Player var1) {
      return !var1.hasInfiniteMaterials() ? new ItemStack(Items.BUCKET) : var0;
   }

   public void checkExtraContent(@Nullable LivingEntity var1, Level var2, ItemStack var3, BlockPos var4) {
   }

   public boolean emptyContents(@Nullable LivingEntity var1, Level var2, BlockPos var3, @Nullable BlockHitResult var4) {
      Fluid var6 = this.content;
      if (!(var6 instanceof FlowingFluid var5)) {
         return false;
      } else {
         Block var7;
         boolean var8;
         boolean var9;
         boolean var10000;
         label106: {
            var16 = var2.getBlockState(var3);
            var7 = var16.getBlock();
            var8 = var16.canBeReplaced(this.content);
            var9 = var1 != null && var1.isShiftKeyDown();
            if (!var8) {
               label103: {
                  if (var7 instanceof LiquidBlockContainer) {
                     LiquidBlockContainer var11 = (LiquidBlockContainer)var7;
                     if (var11.canPlaceLiquid(var1, var2, var3, var16, this.content)) {
                        break label103;
                     }
                  }

                  var10000 = false;
                  break label106;
               }
            }

            var10000 = true;
         }

         boolean var10 = var10000;
         boolean var17 = var16.isAir() || var10 && (!var9 || var4 == null);
         if (!var17) {
            return var4 != null && this.emptyContents(var1, var2, var4.getBlockPos().relative(var4.getDirection()), (BlockHitResult)null);
         } else if (var2.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
            int var18 = var3.getX();
            int var13 = var3.getY();
            int var14 = var3.getZ();
            var2.playSound(var1, (BlockPos)var3, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var2.random.nextFloat() - var2.random.nextFloat()) * 0.8F);

            for(int var15 = 0; var15 < 8; ++var15) {
               var2.addParticle(ParticleTypes.LARGE_SMOKE, (double)var18 + Math.random(), (double)var13 + Math.random(), (double)var14 + Math.random(), 0.0, 0.0, 0.0);
            }

            return true;
         } else {
            if (var7 instanceof LiquidBlockContainer) {
               LiquidBlockContainer var12 = (LiquidBlockContainer)var7;
               if (this.content == Fluids.WATER) {
                  var12.placeLiquid(var2, var3, var16, var5.getSource(false));
                  this.playEmptySound(var1, var2, var3);
                  return true;
               }
            }

            if (!var2.isClientSide() && var8 && !var16.liquid()) {
               var2.destroyBlock(var3, true);
            }

            if (!var2.setBlock(var3, this.content.defaultFluidState().createLegacyBlock(), 11) && !var16.getFluidState().isSource()) {
               return false;
            } else {
               this.playEmptySound(var1, var2, var3);
               return true;
            }
         }
      }
   }

   protected void playEmptySound(@Nullable LivingEntity var1, LevelAccessor var2, BlockPos var3) {
      SoundEvent var4 = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
      var2.playSound(var1, var3, var4, SoundSource.BLOCKS, 1.0F, 1.0F);
      var2.gameEvent(var1, (Holder)GameEvent.FLUID_PLACE, (BlockPos)var3);
   }
}
