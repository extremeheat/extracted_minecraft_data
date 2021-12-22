package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EnderEyeItem extends Item {
   public EnderEyeItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.is(Blocks.END_PORTAL_FRAME) && !(Boolean)var4.getValue(EndPortalFrameBlock.HAS_EYE)) {
         if (var2.isClientSide) {
            return InteractionResult.SUCCESS;
         } else {
            BlockState var5 = (BlockState)var4.setValue(EndPortalFrameBlock.HAS_EYE, true);
            Block.pushEntitiesUp(var4, var5, var2, var3);
            var2.setBlock(var3, var5, 2);
            var2.updateNeighbourForOutputSignal(var3, Blocks.END_PORTAL_FRAME);
            var1.getItemInHand().shrink(1);
            var2.levelEvent(1503, var3, 0);
            BlockPattern.BlockPatternMatch var6 = EndPortalFrameBlock.getOrCreatePortalShape().find(var2, var3);
            if (var6 != null) {
               BlockPos var7 = var6.getFrontTopLeft().offset(-3, 0, -3);

               for(int var8 = 0; var8 < 3; ++var8) {
                  for(int var9 = 0; var9 < 3; ++var9) {
                     var2.setBlock(var7.offset(var8, 0, var9), Blocks.END_PORTAL.defaultBlockState(), 2);
                  }
               }

               var2.globalLevelEvent(1038, var7.offset(1, 0, 1), 0);
            }

            return InteractionResult.CONSUME;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      BlockHitResult var5 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.NONE);
      if (var5.getType() == HitResult.Type.BLOCK && var1.getBlockState(((BlockHitResult)var5).getBlockPos()).is(Blocks.END_PORTAL_FRAME)) {
         return InteractionResultHolder.pass(var4);
      } else {
         var2.startUsingItem(var3);
         if (var1 instanceof ServerLevel) {
            BlockPos var6 = ((ServerLevel)var1).getChunkSource().getGenerator().findNearestMapFeature((ServerLevel)var1, StructureFeature.STRONGHOLD, var2.blockPosition(), 100, false);
            if (var6 != null) {
               EyeOfEnder var7 = new EyeOfEnder(var1, var2.getX(), var2.getY(0.5D), var2.getZ());
               var7.setItem(var4);
               var7.signalTo(var6);
               var1.addFreshEntity(var7);
               if (var2 instanceof ServerPlayer) {
                  CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayer)var2, var6);
               }

               var1.playSound((Player)null, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F));
               var1.levelEvent((Player)null, 1003, var2.blockPosition(), 0);
               if (!var2.getAbilities().instabuild) {
                  var4.shrink(1);
               }

               var2.awardStat(Stats.ITEM_USED.get(this));
               var2.swing(var3, true);
               return InteractionResultHolder.success(var4);
            }
         }

         return InteractionResultHolder.consume(var4);
      }
   }
}
