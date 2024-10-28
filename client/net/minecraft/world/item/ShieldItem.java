package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ShieldItem extends Item {
   public static final int EFFECTIVE_BLOCK_DELAY = 5;
   public static final float MINIMUM_DURABILITY_DAMAGE = 3.0F;

   public ShieldItem(Item.Properties var1) {
      super(var1);
   }

   public Component getName(ItemStack var1) {
      DyeColor var2 = (DyeColor)var1.get(DataComponents.BASE_COLOR);
      if (var2 != null) {
         String var10000 = this.descriptionId;
         return Component.translatable(var10000 + "." + var2.getName());
      } else {
         return super.getName(var1);
      }
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      BannerItem.appendHoverTextFromBannerBlockEntityTag(var1, var3);
   }

   public ItemUseAnimation getUseAnimation(ItemStack var1) {
      return ItemUseAnimation.BLOCK;
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 72000;
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      var2.startUsingItem(var3);
      return InteractionResult.CONSUME;
   }
}
