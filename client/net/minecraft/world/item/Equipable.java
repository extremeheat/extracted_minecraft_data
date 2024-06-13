package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface Equipable {
   EquipmentSlot getEquipmentSlot();

   default Holder<SoundEvent> getEquipSound() {
      return SoundEvents.ARMOR_EQUIP_GENERIC;
   }

   default InteractionResultHolder<ItemStack> swapWithEquipmentSlot(Item var1, Level var2, Player var3, InteractionHand var4) {
      ItemStack var5 = var3.getItemInHand(var4);
      EquipmentSlot var6 = Mob.getEquipmentSlotForItem(var5);
      if (!var3.canUseSlot(var6)) {
         return InteractionResultHolder.pass(var5);
      } else {
         ItemStack var7 = var3.getItemBySlot(var6);
         if ((!EnchantmentHelper.has(var7, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || var3.isCreative()) && !ItemStack.matches(var5, var7)) {
            if (!var2.isClientSide()) {
               var3.awardStat(Stats.ITEM_USED.get(var1));
            }

            ItemStack var8 = var7.isEmpty() ? var5 : var7.copyAndClear();
            ItemStack var9 = var3.isCreative() ? var5.copy() : var5.copyAndClear();
            var3.setItemSlot(var6, var9);
            return InteractionResultHolder.sidedSuccess(var8, var2.isClientSide());
         } else {
            return InteractionResultHolder.fail(var5);
         }
      }
   }

   @Nullable
   static Equipable get(ItemStack var0) {
      Item var2 = var0.getItem();
      if (var2 instanceof Equipable) {
         return (Equipable)var2;
      } else {
         if (var0.getItem() instanceof BlockItem var1) {
            Block var4 = var1.getBlock();
            if (var4 instanceof Equipable) {
               return (Equipable)var4;
            }
         }

         return null;
      }
   }
}
