package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipePlacer implements IRecipePlacer<Integer> {
   protected static final Logger field_194330_a = LogManager.getLogger();
   protected final RecipeItemHelper field_194331_b = new RecipeItemHelper();
   protected InventoryPlayer field_201514_c;
   protected ContainerRecipeBook field_201515_d;

   public ServerRecipePlacer() {
      super();
   }

   public void func_194327_a(EntityPlayerMP var1, @Nullable IRecipe var2, boolean var3) {
      if (var2 != null && var1.func_192037_E().func_193830_f(var2)) {
         this.field_201514_c = var1.field_71071_by;
         this.field_201515_d = (ContainerRecipeBook)var1.field_71070_bA;
         if (this.func_194328_c() || var1.func_184812_l_()) {
            this.field_194331_b.func_194119_a();
            var1.field_71071_by.func_201571_a(this.field_194331_b);
            this.field_201515_d.func_201771_a(this.field_194331_b);
            if (this.field_194331_b.func_194116_a(var2, (IntList)null)) {
               this.func_201508_a(var2, var3);
            } else {
               this.func_201511_a();
               var1.field_71135_a.func_147359_a(new SPacketPlaceGhostRecipe(var1.field_71070_bA.field_75152_c, var2));
            }

            var1.field_71071_by.func_70296_d();
         }
      }
   }

   protected void func_201511_a() {
      for(int var1 = 0; var1 < this.field_201515_d.func_201770_g() * this.field_201515_d.func_201772_h() + 1; ++var1) {
         if (var1 != this.field_201515_d.func_201767_f() || !(this.field_201515_d instanceof ContainerWorkbench) && !(this.field_201515_d instanceof ContainerPlayer)) {
            this.func_201510_a(var1);
         }
      }

      this.field_201515_d.func_201768_e();
   }

   protected void func_201510_a(int var1) {
      ItemStack var2 = this.field_201515_d.func_75139_a(var1).func_75211_c();
      if (!var2.func_190926_b()) {
         for(; var2.func_190916_E() > 0; this.field_201515_d.func_75139_a(var1).func_75209_a(1)) {
            int var3 = this.field_201514_c.func_70432_d(var2);
            if (var3 == -1) {
               var3 = this.field_201514_c.func_70447_i();
            }

            ItemStack var4 = var2.func_77946_l();
            var4.func_190920_e(1);
            if (!this.field_201514_c.func_191971_c(var3, var4)) {
               field_194330_a.error("Can't find any space for item in the inventory");
            }
         }

      }
   }

   protected void func_201508_a(IRecipe var1, boolean var2) {
      boolean var3 = this.field_201515_d.func_201769_a(var1);
      int var4 = this.field_194331_b.func_194114_b(var1, (IntList)null);
      int var5;
      if (var3) {
         for(var5 = 0; var5 < this.field_201515_d.func_201772_h() * this.field_201515_d.func_201770_g() + 1; ++var5) {
            if (var5 != this.field_201515_d.func_201767_f()) {
               ItemStack var6 = this.field_201515_d.func_75139_a(var5).func_75211_c();
               if (!var6.func_190926_b() && Math.min(var4, var6.func_77976_d()) < var6.func_190916_E() + 1) {
                  return;
               }
            }
         }
      }

      var5 = this.func_201509_a(var2, var4, var3);
      IntArrayList var11 = new IntArrayList();
      if (this.field_194331_b.func_194118_a(var1, var11, var5)) {
         int var7 = var5;
         IntListIterator var8 = var11.iterator();

         while(var8.hasNext()) {
            int var9 = (Integer)var8.next();
            int var10 = RecipeItemHelper.func_194115_b(var9).func_77976_d();
            if (var10 < var7) {
               var7 = var10;
            }
         }

         if (this.field_194331_b.func_194118_a(var1, var11, var7)) {
            this.func_201511_a();
            this.func_201501_a(this.field_201515_d.func_201770_g(), this.field_201515_d.func_201772_h(), this.field_201515_d.func_201767_f(), var1, var11.iterator(), var7);
         }
      }

   }

   public void func_201500_a(Iterator<Integer> var1, int var2, int var3, int var4, int var5) {
      Slot var6 = this.field_201515_d.func_75139_a(var2);
      ItemStack var7 = RecipeItemHelper.func_194115_b((Integer)var1.next());
      if (!var7.func_190926_b()) {
         for(int var8 = 0; var8 < var3; ++var8) {
            this.func_194325_a(var6, var7);
         }
      }

   }

   protected int func_201509_a(boolean var1, int var2, boolean var3) {
      int var4 = 1;
      if (var1) {
         var4 = var2;
      } else if (var3) {
         var4 = 64;

         for(int var5 = 0; var5 < this.field_201515_d.func_201770_g() * this.field_201515_d.func_201772_h() + 1; ++var5) {
            if (var5 != this.field_201515_d.func_201767_f()) {
               ItemStack var6 = this.field_201515_d.func_75139_a(var5).func_75211_c();
               if (!var6.func_190926_b() && var4 > var6.func_190916_E()) {
                  var4 = var6.func_190916_E();
               }
            }
         }

         if (var4 < 64) {
            ++var4;
         }
      }

      return var4;
   }

   protected void func_194325_a(Slot var1, ItemStack var2) {
      int var3 = this.field_201514_c.func_194014_c(var2);
      if (var3 != -1) {
         ItemStack var4 = this.field_201514_c.func_70301_a(var3).func_77946_l();
         if (!var4.func_190926_b()) {
            if (var4.func_190916_E() > 1) {
               this.field_201514_c.func_70298_a(var3, 1);
            } else {
               this.field_201514_c.func_70304_b(var3);
            }

            var4.func_190920_e(1);
            if (var1.func_75211_c().func_190926_b()) {
               var1.func_75215_d(var4);
            } else {
               var1.func_75211_c().func_190917_f(1);
            }

         }
      }
   }

   private boolean func_194328_c() {
      ArrayList var1 = Lists.newArrayList();
      int var2 = this.func_203600_c();

      for(int var3 = 0; var3 < this.field_201515_d.func_201770_g() * this.field_201515_d.func_201772_h() + 1; ++var3) {
         if (var3 != this.field_201515_d.func_201767_f()) {
            ItemStack var4 = this.field_201515_d.func_75139_a(var3).func_75211_c().func_77946_l();
            if (!var4.func_190926_b()) {
               int var5 = this.field_201514_c.func_70432_d(var4);
               if (var5 == -1 && var1.size() <= var2) {
                  Iterator var6 = var1.iterator();

                  while(var6.hasNext()) {
                     ItemStack var7 = (ItemStack)var6.next();
                     if (var7.func_77969_a(var4) && var7.func_190916_E() != var7.func_77976_d() && var7.func_190916_E() + var4.func_190916_E() <= var7.func_77976_d()) {
                        var7.func_190917_f(var4.func_190916_E());
                        var4.func_190920_e(0);
                        break;
                     }
                  }

                  if (!var4.func_190926_b()) {
                     if (var1.size() >= var2) {
                        return false;
                     }

                     var1.add(var4);
                  }
               } else if (var5 == -1) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int func_203600_c() {
      int var1 = 0;
      Iterator var2 = this.field_201514_c.field_70462_a.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         if (var3.func_190926_b()) {
            ++var1;
         }
      }

      return var1;
   }
}
