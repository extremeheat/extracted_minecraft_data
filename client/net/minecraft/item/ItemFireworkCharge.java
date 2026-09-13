package net.minecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item {
   private IIcon field_150904_a;

   public ItemFireworkCharge() {
      super();
   }

   @Override
   public IIcon func_77618_c(int var1, int var2) {
      return var2 > 0 ? this.field_150904_a : super.func_77618_c(var1, var2);
   }

   @Override
   public int func_82790_a(ItemStack var1, int var2) {
      if (var2 != 1) {
         return super.func_82790_a(var1, var2);
      } else {
         NBTBase var3 = func_150903_a(var1, "Colors");
         if (var3 != null && var3 instanceof NBTTagIntArray) {
            NBTTagIntArray var4 = (NBTTagIntArray)var3;
            int[] var5 = var4.func_150302_c();
            if (var5.length == 1) {
               return var5[0];
            } else {
               int var6 = 0;
               int var7 = 0;
               int var8 = 0;

               for(int var12 : var5) {
                  var6 += (var12 & 0xFF0000) >> 16;
                  var7 += (var12 & 0xFF00) >> 8;
                  var8 += (var12 & 0xFF) >> 0;
               }

               var6 /= var5.length;
               var7 /= var5.length;
               var8 /= var5.length;
               return var6 << 16 | var7 << 8 | var8;
            }
         } else {
            return 9079434;
         }
      }
   }

   @Override
   public boolean func_77623_v() {
      return true;
   }

   public static NBTBase func_150903_a(ItemStack var0, String var1) {
      if (var0.func_77942_o()) {
         NBTTagCompound var2 = var0.func_77978_p().func_74775_l("Explosion");
         if (var2 != null) {
            return var2.func_74781_a(var1);
         }
      }

      return null;
   }

   @Override
   public void func_77624_a(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
      if (var1.func_77942_o()) {
         NBTTagCompound var5 = var1.func_77978_p().func_74775_l("Explosion");
         if (var5 != null) {
            func_150902_a(var5, var3);
         }
      }
   }

   public static void func_150902_a(NBTTagCompound var0, List var1) {
      byte var2 = var0.func_74771_c("Type");
      if (var2 >= 0 && var2 <= 4) {
         var1.add(StatCollector.func_74838_a("item.fireworksCharge.type." + var2).trim());
      } else {
         var1.add(StatCollector.func_74838_a("item.fireworksCharge.type").trim());
      }

      int[] var3 = var0.func_74759_k("Colors");
      if (var3.length > 0) {
         boolean var4 = true;
         String var5 = "";

         for(int var9 : var3) {
            if (!var4) {
               var5 = var5 + ", ";
            }

            var4 = false;
            boolean var10 = false;

            for(int var11 = 0; var11 < 16; ++var11) {
               if (var9 == ItemDye.field_150922_c[var11]) {
                  var10 = true;
                  var5 = var5 + StatCollector.func_74838_a("item.fireworksCharge." + ItemDye.field_150923_a[var11]);
                  break;
               }
            }

            if (!var10) {
               var5 = var5 + StatCollector.func_74838_a("item.fireworksCharge.customColor");
            }
         }

         var1.add(var5);
      }

      int[] var13 = var0.func_74759_k("FadeColors");
      if (var13.length > 0) {
         boolean var14 = true;
         String var16 = StatCollector.func_74838_a("item.fireworksCharge.fadeTo") + " ";

         for(int var21 : var13) {
            if (!var14) {
               var16 = var16 + ", ";
            }

            var14 = false;
            boolean var22 = false;

            for(int var12 = 0; var12 < 16; ++var12) {
               if (var21 == ItemDye.field_150922_c[var12]) {
                  var22 = true;
                  var16 = var16 + StatCollector.func_74838_a("item.fireworksCharge." + ItemDye.field_150923_a[var12]);
                  break;
               }
            }

            if (!var22) {
               var16 = var16 + StatCollector.func_74838_a("item.fireworksCharge.customColor");
            }
         }

         var1.add(var16);
      }

      boolean var15 = var0.func_74767_n("Trail");
      if (var15) {
         var1.add(StatCollector.func_74838_a("item.fireworksCharge.trail"));
      }

      boolean var17 = var0.func_74767_n("Flicker");
      if (var17) {
         var1.add(StatCollector.func_74838_a("item.fireworksCharge.flicker"));
      }
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
      super.func_94581_a(var1);
      this.field_150904_a = var1.func_94245_a(this.func_111208_A() + "_overlay");
   }
}
