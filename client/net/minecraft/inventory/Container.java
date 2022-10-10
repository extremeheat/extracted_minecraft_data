package net.minecraft.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class Container {
   public NonNullList<ItemStack> field_75153_a = NonNullList.func_191196_a();
   public List<Slot> field_75151_b = Lists.newArrayList();
   public int field_75152_c;
   private short field_75150_e;
   private int field_94535_f = -1;
   private int field_94536_g;
   private final Set<Slot> field_94537_h = Sets.newHashSet();
   protected List<IContainerListener> field_75149_d = Lists.newArrayList();
   private final Set<EntityPlayer> field_75148_f = Sets.newHashSet();

   public Container() {
      super();
   }

   protected Slot func_75146_a(Slot var1) {
      var1.field_75222_d = this.field_75151_b.size();
      this.field_75151_b.add(var1);
      this.field_75153_a.add(ItemStack.field_190927_a);
      return var1;
   }

   public void func_75132_a(IContainerListener var1) {
      if (this.field_75149_d.contains(var1)) {
         throw new IllegalArgumentException("Listener already listening");
      } else {
         this.field_75149_d.add(var1);
         var1.func_71110_a(this, this.func_75138_a());
         this.func_75142_b();
      }
   }

   public void func_82847_b(IContainerListener var1) {
      this.field_75149_d.remove(var1);
   }

   public NonNullList<ItemStack> func_75138_a() {
      NonNullList var1 = NonNullList.func_191196_a();

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
            var3 = var2.func_190926_b() ? ItemStack.field_190927_a : var2.func_77946_l();
            this.field_75153_a.set(var1, var3);

            for(int var4 = 0; var4 < this.field_75149_d.size(); ++var4) {
               ((IContainerListener)this.field_75149_d.get(var4)).func_71111_a(this, var1, var3);
            }
         }
      }

   }

   public boolean func_75140_a(EntityPlayer var1, int var2) {
      return false;
   }

   @Nullable
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
      return var3 != null ? var3.func_75211_c() : ItemStack.field_190927_a;
   }

   public ItemStack func_184996_a(int var1, int var2, ClickType var3, EntityPlayer var4) {
      ItemStack var5 = ItemStack.field_190927_a;
      InventoryPlayer var6 = var4.field_71071_by;
      ItemStack var8;
      ItemStack var9;
      int var15;
      int var18;
      if (var3 == ClickType.QUICK_CRAFT) {
         int var17 = this.field_94536_g;
         this.field_94536_g = func_94532_c(var2);
         if ((var17 != 1 || this.field_94536_g != 2) && var17 != this.field_94536_g) {
            this.func_94533_d();
         } else if (var6.func_70445_o().func_190926_b()) {
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
            Slot var19 = (Slot)this.field_75151_b.get(var1);
            var9 = var6.func_70445_o();
            if (var19 != null && func_94527_a(var19, var9, true) && var19.func_75214_a(var9) && (this.field_94535_f == 2 || var9.func_190916_E() > this.field_94537_h.size()) && this.func_94531_b(var19)) {
               this.field_94537_h.add(var19);
            }
         } else if (this.field_94536_g == 2) {
            if (!this.field_94537_h.isEmpty()) {
               var8 = var6.func_70445_o().func_77946_l();
               var18 = var6.func_70445_o().func_190916_E();
               Iterator var23 = this.field_94537_h.iterator();

               label342:
               while(true) {
                  Slot var20;
                  ItemStack var21;
                  do {
                     do {
                        do {
                           do {
                              if (!var23.hasNext()) {
                                 var8.func_190920_e(var18);
                                 var6.func_70437_b(var8);
                                 break label342;
                              }

                              var20 = (Slot)var23.next();
                              var21 = var6.func_70445_o();
                           } while(var20 == null);
                        } while(!func_94527_a(var20, var21, true));
                     } while(!var20.func_75214_a(var21));
                  } while(this.field_94535_f != 2 && var21.func_190916_E() < this.field_94537_h.size());

                  if (this.func_94531_b(var20)) {
                     ItemStack var22 = var8.func_77946_l();
                     int var24 = var20.func_75216_d() ? var20.func_75211_c().func_190916_E() : 0;
                     func_94525_a(this.field_94537_h, this.field_94535_f, var22, var24);
                     var15 = Math.min(var22.func_77976_d(), var20.func_178170_b(var22));
                     if (var22.func_190916_E() > var15) {
                        var22.func_190920_e(var15);
                     }

                     var18 -= var22.func_190916_E() - var24;
                     var20.func_75215_d(var22);
                  }
               }
            }

            this.func_94533_d();
         } else {
            this.func_94533_d();
         }
      } else if (this.field_94536_g != 0) {
         this.func_94533_d();
      } else {
         Slot var7;
         int var10;
         if (var3 != ClickType.PICKUP && var3 != ClickType.QUICK_MOVE || var2 != 0 && var2 != 1) {
            if (var3 == ClickType.SWAP && var2 >= 0 && var2 < 9) {
               var7 = (Slot)this.field_75151_b.get(var1);
               var8 = var6.func_70301_a(var2);
               var9 = var7.func_75211_c();
               if (!var8.func_190926_b() || !var9.func_190926_b()) {
                  if (var8.func_190926_b()) {
                     if (var7.func_82869_a(var4)) {
                        var6.func_70299_a(var2, var9);
                        var7.func_190900_b(var9.func_190916_E());
                        var7.func_75215_d(ItemStack.field_190927_a);
                        var7.func_190901_a(var4, var9);
                     }
                  } else if (var9.func_190926_b()) {
                     if (var7.func_75214_a(var8)) {
                        var10 = var7.func_178170_b(var8);
                        if (var8.func_190916_E() > var10) {
                           var7.func_75215_d(var8.func_77979_a(var10));
                        } else {
                           var7.func_75215_d(var8);
                           var6.func_70299_a(var2, ItemStack.field_190927_a);
                        }
                     }
                  } else if (var7.func_82869_a(var4) && var7.func_75214_a(var8)) {
                     var10 = var7.func_178170_b(var8);
                     if (var8.func_190916_E() > var10) {
                        var7.func_75215_d(var8.func_77979_a(var10));
                        var7.func_190901_a(var4, var9);
                        if (!var6.func_70441_a(var9)) {
                           var4.func_71019_a(var9, true);
                        }
                     } else {
                        var7.func_75215_d(var8);
                        var6.func_70299_a(var2, var9);
                        var7.func_190901_a(var4, var9);
                     }
                  }
               }
            } else if (var3 == ClickType.CLONE && var4.field_71075_bZ.field_75098_d && var6.func_70445_o().func_190926_b() && var1 >= 0) {
               var7 = (Slot)this.field_75151_b.get(var1);
               if (var7 != null && var7.func_75216_d()) {
                  var8 = var7.func_75211_c().func_77946_l();
                  var8.func_190920_e(var8.func_77976_d());
                  var6.func_70437_b(var8);
               }
            } else if (var3 == ClickType.THROW && var6.func_70445_o().func_190926_b() && var1 >= 0) {
               var7 = (Slot)this.field_75151_b.get(var1);
               if (var7 != null && var7.func_75216_d() && var7.func_82869_a(var4)) {
                  var8 = var7.func_75209_a(var2 == 0 ? 1 : var7.func_75211_c().func_190916_E());
                  var7.func_190901_a(var4, var8);
                  var4.func_71019_a(var8, true);
               }
            } else if (var3 == ClickType.PICKUP_ALL && var1 >= 0) {
               var7 = (Slot)this.field_75151_b.get(var1);
               var8 = var6.func_70445_o();
               if (!var8.func_190926_b() && (var7 == null || !var7.func_75216_d() || !var7.func_82869_a(var4))) {
                  var18 = var2 == 0 ? 0 : this.field_75151_b.size() - 1;
                  var10 = var2 == 0 ? 1 : -1;

                  for(int var11 = 0; var11 < 2; ++var11) {
                     for(int var12 = var18; var12 >= 0 && var12 < this.field_75151_b.size() && var8.func_190916_E() < var8.func_77976_d(); var12 += var10) {
                        Slot var13 = (Slot)this.field_75151_b.get(var12);
                        if (var13.func_75216_d() && func_94527_a(var13, var8, true) && var13.func_82869_a(var4) && this.func_94530_a(var8, var13)) {
                           ItemStack var14 = var13.func_75211_c();
                           if (var11 != 0 || var14.func_190916_E() != var14.func_77976_d()) {
                              var15 = Math.min(var8.func_77976_d() - var8.func_190916_E(), var14.func_190916_E());
                              ItemStack var16 = var13.func_75209_a(var15);
                              var8.func_190917_f(var15);
                              if (var16.func_190926_b()) {
                                 var13.func_75215_d(ItemStack.field_190927_a);
                              }

                              var13.func_190901_a(var4, var16);
                           }
                        }
                     }
                  }
               }

               this.func_75142_b();
            }
         } else if (var1 == -999) {
            if (!var6.func_70445_o().func_190926_b()) {
               if (var2 == 0) {
                  var4.func_71019_a(var6.func_70445_o(), true);
                  var6.func_70437_b(ItemStack.field_190927_a);
               }

               if (var2 == 1) {
                  var4.func_71019_a(var6.func_70445_o().func_77979_a(1), true);
               }
            }
         } else if (var3 == ClickType.QUICK_MOVE) {
            if (var1 < 0) {
               return ItemStack.field_190927_a;
            }

            var7 = (Slot)this.field_75151_b.get(var1);
            if (var7 == null || !var7.func_82869_a(var4)) {
               return ItemStack.field_190927_a;
            }

            for(var8 = this.func_82846_b(var4, var1); !var8.func_190926_b() && ItemStack.func_179545_c(var7.func_75211_c(), var8); var8 = this.func_82846_b(var4, var1)) {
               var5 = var8.func_77946_l();
            }
         } else {
            if (var1 < 0) {
               return ItemStack.field_190927_a;
            }

            var7 = (Slot)this.field_75151_b.get(var1);
            if (var7 != null) {
               var8 = var7.func_75211_c();
               var9 = var6.func_70445_o();
               if (!var8.func_190926_b()) {
                  var5 = var8.func_77946_l();
               }

               if (var8.func_190926_b()) {
                  if (!var9.func_190926_b() && var7.func_75214_a(var9)) {
                     var10 = var2 == 0 ? var9.func_190916_E() : 1;
                     if (var10 > var7.func_178170_b(var9)) {
                        var10 = var7.func_178170_b(var9);
                     }

                     var7.func_75215_d(var9.func_77979_a(var10));
                  }
               } else if (var7.func_82869_a(var4)) {
                  if (var9.func_190926_b()) {
                     if (var8.func_190926_b()) {
                        var7.func_75215_d(ItemStack.field_190927_a);
                        var6.func_70437_b(ItemStack.field_190927_a);
                     } else {
                        var10 = var2 == 0 ? var8.func_190916_E() : (var8.func_190916_E() + 1) / 2;
                        var6.func_70437_b(var7.func_75209_a(var10));
                        if (var8.func_190926_b()) {
                           var7.func_75215_d(ItemStack.field_190927_a);
                        }

                        var7.func_190901_a(var4, var6.func_70445_o());
                     }
                  } else if (var7.func_75214_a(var9)) {
                     if (func_195929_a(var8, var9)) {
                        var10 = var2 == 0 ? var9.func_190916_E() : 1;
                        if (var10 > var7.func_178170_b(var9) - var8.func_190916_E()) {
                           var10 = var7.func_178170_b(var9) - var8.func_190916_E();
                        }

                        if (var10 > var9.func_77976_d() - var8.func_190916_E()) {
                           var10 = var9.func_77976_d() - var8.func_190916_E();
                        }

                        var9.func_190918_g(var10);
                        var8.func_190917_f(var10);
                     } else if (var9.func_190916_E() <= var7.func_178170_b(var9)) {
                        var7.func_75215_d(var9);
                        var6.func_70437_b(var8);
                     }
                  } else if (var9.func_77976_d() > 1 && func_195929_a(var8, var9) && !var8.func_190926_b()) {
                     var10 = var8.func_190916_E();
                     if (var10 + var9.func_190916_E() <= var9.func_77976_d()) {
                        var9.func_190917_f(var10);
                        var8 = var7.func_75209_a(var10);
                        if (var8.func_190926_b()) {
                           var7.func_75215_d(ItemStack.field_190927_a);
                        }

                        var7.func_190901_a(var4, var6.func_70445_o());
                     }
                  }
               }

               var7.func_75218_e();
            }
         }
      }

      return var5;
   }

   public static boolean func_195929_a(ItemStack var0, ItemStack var1) {
      return var0.func_77973_b() == var1.func_77973_b() && ItemStack.func_77970_a(var0, var1);
   }

   public boolean func_94530_a(ItemStack var1, Slot var2) {
      return true;
   }

   public void func_75134_a(EntityPlayer var1) {
      InventoryPlayer var2 = var1.field_71071_by;
      if (!var2.func_70445_o().func_190926_b()) {
         var1.func_71019_a(var2.func_70445_o(), false);
         var2.func_70437_b(ItemStack.field_190927_a);
      }

   }

   protected void func_193327_a(EntityPlayer var1, World var2, IInventory var3) {
      int var4;
      if (!var1.func_70089_S() || var1 instanceof EntityPlayerMP && ((EntityPlayerMP)var1).func_193105_t()) {
         for(var4 = 0; var4 < var3.func_70302_i_(); ++var4) {
            var1.func_71019_a(var3.func_70304_b(var4), false);
         }

      } else {
         for(var4 = 0; var4 < var3.func_70302_i_(); ++var4) {
            var1.field_71071_by.func_191975_a(var2, var3.func_70304_b(var4));
         }

      }
   }

   public void func_75130_a(IInventory var1) {
      this.func_75142_b();
   }

   public void func_75141_a(int var1, ItemStack var2) {
      this.func_75139_a(var1).func_75215_d(var2);
   }

   public void func_190896_a(List<ItemStack> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.func_75139_a(var2).func_75215_d((ItemStack)var1.get(var2));
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
         while(!var1.func_190926_b()) {
            if (var4) {
               if (var6 < var2) {
                  break;
               }
            } else if (var6 >= var3) {
               break;
            }

            var7 = (Slot)this.field_75151_b.get(var6);
            var8 = var7.func_75211_c();
            if (!var8.func_190926_b() && func_195929_a(var1, var8)) {
               int var9 = var8.func_190916_E() + var1.func_190916_E();
               if (var9 <= var1.func_77976_d()) {
                  var1.func_190920_e(0);
                  var8.func_190920_e(var9);
                  var7.func_75218_e();
                  var5 = true;
               } else if (var8.func_190916_E() < var1.func_77976_d()) {
                  var1.func_190918_g(var1.func_77976_d() - var8.func_190916_E());
                  var8.func_190920_e(var1.func_77976_d());
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

      if (!var1.func_190926_b()) {
         if (var4) {
            var6 = var3 - 1;
         } else {
            var6 = var2;
         }

         while(true) {
            if (var4) {
               if (var6 < var2) {
                  break;
               }
            } else if (var6 >= var3) {
               break;
            }

            var7 = (Slot)this.field_75151_b.get(var6);
            var8 = var7.func_75211_c();
            if (var8.func_190926_b() && var7.func_75214_a(var1)) {
               if (var1.func_190916_E() > var7.func_75219_a()) {
                  var7.func_75215_d(var1.func_77979_a(var7.func_75219_a()));
               } else {
                  var7.func_75215_d(var1.func_77979_a(var1.func_190916_E()));
               }

               var7.func_75218_e();
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

   public static boolean func_94527_a(@Nullable Slot var0, ItemStack var1, boolean var2) {
      boolean var3 = var0 == null || !var0.func_75216_d();
      if (!var3 && var1.func_77969_a(var0.func_75211_c()) && ItemStack.func_77970_a(var0.func_75211_c(), var1)) {
         return var0.func_75211_c().func_190916_E() + (var2 ? 0 : var1.func_190916_E()) <= var1.func_77976_d();
      } else {
         return var3;
      }
   }

   public static void func_94525_a(Set<Slot> var0, int var1, ItemStack var2, int var3) {
      switch(var1) {
      case 0:
         var2.func_190920_e(MathHelper.func_76141_d((float)var2.func_190916_E() / (float)var0.size()));
         break;
      case 1:
         var2.func_190920_e(1);
         break;
      case 2:
         var2.func_190920_e(var2.func_77973_b().func_77639_j());
      }

      var2.func_190917_f(var3);
   }

   public boolean func_94531_b(Slot var1) {
      return true;
   }

   public static int func_178144_a(@Nullable TileEntity var0) {
      return var0 instanceof IInventory ? func_94526_b((IInventory)var0) : 0;
   }

   public static int func_94526_b(@Nullable IInventory var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 0;
         float var2 = 0.0F;

         for(int var3 = 0; var3 < var0.func_70302_i_(); ++var3) {
            ItemStack var4 = var0.func_70301_a(var3);
            if (!var4.func_190926_b()) {
               var2 += (float)var4.func_190916_E() / (float)Math.min(var0.func_70297_j_(), var4.func_77976_d());
               ++var1;
            }
         }

         var2 /= (float)var0.func_70302_i_();
         return MathHelper.func_76141_d(var2 * 14.0F) + (var1 > 0 ? 1 : 0);
      }
   }

   protected void func_192389_a(World var1, EntityPlayer var2, IInventory var3, InventoryCraftResult var4) {
      if (!var1.field_72995_K) {
         EntityPlayerMP var5 = (EntityPlayerMP)var2;
         ItemStack var6 = ItemStack.field_190927_a;
         IRecipe var7 = var1.func_73046_m().func_199529_aN().func_199515_b(var3, var1);
         if (var4.func_201561_a(var1, var5, var7) && var7 != null) {
            var6 = var7.func_77572_b(var3);
         }

         var4.func_70299_a(0, var6);
         var5.field_71135_a.func_147359_a(new SPacketSetSlot(this.field_75152_c, 0, var6));
      }
   }
}
