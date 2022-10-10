package net.minecraft.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ServerRecipePlacerFurnace extends ServerRecipePlacer {
   private boolean field_201517_e;

   public ServerRecipePlacerFurnace() {
      super();
   }

   protected void func_201508_a(IRecipe var1, boolean var2) {
      this.field_201517_e = this.field_201515_d.func_201769_a(var1);
      int var3 = this.field_194331_b.func_194114_b(var1, (IntList)null);
      if (this.field_201517_e) {
         ItemStack var4 = this.field_201515_d.func_75139_a(0).func_75211_c();
         if (var4.func_190926_b() || var3 <= var4.func_190916_E()) {
            return;
         }
      }

      int var6 = this.func_201509_a(var2, var3, this.field_201517_e);
      IntArrayList var5 = new IntArrayList();
      if (this.field_194331_b.func_194118_a(var1, var5, var6)) {
         if (!this.field_201517_e) {
            this.func_201510_a(this.field_201515_d.func_201767_f());
            this.func_201510_a(0);
         }

         this.func_201516_a(var6, var5);
      }
   }

   protected void func_201511_a() {
      this.func_201510_a(this.field_201515_d.func_201767_f());
      super.func_201511_a();
   }

   protected void func_201516_a(int var1, IntList var2) {
      IntListIterator var3 = var2.iterator();
      Slot var4 = this.field_201515_d.func_75139_a(0);
      ItemStack var5 = RecipeItemHelper.func_194115_b((Integer)var3.next());
      if (!var5.func_190926_b()) {
         int var6 = Math.min(var5.func_77976_d(), var1);
         if (this.field_201517_e) {
            var6 -= var4.func_75211_c().func_190916_E();
         }

         for(int var7 = 0; var7 < var6; ++var7) {
            this.func_194325_a(var4, var5);
         }

      }
   }
}
