package net.minecraft.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class FishingRodItem extends Item {
   public FishingRodItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (var2.fishing != null) {
         if (!var1.isClientSide) {
            int var5 = var2.fishing.retrieve(var4);
            var4.hurtAndBreak(var5, var2, LivingEntity.getSlotForHand(var3));
         }

         var1.playSound((Player)null, var2.getX(), var2.getY(), var2.getZ(), (SoundEvent)SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F));
         var2.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
      } else {
         var1.playSound((Player)null, var2.getX(), var2.getY(), var2.getZ(), (SoundEvent)SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F));
         if (var1 instanceof ServerLevel) {
            ServerLevel var8 = (ServerLevel)var1;
            int var6 = (int)(EnchantmentHelper.getFishingTimeReduction(var8, var4, var2) * 20.0F);
            int var7 = EnchantmentHelper.getFishingLuckBonus(var8, var4, var2);
            var1.addFreshEntity(new FishingHook(var2, var1, var7, var6));
         }

         var2.awardStat(Stats.ITEM_USED.get(this));
         var2.gameEvent(GameEvent.ITEM_INTERACT_START);
      }

      return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
   }

   public int getEnchantmentValue() {
      return 1;
   }
}
