package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotionItem extends Item {
   private static final int DRINK_DURATION = 32;

   public PotionItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
   }

   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      Player var4 = var3 instanceof Player ? (Player)var3 : null;
      if (var4 instanceof ServerPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)var4, var1);
      }

      if (!var2.isClientSide) {
         for(MobEffectInstance var7 : PotionUtils.getMobEffects(var1)) {
            if (var7.getEffect().isInstantenous()) {
               var7.getEffect().applyInstantenousEffect(var4, var4, var3, var7.getAmplifier(), 1.0);
            } else {
               var3.addEffect(new MobEffectInstance(var7));
            }
         }
      }

      if (var4 != null) {
         var4.awardStat(Stats.ITEM_USED.get(this));
         if (!var4.getAbilities().instabuild) {
            var1.shrink(1);
         }
      }

      if (var4 == null || !var4.getAbilities().instabuild) {
         if (var1.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (var4 != null) {
            var4.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      var3.gameEvent(GameEvent.DRINK);
      return var1;
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      Player var4 = var1.getPlayer();
      ItemStack var5 = var1.getItemInHand();
      BlockState var6 = var2.getBlockState(var3);
      if (var1.getClickedFace() != Direction.DOWN && var6.is(BlockTags.CONVERTABLE_TO_MUD) && PotionUtils.getPotion(var5) == Potions.WATER) {
         var2.playSound(null, var3, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
         var4.setItemInHand(var1.getHand(), ItemUtils.createFilledResult(var5, var4, new ItemStack(Items.GLASS_BOTTLE)));
         var4.awardStat(Stats.ITEM_USED.get(var5.getItem()));
         if (!var2.isClientSide) {
            ServerLevel var7 = (ServerLevel)var2;

            for(int var8 = 0; var8 < 5; ++var8) {
               var7.sendParticles(
                  ParticleTypes.SPLASH,
                  (double)var3.getX() + var2.random.nextDouble(),
                  (double)(var3.getY() + 1),
                  (double)var3.getZ() + var2.random.nextDouble(),
                  1,
                  0.0,
                  0.0,
                  0.0,
                  1.0
               );
            }
         }

         var2.playSound(null, var3, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
         var2.gameEvent(null, GameEvent.FLUID_PLACE, var3);
         var2.setBlockAndUpdate(var3, Blocks.MUD.defaultBlockState());
         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public int getUseDuration(ItemStack var1) {
      return 32;
   }

   @Override
   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.DRINK;
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      return ItemUtils.startUsingInstantly(var1, var2, var3);
   }

   @Override
   public String getDescriptionId(ItemStack var1) {
      return PotionUtils.getPotion(var1).getName(this.getDescriptionId() + ".effect.");
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      PotionUtils.addPotionTooltip(var1, var3, 1.0F, var2 == null ? 20.0F : var2.tickRateManager().tickrate());
   }
}
