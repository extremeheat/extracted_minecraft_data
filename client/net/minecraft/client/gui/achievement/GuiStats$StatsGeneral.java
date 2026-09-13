package net.minecraft.client.gui.achievement;

import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

class GuiStats$StatsGeneral extends GuiSlot {
   public GuiStats$StatsGeneral(GuiStats var1) {
      super(GuiStats.access$000(var1), var1.field_146294_l, var1.field_146295_m, 32, var1.field_146295_m - 64, 10);
      this.field_148208_k = var1;
      this.func_148130_a(false);
   }

   @Override
   protected int func_148127_b() {
      return StatList.field_75941_c.size();
   }

   @Override
   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
   }

   @Override
   protected boolean func_148131_a(int var1) {
      return false;
   }

   @Override
   protected int func_148138_e() {
      return this.func_148127_b() * 10;
   }

   @Override
   protected void func_148123_a() {
      this.field_148208_k.func_146276_q_();
   }

   @Override
   protected void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {
      StatBase var8 = (StatBase)StatList.field_75941_c.get(var1);
      this.field_148208_k
         .func_73731_b(GuiStats.access$100(this.field_148208_k), var8.func_150951_e().func_150260_c(), var2 + 2, var3 + 1, var1 % 2 == 0 ? 16777215 : 9474192);
      String var9 = var8.func_75968_a(GuiStats.access$200(this.field_148208_k).func_77444_a(var8));
      this.field_148208_k
         .func_73731_b(
            GuiStats.access$300(this.field_148208_k),
            var9,
            var2 + 2 + 213 - GuiStats.access$400(this.field_148208_k).func_78256_a(var9),
            var3 + 1,
            var1 % 2 == 0 ? 16777215 : 9474192
         );
   }
}
