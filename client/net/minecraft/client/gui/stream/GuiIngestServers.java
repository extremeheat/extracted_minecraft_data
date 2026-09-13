package net.minecraft.client.gui.stream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiIngestServers extends GuiScreen {
   private final GuiScreen field_152309_a;
   private String field_152310_f;
   private GuiIngestServers$ServerList field_152311_g;

   public GuiIngestServers(GuiScreen var1) {
      super();
      this.field_152309_a = var1;
   }

   @Override
   public void func_73866_w_() {
      this.field_152310_f = I18n.func_135052_a("options.stream.ingest.title");
      this.field_152311_g = new GuiIngestServers$ServerList(this);
      if (!this.field_146297_k.func_152346_Z().func_152908_z()) {
         this.field_146297_k.func_152346_Z().func_152909_x();
      }

      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 155, this.field_146295_m - 24 - 6, 150, 20, I18n.func_135052_a("gui.done")));
      this.field_146292_n
         .add(new GuiButton(2, this.field_146294_l / 2 + 5, this.field_146295_m - 24 - 6, 150, 20, I18n.func_135052_a("options.stream.ingest.reset")));
   }

   @Override
   public void func_146281_b() {
      if (this.field_146297_k.func_152346_Z().func_152908_z()) {
         this.field_146297_k.func_152346_Z().func_152932_y().func_153039_l();
      }
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 1) {
            this.field_146297_k.func_147108_a(this.field_152309_a);
         } else {
            this.field_146297_k.field_71474_y.field_152407_Q = "";
            this.field_146297_k.field_71474_y.func_74303_b();
         }
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_152311_g.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_152310_f, this.field_146294_l / 2, 20, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
