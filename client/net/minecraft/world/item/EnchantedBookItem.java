package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

public class EnchantedBookItem extends Item {
   public EnchantedBookItem(Item.Properties var1) {
      super(var1);
   }

   public boolean isFoil(ItemStack var1) {
      return true;
   }

   public boolean isEnchantable(ItemStack var1) {
      return false;
   }

   public static ListTag getEnchantments(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      return var1 != null ? var1.getList("StoredEnchantments", 10) : new ListTag();
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      ItemStack.appendEnchantmentNames(var3, getEnchantments(var1));
   }

   public static void addEnchantment(ItemStack var0, EnchantmentInstance var1) {
      ListTag var2 = getEnchantments(var0);
      boolean var3 = true;
      ResourceLocation var4 = Registry.ENCHANTMENT.getKey(var1.enchantment);

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         CompoundTag var6 = var2.getCompound(var5);
         ResourceLocation var7 = ResourceLocation.tryParse(var6.getString("id"));
         if (var7 != null && var7.equals(var4)) {
            if (var6.getInt("lvl") < var1.level) {
               var6.putShort("lvl", (short)var1.level);
            }

            var3 = false;
            break;
         }
      }

      if (var3) {
         CompoundTag var8 = new CompoundTag();
         var8.putString("id", String.valueOf(var4));
         var8.putShort("lvl", (short)var1.level);
         var2.add(var8);
      }

      var0.getOrCreateTag().put("StoredEnchantments", var2);
   }

   public static ItemStack createForEnchantment(EnchantmentInstance var0) {
      ItemStack var1 = new ItemStack(Items.ENCHANTED_BOOK);
      addEnchantment(var1, var0);
      return var1;
   }

   public void fillItemCategory(CreativeModeTab var1, NonNullList<ItemStack> var2) {
      Iterator var3;
      Enchantment var4;
      if (var1 == CreativeModeTab.TAB_SEARCH) {
         var3 = Registry.ENCHANTMENT.iterator();

         while(true) {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (Enchantment)var3.next();
            } while(var4.category == null);

            for(int var5 = var4.getMinLevel(); var5 <= var4.getMaxLevel(); ++var5) {
               var2.add(createForEnchantment(new EnchantmentInstance(var4, var5)));
            }
         }
      } else if (var1.getEnchantmentCategories().length != 0) {
         var3 = Registry.ENCHANTMENT.iterator();

         while(var3.hasNext()) {
            var4 = (Enchantment)var3.next();
            if (var1.hasEnchantmentCategory(var4.category)) {
               var2.add(createForEnchantment(new EnchantmentInstance(var4, var4.getMaxLevel())));
            }
         }
      }

   }
}
