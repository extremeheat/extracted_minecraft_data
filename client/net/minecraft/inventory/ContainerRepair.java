package net.minecraft.inventory;

import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {
   private static final Logger field_148326_f = LogManager.getLogger();
   private IInventory field_82852_f = new InventoryCraftResult();
   private IInventory field_82853_g = new ContainerRepair$1(this, "Repair", true, 2);
   private World field_82860_h;
   private int field_82861_i;
   private int field_82858_j;
   private int field_82859_k;
   public int field_82854_e;
   private int field_82856_l;
   private String field_82857_m;
   private final EntityPlayer field_82855_n;

   public ContainerRepair(InventoryPlayer var1, World var2, int var3, int var4, int var5, EntityPlayer var6) {
      super();
      this.field_82860_h = var2;
      this.field_82861_i = var3;
      this.field_82858_j = var4;
      this.field_82859_k = var5;
      this.field_82855_n = var6;
      this.func_75146_a(new Slot(this.field_82853_g, 0, 27, 47));
      this.func_75146_a(new Slot(this.field_82853_g, 1, 76, 47));
      this.func_75146_a(new ContainerRepair$2(this, this.field_82852_f, 2, 134, 47, var2, var3, var4, var5));

      for(int var7 = 0; var7 < 3; ++var7) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.func_75146_a(new Slot(var1, var8 + var7 * 9 + 9, 8 + var8 * 18, 84 + var7 * 18));
         }
      }

      for(int var9 = 0; var9 < 9; ++var9) {
         this.func_75146_a(new Slot(var1, var9, 8 + var9 * 18, 142));
      }
   }

   @Override
   public void func_75130_a(IInventory var1) {
      super.func_75130_a(var1);
      if (var1 == this.field_82853_g) {
         this.func_82848_d();
      }
   }

   public void func_82848_d() {
      ItemStack var1 = this.field_82853_g.func_70301_a(0);
      this.field_82854_e = 0;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;
      if (var1 == null) {
         this.field_82852_f.func_70299_a(0, null);
         this.field_82854_e = 0;
      } else {
         ItemStack var5 = var1.func_77946_l();
         ItemStack var6 = this.field_82853_g.func_70301_a(1);
         Map var7 = EnchantmentHelper.func_82781_a(var5);
         boolean var8 = false;
         var3 += var1.func_82838_A() + (var6 == null ? 0 : var6.func_82838_A());
         this.field_82856_l = 0;
         if (var6 != null) {
            var8 = var6.func_77973_b() == Items.field_151134_bR && Items.field_151134_bR.func_92110_g(var6).func_74745_c() > 0;
            if (var5.func_77984_f() && var5.func_77973_b().func_82789_a(var1, var6)) {
               int var21 = Math.min(var5.func_77952_i(), var5.func_77958_k() / 4);
               if (var21 <= 0) {
                  this.field_82852_f.func_70299_a(0, null);
                  this.field_82854_e = 0;
                  return;
               }

               int var24;
               for(var24 = 0; var21 > 0 && var24 < var6.field_77994_a; ++var24) {
                  int var29 = var5.func_77952_i() - var21;
                  var5.func_77964_b(var29);
                  var2 += Math.max(1, var21 / 100) + var7.size();
                  var21 = Math.min(var5.func_77952_i(), var5.func_77958_k() / 4);
               }

               this.field_82856_l = var24;
            } else {
               if (!var8 && (var5.func_77973_b() != var6.func_77973_b() || !var5.func_77984_f())) {
                  this.field_82852_f.func_70299_a(0, null);
                  this.field_82854_e = 0;
                  return;
               }

               if (var5.func_77984_f() && !var8) {
                  int var9 = var1.func_77958_k() - var1.func_77952_i();
                  int var10 = var6.func_77958_k() - var6.func_77952_i();
                  int var11 = var10 + var5.func_77958_k() * 12 / 100;
                  int var12 = var9 + var11;
                  int var13 = var5.func_77958_k() - var12;
                  if (var13 < 0) {
                     var13 = 0;
                  }

                  if (var13 < var5.func_77960_j()) {
                     var5.func_77964_b(var13);
                     var2 += Math.max(1, var11 / 100);
                  }
               }

               Map var20 = EnchantmentHelper.func_82781_a(var6);

               for(int var28 : var20.keySet()) {
                  Enchantment var31 = Enchantment.field_77331_b[var28];
                  int var33 = var7.containsKey(var28) ? var7.get(var28) : 0;
                  int var14 = var20.get(var28);
                  var14 = var33 == var14 ? ++var14 : Math.max(var14, var33);
                  int var15 = var14 - var33;
                  boolean var16 = var31.func_92089_a(var1);
                  if (this.field_82855_n.field_71075_bZ.field_75098_d || var1.func_77973_b() == Items.field_151134_bR) {
                     var16 = true;
                  }

                  for(int var18 : var7.keySet()) {
                     if (var18 != var28 && !var31.func_77326_a(Enchantment.field_77331_b[var18])) {
                        var16 = false;
                        var2 += var15;
                     }
                  }

                  if (var16) {
                     if (var14 > var31.func_77325_b()) {
                        var14 = var31.func_77325_b();
                     }

                     var7.put(var28, var14);
                     int var38 = 0;
                     switch(var31.func_77324_c()) {
                        case 1:
                           var38 = 8;
                           break;
                        case 2:
                           var38 = 4;
                        case 3:
                        case 4:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        default:
                           break;
                        case 5:
                           var38 = 2;
                           break;
                        case 10:
                           var38 = 1;
                     }

                     if (var8) {
                        var38 = Math.max(1, var38 / 2);
                     }

                     var2 += var38 * var15;
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.field_82857_m)) {
            if (var1.func_82837_s()) {
               var4 = var1.func_77984_f() ? 7 : var1.field_77994_a * 5;
               var2 += var4;
               var5.func_135074_t();
            }
         } else if (!this.field_82857_m.equals(var1.func_82833_r())) {
            var4 = var1.func_77984_f() ? 7 : var1.field_77994_a * 5;
            var2 += var4;
            if (var1.func_82837_s()) {
               var3 += var4 / 2;
            }

            var5.func_151001_c(this.field_82857_m);
         }

         int var22 = 0;

         for(int var30 : var7.keySet()) {
            Enchantment var32 = Enchantment.field_77331_b[var30];
            int var34 = var7.get(var30);
            int var37 = 0;
            ++var22;
            switch(var32.func_77324_c()) {
               case 1:
                  var37 = 8;
                  break;
               case 2:
                  var37 = 4;
               case 3:
               case 4:
               case 6:
               case 7:
               case 8:
               case 9:
               default:
                  break;
               case 5:
                  var37 = 2;
                  break;
               case 10:
                  var37 = 1;
            }

            if (var8) {
               var37 = Math.max(1, var37 / 2);
            }

            var3 += var22 + var34 * var37;
         }

         if (var8) {
            var3 = Math.max(1, var3 / 2);
         }

         this.field_82854_e = var3 + var2;
         if (var2 <= 0) {
            var5 = null;
         }

         if (var4 == var2 && var4 > 0 && this.field_82854_e >= 40) {
            this.field_82854_e = 39;
         }

         if (this.field_82854_e >= 40 && !this.field_82855_n.field_71075_bZ.field_75098_d) {
            var5 = null;
         }

         if (var5 != null) {
            int var26 = var5.func_82838_A();
            if (var6 != null && var26 < var6.func_82838_A()) {
               var26 = var6.func_82838_A();
            }

            if (var5.func_82837_s()) {
               var26 -= 9;
            }

            if (var26 < 0) {
               var26 = 0;
            }

            var26 += 2;
            var5.func_82841_c(var26);
            EnchantmentHelper.func_82782_a(var7, var5);
         }

         this.field_82852_f.func_70299_a(0, var5);
         this.func_75142_b();
      }
   }

   @Override
   public void func_75132_a(ICrafting var1) {
      super.func_75132_a(var1);
      var1.func_71112_a(this, 0, this.field_82854_e);
   }

   @Override
   public void func_75137_b(int var1, int var2) {
      if (var1 == 0) {
         this.field_82854_e = var2;
      }
   }

   @Override
   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      if (!this.field_82860_h.field_72995_K) {
         for(int var2 = 0; var2 < this.field_82853_g.func_70302_i_(); ++var2) {
            ItemStack var3 = this.field_82853_g.func_70304_b(var2);
            if (var3 != null) {
               var1.func_71019_a(var3, false);
            }
         }
      }
   }

   @Override
   public boolean func_75145_c(EntityPlayer var1) {
      if (this.field_82860_h.func_147439_a(this.field_82861_i, this.field_82858_j, this.field_82859_k) != Blocks.field_150467_bQ) {
         return false;
      } else {
         return !(var1.func_70092_e((double)this.field_82861_i + 0.5, (double)this.field_82858_j + 0.5, (double)this.field_82859_k + 0.5) > 64.0);
      }
   }

   @Override
   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 == 2) {
            if (!this.func_75135_a(var5, 3, 39, true)) {
               return null;
            }

            var4.func_75220_a(var5, var3);
         } else if (var2 != 0 && var2 != 1) {
            if (var2 >= 3 && var2 < 39 && !this.func_75135_a(var5, 0, 2, false)) {
               return null;
            }
         } else if (!this.func_75135_a(var5, 3, 39, false)) {
            return null;
         }

         if (var5.field_77994_a == 0) {
            var4.func_75215_d(null);
         } else {
            var4.func_75218_e();
         }

         if (var5.field_77994_a == var3.field_77994_a) {
            return null;
         }

         var4.func_82870_a(var1, var5);
      }

      return var3;
   }

   public void func_82850_a(String var1) {
      this.field_82857_m = var1;
      if (this.func_75139_a(2).func_75216_d()) {
         ItemStack var2 = this.func_75139_a(2).func_75211_c();
         if (StringUtils.isBlank(var1)) {
            var2.func_135074_t();
         } else {
            var2.func_151001_c(this.field_82857_m);
         }
      }

      this.func_82848_d();
   }
}
