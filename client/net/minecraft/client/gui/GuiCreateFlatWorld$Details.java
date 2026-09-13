package net.minecraft.client.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.opengl.GL11;

class GuiCreateFlatWorld$Details extends GuiSlot {
   public int field_148228_k;

   public GuiCreateFlatWorld$Details(GuiCreateFlatWorld var1) {
      super(var1.field_146297_k, var1.field_146294_l, var1.field_146295_m, 43, var1.field_146295_m - 60, 24);
      this.field_148227_l = var1;
      this.field_148228_k = -1;
   }

   private void func_148225_a(int var1, int var2, ItemStack var3) {
      this.func_148226_e(var1 + 1, var2 + 1);
      GL11.glEnable(32826);
      if (var3 != null) {
         RenderHelper.func_74520_c();
         GuiCreateFlatWorld.access$000()
            .func_77015_a(this.field_148227_l.field_146289_q, this.field_148227_l.field_146297_k.func_110434_K(), var3, var1 + 2, var2 + 2);
         RenderHelper.func_74518_a();
      }

      GL11.glDisable(32826);
   }

   private void func_148226_e(int var1, int var2) {
      this.func_148224_c(var1, var2, 0, 0);
   }

   private void func_148224_c(int var1, int var2, int var3, int var4) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_148227_l.field_146297_k.func_110434_K().func_110577_a(Gui.field_110323_l);
      float var5 = 0.0078125F;
      float var6 = 0.0078125F;
      boolean var7 = true;
      boolean var8 = true;
      Tessellator var9 = Tessellator.field_78398_a;
      var9.func_78382_b();
      var9.func_78374_a(
         (double)(var1 + 0),
         (double)(var2 + 18),
         (double)GuiCreateFlatWorld.access$100(this.field_148227_l),
         (double)((float)(var3 + 0) * 0.0078125F),
         (double)((float)(var4 + 18) * 0.0078125F)
      );
      var9.func_78374_a(
         (double)(var1 + 18),
         (double)(var2 + 18),
         (double)GuiCreateFlatWorld.access$200(this.field_148227_l),
         (double)((float)(var3 + 18) * 0.0078125F),
         (double)((float)(var4 + 18) * 0.0078125F)
      );
      var9.func_78374_a(
         (double)(var1 + 18),
         (double)(var2 + 0),
         (double)GuiCreateFlatWorld.access$300(this.field_148227_l),
         (double)((float)(var3 + 18) * 0.0078125F),
         (double)((float)(var4 + 0) * 0.0078125F)
      );
      var9.func_78374_a(
         (double)(var1 + 0),
         (double)(var2 + 0),
         (double)GuiCreateFlatWorld.access$400(this.field_148227_l),
         (double)((float)(var3 + 0) * 0.0078125F),
         (double)((float)(var4 + 0) * 0.0078125F)
      );
      var9.func_78381_a();
   }

   @Override
   protected int func_148127_b() {
      return GuiCreateFlatWorld.access$500(this.field_148227_l).func_82650_c().size();
   }

   @Override
   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
      this.field_148228_k = var1;
      this.field_148227_l.func_146375_g();
   }

   @Override
   protected boolean func_148131_a(int var1) {
      return var1 == this.field_148228_k;
   }

   @Override
   protected void func_148123_a() {
   }

   @Override
   protected void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {
      FlatLayerInfo var8 = (FlatLayerInfo)GuiCreateFlatWorld.access$500(this.field_148227_l)
         .func_82650_c()
         .get(GuiCreateFlatWorld.access$500(this.field_148227_l).func_82650_c().size() - var1 - 1);
      Item var9 = Item.func_150898_a(var8.func_151536_b());
      ItemStack var10 = var8.func_151536_b() == Blocks.field_150350_a ? null : new ItemStack(var9, 1, var8.func_82658_c());
      String var11 = var10 != null && var9 != null ? var9.func_77653_i(var10) : "Air";
      this.func_148225_a(var2, var3, var10);
      this.field_148227_l.field_146289_q.func_78276_b(var11, var2 + 18 + 5, var3 + 3, 16777215);
      String var12;
      if (var1 == 0) {
         var12 = I18n.func_135052_a("createWorld.customize.flat.layer.top", var8.func_82657_a());
      } else if (var1 == GuiCreateFlatWorld.access$500(this.field_148227_l).func_82650_c().size() - 1) {
         var12 = I18n.func_135052_a("createWorld.customize.flat.layer.bottom", var8.func_82657_a());
      } else {
         var12 = I18n.func_135052_a("createWorld.customize.flat.layer", var8.func_82657_a());
      }

      this.field_148227_l.field_146289_q.func_78276_b(var12, var2 + 2 + 213 - this.field_148227_l.field_146289_q.func_78256_a(var12), var3 + 3, 16777215);
   }

   @Override
   protected int func_148137_d() {
      return this.field_148155_a - 70;
   }
}
