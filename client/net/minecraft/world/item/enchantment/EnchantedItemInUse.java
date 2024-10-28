package net.minecraft.world.item.enchantment;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record EnchantedItemInUse(ItemStack itemStack, @Nullable EquipmentSlot inSlot, @Nullable LivingEntity owner, Consumer<Item> onBreak) {
   public EnchantedItemInUse(ItemStack var1, EquipmentSlot var2, LivingEntity var3) {
      this(var1, var2, var3, (var2x) -> {
         var3.onEquippedItemBroken(var2x, var2);
      });
   }

   public EnchantedItemInUse(ItemStack var1, @Nullable EquipmentSlot var2, @Nullable LivingEntity var3, Consumer<Item> var4) {
      super();
      this.itemStack = var1;
      this.inSlot = var2;
      this.owner = var3;
      this.onBreak = var4;
   }

   public ItemStack itemStack() {
      return this.itemStack;
   }

   @Nullable
   public EquipmentSlot inSlot() {
      return this.inSlot;
   }

   @Nullable
   public LivingEntity owner() {
      return this.owner;
   }

   public Consumer<Item> onBreak() {
      return this.onBreak;
   }
}
