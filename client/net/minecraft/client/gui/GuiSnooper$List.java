package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tessellator;

class GuiSnooper$List extends GuiSlot {
   public GuiSnooper$List(GuiSnooper var1) {
      super(var1.field_146297_k, var1.field_146294_l, var1.field_146295_m, 80, var1.field_146295_m - 40, var1.field_146289_q.field_78288_b + 1);
      this.field_148206_k = var1;
   }

   @Override
   protected int func_148127_b() {
      return GuiSnooper.access$000(this.field_148206_k).size();
   }

   @Override
   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
   }

   @Override
   protected boolean func_148131_a(int var1) {
      return false;
   }

   @Override
   protected void func_148123_a() {
   }

   @Override
   protected void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {
      this.field_148206_k.field_146289_q.func_78276_b((String)GuiSnooper.access$000(this.field_148206_k).get(var1), 10, var3, 16777215);
      this.field_148206_k.field_146289_q.func_78276_b((String)GuiSnooper.access$100(this.field_148206_k).get(var1), 230, var3, 16777215);
   }

   @Override
   protected int func_148137_d() {
      return this.field_148155_a - 10;
   }
}
