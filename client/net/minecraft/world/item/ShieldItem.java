package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ShieldItem extends Item implements Equipable {
   public static final int EFFECTIVE_BLOCK_DELAY = 5;
   public static final float MINIMUM_DURABILITY_DAMAGE = 3.0F;

   public ShieldItem(Item.Properties var1) {
      super(var1);
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   @Override
   public String getDescriptionId(ItemStack var1) {
      DyeColor var2 = var1.get(DataComponents.BASE_COLOR);
      return var2 != null ? this.getDescriptionId() + "." + var2.getName() : super.getDescriptionId(var1);
   }

   @Override
   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      BannerItem.appendHoverTextFromBannerBlockEntityTag(var1, var3);
   }

   @Override
   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.BLOCK;
   }

   @Override
   public int getUseDuration(ItemStack var1) {
      return 72000;
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      var2.startUsingItem(var3);
      return InteractionResultHolder.consume(var4);
   }

   @Override
   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return var2.is(ItemTags.PLANKS) || super.isValidRepairItem(var1, var2);
   }

   @Override
   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.OFFHAND;
   }
}