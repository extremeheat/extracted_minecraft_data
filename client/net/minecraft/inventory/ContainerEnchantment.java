package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container {
   public IInventory field_75168_e = new ContainerEnchantment$1(this, "Enchant", true, 1);
   private World field_75172_h;
   private int field_75173_i;
   private int field_75170_j;
   private int field_75171_k;
   private Random field_75169_l = new Random();
   public long field_75166_f;
   public int[] field_75167_g = new int[3];

   public ContainerEnchantment(InventoryPlayer var1, World var2, int var3, int var4, int var5) {
      super();
      this.field_75172_h = var2;
      this.field_75173_i = var3;
      this.field_75170_j = var4;
      this.field_75171_k = var5;
      this.func_75146_a(new ContainerEnchantment$2(this, this.field_75168_e, 0, 25, 47));

      for(int var6 = 0; var6 < 3; ++var6) {
         for(int var7 = 0; var7 < 9; ++var7) {
            this.func_75146_a(new Slot(var1, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
         }
      }

      for(int var8 = 0; var8 < 9; ++var8) {
         this.func_75146_a(new Slot(var1, var8, 8 + var8 * 18, 142));
      }
   }

   @Override
   public void func_75132_a(ICrafting var1) {
      super.func_75132_a(var1);
      var1.func_71112_a(this, 0, this.field_75167_g[0]);
      var1.func_71112_a(this, 1, this.field_75167_g[1]);
      var1.func_71112_a(this, 2, this.field_75167_g[2]);
   }

   @Override
   public void func_75142_b() {
      super.func_75142_b();

      for(int var1 = 0; var1 < this.field_75149_d.size(); ++var1) {
         ICrafting var2 = (ICrafting)this.field_75149_d.get(var1);
         var2.func_71112_a(this, 0, this.field_75167_g[0]);
         var2.func_71112_a(this, 1, this.field_75167_g[1]);
         var2.func_71112_a(this, 2, this.field_75167_g[2]);
      }
   }

   @Override
   public void func_75137_b(int var1, int var2) {
      if (var1 >= 0 && var1 <= 2) {
         this.field_75167_g[var1] = var2;
      } else {
         super.func_75137_b(var1, var2);
      }
   }

   @Override
   public void func_75130_a(IInventory var1) {
      if (var1 == this.field_75168_e) {
         ItemStack var2 = var1.func_70301_a(0);
         if (var2 != null && var2.func_77956_u()) {
            this.field_75166_f = this.field_75169_l.nextLong();
            if (!this.field_75172_h.field_72995_K) {
               int var6 = 0;

               for(int var4 = -1; var4 <= 1; ++var4) {
                  for(int var5 = -1; var5 <= 1; ++var5) {
                     if ((var4 != 0 || var5 != 0)
                        && this.field_75172_h.func_147437_c(this.field_75173_i + var5, this.field_75170_j, this.field_75171_k + var4)
                        && this.field_75172_h.func_147437_c(this.field_75173_i + var5, this.field_75170_j + 1, this.field_75171_k + var4)) {
                        if (this.field_75172_h.func_147439_a(this.field_75173_i + var5 * 2, this.field_75170_j, this.field_75171_k + var4 * 2)
                           == Blocks.field_150342_X) {
                           ++var6;
                        }

                        if (this.field_75172_h.func_147439_a(this.field_75173_i + var5 * 2, this.field_75170_j + 1, this.field_75171_k + var4 * 2)
                           == Blocks.field_150342_X) {
                           ++var6;
                        }

                        if (var5 != 0 && var4 != 0) {
                           if (this.field_75172_h.func_147439_a(this.field_75173_i + var5 * 2, this.field_75170_j, this.field_75171_k + var4)
                              == Blocks.field_150342_X) {
                              ++var6;
                           }

                           if (this.field_75172_h.func_147439_a(this.field_75173_i + var5 * 2, this.field_75170_j + 1, this.field_75171_k + var4)
                              == Blocks.field_150342_X) {
                              ++var6;
                           }

                           if (this.field_75172_h.func_147439_a(this.field_75173_i + var5, this.field_75170_j, this.field_75171_k + var4 * 2)
                              == Blocks.field_150342_X) {
                              ++var6;
                           }

                           if (this.field_75172_h.func_147439_a(this.field_75173_i + var5, this.field_75170_j + 1, this.field_75171_k + var4 * 2)
                              == Blocks.field_150342_X) {
                              ++var6;
                           }
                        }
                     }
                  }
               }

               for(int var7 = 0; var7 < 3; ++var7) {
                  this.field_75167_g[var7] = EnchantmentHelper.func_77514_a(this.field_75169_l, var7, var6, var2);
               }

               this.func_75142_b();
            }
         } else {
            for(int var3 = 0; var3 < 3; ++var3) {
               this.field_75167_g[var3] = 0;
            }
         }
      }
   }

   @Override
   public boolean func_75140_a(EntityPlayer var1, int var2) {
      ItemStack var3 = this.field_75168_e.func_70301_a(0);
      if (this.field_75167_g[var2] > 0 && var3 != null && (var1.field_71068_ca >= this.field_75167_g[var2] || var1.field_71075_bZ.field_75098_d)) {
         if (!this.field_75172_h.field_72995_K) {
            List var4 = EnchantmentHelper.func_77513_b(this.field_75169_l, var3, this.field_75167_g[var2]);
            boolean var5 = var3.func_77973_b() == Items.field_151122_aG;
            if (var4 != null) {
               var1.func_82242_a(-this.field_75167_g[var2]);
               if (var5) {
                  var3.func_150996_a(Items.field_151134_bR);
               }

               int var6 = var5 && var4.size() > 1 ? this.field_75169_l.nextInt(var4.size()) : -1;

               for(int var7 = 0; var7 < var4.size(); ++var7) {
                  EnchantmentData var8 = (EnchantmentData)var4.get(var7);
                  if (!var5 || var7 != var6) {
                     if (var5) {
                        Items.field_151134_bR.func_92115_a(var3, var8);
                     } else {
                        var3.func_77966_a(var8.field_76302_b, var8.field_76303_c);
                     }
                  }
               }

               this.func_75130_a(this.field_75168_e);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      if (!this.field_75172_h.field_72995_K) {
         ItemStack var2 = this.field_75168_e.func_70304_b(0);
         if (var2 != null) {
            var1.func_71019_a(var2, false);
         }
      }
   }

   @Override
   public boolean func_75145_c(EntityPlayer var1) {
      if (this.field_75172_h.func_147439_a(this.field_75173_i, this.field_75170_j, this.field_75171_k) != Blocks.field_150381_bn) {
         return false;
      } else {
         return !(var1.func_70092_e((double)this.field_75173_i + 0.5, (double)this.field_75170_j + 0.5, (double)this.field_75171_k + 0.5) > 64.0);
      }
   }

   @Override
   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 == 0) {
            if (!this.func_75135_a(var5, 1, 37, true)) {
               return null;
            }
         } else {
            if (((Slot)this.field_75151_b.get(0)).func_75216_d() || !((Slot)this.field_75151_b.get(0)).func_75214_a(var5)) {
               return null;
            }

            if (var5.func_77942_o() && var5.field_77994_a == 1) {
               ((Slot)this.field_75151_b.get(0)).func_75215_d(var5.func_77946_l());
               var5.field_77994_a = 0;
            } else if (var5.field_77994_a >= 1) {
               ((Slot)this.field_75151_b.get(0)).func_75215_d(new ItemStack(var5.func_77973_b(), 1, var5.func_77960_j()));
               --var5.field_77994_a;
            }
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
}
