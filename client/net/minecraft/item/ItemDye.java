package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLog;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemDye extends Item {
   public static final String[] field_150923_a = new String[]{
      "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"
   };
   public static final String[] field_150921_b = new String[]{
      "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"
   };
   public static final int[] field_150922_c = new int[]{
      1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320
   };
   private IIcon[] field_150920_d;

   public ItemDye() {
      super();
      this.func_77627_a(true);
      this.func_77656_e(0);
      this.func_77637_a(CreativeTabs.field_78035_l);
   }

   @Override
   public IIcon func_77617_a(int var1) {
      int var2 = MathHelper.func_76125_a(var1, 0, 15);
      return this.field_150920_d[var2];
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      int var2 = MathHelper.func_76125_a(var1.func_77960_j(), 0, 15);
      return super.func_77658_a() + "." + field_150923_a[var2];
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (!var2.func_82247_a(var4, var5, var6, var7, var1)) {
         return false;
      } else {
         if (var1.func_77960_j() == 15) {
            if (func_150919_a(var1, var3, var4, var5, var6)) {
               if (!var3.field_72995_K) {
                  var3.func_72926_e(2005, var4, var5, var6, 0);
               }

               return true;
            }
         } else if (var1.func_77960_j() == 3) {
            Block var11 = var3.func_147439_a(var4, var5, var6);
            int var12 = var3.func_72805_g(var4, var5, var6);
            if (var11 == Blocks.field_150364_r && BlockLog.func_150165_c(var12) == 3) {
               if (var7 == 0) {
                  return false;
               }

               if (var7 == 1) {
                  return false;
               }

               if (var7 == 2) {
                  --var6;
               }

               if (var7 == 3) {
                  ++var6;
               }

               if (var7 == 4) {
                  --var4;
               }

               if (var7 == 5) {
                  ++var4;
               }

               if (var3.func_147437_c(var4, var5, var6)) {
                  int var13 = Blocks.field_150375_by.func_149660_a(var3, var4, var5, var6, var7, var8, var9, var10, 0);
                  var3.func_147465_d(var4, var5, var6, Blocks.field_150375_by, var13, 2);
                  if (!var2.field_71075_bZ.field_75098_d) {
                     --var1.field_77994_a;
                  }
               }

               return true;
            }
         }

         return false;
      }
   }

   public static boolean func_150919_a(ItemStack var0, World var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3, var4);
      if (var5 instanceof IGrowable) {
         IGrowable var6 = (IGrowable)var5;
         if (var6.func_149851_a(var1, var2, var3, var4, var1.field_72995_K)) {
            if (!var1.field_72995_K) {
               if (var6.func_149852_a(var1, var1.field_73012_v, var2, var3, var4)) {
                  var6.func_149853_b(var1, var1.field_73012_v, var2, var3, var4);
               }

               --var0.field_77994_a;
            }

            return true;
         }
      }

      return false;
   }

   public static void func_150918_a(World var0, int var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         var4 = 15;
      }

      Block var5 = var0.func_147439_a(var1, var2, var3);
      if (var5.func_149688_o() != Material.field_151579_a) {
         var5.func_149719_a(var0, var1, var2, var3);

         for(int var6 = 0; var6 < var4; ++var6) {
            double var7 = field_77697_d.nextGaussian() * 0.02;
            double var9 = field_77697_d.nextGaussian() * 0.02;
            double var11 = field_77697_d.nextGaussian() * 0.02;
            var0.func_72869_a(
               "happyVillager",
               (double)((float)var1 + field_77697_d.nextFloat()),
               (double)var2 + (double)field_77697_d.nextFloat() * var5.func_149669_A(),
               (double)((float)var3 + field_77697_d.nextFloat()),
               var7,
               var9,
               var11
            );
         }
      }
   }

   @Override
   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      if (var3 instanceof EntitySheep) {
         EntitySheep var4 = (EntitySheep)var3;
         int var5 = BlockColored.func_150032_b(var1.func_77960_j());
         if (!var4.func_70892_o() && var4.func_70896_n() != var5) {
            var4.func_70891_b(var5);
            --var1.field_77994_a;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_150895_a(Item var1, CreativeTabs var2, List var3) {
      for(int var4 = 0; var4 < 16; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
      this.field_150920_d = new IIcon[field_150921_b.length];

      for(int var2 = 0; var2 < field_150921_b.length; ++var2) {
         this.field_150920_d[var2] = var1.func_94245_a(this.func_111208_A() + "_" + field_150921_b[var2]);
      }
   }
}
