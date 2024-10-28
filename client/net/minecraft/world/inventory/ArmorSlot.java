package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

class ArmorSlot extends Slot {
   private final LivingEntity owner;
   private final EquipmentSlot slot;
   @Nullable
   private final ResourceLocation emptyIcon;

   public ArmorSlot(Container var1, LivingEntity var2, EquipmentSlot var3, int var4, int var5, int var6, @Nullable ResourceLocation var7) {
      super(var1, var4, var5, var6);
      this.owner = var2;
      this.slot = var3;
      this.emptyIcon = var7;
   }

   public void setByPlayer(ItemStack var1, ItemStack var2) {
      this.owner.onEquipItem(this.slot, var2, var1);
      super.setByPlayer(var1, var2);
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean mayPlace(ItemStack var1) {
      return this.slot == this.owner.getEquipmentSlotForItem(var1);
   }

   public boolean mayPickup(Player var1) {
      ItemStack var2 = this.getItem();
      return !var2.isEmpty() && !var1.isCreative() && EnchantmentHelper.has(var2, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) ? false : super.mayPickup(var1);
   }

   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return this.emptyIcon != null ? Pair.of(InventoryMenu.BLOCK_ATLAS, this.emptyIcon) : super.getNoItemIcon();
   }
}
