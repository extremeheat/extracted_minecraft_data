package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandom;

public class EnchantmentHelper {
   private static final Random field_77522_a = new Random();
   private static final EnchantmentHelper.ModifierDamage field_77520_b = new EnchantmentHelper.ModifierDamage();
   private static final EnchantmentHelper.ModifierLiving field_77521_c = new EnchantmentHelper.ModifierLiving();
   private static final EnchantmentHelper.HurtIterator field_151388_d = new EnchantmentHelper.HurtIterator();
   private static final EnchantmentHelper.DamageIterator field_151389_e = new EnchantmentHelper.DamageIterator();

   public static int func_77506_a(int var0, ItemStack var1) {
      if (var1 == null) {
         return 0;
      } else {
         NBTTagList var2 = var1.func_77986_q();
         if (var2 == null) {
            return 0;
         } else {
            for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
               short var4 = var2.func_150305_b(var3).func_74765_d("id");
               short var5 = var2.func_150305_b(var3).func_74765_d("lvl");
               if (var4 == var0) {
                  return var5;
               }
            }

            return 0;
         }
      }
   }

   public static Map<Integer, Integer> func_82781_a(ItemStack var0) {
      LinkedHashMap var1 = Maps.newLinkedHashMap();
      NBTTagList var2 = var0.func_77973_b() == Items.field_151134_bR ? Items.field_151134_bR.func_92110_g(var0) : var0.func_77986_q();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
            short var4 = var2.func_150305_b(var3).func_74765_d("id");
            short var5 = var2.func_150305_b(var3).func_74765_d("lvl");
            var1.put(Integer.valueOf(var4), Integer.valueOf(var5));
         }
      }

      return var1;
   }

   public static void func_82782_a(Map<Integer, Integer> var0, ItemStack var1) {
      NBTTagList var2 = new NBTTagList();
      Iterator var3 = var0.keySet().iterator();

      while(var3.hasNext()) {
         int var4 = (Integer)var3.next();
         Enchantment var5 = Enchantment.func_180306_c(var4);
         if (var5 != null) {
            NBTTagCompound var6 = new NBTTagCompound();
            var6.func_74777_a("id", (short)var4);
            var6.func_74777_a("lvl", (short)(Integer)var0.get(var4));
            var2.func_74742_a(var6);
            if (var1.func_77973_b() == Items.field_151134_bR) {
               Items.field_151134_bR.func_92115_a(var1, new EnchantmentData(var5, (Integer)var0.get(var4)));
            }
         }
      }

      if (var2.func_74745_c() > 0) {
         if (var1.func_77973_b() != Items.field_151134_bR) {
            var1.func_77983_a("ench", var2);
         }
      } else if (var1.func_77942_o()) {
         var1.func_77978_p().func_82580_o("ench");
      }

   }

   public static int func_77511_a(int var0, ItemStack[] var1) {
      if (var1 == null) {
         return 0;
      } else {
         int var2 = 0;
         ItemStack[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ItemStack var6 = var3[var5];
            int var7 = func_77506_a(var0, var6);
            if (var7 > var2) {
               var2 = var7;
            }
         }

         return var2;
      }
   }

   private static void func_77518_a(EnchantmentHelper.IModifier var0, ItemStack var1) {
      if (var1 != null) {
         NBTTagList var2 = var1.func_77986_q();
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
               short var4 = var2.func_150305_b(var3).func_74765_d("id");
               short var5 = var2.func_150305_b(var3).func_74765_d("lvl");
               if (Enchantment.func_180306_c(var4) != null) {
                  var0.func_77493_a(Enchantment.func_180306_c(var4), var5);
               }
            }

         }
      }
   }

   private static void func_77516_a(EnchantmentHelper.IModifier var0, ItemStack[] var1) {
      ItemStack[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemStack var5 = var2[var4];
         func_77518_a(var0, var5);
      }

   }

   public static int func_77508_a(ItemStack[] var0, DamageSource var1) {
      field_77520_b.field_77497_a = 0;
      field_77520_b.field_77496_b = var1;
      func_77516_a(field_77520_b, var0);
      if (field_77520_b.field_77497_a > 25) {
         field_77520_b.field_77497_a = 25;
      } else if (field_77520_b.field_77497_a < 0) {
         field_77520_b.field_77497_a = 0;
      }

      return (field_77520_b.field_77497_a + 1 >> 1) + field_77522_a.nextInt((field_77520_b.field_77497_a >> 1) + 1);
   }

   public static float func_152377_a(ItemStack var0, EnumCreatureAttribute var1) {
      field_77521_c.field_77495_a = 0.0F;
      field_77521_c.field_77494_b = var1;
      func_77518_a(field_77521_c, var0);
      return field_77521_c.field_77495_a;
   }

   public static void func_151384_a(EntityLivingBase var0, Entity var1) {
      field_151388_d.field_151363_b = var1;
      field_151388_d.field_151364_a = var0;
      if (var0 != null) {
         func_77516_a(field_151388_d, var0.func_70035_c());
      }

      if (var1 instanceof EntityPlayer) {
         func_77518_a(field_151388_d, var0.func_70694_bm());
      }

   }

   public static void func_151385_b(EntityLivingBase var0, Entity var1) {
      field_151389_e.field_151366_a = var0;
      field_151389_e.field_151365_b = var1;
      if (var0 != null) {
         func_77516_a(field_151389_e, var0.func_70035_c());
      }

      if (var0 instanceof EntityPlayer) {
         func_77518_a(field_151389_e, var0.func_70694_bm());
      }

   }

   public static int func_77501_a(EntityLivingBase var0) {
      return func_77506_a(Enchantment.field_180313_o.field_77352_x, var0.func_70694_bm());
   }

   public static int func_90036_a(EntityLivingBase var0) {
      return func_77506_a(Enchantment.field_77334_n.field_77352_x, var0.func_70694_bm());
   }

   public static int func_180319_a(Entity var0) {
      return func_77511_a(Enchantment.field_180317_h.field_77352_x, var0.func_70035_c());
   }

   public static int func_180318_b(Entity var0) {
      return func_77511_a(Enchantment.field_180316_k.field_77352_x, var0.func_70035_c());
   }

   public static int func_77509_b(EntityLivingBase var0) {
      return func_77506_a(Enchantment.field_77349_p.field_77352_x, var0.func_70694_bm());
   }

   public static boolean func_77502_d(EntityLivingBase var0) {
      return func_77506_a(Enchantment.field_77348_q.field_77352_x, var0.func_70694_bm()) > 0;
   }

   public static int func_77517_e(EntityLivingBase var0) {
      return func_77506_a(Enchantment.field_77346_s.field_77352_x, var0.func_70694_bm());
   }

   public static int func_151386_g(EntityLivingBase var0) {
      return func_77506_a(Enchantment.field_151370_z.field_77352_x, var0.func_70694_bm());
   }

   public static int func_151387_h(EntityLivingBase var0) {
      return func_77506_a(Enchantment.field_151369_A.field_77352_x, var0.func_70694_bm());
   }

   public static int func_77519_f(EntityLivingBase var0) {
      return func_77506_a(Enchantment.field_77335_o.field_77352_x, var0.func_70694_bm());
   }

   public static boolean func_77510_g(EntityLivingBase var0) {
      return func_77511_a(Enchantment.field_77341_i.field_77352_x, var0.func_70035_c()) > 0;
   }

   public static ItemStack func_92099_a(Enchantment var0, EntityLivingBase var1) {
      ItemStack[] var2 = var1.func_70035_c();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemStack var5 = var2[var4];
         if (var5 != null && func_77506_a(var0.field_77352_x, var5) > 0) {
            return var5;
         }
      }

      return null;
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

   public static ItemStack func_77504_a(Random var0, ItemStack var1, int var2) {
      List var3 = func_77513_b(var0, var1, var2);
      boolean var4 = var1.func_77973_b() == Items.field_151122_aG;
      if (var4) {
         var1.func_150996_a(Items.field_151134_bR);
      }

      if (var3 != null) {
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            EnchantmentData var6 = (EnchantmentData)var5.next();
            if (var4) {
               Items.field_151134_bR.func_92115_a(var1, var6);
            } else {
               var1.func_77966_a(var6.field_76302_b, var6.field_76303_c);
            }
         }
      }

      return var1;
   }

   public static List<EnchantmentData> func_77513_b(Random var0, ItemStack var1, int var2) {
      Item var3 = var1.func_77973_b();
      int var4 = var3.func_77619_b();
      if (var4 <= 0) {
         return null;
      } else {
         var4 /= 2;
         var4 = 1 + var0.nextInt((var4 >> 1) + 1) + var0.nextInt((var4 >> 1) + 1);
         int var5 = var4 + var2;
         float var6 = (var0.nextFloat() + var0.nextFloat() - 1.0F) * 0.15F;
         int var7 = (int)((float)var5 * (1.0F + var6) + 0.5F);
         if (var7 < 1) {
            var7 = 1;
         }

         ArrayList var8 = null;
         Map var9 = func_77505_b(var7, var1);
         if (var9 != null && !var9.isEmpty()) {
            EnchantmentData var10 = (EnchantmentData)WeightedRandom.func_76271_a(var0, var9.values());
            if (var10 != null) {
               var8 = Lists.newArrayList();
               var8.add(var10);

               for(int var11 = var7; var0.nextInt(50) <= var11; var11 >>= 1) {
                  Iterator var12 = var9.keySet().iterator();

                  while(var12.hasNext()) {
                     Integer var13 = (Integer)var12.next();
                     boolean var14 = true;
                     Iterator var15 = var8.iterator();

                     while(var15.hasNext()) {
                        EnchantmentData var16 = (EnchantmentData)var15.next();
                        if (!var16.field_76302_b.func_77326_a(Enchantment.func_180306_c(var13))) {
                           var14 = false;
                           break;
                        }
                     }

                     if (!var14) {
                        var12.remove();
                     }
                  }

                  if (!var9.isEmpty()) {
                     EnchantmentData var17 = (EnchantmentData)WeightedRandom.func_76271_a(var0, var9.values());
                     var8.add(var17);
                  }
               }
            }
         }

         return var8;
      }
   }

   public static Map<Integer, EnchantmentData> func_77505_b(int var0, ItemStack var1) {
      Item var2 = var1.func_77973_b();
      HashMap var3 = null;
      boolean var4 = var1.func_77973_b() == Items.field_151122_aG;
      Enchantment[] var5 = Enchantment.field_77331_b;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Enchantment var8 = var5[var7];
         if (var8 != null && (var8.field_77351_y.func_77557_a(var2) || var4)) {
            for(int var9 = var8.func_77319_d(); var9 <= var8.func_77325_b(); ++var9) {
               if (var0 >= var8.func_77321_a(var9) && var0 <= var8.func_77317_b(var9)) {
                  if (var3 == null) {
                     var3 = Maps.newHashMap();
                  }

                  var3.put(var8.field_77352_x, new EnchantmentData(var8, var9));
               }
            }
         }
      }

      return var3;
   }

   static final class DamageIterator implements EnchantmentHelper.IModifier {
      public EntityLivingBase field_151366_a;
      public Entity field_151365_b;

      private DamageIterator() {
         super();
      }

      public void func_77493_a(Enchantment var1, int var2) {
         var1.func_151368_a(this.field_151366_a, this.field_151365_b, var2);
      }

      // $FF: synthetic method
      DamageIterator(Object var1) {
         this();
      }
   }

   static final class HurtIterator implements EnchantmentHelper.IModifier {
      public EntityLivingBase field_151364_a;
      public Entity field_151363_b;

      private HurtIterator() {
         super();
      }

      public void func_77493_a(Enchantment var1, int var2) {
         var1.func_151367_b(this.field_151364_a, this.field_151363_b, var2);
      }

      // $FF: synthetic method
      HurtIterator(Object var1) {
         this();
      }
   }

   static final class ModifierLiving implements EnchantmentHelper.IModifier {
      public float field_77495_a;
      public EnumCreatureAttribute field_77494_b;

      private ModifierLiving() {
         super();
      }

      public void func_77493_a(Enchantment var1, int var2) {
         this.field_77495_a += var1.func_152376_a(var2, this.field_77494_b);
      }

      // $FF: synthetic method
      ModifierLiving(Object var1) {
         this();
      }
   }

   static final class ModifierDamage implements EnchantmentHelper.IModifier {
      public int field_77497_a;
      public DamageSource field_77496_b;

      private ModifierDamage() {
         super();
      }

      public void func_77493_a(Enchantment var1, int var2) {
         this.field_77497_a += var1.func_77318_a(var2, this.field_77496_b);
      }

      // $FF: synthetic method
      ModifierDamage(Object var1) {
         this();
      }
   }

   interface IModifier {
      void func_77493_a(Enchantment var1, int var2);
   }
}
