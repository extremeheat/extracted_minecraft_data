package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HoneyBottleItem extends Item {
   private static final int DRINK_DURATION = 40;

   public HoneyBottleItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      super.finishUsingItem(var1, var2, var3);
      if (var3 instanceof ServerPlayer) {
         ServerPlayer var4 = (ServerPlayer)var3;
         CriteriaTriggers.CONSUME_ITEM.trigger(var4, var1);
         var4.awardStat(Stats.ITEM_USED.get(this));
      }

      if (!var2.isClientSide) {
         var3.removeEffect(MobEffects.POISON);
      }

      if (var1.isEmpty()) {
         return new ItemStack(Items.GLASS_BOTTLE);
      } else {
         if (var3 instanceof Player && !((Player)var3).getAbilities().instabuild) {
            ItemStack var6 = new ItemStack(Items.GLASS_BOTTLE);
            Player var5 = (Player)var3;
            if (!var5.getInventory().add(var6)) {
               var5.drop(var6, false);
            }
         }

         return var1;
      }
   }

   public int getUseDuration(ItemStack var1) {
      return 40;
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.DRINK;
   }

   public SoundEvent getDrinkingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public SoundEvent getEatingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      return ItemUtils.startUsingInstantly(var1, var2, var3);
   }
}
