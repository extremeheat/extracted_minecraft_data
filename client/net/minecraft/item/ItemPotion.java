package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion extends Item {
   private Map<Integer, List<PotionEffect>> field_77836_a = Maps.newHashMap();
   private static final Map<List<PotionEffect>, Integer> field_77835_b = Maps.newLinkedHashMap();

   public ItemPotion() {
      super();
      this.func_77625_d(1);
      this.func_77627_a(true);
      this.func_77656_e(0);
      this.func_77637_a(CreativeTabs.field_78038_k);
   }

   public List<PotionEffect> func_77832_l(ItemStack var1) {
      if (var1.func_77942_o() && var1.func_77978_p().func_150297_b("CustomPotionEffects", 9)) {
         ArrayList var7 = Lists.newArrayList();
         NBTTagList var3 = var1.func_77978_p().func_150295_c("CustomPotionEffects", 10);

         for(int var4 = 0; var4 < var3.func_74745_c(); ++var4) {
            NBTTagCompound var5 = var3.func_150305_b(var4);
            PotionEffect var6 = PotionEffect.func_82722_b(var5);
            if (var6 != null) {
               var7.add(var6);
            }
         }

         return var7;
      } else {
         List var2 = (List)this.field_77836_a.get(var1.func_77960_j());
         if (var2 == null) {
            var2 = PotionHelper.func_77917_b(var1.func_77960_j(), false);
            this.field_77836_a.put(var1.func_77960_j(), var2);
         }

         return var2;
      }
   }

   public List<PotionEffect> func_77834_f(int var1) {
      List var2 = (List)this.field_77836_a.get(var1);
      if (var2 == null) {
         var2 = PotionHelper.func_77917_b(var1, false);
         this.field_77836_a.put(var1, var2);
      }

      return var2;
   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var3.field_71075_bZ.field_75098_d) {
         --var1.field_77994_a;
      }

      if (!var2.field_72995_K) {
         List var4 = this.func_77832_l(var1);
         if (var4 != null) {
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               PotionEffect var6 = (PotionEffect)var5.next();
               var3.func_70690_d(new PotionEffect(var6));
            }
         }
      }

      var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
      if (!var3.field_71075_bZ.field_75098_d) {
         if (var1.field_77994_a <= 0) {
            return new ItemStack(Items.field_151069_bo);
         }

         var3.field_71071_by.func_70441_a(new ItemStack(Items.field_151069_bo));
      }

      return var1;
   }

   public int func_77626_a(ItemStack var1) {
      return 32;
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.DRINK;
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      if (func_77831_g(var1.func_77960_j())) {
         if (!var3.field_71075_bZ.field_75098_d) {
            --var1.field_77994_a;
         }

         var2.func_72956_a(var3, "random.bow", 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
         if (!var2.field_72995_K) {
            var2.func_72838_d(new EntityPotion(var2, var3, var1));
         }

         var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
         return var1;
      } else {
         var3.func_71008_a(var1, this.func_77626_a(var1));
         return var1;
      }
   }

   public static boolean func_77831_g(int var0) {
      return (var0 & 16384) != 0;
   }

   public int func_77620_a(int var1) {
      return PotionHelper.func_77915_a(var1, false);
   }

   public int func_82790_a(ItemStack var1, int var2) {
      return var2 > 0 ? 16777215 : this.func_77620_a(var1.func_77960_j());
   }

   public boolean func_77833_h(int var1) {
      List var2 = this.func_77834_f(var1);
      if (var2 != null && !var2.isEmpty()) {
         Iterator var3 = var2.iterator();

         PotionEffect var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (PotionEffect)var3.next();
         } while(!Potion.field_76425_a[var4.func_76456_a()].func_76403_b());

         return true;
      } else {
         return false;
      }
   }

   public String func_77653_i(ItemStack var1) {
      if (var1.func_77960_j() == 0) {
         return StatCollector.func_74838_a("item.emptyPotion.name").trim();
      } else {
         String var2 = "";
         if (func_77831_g(var1.func_77960_j())) {
            var2 = StatCollector.func_74838_a("potion.prefix.grenade").trim() + " ";
         }

         List var3 = Items.field_151068_bn.func_77832_l(var1);
         String var4;
         if (var3 != null && !var3.isEmpty()) {
            var4 = ((PotionEffect)var3.get(0)).func_76453_d();
            var4 = var4 + ".postfix";
            return var2 + StatCollector.func_74838_a(var4).trim();
         } else {
            var4 = PotionHelper.func_77905_c(var1.func_77960_j());
            return StatCollector.func_74838_a(var4).trim() + " " + super.func_77653_i(var1);
         }
      }
   }

   public void func_77624_a(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      if (var1.func_77960_j() != 0) {
         List var5 = Items.field_151068_bn.func_77832_l(var1);
         HashMultimap var6 = HashMultimap.create();
         Iterator var16;
         if (var5 != null && !var5.isEmpty()) {
            var16 = var5.iterator();

            while(var16.hasNext()) {
               PotionEffect var8 = (PotionEffect)var16.next();
               String var9 = StatCollector.func_74838_a(var8.func_76453_d()).trim();
               Potion var10 = Potion.field_76425_a[var8.func_76456_a()];
               Map var11 = var10.func_111186_k();
               if (var11 != null && var11.size() > 0) {
                  Iterator var12 = var11.entrySet().iterator();

                  while(var12.hasNext()) {
                     Entry var13 = (Entry)var12.next();
                     AttributeModifier var14 = (AttributeModifier)var13.getValue();
                     AttributeModifier var15 = new AttributeModifier(var14.func_111166_b(), var10.func_111183_a(var8.func_76458_c(), var14), var14.func_111169_c());
                     var6.put(((IAttribute)var13.getKey()).func_111108_a(), var15);
                  }
               }

               if (var8.func_76458_c() > 0) {
                  var9 = var9 + " " + StatCollector.func_74838_a("potion.potency." + var8.func_76458_c()).trim();
               }

               if (var8.func_76459_b() > 20) {
                  var9 = var9 + " (" + Potion.func_76389_a(var8) + ")";
               }

               if (var10.func_76398_f()) {
                  var3.add(EnumChatFormatting.RED + var9);
               } else {
                  var3.add(EnumChatFormatting.GRAY + var9);
               }
            }
         } else {
            String var7 = StatCollector.func_74838_a("potion.empty").trim();
            var3.add(EnumChatFormatting.GRAY + var7);
         }

         if (!var6.isEmpty()) {
            var3.add("");
            var3.add(EnumChatFormatting.DARK_PURPLE + StatCollector.func_74838_a("potion.effects.whenDrank"));
            var16 = var6.entries().iterator();

            while(var16.hasNext()) {
               Entry var17 = (Entry)var16.next();
               AttributeModifier var19 = (AttributeModifier)var17.getValue();
               double var18 = var19.func_111164_d();
               double var20;
               if (var19.func_111169_c() != 1 && var19.func_111169_c() != 2) {
                  var20 = var19.func_111164_d();
               } else {
                  var20 = var19.func_111164_d() * 100.0D;
               }

               if (var18 > 0.0D) {
                  var3.add(EnumChatFormatting.BLUE + StatCollector.func_74837_a("attribute.modifier.plus." + var19.func_111169_c(), ItemStack.field_111284_a.format(var20), StatCollector.func_74838_a("attribute.name." + (String)var17.getKey())));
               } else if (var18 < 0.0D) {
                  var20 *= -1.0D;
                  var3.add(EnumChatFormatting.RED + StatCollector.func_74837_a("attribute.modifier.take." + var19.func_111169_c(), ItemStack.field_111284_a.format(var20), StatCollector.func_74838_a("attribute.name." + (String)var17.getKey())));
               }
            }
         }

      }
   }

   public boolean func_77636_d(ItemStack var1) {
      List var2 = this.func_77832_l(var1);
      return var2 != null && !var2.isEmpty();
   }

   public void func_150895_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      super.func_150895_a(var1, var2, var3);
      int var5;
      if (field_77835_b.isEmpty()) {
         for(int var4 = 0; var4 <= 15; ++var4) {
            for(var5 = 0; var5 <= 1; ++var5) {
               int var6;
               if (var5 == 0) {
                  var6 = var4 | 8192;
               } else {
                  var6 = var4 | 16384;
               }

               for(int var7 = 0; var7 <= 2; ++var7) {
                  int var8 = var6;
                  if (var7 != 0) {
                     if (var7 == 1) {
                        var8 = var6 | 32;
                     } else if (var7 == 2) {
                        var8 = var6 | 64;
                     }
                  }

                  List var9 = PotionHelper.func_77917_b(var8, false);
                  if (var9 != null && !var9.isEmpty()) {
                     field_77835_b.put(var9, var8);
                  }
               }
            }
         }
      }

      Iterator var10 = field_77835_b.values().iterator();

      while(var10.hasNext()) {
         var5 = (Integer)var10.next();
         var3.add(new ItemStack(var1, 1, var5));
      }

   }
}
