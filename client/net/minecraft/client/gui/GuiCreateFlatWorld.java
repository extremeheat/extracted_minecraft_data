package net.minecraft.client.gui;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.gen.FlatGeneratorInfo;

public class GuiCreateFlatWorld extends GuiScreen {
   private static RenderItem field_146392_a = new RenderItem();
   private final GuiCreateWorld field_146385_f;
   private FlatGeneratorInfo field_146387_g = FlatGeneratorInfo.func_82649_e();
   private String field_146393_h;
   private String field_146394_i;
   private String field_146391_r;
   private GuiCreateFlatWorld$Details field_146390_s;
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

   @Override
   public void func_73866_w_() {
      this.field_146292_n.clear();
      this.field_146393_h = I18n.func_135052_a("createWorld.customize.flat.title");
      this.field_146394_i = I18n.func_135052_a("createWorld.customize.flat.tile");
      this.field_146391_r = I18n.func_135052_a("createWorld.customize.flat.height");
      this.field_146390_s = new GuiCreateFlatWorld$Details(this);
      this.field_146292_n
         .add(
            this.field_146389_t = new GuiButton(
               2, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 100, 20, I18n.func_135052_a("createWorld.customize.flat.addLayer") + " (NYI)"
            )
         );
      this.field_146292_n
         .add(
            this.field_146388_u = new GuiButton(
               3, this.field_146294_l / 2 - 50, this.field_146295_m - 52, 100, 20, I18n.func_135052_a("createWorld.customize.flat.editLayer") + " (NYI)"
            )
         );
      this.field_146292_n
         .add(
            this.field_146386_v = new GuiButton(
               4, this.field_146294_l / 2 - 155, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("createWorld.customize.flat.removeLayer")
            )
         );
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 155, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.done")));
      this.field_146292_n
         .add(new GuiButton(5, this.field_146294_l / 2 + 5, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("createWorld.customize.presets")));
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 + 5, this.field_146295_m - 28, 150, 20, I18n.func_135052_a("gui.cancel")));
      this.field_146389_t.field_146125_m = this.field_146388_u.field_146125_m = false;
      this.field_146387_g.func_82645_d();
      this.func_146375_g();
   }

   @Override
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

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146390_s.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146393_h, this.field_146294_l / 2, 8, 16777215);
      int var4 = this.field_146294_l / 2 - 92 - 16;
      this.func_73731_b(this.field_146289_q, this.field_146394_i, var4, 32, 16777215);
      this.func_73731_b(this.field_146289_q, this.field_146391_r, var4 + 2 + 213 - this.field_146289_q.func_78256_a(this.field_146391_r), 32, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
