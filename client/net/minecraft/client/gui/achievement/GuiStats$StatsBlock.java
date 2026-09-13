package net.minecraft.client.gui.achievement;

import java.util.ArrayList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;

class GuiStats$StatsBlock extends GuiStats$Stats {
   public GuiStats$StatsBlock(GuiStats var1) {
      super(var1);
      this.field_148221_k = var1;
      this.field_148219_m = new ArrayList();

      for(StatCrafting var3 : StatList.field_75939_e) {
         boolean var4 = false;
         int var5 = Item.func_150891_b(var3.func_150959_a());
         if (GuiStats.access$200(var1).func_77444_a(var3) > 0) {
            var4 = true;
         } else if (StatList.field_75929_E[var5] != null && GuiStats.access$200(var1).func_77444_a(StatList.field_75929_E[var5]) > 0) {
            var4 = true;
         } else if (StatList.field_75928_D[var5] != null && GuiStats.access$200(var1).func_77444_a(StatList.field_75928_D[var5]) > 0) {
            var4 = true;
         }

         if (var4) {
            this.field_148219_m.add(var3);
         }
      }

      this.field_148216_n = new GuiStats$StatsBlock$1(this, var1);
   }

   @Override
   protected void func_148129_a(int var1, int var2, Tessellator var3) {
      super.func_148129_a(var1, var2, var3);
      if (this.field_148218_l == 0) {
         GuiStats.access$600(this.field_148221_k, var1 + 115 - 18 + 1, var2 + 1 + 1, 18, 18);
      } else {
         GuiStats.access$600(this.field_148221_k, var1 + 115 - 18, var2 + 1, 18, 18);
      }

      if (this.field_148218_l == 1) {
         GuiStats.access$600(this.field_148221_k, var1 + 165 - 18 + 1, var2 + 1 + 1, 36, 18);
      } else {
         GuiStats.access$600(this.field_148221_k, var1 + 165 - 18, var2 + 1, 36, 18);
      }

      if (this.field_148218_l == 2) {
         GuiStats.access$600(this.field_148221_k, var1 + 215 - 18 + 1, var2 + 1 + 1, 54, 18);
      } else {
         GuiStats.access$600(this.field_148221_k, var1 + 215 - 18, var2 + 1, 54, 18);
      }
   }

   @Override
   protected void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {
      StatCrafting var8 = this.func_148211_c(var1);
      Item var9 = var8.func_150959_a();
      GuiStats.access$1800(this.field_148221_k, var2 + 40, var3, var9);
      int var10 = Item.func_150891_b(var9);
      this.func_148209_a(StatList.field_75928_D[var10], var2 + 115, var3, var1 % 2 == 0);
      this.func_148209_a(StatList.field_75929_E[var10], var2 + 165, var3, var1 % 2 == 0);
      this.func_148209_a(var8, var2 + 215, var3, var1 % 2 == 0);
   }

   @Override
   protected String func_148210_b(int var1) {
      if (var1 == 0) {
         return "stat.crafted";
      } else {
         return var1 == 1 ? "stat.used" : "stat.mined";
      }
   }
}
