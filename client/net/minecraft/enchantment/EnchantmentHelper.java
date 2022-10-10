package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   public static int func_77506_a(Enchantment var0, ItemStack var1) {
      if (var1.func_190926_b()) {
         return 0;
      } else {
         ResourceLocation var2 = IRegistry.field_212628_q.func_177774_c(var0);
         NBTTagList var3 = var1.func_77986_q();

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            NBTTagCompound var5 = var3.func_150305_b(var4);
            ResourceLocation var6 = ResourceLocation.func_208304_a(var5.func_74779_i("id"));
            if (var6 != null && var6.equals(var2)) {
               return var5.func_74762_e("lvl");
            }
         }

         return 0;
      }
   }

   public static Map<Enchantment, Integer> func_82781_a(ItemStack var0) {
      LinkedHashMap var1 = Maps.newLinkedHashMap();
      NBTTagList var2 = var0.func_77973_b() == Items.field_151134_bR ? ItemEnchantedBook.func_92110_g(var0) : var0.func_77986_q();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         Enchantment var5 = (Enchantment)IRegistry.field_212628_q.func_212608_b(ResourceLocation.func_208304_a(var4.func_74779_i("id")));
         if (var5 != null) {
            var1.put(var5, var4.func_74762_e("lvl"));
         }
      }

      return var1;
   }

   public static void func_82782_a(Map<Enchantment, Integer> var0, ItemStack var1) {
      NBTTagList var2 = new NBTTagList();
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         Enchantment var5 = (Enchantment)var4.getKey();
         if (var5 != null) {
            int var6 = (Integer)var4.getValue();
            NBTTagCompound var7 = new NBTTagCompound();
            var7.func_74778_a("id", String.valueOf(IRegistry.field_212628_q.func_177774_c(var5)));
            var7.func_74777_a("lvl", (short)var6);
            var2.add((INBTBase)var7);
            if (var1.func_77973_b() == Items.field_151134_bR) {
               ItemEnchantedBook.func_92115_a(var1, new EnchantmentData(var5, var6));
            }
         }
      }

      if (var2.isEmpty()) {
         var1.func_196083_e("Enchantments");
      } else if (var1.func_77973_b() != Items.field_151134_bR) {
         var1.func_77983_a("Enchantments", var2);
      }

   }

   private static void func_77518_a(EnchantmentHelper.IEnchantmentVisitor var0, ItemStack var1) {
      if (!var1.func_190926_b()) {
         NBTTagList var2 = var1.func_77986_q();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            String var4 = var2.func_150305_b(var3).func_74779_i("id");
            int var5 = var2.func_150305_b(var3).func_74762_e("lvl");
            Enchantment var6 = (Enchantment)IRegistry.field_212628_q.func_212608_b(ResourceLocation.func_208304_a(var4));
            if (var6 != null) {
               var0.accept(var6, var5);
            }
         }

      }
   }

   private static void func_77516_a(EnchantmentHelper.IEnchantmentVisitor var0, Iterable<ItemStack> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         func_77518_a(var0, var3);
      }

   }

   public static int func_77508_a(Iterable<ItemStack> var0, DamageSource var1) {
      MutableInt var2 = new MutableInt();
      func_77516_a((var2x, var3) -> {
         var2.add(var2x.func_77318_a(var3, var1));
      }, var0);
      return var2.intValue();
   }

   public static float func_152377_a(ItemStack var0, CreatureAttribute var1) {
      MutableFloat var2 = new MutableFloat();
      func_77518_a((var2x, var3) -> {
         var2.add(var2x.func_152376_a(var3, var1));
      }, var0);
      return var2.floatValue();
   }

   public static float func_191527_a(EntityLivingBase var0) {
      int var1 = func_185284_a(Enchantments.field_191530_r, var0);
      return var1 > 0 ? EnchantmentSweepingEdge.func_191526_e(var1) : 0.0F;
   }

   public static void func_151384_a(EntityLivingBase var0, Entity var1) {
      EnchantmentHelper.IEnchantmentVisitor var2 = (var2x, var3) -> {
         var2x.func_151367_b(var0, var1, var3);
      };
      if (var0 != null) {
         func_77516_a(var2, var0.func_184209_aF());
      }

      if (var1 instanceof EntityPlayer) {
         func_77518_a(var2, var0.func_184614_ca());
      }

   }

   public static void func_151385_b(EntityLivingBase var0, Entity var1) {
      EnchantmentHelper.IEnchantmentVisitor var2 = (var2x, var3) -> {
         var2x.func_151368_a(var0, var1, var3);
      };
      if (var0 != null) {
         func_77516_a(var2, var0.func_184209_aF());
      }

      if (var0 instanceof EntityPlayer) {
         func_77518_a(var2, var0.func_184614_ca());
      }

   }

   public static int func_185284_a(Enchantment var0, EntityLivingBase var1) {
      List var2 = var0.func_185260_a(var1);
      if (var2 == null) {
         return 0;
      } else {
         int var3 = 0;
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            ItemStack var5 = (ItemStack)var4.next();
            int var6 = func_77506_a(var0, var5);
            if (var6 > var3) {
               var3 = var6;
            }
         }

         return var3;
      }
   }

   public static int func_77501_a(EntityLivingBase var0) {
      return func_185284_a(Enchantments.field_180313_o, var0);
   }

   public static int func_90036_a(EntityLivingBase var0) {
      return func_185284_a(Enchantments.field_77334_n, var0);
   }

   public static int func_185292_c(EntityLivingBase var0) {
      return func_185284_a(Enchantments.field_185298_f, var0);
   }

   public static int func_185294_d(EntityLivingBase var0) {
      return func_185284_a(Enchantments.field_185300_i, var0);
   }

   public static int func_185293_e(EntityLivingBase var0) {
      return func_185284_a(Enchantments.field_185305_q, var0);
   }

   public static int func_191529_b(ItemStack var0) {
      return func_77506_a(Enchantments.field_151370_z, var0);
   }

   public static int func_191528_c(ItemStack var0) {
      return func_77506_a(Enchantments.field_151369_A, var0);
   }

   public static int func_185283_h(EntityLivingBase var0) {
      return func_185284_a(Enchantments.field_185304_p, var0);
   }

   public static boolean func_185287_i(EntityLivingBase var0) {
      return func_185284_a(Enchantments.field_185299_g, var0) > 0;
   }

   public static boolean func_189869_j(EntityLivingBase var0) {
      return func_185284_a(Enchantments.field_185301_j, var0) > 0;
   }

   public static boolean func_190938_b(ItemStack var0) {
      return func_77506_a(Enchantments.field_190941_k, var0) > 0;
   }

   public static boolean func_190939_c(ItemStack var0) {
      return func_77506_a(Enchantments.field_190940_C, var0) > 0;
   }

   public static int func_203191_f(ItemStack var0) {
      return func_77506_a(Enchantments.field_203193_C, var0);
   }

   public static int func_203190_g(ItemStack var0) {
      return func_77506_a(Enchantments.field_203195_E, var0);
   }

   public static boolean func_203192_h(ItemStack var0) {
      return func_77506_a(Enchantments.field_203196_F, var0) > 0;
   }

   public static ItemStack func_92099_a(Enchantment var0, EntityLivingBase var1) {
      List var2 = var0.func_185260_a(var1);
      if (var2.isEmpty()) {
         return ItemStack.field_190927_a;
      } else {
         ArrayList var3 = Lists.newArrayList();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            ItemStack var5 = (ItemStack)var4.next();
            if (!var5.func_190926_b() && func_77506_a(var0, var5) > 0) {
               var3.add(var5);
            }
         }

         return var3.isEmpty() ? ItemStack.field_190927_a : (ItemStack)var3.get(var1.func_70681_au().nextInt(var3.size()));
      }
   }

   public static int func_77514_a(Random var0, int var1, int var2, ItemStack var3) {
      Item var4 = var3.func_77973_b();
      int var5 = var4.func_77619_b();
      if (var5 <= 0) {
         return 0;
      } else {
         if (var2 > 15) {
            var2 = 15;
         }

         int var6 = var0.nextInt(8) + 1 + (var2 >> 1) + var0.nextInt(var2 + 1);
         if (var1 == 0) {
            return Math.max(var6 / 3, 1);
         } else {
            return var1 == 1 ? var6 * 2 / 3 + 1 : Math.max(var6, var2 * 2);
         }
      }
   }

   public static ItemStack func_77504_a(Random var0, ItemStack var1, int var2, boolean var3) {
      List var4 = func_77513_b(var0, var1, var2, var3);
      boolean var5 = var1.func_77973_b() == Items.field_151122_aG;
      if (var5) {
         var1 = new ItemStack(Items.field_151134_bR);
      }

      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         EnchantmentData var7 = (EnchantmentData)var6.next();
         if (var5) {
            ItemEnchantedBook.func_92115_a(var1, var7);
         } else {
            var1.func_77966_a(var7.field_76302_b, var7.field_76303_c);
         }
      }

      return var1;
   }

   public static List<EnchantmentData> func_77513_b(Random var0, ItemStack var1, int var2, boolean var3) {
      ArrayList var4 = Lists.newArrayList();
      Item var5 = var1.func_77973_b();
      int var6 = var5.func_77619_b();
      if (var6 <= 0) {
         return var4;
      } else {
         var2 += 1 + var0.nextInt(var6 / 4 + 1) + var0.nextInt(var6 / 4 + 1);
         float var7 = (var0.nextFloat() + var0.nextFloat() - 1.0F) * 0.15F;
         var2 = MathHelper.func_76125_a(Math.round((float)var2 + (float)var2 * var7), 1, 2147483647);
         List var8 = func_185291_a(var2, var1, var3);
         if (!var8.isEmpty()) {
            var4.add(WeightedRandom.func_76271_a(var0, var8));

            while(var0.nextInt(50) <= var2) {
               func_185282_a(var8, (EnchantmentData)Util.func_184878_a(var4));
               if (var8.isEmpty()) {
                  break;
               }

               var4.add(WeightedRandom.func_76271_a(var0, var8));
               var2 /= 2;
            }
         }

         return var4;
      }
   }

   public static void func_185282_a(List<EnchantmentData> var0, EnchantmentData var1) {
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         if (!var1.field_76302_b.func_191560_c(((EnchantmentData)var2.next()).field_76302_b)) {
            var2.remove();
         }
      }

   }

   public static boolean func_201840_a(Collection<Enchantment> var0, Enchantment var1) {
      Iterator var2 = var0.iterator();

      Enchantment var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Enchantment)var2.next();
      } while(var3.func_191560_c(var1));

      return false;
   }

   public static List<EnchantmentData> func_185291_a(int var0, ItemStack var1, boolean var2) {
      ArrayList var3 = Lists.newArrayList();
      Item var4 = var1.func_77973_b();
      boolean var5 = var1.func_77973_b() == Items.field_151122_aG;
      Iterator var6 = IRegistry.field_212628_q.iterator();

      while(true) {
         while(true) {
            Enchantment var7;
            do {
               do {
                  if (!var6.hasNext()) {
                     return var3;
                  }

                  var7 = (Enchantment)var6.next();
               } while(var7.func_185261_e() && !var2);
            } while(!var7.field_77351_y.func_77557_a(var4) && !var5);

            for(int var8 = var7.func_77325_b(); var8 > var7.func_77319_d() - 1; --var8) {
               if (var0 >= var7.func_77321_a(var8) && var0 <= var7.func_77317_b(var8)) {
                  var3.add(new EnchantmentData(var7, var8));
                  break;
               }
            }
         }
      }
   }

   @FunctionalInterface
   interface IEnchantmentVisitor {
      void accept(Enchantment var1, int var2);
   }
}
