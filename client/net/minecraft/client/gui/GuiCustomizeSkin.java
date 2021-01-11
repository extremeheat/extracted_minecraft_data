package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;

public class GuiCustomizeSkin extends GuiScreen {
   private final GuiScreen field_175361_a;
   private String field_175360_f;

   public GuiCustomizeSkin(GuiScreen var1) {
      super();
      this.field_175361_a = var1;
   }

   public void func_73866_w_() {
      int var1 = 0;
      this.field_175360_f = I18n.func_135052_a("options.skinCustomisation.title");
      EnumPlayerModelParts[] var2 = EnumPlayerModelParts.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumPlayerModelParts var5 = var2[var4];
         this.field_146292_n.add(new GuiCustomizeSkin.ButtonPart(var5.func_179328_b(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 + 24 * (var1 >> 1), 150, 20, var5));
         ++var1;
      }

      if (var1 % 2 == 1) {
         ++var1;
      }

      this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 24 * (var1 >> 1), I18n.func_135052_a("gui.done")));
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 200) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(this.field_175361_a);
         } else if (var1 instanceof GuiCustomizeSkin.ButtonPart) {
            EnumPlayerModelParts var2 = ((GuiCustomizeSkin.ButtonPart)var1).field_175234_p;
            this.field_146297_k.field_71474_y.func_178877_a(var2);
            var1.field_146126_j = this.func_175358_a(var2);
         }

      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_175360_f, this.field_146294_l / 2, 20, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   private String func_175358_a(EnumPlayerModelParts var1) {
      String var2;
      if (this.field_146297_k.field_71474_y.func_178876_d().contains(var1)) {
         var2 = I18n.func_135052_a("options.on");
      } else {
         var2 = I18n.func_135052_a("options.off");
      }

      return var1.func_179326_d().func_150254_d() + ": " + var2;
   }

   class ButtonPart extends GuiButton {
      private final EnumPlayerModelParts field_175234_p;

      private ButtonPart(int var2, int var3, int var4, int var5, int var6, EnumPlayerModelParts var7) {
         super(var2, var3, var4, var5, var6, GuiCustomizeSkin.this.func_175358_a(var7));
         this.field_175234_p = var7;
      }

      // $FF: synthetic method
      ButtonPart(int var2, int var3, int var4, int var5, int var6, EnumPlayerModelParts var7, Object var8) {
         this(var2, var3, var4, var5, var6, var7);
      }
   }
}
