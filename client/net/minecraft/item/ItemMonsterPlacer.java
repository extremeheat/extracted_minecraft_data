package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList$EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item {
   private IIcon field_94593_a;

   public ItemMonsterPlacer() {
      super();
      this.func_77627_a(true);
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   @Override
   public String func_77653_i(ItemStack var1) {
      String var2 = ("" + StatCollector.func_74838_a(this.func_77658_a() + ".name")).trim();
      String var3 = EntityList.func_75617_a(var1.func_77960_j());
      if (var3 != null) {
         var2 = var2 + " " + StatCollector.func_74838_a("entity." + var3 + ".name");
      }

      return var2;
   }

   @Override
   public int func_82790_a(ItemStack var1, int var2) {
      EntityList$EntityEggInfo var3 = (EntityList$EntityEggInfo)EntityList.field_75627_a.get(var1.func_77960_j());
      if (var3 != null) {
         return var2 == 0 ? var3.field_75611_b : var3.field_75612_c;
      } else {
         return 16777215;
      }
   }

   @Override
   public boolean func_77623_v() {
      return true;
   }

   @Override
   public IIcon func_77618_c(int var1, int var2) {
      return var2 > 0 ? this.field_94593_a : super.func_77618_c(var1, var2);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var3.field_72995_K) {
         return true;
      } else {
         Block var11 = var3.func_147439_a(var4, var5, var6);
         var4 += Facing.field_71586_b[var7];
         var5 += Facing.field_71587_c[var7];
         var6 += Facing.field_71585_d[var7];
         double var12 = 0.0;
         if (var7 == 1 && var11.func_149645_b() == 11) {
            var12 = 0.5;
         }

         Entity var14 = func_77840_a(var3, var1.func_77960_j(), (double)var4 + 0.5, (double)var5 + var12, (double)var6 + 0.5);
         if (var14 != null) {
            if (var14 instanceof EntityLivingBase && var1.func_82837_s()) {
               ((EntityLiving)var14).func_94058_c(var1.func_82833_r());
            }

            if (!var2.field_71075_bZ.field_75098_d) {
               --var1.field_77994_a;
            }
         }

         return true;
      }
   }

   @Override
   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      if (var2.field_72995_K) {
         return var1;
      } else {
         MovingObjectPosition var4 = this.func_77621_a(var2, var3, true);
         if (var4 == null) {
            return var1;
         } else {
            if (var4.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK) {
               int var5 = var4.field_72311_b;
               int var6 = var4.field_72312_c;
               int var7 = var4.field_72309_d;
               if (!var2.func_72962_a(var3, var5, var6, var7)) {
                  return var1;
               }

               if (!var3.func_82247_a(var5, var6, var7, var4.field_72310_e, var1)) {
                  return var1;
               }

               if (var2.func_147439_a(var5, var6, var7) instanceof BlockLiquid) {
                  Entity var8 = func_77840_a(var2, var1.func_77960_j(), (double)var5, (double)var6, (double)var7);
                  if (var8 != null) {
                     if (var8 instanceof EntityLivingBase && var1.func_82837_s()) {
                        ((EntityLiving)var8).func_94058_c(var1.func_82833_r());
                     }

                     if (!var3.field_71075_bZ.field_75098_d) {
                        --var1.field_77994_a;
                     }
                  }
               }
            }

            return var1;
         }
      }
   }

   public static Entity func_77840_a(World var0, int var1, double var2, double var4, double var6) {
      if (!EntityList.field_75627_a.containsKey(var1)) {
         return null;
      } else {
         Entity var8 = null;

         for(int var9 = 0; var9 < 1; ++var9) {
            var8 = EntityList.func_75616_a(var1, var0);
            if (var8 != null && var8 instanceof EntityLivingBase) {
               EntityLiving var10 = (EntityLiving)var8;
               var8.func_70012_b(var2, var4, var6, MathHelper.func_76142_g(var0.field_73012_v.nextFloat() * 360.0F), 0.0F);
               var10.field_70759_as = var10.field_70177_z;
               var10.field_70761_aq = var10.field_70177_z;
               var10.func_110161_a(null);
               var0.func_72838_d(var8);
               var10.func_70642_aH();
            }
         }

         return var8;
      }
   }

   @Override
   public void func_150895_a(Item var1, CreativeTabs var2, List var3) {
      for(EntityList$EntityEggInfo var5 : EntityList.field_75627_a.values()) {
         var3.add(new ItemStack(var1, 1, var5.field_75613_a));
      }
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
      super.func_94581_a(var1);
      this.field_94593_a = var1.func_94245_a(this.func_111208_A() + "_overlay");
   }
}
