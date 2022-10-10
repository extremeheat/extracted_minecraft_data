package net.minecraft.inventory;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {
   private static final Logger field_148326_f = LogManager.getLogger();
   private final IInventory field_82852_f;
   private final IInventory field_82853_g;
   private final World field_82860_h;
   private final BlockPos field_178156_j;
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
      this.field_82853_g = new InventoryBasic(new TextComponentString("Repair"), 2) {
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

         public ItemStack func_190901_a(EntityPlayer var1, ItemStack var2x) {
            if (!var1.field_71075_bZ.field_75098_d) {
               var1.func_82242_a(-ContainerRepair.this.field_82854_e);
            }

            ContainerRepair.this.field_82853_g.func_70299_a(0, ItemStack.field_190927_a);
            if (ContainerRepair.this.field_82856_l > 0) {
               ItemStack var3x = ContainerRepair.this.field_82853_g.func_70301_a(1);
               if (!var3x.func_190926_b() && var3x.func_190916_E() > ContainerRepair.this.field_82856_l) {
                  var3x.func_190918_g(ContainerRepair.this.field_82856_l);
                  ContainerRepair.this.field_82853_g.func_70299_a(1, var3x);
               } else {
                  ContainerRepair.this.field_82853_g.func_70299_a(1, ItemStack.field_190927_a);
               }
            } else {
               ContainerRepair.this.field_82853_g.func_70299_a(1, ItemStack.field_190927_a);
            }

            ContainerRepair.this.field_82854_e = 0;
            IBlockState var5 = var2.func_180495_p(var3);
            if (!var2.field_72995_K) {
               if (!var1.field_71075_bZ.field_75098_d && var5.func_203425_a(BlockTags.field_200572_k) && var1.func_70681_au().nextFloat() < 0.12F) {
                  IBlockState var4 = BlockAnvil.func_196433_f(var5);
                  if (var4 == null) {
                     var2.func_175698_g(var3);
                     var2.func_175718_b(1029, var3, 0);
                  } else {
                     var2.func_180501_a(var3, var4, 2);
                     var2.func_175718_b(1030, var3, 0);
                  }
               } else {
                  var2.func_175718_b(1030, var3, 0);
               }
            }

            return var2x;
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
      ItemStack var1 = this.field_82853_g.func_70301_a(0);
      this.field_82854_e = 1;
      int var2 = 0;
      byte var3 = 0;
      byte var4 = 0;
      if (var1.func_190926_b()) {
         this.field_82852_f.func_70299_a(0, ItemStack.field_190927_a);
         this.field_82854_e = 0;
      } else {
         ItemStack var5 = var1.func_77946_l();
         ItemStack var6 = this.field_82853_g.func_70301_a(1);
         Map var7 = EnchantmentHelper.func_82781_a(var5);
         int var19 = var3 + var1.func_82838_A() + (var6.func_190926_b() ? 0 : var6.func_82838_A());
         this.field_82856_l = 0;
         if (!var6.func_190926_b()) {
            boolean var8 = var6.func_77973_b() == Items.field_151134_bR && !ItemEnchantedBook.func_92110_g(var6).isEmpty();
            int var9;
            int var10;
            int var11;
            if (var5.func_77984_f() && var5.func_77973_b().func_82789_a(var1, var6)) {
               var9 = Math.min(var5.func_77952_i(), var5.func_77958_k() / 4);
               if (var9 <= 0) {
                  this.field_82852_f.func_70299_a(0, ItemStack.field_190927_a);
                  this.field_82854_e = 0;
                  return;
               }

               for(var10 = 0; var9 > 0 && var10 < var6.func_190916_E(); ++var10) {
                  var11 = var5.func_77952_i() - var9;
                  var5.func_196085_b(var11);
                  ++var2;
                  var9 = Math.min(var5.func_77952_i(), var5.func_77958_k() / 4);
               }

               this.field_82856_l = var10;
            } else {
               if (!var8 && (var5.func_77973_b() != var6.func_77973_b() || !var5.func_77984_f())) {
                  this.field_82852_f.func_70299_a(0, ItemStack.field_190927_a);
                  this.field_82854_e = 0;
                  return;
               }

               if (var5.func_77984_f() && !var8) {
                  var9 = var1.func_77958_k() - var1.func_77952_i();
                  var10 = var6.func_77958_k() - var6.func_77952_i();
                  var11 = var10 + var5.func_77958_k() * 12 / 100;
                  int var12 = var9 + var11;
                  int var13 = var5.func_77958_k() - var12;
                  if (var13 < 0) {
                     var13 = 0;
                  }

                  if (var13 < var5.func_77952_i()) {
                     var5.func_196085_b(var13);
                     var2 += 2;
                  }
               }

               Map var21 = EnchantmentHelper.func_82781_a(var6);
               boolean var22 = false;
               boolean var23 = false;
               Iterator var24 = var21.keySet().iterator();

               label160:
               while(true) {
                  Enchantment var25;
                  do {
                     if (!var24.hasNext()) {
                        if (var23 && !var22) {
                           this.field_82852_f.func_70299_a(0, ItemStack.field_190927_a);
                           this.field_82854_e = 0;
                           return;
                        }
                        break label160;
                     }

                     var25 = (Enchantment)var24.next();
                  } while(var25 == null);

                  int var14 = var7.containsKey(var25) ? (Integer)var7.get(var25) : 0;
                  int var15 = (Integer)var21.get(var25);
                  var15 = var14 == var15 ? var15 + 1 : Math.max(var15, var14);
                  boolean var16 = var25.func_92089_a(var1);
                  if (this.field_82855_n.field_71075_bZ.field_75098_d || var1.func_77973_b() == Items.field_151134_bR) {
                     var16 = true;
                  }

                  Iterator var17 = var7.keySet().iterator();

                  while(var17.hasNext()) {
                     Enchantment var18 = (Enchantment)var17.next();
                     if (var18 != var25 && !var25.func_191560_c(var18)) {
                        var16 = false;
                        ++var2;
                     }
                  }

                  if (!var16) {
                     var23 = true;
                  } else {
                     var22 = true;
                     if (var15 > var25.func_77325_b()) {
                        var15 = var25.func_77325_b();
                     }

                     var7.put(var25, var15);
                     int var26 = 0;
                     switch(var25.func_77324_c()) {
                     case COMMON:
                        var26 = 1;
                        break;
                     case UNCOMMON:
                        var26 = 2;
                        break;
                     case RARE:
                        var26 = 4;
                        break;
                     case VERY_RARE:
                        var26 = 8;
                     }

                     if (var8) {
                        var26 = Math.max(1, var26 / 2);
                     }

                     var2 += var26 * var15;
                     if (var1.func_190916_E() > 1) {
                        var2 = 40;
                     }
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.field_82857_m)) {
            if (var1.func_82837_s()) {
               var4 = 1;
               var2 += var4;
               var5.func_135074_t();
            }
         } else if (!this.field_82857_m.equals(var1.func_200301_q().getString())) {
            var4 = 1;
            var2 += var4;
            var5.func_200302_a(new TextComponentString(this.field_82857_m));
         }

         this.field_82854_e = var19 + var2;
         if (var2 <= 0) {
            var5 = ItemStack.field_190927_a;
         }

         if (var4 == var2 && var4 > 0 && this.field_82854_e >= 40) {
            this.field_82854_e = 39;
         }

         if (this.field_82854_e >= 40 && !this.field_82855_n.field_71075_bZ.field_75098_d) {
            var5 = ItemStack.field_190927_a;
         }

         if (!var5.func_190926_b()) {
            int var20 = var5.func_82838_A();
            if (!var6.func_190926_b() && var20 < var6.func_82838_A()) {
               var20 = var6.func_82838_A();
            }

            if (var4 != var2 || var4 == 0) {
               var20 = var20 * 2 + 1;
            }

            var5.func_82841_c(var20);
            EnchantmentHelper.func_82782_a(var7, var5);
         }

         this.field_82852_f.func_70299_a(0, var5);
         this.func_75142_b();
      }
   }

   public void func_75132_a(IContainerListener var1) {
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
         this.func_193327_a(var1, this.field_82860_h, this.field_82853_g);
      }
   }

   public boolean func_75145_c(EntityPlayer var1) {
      if (!this.field_82860_h.func_180495_p(this.field_178156_j).func_203425_a(BlockTags.field_200572_k)) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_178156_j.func_177958_n() + 0.5D, (double)this.field_178156_j.func_177956_o() + 0.5D, (double)this.field_178156_j.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = ItemStack.field_190927_a;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 == 2) {
            if (!this.func_75135_a(var5, 3, 39, true)) {
               return ItemStack.field_190927_a;
            }

            var4.func_75220_a(var5, var3);
         } else if (var2 != 0 && var2 != 1) {
            if (var2 >= 3 && var2 < 39 && !this.func_75135_a(var5, 0, 2, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(var5, 3, 39, false)) {
            return ItemStack.field_190927_a;
         }

         if (var5.func_190926_b()) {
            var4.func_75215_d(ItemStack.field_190927_a);
         } else {
            var4.func_75218_e();
         }

         if (var5.func_190916_E() == var3.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         var4.func_190901_a(var1, var5);
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
            var2.func_200302_a(new TextComponentString(this.field_82857_m));
         }
      }

      this.func_82848_d();
   }
}
