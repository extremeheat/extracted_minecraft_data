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

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      super.finishUsingItem(var1, var2, var3);
      if (var3 instanceof ServerPlayer var4) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)var4, var1);
         ((ServerPlayer)var4).awardStat(Stats.ITEM_USED.get(this));
      }

      if (!var2.isClientSide) {
         var3.removeEffect(MobEffects.POISON);
      }

      if (var1.isEmpty()) {
         return new ItemStack(Items.GLASS_BOTTLE);
      } else {
         if (var3 instanceof Player var6 && !var6.getAbilities().instabuild) {
            ItemStack var5 = new ItemStack(Items.GLASS_BOTTLE);
            if (!var6.getInventory().add(var5)) {
               var6.drop(var5, false);
            }
         }

         return var1;
      }
   }

   @Override
   public int getUseDuration(ItemStack var1) {
      return 40;
   }

   @Override
   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.DRINK;
   }

   @Override
   public SoundEvent getDrinkingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   @Override
   public SoundEvent getEatingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      return ItemUtils.startUsingInstantly(var1, var2, var3);
   }
}
