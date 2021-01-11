package net.minecraft.client.gui;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;

public class GuiCreateFlatWorld extends GuiScreen {
   private final GuiCreateWorld field_146385_f;
   private FlatGeneratorInfo field_146387_g = FlatGeneratorInfo.func_82649_e();
   private String field_146393_h;
   private String field_146394_i;
   private String field_146391_r;
   private GuiCreateFlatWorld.Details field_146390_s;
   private GuiButton field_146389_t;
   private GuiButton field_146388_u;
   private GuiButton field_146386_v;

   public GuiCreateFlatWorld(GuiCreateWorld var1, String var2) {
      super();
      this.field_146385_f = var1;
      this.func_146383_a(var2);
   }

   public String func_146384_e() {
      return this.field_146387_g.toString();
   }

   public void func_146383_a(String var1) {
      this.field_146387_g = FlatGeneratorInfo.func_82651_a(var1);
   }

   public void func_73866_w_() {
      this.field_146292_n.clear();
      this.field_146393_h = I18n.func_135052_a("createWorld.customize.flat.title");
      this.field_146394_i = I18n.func_135052_a("createWorld.customize.flat.tile");
      this.field_146391_r = I18n.func_135052_a("createWorld.customize.flat.height");
      this.field_146390_s = new GuiCreateFlatWorld.Details();
      this.field_146292_n.add(this.field_146389_t = new GuiButton(2, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 100, 20, I18n.func_135052_a("createWorld.customize.flat.addLayer") + " (NYI)"));
      this.field_146292_n.add(this.field_146388_u = new GuiButton(3, this.field_146294_l / 2 - 50, this.field_146295_m - 52, 100, 20, I18n.func_135052_a("createWorld.customize.flat.editLayer") + " (NYI)"));
      this.field_146292_n.add(this.field_146386_v = new GuiButton(4, this.field_146294_l / 2 - 155, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("createWorld.customize.flat.removeLayer")));
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.done")));
      this.field_146292_n.add(new GuiButton(5, this.field_146294_l / 2 + 5, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("createWorld.customize.presets")));
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.cancel")));
      this.field_146389_t.field_146125_m = this.field_146388_u.field_146125_m = false;
      this.field_146387_g.func_82645_d();
      this.func_146375_g();
   }

   public void func_146274_d() {
      super.func_146274_d();
      this.field_146390_s.func_178039_p();
   }

   protected void func_146284_a(GuiButton var1) {
      int var2 = this.field_146387_g.func_82650_c().size() - this.field_146390_s.field_148228_k - 1;
      if (var1.field_146127_k == 1) {
         this.field_146297_k.func_147108_a(this.field_146385_f);
      } else if (var1.field_146127_k == 0) {
         this.field_146385_f.field_146334_a = this.func_146384_e();
         this.field_146297_k.func_147108_a(this.field_146385_f);
      } else if (var1.field_146127_k == 5) {
         this.field_146297_k.func_147108_a(new GuiFlatPresets(this));
      } else if (var1.field_146127_k == 4 && this.func_146382_i()) {
         this.field_146387_g.func_82650_c().remove(var2);
         this.field_146390_s.field_148228_k = Math.min(this.field_146390_s.field_148228_k, this.field_146387_g.func_82650_c().size() - 1);
      }

      this.field_146387_g.func_82645_d();
      this.func_146375_g();
   }

   public void func_146375_g() {
      boolean var1 = this.func_146382_i();
      this.field_146386_v.field_146124_l = var1;
      this.field_146388_u.field_146124_l = var1;
      this.field_146388_u.field_146124_l = false;
      this.field_146389_t.field_146124_l = false;
   }

