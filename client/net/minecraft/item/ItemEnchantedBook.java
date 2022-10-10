package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemEnchantedBook extends Item {
   public ItemEnchantedBook(Item.Properties var1) {
      super(var1);
   }

   public boolean func_77636_d(ItemStack var1) {
      return true;
   }

   public boolean func_77616_k(ItemStack var1) {
      return false;
   }

   public static NBTTagList func_92110_g(ItemStack var0) {
      NBTTagCompound var1 = var0.func_77978_p();
      return var1 != null ? var1.func_150295_c("StoredEnchantments", 10) : new NBTTagList();
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      super.func_77624_a(var1, var2, var3, var4);
      NBTTagList var5 = func_92110_g(var1);

      for(int var6 = 0; var6 < var5.size(); ++var6) {
         NBTTagCompound var7 = var5.func_150305_b(var6);
         Enchantment var8 = (Enchantment)IRegistry.field_212628_q.func_212608_b(ResourceLocation.func_208304_a(var7.func_74779_i("id")));
         if (var8 != null) {
            var3.add(var8.func_200305_d(var7.func_74762_e("lvl")));
         }
      }

   }

   public static void func_92115_a(ItemStack var0, EnchantmentData var1) {
      NBTTagList var2 = func_92110_g(var0);
      boolean var3 = true;
      ResourceLocation var4 = IRegistry.field_212628_q.func_177774_c(var1.field_76302_b);

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         NBTTagCompound var6 = var2.func_150305_b(var5);
         ResourceLocation var7 = ResourceLocation.func_208304_a(var6.func_74779_i("id"));
         if (var7 != null && var7.equals(var4)) {
            if (var6.func_74762_e("lvl") < var1.field_76303_c) {
               var6.func_74777_a("lvl", (short)var1.field_76303_c);
            }

            var3 = false;
            break;
         }
      }

      if (var3) {
         NBTTagCompound var8 = new NBTTagCompound();
         var8.func_74778_a("id", String.valueOf(var4));
         var8.func_74777_a("lvl", (short)var1.field_76303_c);
         var2.add((INBTBase)var8);
      }

      var0.func_196082_o().func_74782_a("StoredEnchantments", var2);
   }

   public static ItemStack func_92111_a(EnchantmentData var0) {
      ItemStack var1 = new ItemStack(Items.field_151134_bR);
      func_92115_a(var1, var0);
      return var1;
   }

   public void func_150895_a(ItemGroup var1, NonNullList<ItemStack> var2) {
      Iterator var3;
      Enchantment var4;
      if (var1 == ItemGroup.field_78027_g) {
         var3 = IRegistry.field_212628_q.iterator();

         while(true) {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (Enchantment)var3.next();
            } while(var4.field_77351_y == null);

            for(int var5 = var4.func_77319_d(); var5 <= var4.func_77325_b(); ++var5) {
               var2.add(func_92111_a(new EnchantmentData(var4, var5)));
            }
         }
      } else if (var1.func_111225_m().length != 0) {
         var3 = IRegistry.field_212628_q.iterator();

         while(var3.hasNext()) {
            var4 = (Enchantment)var3.next();
            if (var1.func_111226_a(var4.field_77351_y)) {
               var2.add(func_92111_a(new EnchantmentData(var4, var4.func_77325_b())));
            }
         }
      }

   }
}
