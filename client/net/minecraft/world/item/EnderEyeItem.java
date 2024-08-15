package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EnderEyeItem extends Item {
   public EnderEyeItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (!var4.is(Blocks.END_PORTAL_FRAME) || var4.getValue(EndPortalFrameBlock.HAS_EYE)) {
         return InteractionResult.PASS;
      } else if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockState var5 = var4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(true));
         Block.pushEntitiesUp(var4, var5, var2, var3);
         var2.setBlock(var3, var5, 2);
         var2.updateNeighbourForOutputSignal(var3, Blocks.END_PORTAL_FRAME);
         var1.getItemInHand().shrink(1);
         var2.levelEvent(1503, var3, 0);
         BlockPattern.BlockPatternMatch var6 = EndPortalFrameBlock.getOrCreatePortalShape().find(var2, var3);
         if (var6 != null) {
            BlockPos var7 = var6.getFrontTopLeft().offset(-3, 0, -3);

            for (int var8 = 0; var8 < 3; var8++) {
               for (int var9 = 0; var9 < 3; var9++) {
                  var2.setBlock(var7.offset(var8, 0, var9), Blocks.END_PORTAL.defaultBlockState(), 2);
               }
            }

            var2.globalLevelEvent(1038, var7.offset(1, 0, 1), 0);
         }

         return InteractionResult.SUCCESS;
      }
   }

   @Override
   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 0;
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      BlockHitResult var5 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.NONE);
      if (var5.getType() == HitResult.Type.BLOCK && var1.getBlockState(var5.getBlockPos()).is(Blocks.END_PORTAL_FRAME)) {
         return InteractionResult.PASS;
      } else {
         var2.startUsingItem(var3);
         if (var1 instanceof ServerLevel var6) {
            BlockPos var7 = var6.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, var2.blockPosition(), 100, false);
            if (var7 == null) {
               return InteractionResult.CONSUME;
            }

            EyeOfEnder var8 = new EyeOfEnder(var1, var2.getX(), var2.getY(0.5), var2.getZ());
            var8.setItem(var4);
            var8.signalTo(var7);
            var1.gameEvent(GameEvent.PROJECTILE_SHOOT, var8.position(), GameEvent.Context.of(var2));
            var1.addFreshEntity(var8);
            if (var2 instanceof ServerPlayer var9) {
               CriteriaTriggers.USED_ENDER_EYE.trigger(var9, var7);
            }

            float var10 = Mth.lerp(var1.random.nextFloat(), 0.33F, 0.5F);
            var1.playSound(null, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, var10);
            var4.consume(1, var2);
            var2.awardStat(Stats.ITEM_USED.get(this));
         }

         return InteractionResult.SUCCESS_SERVER;
      }
   }
}
