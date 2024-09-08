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

   @Override
   public Component getName(ItemStack var1) {
      DyeColor var2 = var1.get(DataComponents.BASE_COLOR);
      return (Component)(var2 != null ? Component.translatable(this.descriptionId + "." + var2.getName()) : super.getName(var1));
   }

   @Override
   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      BannerItem.appendHoverTextFromBannerBlockEntityTag(var1, var3);
   }

   @Override
   public ItemUseAnimation getUseAnimation(ItemStack var1) {
      return ItemUseAnimation.BLOCK;
   }

   @Override
   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 72000;
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      var2.startUsingItem(var3);
      return InteractionResult.CONSUME;
   }
}
