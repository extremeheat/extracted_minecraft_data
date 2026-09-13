package net.minecraft.client.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

class GuiFlatPresets$ListSlot extends GuiSlot {
   public int field_148175_k;

   public GuiFlatPresets$ListSlot(GuiFlatPresets var1) {
      super(var1.field_146297_k, var1.field_146294_l, var1.field_146295_m, 80, var1.field_146295_m - 37, 24);
      this.field_148174_l = var1;
      this.field_148175_k = -1;
   }

   private void func_148172_a(int var1, int var2, Item var3) {
      this.func_148173_e(var1 + 1, var2 + 1);
      GL11.glEnable(32826);
      RenderHelper.func_74520_c();
      GuiFlatPresets.access$000()
         .func_77015_a(this.field_148174_l.field_146289_q, this.field_148174_l.field_146297_k.func_110434_K(), new ItemStack(var3, 1, 0), var1 + 2, var2 + 2);
      RenderHelper.func_74518_a();
      GL11.glDisable(32826);
   }

   private void func_148173_e(int var1, int var2) {
      this.func_148171_c(var1, var2, 0, 0);
   }

   private void func_148171_c(int var1, int var2, int var3, int var4) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_148174_l.field_146297_k.func_110434_K().func_110577_a(Gui.field_110323_l);
      float var5 = 0.0078125F;
      float var6 = 0.0078125F;
      boolean var7 = true;
      boolean var8 = true;
      Tessellator var9 = Tessellator.field_78398_a;
      var9.func_78382_b();
      var9.func_78374_a(
         (double)(var1 + 0),
         (double)(var2 + 18),
         (double)GuiFlatPresets.access$100(this.field_148174_l),
         (double)((float)(var3 + 0) * 0.0078125F),
         (double)((float)(var4 + 18) * 0.0078125F)
      );
      var9.func_78374_a(
         (double)(var1 + 18),
         (double)(var2 + 18),
         (double)GuiFlatPresets.access$200(this.field_148174_l),
         (double)((float)(var3 + 18) * 0.0078125F),
         (double)((float)(var4 + 18) * 0.0078125F)
      );
      var9.func_78374_a(
         (double)(var1 + 18),
         (double)(var2 + 0),
         (double)GuiFlatPresets.access$300(this.field_148174_l),
         (double)((float)(var3 + 18) * 0.0078125F),
         (double)((float)(var4 + 0) * 0.0078125F)
      );
      var9.func_78374_a(
         (double)(var1 + 0),
         (double)(var2 + 0),
         (double)GuiFlatPresets.access$400(this.field_148174_l),
         (double)((float)(var3 + 0) * 0.0078125F),
         (double)((float)(var4 + 0) * 0.0078125F)
      );
      var9.func_78381_a();
   }

   @Override
   protected int func_148127_b() {
      return GuiFlatPresets.access$500().size();
   }

   @Override
   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
      this.field_148175_k = var1;
      this.field_148174_l.func_146426_g();
      GuiFlatPresets.access$700(this.field_148174_l)
         .func_146180_a(
            ((GuiFlatPresets$LayerItem)GuiFlatPresets.access$500().get(GuiFlatPresets.access$600(this.field_148174_l).field_148175_k)).field_148233_c
         );
   }

   @Override
   protected boolean func_148131_a(int var1) {
      return var1 == this.field_148175_k;
   }

   @Override
   protected void func_148123_a() {
   }

   @Override
   protected void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {
      GuiFlatPresets$LayerItem var8 = (GuiFlatPresets$LayerItem)GuiFlatPresets.access$500().get(var1);
      this.func_148172_a(var2, var3, var8.field_148234_a);
      this.field_148174_l.field_146289_q.func_78276_b(var8.field_148232_b, var2 + 18 + 5, var3 + 6, 16777215);
   }
}
