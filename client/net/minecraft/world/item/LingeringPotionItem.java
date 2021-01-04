package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class LingeringPotionItem extends PotionItem {
   public LingeringPotionItem(Item.Properties var1) {
      super(var1);
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      PotionUtils.addPotionTooltip(var1, var3, 0.25F);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      ItemStack var5 = var2.abilities.instabuild ? var4.copy() : var4.split(1);
      var1.playSound((Player)null, var2.x, var2.y, var2.z, SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!var1.isClientSide) {
         ThrownPotion var6 = new ThrownPotion(var1, var2);
         var6.setItem(var5);
         var6.shootFromRotation(var2, var2.xRot, var2.yRot, -20.0F, 0.5F, 1.0F);
         var1.addFreshEntity(var6);
      }

      var2.awardStat(Stats.ITEM_USED.get(this));
      return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
   }
}
