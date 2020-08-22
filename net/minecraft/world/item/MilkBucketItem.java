package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MilkBucketItem extends Item {
   public MilkBucketItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      if (var3 instanceof ServerPlayer) {
         ServerPlayer var4 = (ServerPlayer)var3;
         CriteriaTriggers.CONSUME_ITEM.trigger(var4, var1);
         var4.awardStat(Stats.ITEM_USED.get(this));
      }

      if (var3 instanceof Player && !((Player)var3).abilities.instabuild) {
         var1.shrink(1);
      }

      if (!var2.isClientSide) {
         var3.removeAllEffects();
      }

      return var1.isEmpty() ? new ItemStack(Items.BUCKET) : var1;
   }

   public int getUseDuration(ItemStack var1) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder use(Level var1, Player var2, InteractionHand var3) {
      var2.startUsingItem(var3);
      return InteractionResultHolder.success(var2.getItemInHand(var3));
   }
}
