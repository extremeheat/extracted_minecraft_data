package net.minecraft.client.gui.achievement;

import java.util.Comparator;
import net.minecraft.item.Item;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;

class GuiStats$StatsItem$1 implements Comparator {
   GuiStats$StatsItem$1(GuiStats$StatsItem var1, GuiStats var2) {
      super();
      this.field_148343_b = var1;
      this.field_148344_a = var2;
   }

   public int compare(StatCrafting var1, StatCrafting var2) {
      int var3 = Item.func_150891_b(var1.func_150959_a());
      int var4 = Item.func_150891_b(var2.func_150959_a());
      StatBase var5 = null;
      StatBase var6 = null;
      if (this.field_148343_b.field_148217_o == 0) {
         var5 = StatList.field_75930_F[var3];
         var6 = StatList.field_75930_F[var4];
      } else if (this.field_148343_b.field_148217_o == 1) {
         var5 = StatList.field_75928_D[var3];
         var6 = StatList.field_75928_D[var4];
      } else if (this.field_148343_b.field_148217_o == 2) {
         var5 = StatList.field_75929_E[var3];
         var6 = StatList.field_75929_E[var4];
      }

      if (var5 != null || var6 != null) {
         if (var5 == null) {
            return 1;
         }

         if (var6 == null) {
            return -1;
         }

         int var7 = GuiStats.access$200(this.field_148343_b.field_148220_k).func_77444_a(var5);
         int var8 = GuiStats.access$200(this.field_148343_b.field_148220_k).func_77444_a(var6);
         if (var7 != var8) {
            return (var7 - var8) * this.field_148343_b.field_148215_p;
         }
      }

      return var3 - var4;
   }
}