   private boolean func_146382_i() {
      return this.field_146390_s.field_148228_k > -1 && this.field_146390_s.field_148228_k < this.field_146387_g.func_82650_c().size();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146390_s.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146393_h, this.field_146294_l / 2, 8, 16777215);
      int var4 = this.field_146294_l / 2 - 92 - 16;
      this.func_73731_b(this.field_146289_q, this.field_146394_i, var4, 32, 16777215);
      this.func_73731_b(this.field_146289_q, this.field_146391_r, var4 + 2 + 213 - this.field_146289_q.func_78256_a(this.field_146391_r), 32, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   class Details extends GuiSlot {
      public int field_148228_k = -1;

      public Details() {
         super(GuiCreateFlatWorld.this.field_146297_k, GuiCreateFlatWorld.this.field_146294_l, GuiCreateFlatWorld.this.field_146295_m, 43, GuiCreateFlatWorld.this.field_146295_m - 60, 24);
      }

      private void func_148225_a(int var1, int var2, ItemStack var3) {
         this.func_148226_e(var1 + 1, var2 + 1);
         GlStateManager.func_179091_B();
         if (var3 != null && var3.func_77973_b() != null) {
            RenderHelper.func_74520_c();
            GuiCreateFlatWorld.this.field_146296_j.func_175042_a(var3, var1 + 2, var2 + 2);
            RenderHelper.func_74518_a();
         }

         GlStateManager.func_179101_C();
      }

      private void func_148226_e(int var1, int var2) {
         this.func_148224_c(var1, var2, 0, 0);
      }

      private void func_148224_c(int var1, int var2, int var3, int var4) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_148161_k.func_110434_K().func_110577_a(Gui.field_110323_l);
         float var5 = 0.0078125F;
         float var6 = 0.0078125F;
         boolean var7 = true;
         boolean var8 = true;
         Tessellator var9 = Tessellator.func_178181_a();
         WorldRenderer var10 = var9.func_178180_c();
         var10.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var10.func_181662_b((double)(var1 + 0), (double)(var2 + 18), (double)GuiCreateFlatWorld.this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
         var10.func_181662_b((double)(var1 + 18), (double)(var2 + 18), (double)GuiCreateFlatWorld.this.field_73735_i).func_181673_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F)).func_181675_d();
         var10.func_181662_b((double)(var1 + 18), (double)(var2 + 0), (double)GuiCreateFlatWorld.this.field_73735_i).func_181673_a((double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
         var10.func_181662_b((double)(var1 + 0), (double)(var2 + 0), (double)GuiCreateFlatWorld.this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F)).func_181675_d();
         var9.func_78381_a();
      }

      protected int func_148127_b() {
         return GuiCreateFlatWorld.this.field_146387_g.func_82650_c().size();
      }

      protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
         this.field_148228_k = var1;
         GuiCreateFlatWorld.this.func_146375_g();
      }

      protected boolean func_148131_a(int var1) {
         return var1 == this.field_148228_k;
      }

      protected void func_148123_a() {
      }

      protected void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6) {
         FlatLayerInfo var7 = (FlatLayerInfo)GuiCreateFlatWorld.this.field_146387_g.func_82650_c().get(GuiCreateFlatWorld.this.field_146387_g.func_82650_c().size() - var1 - 1);
         IBlockState var8 = var7.func_175900_c();
         Block var9 = var8.func_177230_c();
         Item var10 = Item.func_150898_a(var9);
         ItemStack var11 = var9 != Blocks.field_150350_a && var10 != null ? new ItemStack(var10, 1, var9.func_176201_c(var8)) : null;
         String var12 = var11 == null ? "Air" : var10.func_77653_i(var11);
         if (var10 == null) {
            if (var9 != Blocks.field_150355_j && var9 != Blocks.field_150358_i) {
               if (var9 == Blocks.field_150353_l || var9 == Blocks.field_150356_k) {
                  var10 = Items.field_151129_at;
               }
            } else {
               var10 = Items.field_151131_as;
            }

            if (var10 != null) {
               var11 = new ItemStack(var10, 1, var9.func_176201_c(var8));
               var12 = var9.func_149732_F();
            }
         }

         this.func_148225_a(var2, var3, var11);
         GuiCreateFlatWorld.this.field_146289_q.func_78276_b(var12, var2 + 18 + 5, var3 + 3, 16777215);
         String var13;
         if (var1 == 0) {
            var13 = I18n.func_135052_a("createWorld.customize.flat.layer.top", var7.func_82657_a());
         } else if (var1 == GuiCreateFlatWorld.this.field_146387_g.func_82650_c().size() - 1) {
            var13 = I18n.func_135052_a("createWorld.customize.flat.layer.bottom", var7.func_82657_a());
         } else {
            var13 = I18n.func_135052_a("createWorld.customize.flat.layer", var7.func_82657_a());
         }

         GuiCreateFlatWorld.this.field_146289_q.func_78276_b(var13, var2 + 2 + 213 - GuiCreateFlatWorld.this.field_146289_q.func_78256_a(var13), var3 + 3, 16777215);
      }

      protected int func_148137_d() {
         return this.field_148155_a - 70;
      }
   }
}
