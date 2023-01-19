package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

public class EnchantedBookItem extends Item {
   public static final String TAG_STORED_ENCHANTMENTS = "StoredEnchantments";

   public EnchantedBookItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public boolean isFoil(ItemStack var1) {
      return true;
   }

   @Override
   public boolean isEnchantable(ItemStack var1) {
      return false;
   }

   public static ListTag getEnchantments(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      return var1 != null ? var1.getList("StoredEnchantments", 10) : new ListTag();
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      ItemStack.appendEnchantmentNames(var3, getEnchantments(var1));
   }

   public static void addEnchantment(ItemStack var0, EnchantmentInstance var1) {
      ListTag var2 = getEnchantments(var0);
      boolean var3 = true;
      ResourceLocation var4 = EnchantmentHelper.getEnchantmentId(var1.enchantment);

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         CompoundTag var6 = var2.getCompound(var5);
         ResourceLocation var7 = EnchantmentHelper.getEnchantmentId(var6);
         if (var7 != null && var7.equals(var4)) {
            if (EnchantmentHelper.getEnchantmentLevel(var6) < var1.level) {
               EnchantmentHelper.setEnchantmentLevel(var6, var1.level);
            }

            var3 = false;
            break;
         }
      }

      if (var3) {
         var2.add(EnchantmentHelper.storeEnchantment(var4, var1.level));
      }

      var0.getOrCreateTag().put("StoredEnchantments", var2);
   }

   public static ItemStack createForEnchantment(EnchantmentInstance var0) {
      ItemStack var1 = new ItemStack(Items.ENCHANTED_BOOK);
      addEnchantment(var1, var0);
      return var1;
   }
}
