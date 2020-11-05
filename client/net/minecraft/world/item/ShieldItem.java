package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ShieldItem extends Item {
   public ShieldItem(Item.Properties var1) {
      super(var1);
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public String getDescriptionId(ItemStack var1) {
      return var1.getTagElement("BlockEntityTag") != null ? this.getDescriptionId() + '.' + getColor(var1).getName() : super.getDescriptionId(var1);
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      BannerItem.appendHoverTextFromBannerBlockEntityTag(var1, var3);
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.BLOCK;
   }

   public int getUseDuration(ItemStack var1) {
      return 72000;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      var2.startUsingItem(var3);
      return InteractionResultHolder.consume(var4);
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return var2.is((Tag)ItemTags.PLANKS) || super.isValidRepairItem(var1, var2);
   }

   public static DyeColor getColor(ItemStack var0) {
      return DyeColor.byId(var0.getOrCreateTagElement("BlockEntityTag").getInt("Base"));
   }
}
