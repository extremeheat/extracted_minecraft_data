package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class LingeringPotionItem extends ThrowablePotionItem {
   public LingeringPotionItem(Item.Properties var1) {
      super(var1);
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      PotionUtils.addPotionTooltip(var1, var3, 0.25F);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      var1.playSound((Player)null, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F));
      return super.use(var1, var2, var3);
   }
}
