package net.minecraft.world.item.enchantment;

import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record EnchantedItemInUse(ItemStack itemStack, @Nullable EquipmentSlot inSlot, @Nullable LivingEntity owner, Runnable onBreak) {
   public EnchantedItemInUse(ItemStack var1, EquipmentSlot var2, LivingEntity var3) {
      this(var1, var2, var3, () -> var3.broadcastBreakEvent(var2));
   }

   public EnchantedItemInUse(ItemStack itemStack, @Nullable EquipmentSlot inSlot, @Nullable LivingEntity owner, Runnable onBreak) {
      super();
      this.itemStack = itemStack;
      this.inSlot = inSlot;
      this.owner = owner;
      this.onBreak = onBreak;
   }
}
