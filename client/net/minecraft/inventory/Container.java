package net.minecraft.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public abstract class Container {
   public List<ItemStack> field_75153_a = Lists.newArrayList();
   public List<Slot> field_75151_b = Lists.newArrayList();
   public int field_75152_c;
   private short field_75150_e;
   private int field_94535_f = -1;
   private int field_94536_g;
   private final Set<Slot> field_94537_h = Sets.newHashSet();
   protected List<ICrafting> field_75149_d = Lists.newArrayList();
   private Set<EntityPlayer> field_75148_f = Sets.newHashSet();

   public Container() {
      super();
   }

   protected Slot func_75146_a(Slot var1) {
      var1.field_75222_d = this.field_75151_b.size();
      this.field_75151_b.add(var1);
      this.field_75153_a.add((Object)null);
      return var1;
   }

   public void func_75132_a(ICrafting var1) {
      if (this.field_75149_d.contains(var1)) {
         throw new IllegalArgumentException("Listener already listening");
      } else {
         this.field_75149_d.add(var1);
         var1.func_71110_a(this, this.func_75138_a());
         this.func_75142_b();
      }
   }

   public void func_82847_b(ICrafting var1) {
      this.field_75149_d.remove(var1);
   }

   public List<ItemStack> func_75138_a() {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 < this.field_75151_b.size(); ++var2) {
         var1.add(((Slot)this.field_75151_b.get(var2)).func_75211_c());
      }

      return var1;
   }

   public void func_75142_b() {
      for(int var1 = 0; var1 < this.field_75151_b.size(); ++var1) {
         ItemStack var2 = ((Slot)this.field_75151_b.get(var1)).func_75211_c();
         ItemStack var3 = (ItemStack)this.field_75153_a.get(var1);
         if (!ItemStack.func_77989_b(var3, var2)) {
            var3 = var2 == null ? null : var2.func_77946_l();
            this.field_75153_a.set(var1, var3);

            for(int var4 = 0; var4 < this.field_75149_d.size(); ++var4) {
               ((ICrafting)this.field_75149_d.get(var4)).func_71111_a(this, var1, var3);
            }
         }
      }

   }

   public boolean func_75140_a(EntityPlayer var1, int var2) {
      return false;
   }

   public Slot func_75147_a(IInventory var1, int var2) {
      for(int var3 = 0; var3 < this.field_75151_b.size(); ++var3) {
         Slot var4 = (Slot)this.field_75151_b.get(var3);
         if (var4.func_75217_a(var1, var2)) {
            return var4;
         }
      }

      return null;
   }

   public Slot func_75139_a(int var1) {
      return (Slot)this.field_75151_b.get(var1);
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      Slot var3 = (Slot)this.field_75151_b.get(var2);
      return var3 != null ? var3.func_75211_c() : null;
   }

   public ItemStack func_75144_a(int var1, int var2, int var3, EntityPlayer var4) {
      ItemStack var5 = null;
      InventoryPlayer var6 = var4.field_71071_by;
      int var9;
      ItemStack var17;
      if (var3 == 5) {
         int var7 = this.field_94536_g;
         this.field_94536_g = func_94532_c(var2);
         if ((var7 != 1 || this.field_94536_g != 2) && var7 != this.field_94536_g) {
            this.func_94533_d();
         } else if (var6.func_70445_o() == null) {
            this.func_94533_d();
         } else if (this.field_94536_g == 0) {
            this.field_94535_f = func_94529_b(var2);
            if (func_180610_a(this.field_94535_f, var4)) {
               this.field_94536_g = 1;
               this.field_94537_h.clear();
            } else {
               this.func_94533_d();
            }
         } else if (this.field_94536_g == 1) {
            Slot var8 = (Slot)this.field_75151_b.get(var1);
            if (var8 != null && func_94527_a(var8, var6.func_70445_o(), true) && var8.func_75214_a(var6.func_70445_o()) && var6.func_70445_o().field_77994_a > this.field_94537_h.size() && this.func_94531_b(var8)) {
               this.field_94537_h.add(var8);
            }
         } else if (this.field_94536_g == 2) {
            if (!this.field_94537_h.isEmpty()) {
               var17 = var6.func_70445_o().func_77946_l();
               var9 = var6.func_70445_o().field_77994_a;
               Iterator var10 = this.field_94537_h.iterator();

               while(var10.hasNext()) {
                  Slot var11 = (Slot)var10.next();
                  if (var11 != null && func_94527_a(var11, var6.func_70445_o(), true) && var11.func_75214_a(var6.func_70445_o()) && var6.func_70445_o().field_77994_a >= this.field_94537_h.size() && this.func_94531_b(var11)) {
                     ItemStack var12 = var17.func_77946_l();
                     int var13 = var11.func_75216_d() ? var11.func_75211_c().field_77994_a : 0;
                     func_94525_a(this.field_94537_h, this.field_94535_f, var12, var13);
                     if (var12.field_77994_a > var12.func_77976_d()) {
                        var12.field_77994_a = var12.func_77976_d();
                     }

                     if (var12.field_77994_a > var11.func_178170_b(var12)) {
                        var12.field_77994_a = var11.func_178170_b(var12);
                     }

                     var9 -= var12.field_77994_a - var13;
                     var11.func_75215_d(var12);
                  }
               }

               var17.field_77994_a = var9;
               if (var17.field_77994_a <= 0) {
                  var17 = null;
               }

               var6.func_70437_b(var17);
            }

            this.func_94533_d();
         } else {
            this.func_94533_d();
         }
      } else if (this.field_94536_g != 0) {
         this.func_94533_d();
      } else {
         Slot var16;
         int var19;
         ItemStack var23;
         if ((var3 == 0 || var3 == 1) && (var2 == 0 || var2 == 1)) {
            if (var1 == -999) {
               if (var6.func_70445_o() != null) {
                  if (var2 == 0) {
                     var4.func_71019_a(var6.func_70445_o(), true);
                     var6.func_70437_b((ItemStack)null);
                  }

                  if (var2 == 1) {
                     var4.func_71019_a(var6.func_70445_o().func_77979_a(1), true);
                     if (var6.func_70445_o().field_77994_a == 0) {
                        var6.func_70437_b((ItemStack)null);
                     }
                  }
               }
            } else if (var3 == 1) {
               if (var1 < 0) {
                  return null;
               }

               var16 = (Slot)this.field_75151_b.get(var1);
               if (var16 != null && var16.func_82869_a(var4)) {
                  var17 = this.func_82846_b(var4, var1);
                  if (var17 != null) {
                     Item var20 = var17.func_77973_b();
                     var5 = var17.func_77946_l();
                     if (var16.func_75211_c() != null && var16.func_75211_c().func_77973_b() == var20) {
                        this.func_75133_b(var1, var2, true, var4);
                     }
                  }
               }
            } else {
               if (var1 < 0) {
                  return null;
               }

               var16 = (Slot)this.field_75151_b.get(var1);
               if (var16 != null) {
                  var17 = var16.func_75211_c();
                  ItemStack var21 = var6.func_70445_o();
                  if (var17 != null) {
                     var5 = var17.func_77946_l();
                  }

                  if (var17 == null) {
                     if (var21 != null && var16.func_75214_a(var21)) {
                        var19 = var2 == 0 ? var21.field_77994_a : 1;
                        if (var19 > var16.func_178170_b(var21)) {
                           var19 = var16.func_178170_b(var21);
                        }

                        if (var21.field_77994_a >= var19) {
                           var16.func_75215_d(var21.func_77979_a(var19));
                        }

                        if (var21.field_77994_a == 0) {
                           var6.func_70437_b((ItemStack)null);
                        }
                     }
                  } else if (var16.func_82869_a(var4)) {
                     if (var21 == null) {
                        var19 = var2 == 0 ? var17.field_77994_a : (var17.field_77994_a + 1) / 2;
                        var23 = var16.func_75209_a(var19);
                        var6.func_70437_b(var23);
                        if (var17.field_77994_a == 0) {
                           var16.func_75215_d((ItemStack)null);
                        }

                        var16.func_82870_a(var4, var6.func_70445_o());
                     } else if (var16.func_75214_a(var21)) {
                        if (var17.func_77973_b() == var21.func_77973_b() && var17.func_77960_j() == var21.func_77960_j() && ItemStack.func_77970_a(var17, var21)) {
                           var19 = var2 == 0 ? var21.field_77994_a : 1;
                           if (var19 > var16.func_178170_b(var21) - var17.field_77994_a) {
                              var19 = var16.func_178170_b(var21) - var17.field_77994_a;
                           }

                           if (var19 > var21.func_77976_d() - var17.field_77994_a) {
                              var19 = var21.func_77976_d() - var17.field_77994_a;
                           }

                           var21.func_77979_a(var19);
                           if (var21.field_77994_a == 0) {
                              var6.func_70437_b((ItemStack)null);
                           }

                           var17.field_77994_a += var19;
                        } else if (var21.field_77994_a <= var16.func_178170_b(var21)) {
                           var16.func_75215_d(var21);
                           var6.func_70437_b(var17);
                        }
                     } else if (var17.func_77973_b() == var21.func_77973_b() && var21.func_77976_d() > 1 && (!var17.func_77981_g() || var17.func_77960_j() == var21.func_77960_j()) && ItemStack.func_77970_a(var17, var21)) {
                        var19 = var17.field_77994_a;
                        if (var19 > 0 && var19 + var21.field_77994_a <= var21.func_77976_d()) {
                           var21.field_77994_a += var19;
                           var17 = var16.func_75209_a(var19);
                           if (var17.field_77994_a == 0) {
                              var16.func_75215_d((ItemStack)null);
                           }

                           var16.func_82870_a(var4, var6.func_70445_o());
                        }
                     }
                  }

                  var16.func_75218_e();
               }
            }
         } else if (var3 == 2 && var2 >= 0 && var2 < 9) {
            var16 = (Slot)this.field_75151_b.get(var1);
            if (var16.func_82869_a(var4)) {
               var17 = var6.func_70301_a(var2);
               boolean var18 = var17 == null || var16.field_75224_c == var6 && var16.func_75214_a(var17);
               var19 = -1;
               if (!var18) {
                  var19 = var6.func_70447_i();
                  var18 |= var19 > -1;
               }

               if (var16.func_75216_d() && var18) {
                  var23 = var16.func_75211_c();
                  var6.func_70299_a(var2, var23.func_77946_l());
                  if ((var16.field_75224_c != var6 || !var16.func_75214_a(var17)) && var17 != null) {
                     if (var19 > -1) {
                        var6.func_70441_a(var17);
                        var16.func_75209_a(var23.field_77994_a);
                        var16.func_75215_d((ItemStack)null);
                        var16.func_82870_a(var4, var23);
                     }
                  } else {
                     var16.func_75209_a(var23.field_77994_a);
                     var16.func_75215_d(var17);
                     var16.func_82870_a(var4, var23);
                  }
               } else if (!var16.func_75216_d() && var17 != null && var16.func_75214_a(var17)) {
                  var6.func_70299_a(var2, (ItemStack)null);
                  var16.func_75215_d(var17);
               }
            }
         } else if (var3 == 3 && var4.field_71075_bZ.field_75098_d && var6.func_70445_o() == null && var1 >= 0) {
            var16 = (Slot)this.field_75151_b.get(var1);
            if (var16 != null && var16.func_75216_d()) {
               var17 = var16.func_75211_c().func_77946_l();
               var17.field_77994_a = var17.func_77976_d();
               var6.func_70437_b(var17);
            }
         } else if (var3 == 4 && var6.func_70445_o() == null && var1 >= 0) {
            var16 = (Slot)this.field_75151_b.get(var1);
            if (var16 != null && var16.func_75216_d() && var16.func_82869_a(var4)) {
               var17 = var16.func_75209_a(var2 == 0 ? 1 : var16.func_75211_c().field_77994_a);
               var16.func_82870_a(var4, var17);
               var4.func_71019_a(var17, true);
            }
         } else if (var3 == 6 && var1 >= 0) {
            var16 = (Slot)this.field_75151_b.get(var1);
            var17 = var6.func_70445_o();
            if (var17 != null && (var16 == null || !var16.func_75216_d() || !var16.func_82869_a(var4))) {
               var9 = var2 == 0 ? 0 : this.field_75151_b.size() - 1;
               var19 = var2 == 0 ? 1 : -1;

               for(int var22 = 0; var22 < 2; ++var22) {
                  for(int var24 = var9; var24 >= 0 && var24 < this.field_75151_b.size() && var17.field_77994_a < var17.func_77976_d(); var24 += var19) {
                     Slot var25 = (Slot)this.field_75151_b.get(var24);
                     if (var25.func_75216_d() && func_94527_a(var25, var17, true) && var25.func_82869_a(var4) && this.func_94530_a(var17, var25) && (var22 != 0 || var25.func_75211_c().field_77994_a != var25.func_75211_c().func_77976_d())) {
                        int var14 = Math.min(var17.func_77976_d() - var17.field_77994_a, var25.func_75211_c().field_77994_a);
                        ItemStack var15 = var25.func_75209_a(var14);
                        var17.field_77994_a += var14;
                        if (var15.field_77994_a <= 0) {
                           var25.func_75215_d((ItemStack)null);
                        }

                        var25.func_82870_a(var4, var15);
                     }
                  }
               }
            }

            this.func_75142_b();
         }
      }

      return var5;
   }

   public boolean func_94530_a(ItemStack var1, Slot var2) {
      return true;
   }

   protected void func_75133_b(int var1, int var2, boolean var3, EntityPlayer var4) {
      this.func_75144_a(var1, var2, 1, var4);
   }

   public void func_75134_a(EntityPlayer var1) {
      InventoryPlayer var2 = var1.field_71071_by;
      if (var2.func_70445_o() != null) {
         var1.func_71019_a(var2.func_70445_o(), false);
         var2.func_70437_b((ItemStack)null);
      }

   }

   public void func_75130_a(IInventory var1) {
      this.func_75142_b();
   }

   public void func_75141_a(int var1, ItemStack var2) {
      this.func_75139_a(var1).func_75215_d(var2);
   }

   public void func_75131_a(ItemStack[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.func_75139_a(var2).func_75215_d(var1[var2]);
      }

   }

   public void func_75137_b(int var1, int var2) {
   }

   public short func_75136_a(InventoryPlayer var1) {
      ++this.field_75150_e;
      return this.field_75150_e;
   }

   public boolean func_75129_b(EntityPlayer var1) {
      return !this.field_75148_f.contains(var1);
   }

   public void func_75128_a(EntityPlayer var1, boolean var2) {
      if (var2) {
         this.field_75148_f.remove(var1);
      } else {
         this.field_75148_f.add(var1);
      }

   }

   public abstract boolean func_75145_c(EntityPlayer var1);

   protected boolean func_75135_a(ItemStack var1, int var2, int var3, boolean var4) {
      boolean var5 = false;
      int var6 = var2;
      if (var4) {
         var6 = var3 - 1;
      }

      Slot var7;
      ItemStack var8;
      if (var1.func_77985_e()) {
         while(var1.field_77994_a > 0 && (!var4 && var6 < var3 || var4 && var6 >= var2)) {
            var7 = (Slot)this.field_75151_b.get(var6);
            var8 = var7.func_75211_c();
            if (var8 != null && var8.func_77973_b() == var1.func_77973_b() && (!var1.func_77981_g() || var1.func_77960_j() == var8.func_77960_j()) && ItemStack.func_77970_a(var1, var8)) {
               int var9 = var8.field_77994_a + var1.field_77994_a;
               if (var9 <= var1.func_77976_d()) {
                  var1.field_77994_a = 0;
                  var8.field_77994_a = var9;
                  var7.func_75218_e();
                  var5 = true;
               } else if (var8.field_77994_a < var1.func_77976_d()) {
                  var1.field_77994_a -= var1.func_77976_d() - var8.field_77994_a;
                  var8.field_77994_a = var1.func_77976_d();
                  var7.func_75218_e();
                  var5 = true;
               }
            }

            if (var4) {
               --var6;
            } else {
               ++var6;
            }
         }
      }

      if (var1.field_77994_a > 0) {
         if (var4) {
            var6 = var3 - 1;
         } else {
            var6 = var2;
         }

         while(!var4 && var6 < var3 || var4 && var6 >= var2) {
            var7 = (Slot)this.field_75151_b.get(var6);
            var8 = var7.func_75211_c();
            if (var8 == null) {
               var7.func_75215_d(var1.func_77946_l());
               var7.func_75218_e();
               var1.field_77994_a = 0;
               var5 = true;
               break;
            }

            if (var4) {
               --var6;
            } else {
               ++var6;
            }
         }
      }

      return var5;
   }

   public static int func_94529_b(int var0) {
      return var0 >> 2 & 3;
   }

   public static int func_94532_c(int var0) {
      return var0 & 3;
   }

   public static int func_94534_d(int var0, int var1) {
      return var0 & 3 | (var1 & 3) << 2;
   }

   public static boolean func_180610_a(int var0, EntityPlayer var1) {
      if (var0 == 0) {
         return true;
      } else if (var0 == 1) {
         return true;
      } else {
         return var0 == 2 && var1.field_71075_bZ.field_75098_d;
      }
   }

   protected void func_94533_d() {
      this.field_94536_g = 0;
      this.field_94537_h.clear();
   }

   public static boolean func_94527_a(Slot var0, ItemStack var1, boolean var2) {
      boolean var3 = var0 == null || !var0.func_75216_d();
      if (var0 != null && var0.func_75216_d() && var1 != null && var1.func_77969_a(var0.func_75211_c()) && ItemStack.func_77970_a(var0.func_75211_c(), var1)) {
         var3 |= var0.func_75211_c().field_77994_a + (var2 ? 0 : var1.field_77994_a) <= var1.func_77976_d();
      }

      return var3;
   }

   public static void func_94525_a(Set<Slot> var0, int var1, ItemStack var2, int var3) {
      switch(var1) {
      case 0:
         var2.field_77994_a = MathHelper.func_76141_d((float)var2.field_77994_a / (float)var0.size());
         break;
      case 1:
         var2.field_77994_a = 1;
         break;
      case 2:
         var2.field_77994_a = var2.func_77973_b().func_77639_j();
      }

      var2.field_77994_a += var3;
   }

   public boolean func_94531_b(Slot var1) {
      return true;
   }

   public static int func_178144_a(TileEntity var0) {
      return var0 instanceof IInventory ? func_94526_b((IInventory)var0) : 0;
   }

   public static int func_94526_b(IInventory var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 0;
         float var2 = 0.0F;

         for(int var3 = 0; var3 < var0.func_70302_i_(); ++var3) {
            ItemStack var4 = var0.func_70301_a(var3);
            if (var4 != null) {
               var2 += (float)var4.field_77994_a / (float)Math.min(var0.func_70297_j_(), var4.func_77976_d());
               ++var1;
            }
         }

         var2 /= (float)var0.func_70302_i_();
         return MathHelper.func_76141_d(var2 * 14.0F) + (var1 > 0 ? 1 : 0);
      }
   }
}
