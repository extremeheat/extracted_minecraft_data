package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings$Options;

public class GuiLanguage extends GuiScreen {
   protected GuiScreen field_146453_a;
   private GuiLanguage$List field_146450_f;
   private final GameSettings field_146451_g;
   private final LanguageManager field_146454_h;
   private GuiOptionButton field_146455_i;
   private GuiOptionButton field_146452_r;

   public GuiLanguage(GuiScreen var1, GameSettings var2, LanguageManager var3) {
      super();
      this.field_146453_a = var1;
      this.field_146451_g = var2;
      this.field_146454_h = var3;
   }

   @Override
   public void func_73866_w_() {
      boolean var1 = false;
      if (this.field_146455_i != null) {
      }

      this.field_146292_n
         .add(
            this.field_146455_i = new GuiOptionButton(
               100,
               this.field_146294_l / 2 - 155,
               this.field_146295_m - 38,
               GameSettings$Options.FORCE_UNICODE_FONT,
               this.field_146451_g.func_74297_c(GameSettings$Options.FORCE_UNICODE_FONT)
            )
         );
      this.field_146292_n
         .add(this.field_146452_r = new GuiOptionButton(6, this.field_146294_l / 2 - 155 + 160, this.field_146295_m - 38, I18n.func_135052_a("gui.done")));
      this.field_146450_f = new GuiLanguage$List(this);
      this.field_146450_f.func_148134_d(7, 8);
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         switch(var1.field_146127_k) {
            case 5:
               break;
            case 6:
               this.field_146297_k.func_147108_a(this.field_146453_a);
               break;
            case 100:
               if (var1 instanceof GuiOptionButton) {
                  this.field_146451_g.func_74306_a(((GuiOptionButton)var1).func_146136_c(), 1);
                  var1.field_146126_j = this.field_146451_g.func_74297_c(GameSettings$Options.FORCE_UNICODE_FONT);
                  ScaledResolution var2 = new ScaledResolution(this.field_146297_k, this.field_146297_k.field_71443_c, this.field_146297_k.field_71440_d);
                  int var3 = var2.func_78326_a();
                  int var4 = var2.func_78328_b();
                  this.func_146280_a(this.field_146297_k, var3, var4);
               }
               break;
            default:
               this.field_146450_f.func_148147_a(var1);
         }
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.field_146450_f.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("options.language"), this.field_146294_l / 2, 16, 16777215);
      this.func_73732_a(
         this.field_146289_q, "(" + I18n.func_135052_a("options.languageWarning") + ")", this.field_146294_l / 2, this.field_146295_m - 56, 8421504
      );
      super.func_73863_a(var1, var2, var3);
   }
}
