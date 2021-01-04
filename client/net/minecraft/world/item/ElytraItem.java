package net.minecraft.world.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ElytraItem extends Item {
   public ElytraItem(Item.Properties var1) {
      super(var1);
      this.addProperty(new ResourceLocation("broken"), (var0, var1x, var2) -> {
         return isFlyEnabled(var0) ? 0.0F : 1.0F;
      });
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public static boolean isFlyEnabled(ItemStack var0) {
      return var0.getDamageValue() < var0.getMaxDamage() - 1;
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return var2.getItem() == Items.PHANTOM_MEMBRANE;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      EquipmentSlot var5 = Mob.getEquipmentSlotForItem(var4);
      ItemStack var6 = var2.getItemBySlot(var5);
      if (var6.isEmpty()) {
         var2.setItemSlot(var5, var4.copy());
         var4.setCount(0);
         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      } else {
         return new InteractionResultHolder(InteractionResult.FAIL, var4);
      }
   }
}
