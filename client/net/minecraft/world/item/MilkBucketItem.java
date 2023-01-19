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
   private static final int DRINK_DURATION = 32;

   public MilkBucketItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      if (var3 instanceof ServerPlayer var4) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)var4, var1);
         ((ServerPlayer)var4).awardStat(Stats.ITEM_USED.get(this));
      }

      if (var3 instanceof Player && !((Player)var3).getAbilities().instabuild) {
         var1.shrink(1);
      }

      if (!var2.isClientSide) {
         var3.removeAllEffects();
      }

      return var1.isEmpty() ? new ItemStack(Items.BUCKET) : var1;
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
}
