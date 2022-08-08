package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;

public class ThrowablePotionItem extends PotionItem {
   public ThrowablePotionItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (!var1.isClientSide) {
         ThrownPotion var5 = new ThrownPotion(var1, var2);
         var5.setItem(var4);
         var5.shootFromRotation(var2, var2.getXRot(), var2.getYRot(), -20.0F, 0.5F, 1.0F);
         var1.addFreshEntity(var5);
      }

      var2.awardStat(Stats.ITEM_USED.get(this));
      if (!var2.getAbilities().instabuild) {
         var4.shrink(1);
      }

      return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
   }
}
