package net.minecraft.inventory;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {
   private static final Logger field_148326_f = LogManager.getLogger();
   private IInventory field_82852_f;
   private IInventory field_82853_g;
   private World field_82860_h;
   private BlockPos field_178156_j;
   public int field_82854_e;
   private int field_82856_l;
   private String field_82857_m;
   private final EntityPlayer field_82855_n;

   public ContainerRepair(InventoryPlayer var1, World var2, EntityPlayer var3) {
      this(var1, var2, BlockPos.field_177992_a, var3);
   }

   public ContainerRepair(InventoryPlayer var1, final World var2, final BlockPos var3, EntityPlayer var4) {
      super();
      this.field_82852_f = new InventoryCraftResult();
      this.field_82853_g = new InventoryBasic("Repair", true, 2) {
         public void func_70296_d() {
            super.func_70296_d();
            ContainerRepair.this.func_75130_a(this);
         }
      };
      this.field_178156_j = var3;
      this.field_82860_h = var2;
      this.field_82855_n = var4;
      this.func_75146_a(new Slot(this.field_82853_g, 0, 27, 47));
      this.func_75146_a(new Slot(this.field_82853_g, 1, 76, 47));
      this.func_75146_a(new Slot(this.field_82852_f, 2, 134, 47) {
         public boolean func_75214_a(ItemStack var1) {
            return false;
         }

         public boolean func_82869_a(EntityPlayer var1) {
            return (var1.field_71075_bZ.field_75098_d || var1.field_71068_ca >= ContainerRepair.this.field_82854_e) && ContainerRepair.this.field_82854_e > 0 && this.func_75216_d();
         }

         public void func_82870_a(EntityPlayer var1, ItemStack var2x) {
            if (!var1.field_71075_bZ.field_75098_d) {
               var1.func_82242_a(-ContainerRepair.this.field_82854_e);
            }

            ContainerRepair.this.field_82853_g.func_70299_a(0, (ItemStack)null);
            if (ContainerRepair.this.field_82856_l > 0) {
               ItemStack var3x = ContainerRepair.this.field_82853_g.func_70301_a(1);
               if (var3x != null && var3x.field_77994_a > ContainerRepair.this.field_82856_l) {
                  var3x.field_77994_a -= ContainerRepair.this.field_82856_l;
                  ContainerRepair.this.field_82853_g.func_70299_a(1, var3x);
               } else {
                  ContainerRepair.this.field_82853_g.func_70299_a(1, (ItemStack)null);
               }
            } else {
               ContainerRepair.this.field_82853_g.func_70299_a(1, (ItemStack)null);
            }

            ContainerRepair.this.field_82854_e = 0;
            IBlockState var5 = var2.func_180495_p(var3);
            if (!var1.field_71075_bZ.field_75098_d && !var2.field_72995_K && var5.func_177230_c() == Blocks.field_150467_bQ && var1.func_70681_au().nextFloat() < 0.12F) {
               int var4 = (Integer)var5.func_177229_b(BlockAnvil.field_176505_b);
               ++var4;
               if (var4 > 2) {
                  var2.func_175698_g(var3);
                  var2.func_175718_b(1020, var3, 0);
               } else {
                  var2.func_180501_a(var3, var5.func_177226_a(BlockAnvil.field_176505_b, var4), 2);
                  var2.func_175718_b(1021, var3, 0);
               }
            } else if (!var2.field_72995_K) {
               var2.func_175718_b(1021, var3, 0);
            }

         }
      });

      int var5;
      for(var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.func_75146_a(new Slot(var1, var6 + var5 * 9 + 9, 8 + var6 * 18, 84 + var5 * 18));
         }
      }

      for(var5 = 0; var5 < 9; ++var5) {
         this.func_75146_a(new Slot(var1, var5, 8 + var5 * 18, 142));
      }

   }

   public void func_75130_a(IInventory var1) {
      super.func_75130_a(var1);
      if (var1 == this.field_82853_g) {
         this.func_82848_d();
      }

   }

   public void func_82848_d() {
      boolean var1 = false;
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      boolean var5 = true;
      boolean var6 = true;
      boolean var7 = true;
      ItemStack var8 = this.field_82853_g.func_70301_a(0);
      this.field_82854_e = 1;
      int var9 = 0;
      byte var10 = 0;
      byte var11 = 0;
      if (var8 == null) {
         this.field_82852_f.func_70299_a(0, (ItemStack)null);
         this.field_82854_e = 0;
      } else {
         ItemStack var12 = var8.func_77946_l();
         ItemStack var13 = this.field_82853_g.func_70301_a(1);
         Map var14 = EnchantmentHelper.func_82781_a(var12);
         boolean var15 = false;
         int var25 = var10 + var8.func_82838_A() + (var13 == null ? 0 : var13.func_82838_A());
         this.field_82856_l = 0;
         int var16;
         if (var13 != null) {
            var15 = var13.func_77973_b() == Items.field_151134_bR && Items.field_151134_bR.func_92110_g(var13).func_74745_c() > 0;
            int var17;
            int var18;
            if (var12.func_77984_f() && var12.func_77973_b().func_82789_a(var8, var13)) {
               var16 = Math.min(var12.func_77952_i(), var12.func_77958_k() / 4);
               if (var16 <= 0) {
                  this.field_82852_f.func_70299_a(0, (ItemStack)null);
                  this.field_82854_e = 0;
                  return;
               }

               for(var17 = 0; var16 > 0 && var17 < var13.field_77994_a; ++var17) {
                  var18 = var12.func_77952_i() - var16;
                  var12.func_77964_b(var18);
                  ++var9;
                  var16 = Math.min(var12.func_77952_i(), var12.func_77958_k() / 4);
               }

               this.field_82856_l = var17;
            } else {
               if (!var15 && (var12.func_77973_b() != var13.func_77973_b() || !var12.func_77984_f())) {
                  this.field_82852_f.func_70299_a(0, (ItemStack)null);
                  this.field_82854_e = 0;
                  return;
               }

               int var20;
               if (var12.func_77984_f() && !var15) {
                  var16 = var8.func_77958_k() - var8.func_77952_i();
                  var17 = var13.func_77958_k() - var13.func_77952_i();
                  var18 = var17 + var12.func_77958_k() * 12 / 100;
                  int var19 = var16 + var18;
                  var20 = var12.func_77958_k() - var19;
                  if (var20 < 0) {
                     var20 = 0;
                  }

                  if (var20 < var12.func_77960_j()) {
                     var12.func_77964_b(var20);
                     var9 += 2;
                  }
               }

               Map var26 = EnchantmentHelper.func_82781_a(var13);
               Iterator var27 = var26.keySet().iterator();

               label144:
               while(true) {
                  Enchantment var28;
                  do {
                     if (!var27.hasNext()) {
                        break label144;
                     }

                     var18 = (Integer)var27.next();
                     var28 = Enchantment.func_180306_c(var18);
                  } while(var28 == null);

                  var20 = var14.containsKey(var18) ? (Integer)var14.get(var18) : 0;
                  int var21 = (Integer)var26.get(var18);
                  int var10000;
                  if (var20 == var21) {
                     ++var21;
                     var10000 = var21;
                  } else {
                     var10000 = Math.max(var21, var20);
                  }

                  var21 = var10000;
                  boolean var22 = var28.func_92089_a(var8);
                  if (this.field_82855_n.field_71075_bZ.field_75098_d || var8.func_77973_b() == Items.field_151134_bR) {
                     var22 = true;
                  }

                  Iterator var23 = var14.keySet().iterator();

                  while(var23.hasNext()) {
                     int var24 = (Integer)var23.next();
                     if (var24 != var18 && !var28.func_77326_a(Enchantment.func_180306_c(var24))) {
                        var22 = false;
                        ++var9;
                     }
                  }

                  if (var22) {
                     if (var21 > var28.func_77325_b()) {
                        var21 = var28.func_77325_b();
                     }

                     var14.put(var18, var21);
                     int var29 = 0;
                     switch(var28.func_77324_c()) {
                     case 1:
                        var29 = 8;
                        break;
                     case 2:
                        var29 = 4;
                     case 3:
                     case 4:
                     case 6:
                     case 7:
                     case 8:
                     case 9:
                     default:
                        break;
                     case 5:
                        var29 = 2;
                        break;
                     case 10:
                        var29 = 1;
                     }

                     if (var15) {
                        var29 = Math.max(1, var29 / 2);
                     }

                     var9 += var29 * var21;
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.field_82857_m)) {
            if (var8.func_82837_s()) {
               var11 = 1;
               var9 += var11;
               var12.func_135074_t();
            }
         } else if (!this.field_82857_m.equals(var8.func_82833_r())) {
            var11 = 1;
            var9 += var11;
            var12.func_151001_c(this.field_82857_m);
         }

         this.field_82854_e = var25 + var9;
         if (var9 <= 0) {
            var12 = null;
         }

         if (var11 == var9 && var11 > 0 && this.field_82854_e >= 40) {
            this.field_82854_e = 39;
         }

         if (this.field_82854_e >= 40 && !this.field_82855_n.field_71075_bZ.field_75098_d) {
            var12 = null;
         }

         if (var12 != null) {
            var16 = var12.func_82838_A();
            if (var13 != null && var16 < var13.func_82838_A()) {
               var16 = var13.func_82838_A();
            }

            var16 = var16 * 2 + 1;
            var12.func_82841_c(var16);
            EnchantmentHelper.func_82782_a(var14, var12);
         }

         this.field_82852_f.func_70299_a(0, var12);
         this.func_75142_b();
      }
   }

   public void func_75132_a(ICrafting var1) {
      super.func_75132_a(var1);
      var1.func_71112_a(this, 0, this.field_82854_e);
   }

   public void func_75137_b(int var1, int var2) {
      if (var1 == 0) {
         this.field_82854_e = var2;
      }

   }

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

   public boolean func_75145_c(EntityPlayer var1) {
      if (this.field_82860_h.func_180495_p(this.field_178156_j).func_177230_c() != Blocks.field_150467_bQ) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_178156_j.func_177958_n() + 0.5D, (double)this.field_178156_j.func_177956_o() + 0.5D, (double)this.field_178156_j.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

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
            var4.func_75215_d((ItemStack)null);
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
