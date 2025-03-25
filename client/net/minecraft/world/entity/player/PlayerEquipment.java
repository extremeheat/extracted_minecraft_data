package net.minecraft.world.entity.player;

import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class PlayerEquipment extends EntityEquipment {
   private final Player player;

   public PlayerEquipment(Player var1) {
      super();
      this.player = var1;
   }

   public ItemStack set(EquipmentSlot var1, ItemStack var2) {
      return var1 == EquipmentSlot.MAINHAND ? this.player.getInventory().setSelectedItem(var2) : super.set(var1, var2);
   }

   public ItemStack get(EquipmentSlot var1) {
      return var1 == EquipmentSlot.MAINHAND ? this.player.getInventory().getSelectedItem() : super.get(var1);
   }

   public boolean isEmpty() {
      return this.player.getInventory().getSelectedItem().isEmpty() && super.isEmpty();
   }
}
