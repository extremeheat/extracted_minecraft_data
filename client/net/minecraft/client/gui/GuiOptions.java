package net.minecraft.client.gui;

import net.minecraft.client.gui.stream.GuiStreamOptions;
import net.minecraft.client.gui.stream.GuiStreamUnavailable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings$Options;
import net.minecraft.client.stream.IStream;

public class GuiOptions extends GuiScreen implements GuiYesNoCallback {
   private static final GameSettings$Options[] field_146440_f = new GameSettings$Options[]{GameSettings$Options.FOV, GameSettings$Options.DIFFICULTY};
   private final GuiScreen field_146441_g;
   private final GameSettings field_146443_h;
   protected String field_146442_a = "Options";

   public GuiOptions(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146441_g = var1;
      this.field_146443_h = var2;
   }

   @Override
   public void func_73866_w_() {
      int var1 = 0;
      this.field_146442_a = I18n.func_135052_a("options.title");

      for(GameSettings$Options var5 : field_146440_f) {
         if (var5.func_74380_a()) {
            this.field_146292_n
               .add(
                  new GuiOptionSlider(
                     var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 - 12 + 24 * (var1 >> 1), var5
                  )
               );
         } else {
            GuiOptionButton var6 = new GuiOptionButton(
               var5.func_74381_c(),
               this.field_146294_l / 2 - 155 + var1 % 2 * 160,
               this.field_146295_m / 6 - 12 + 24 * (var1 >> 1),
               var5,
               this.field_146443_h.func_74297_c(var5)
            );
            if (var5 == GameSettings$Options.DIFFICULTY
               && this.field_146297_k.field_71441_e != null
               && this.field_146297_k.field_71441_e.func_72912_H().func_76093_s()) {
               var6.field_146124_l = false;
               var6.field_146126_j = I18n.func_135052_a("options.difficulty") + ": " + I18n.func_135052_a("options.difficulty.hardcore");
            }

            this.field_146292_n.add(var6);
         }

         ++var1;
      }

      this.field_146292_n
         .add(new GuiOptions$1(this, 8675309, this.field_146294_l / 2 + 5, this.field_146295_m / 6 + 48 - 6, 150, 20, "Super Secret Settings..."));
      this.field_146292_n
         .add(new GuiButton(106, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 72 - 6, 150, 20, I18n.func_135052_a("options.sounds")));
      this.field_146292_n
         .add(new GuiButton(107, this.field_146294_l / 2 + 5, this.field_146295_m / 6 + 72 - 6, 150, 20, I18n.func_135052_a("options.stream")));
      this.field_146292_n
         .add(new GuiButton(101, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 96 - 6, 150, 20, I18n.func_135052_a("options.video")));
      this.field_146292_n
         .add(new GuiButton(100, this.field_146294_l / 2 + 5, this.field_146295_m / 6 + 96 - 6, 150, 20, I18n.func_135052_a("options.controls")));
      this.field_146292_n
         .add(new GuiButton(102, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 120 - 6, 150, 20, I18n.func_135052_a("options.language")));
      this.field_146292_n
         .add(new GuiButton(103, this.field_146294_l / 2 + 5, this.field_146295_m / 6 + 120 - 6, 150, 20, I18n.func_135052_a("options.multiplayer.title")));
      this.field_146292_n
         .add(new GuiButton(105, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 144 - 6, 150, 20, I18n.func_135052_a("options.resourcepack")));
      this.field_146292_n
         .add(new GuiButton(104, this.field_146294_l / 2 + 5, this.field_146295_m / 6 + 144 - 6, 150, 20, I18n.func_135052_a("options.snooper.view")));
      this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 168, I18n.func_135052_a("gui.done")));
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k < 100 && var1 instanceof GuiOptionButton) {
            this.field_146443_h.func_74306_a(((GuiOptionButton)var1).func_146136_c(), 1);
            var1.field_146126_j = this.field_146443_h.func_74297_c(GameSettings$Options.func_74379_a(var1.field_146127_k));
         }

         if (var1.field_146127_k == 8675309) {
            this.field_146297_k.field_71460_t.func_147705_c();
         }

         if (var1.field_146127_k == 101) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(new GuiVideoSettings(this, this.field_146443_h));
         }

         if (var1.field_146127_k == 100) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(new GuiControls(this, this.field_146443_h));
         }

         if (var1.field_146127_k == 102) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(new GuiLanguage(this, this.field_146443_h, this.field_146297_k.func_135016_M()));
         }

         if (var1.field_146127_k == 103) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(new ScreenChatOptions(this, this.field_146443_h));
         }

         if (var1.field_146127_k == 104) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(new GuiSnooper(this, this.field_146443_h));
         }

         if (var1.field_146127_k == 200) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(this.field_146441_g);
         }

         if (var1.field_146127_k == 105) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(new GuiScreenResourcePacks(this));
         }

         if (var1.field_146127_k == 106) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(new GuiScreenOptionsSounds(this, this.field_146443_h));
         }

         if (var1.field_146127_k == 107) {
            this.field_146297_k.field_71474_y.func_74303_b();
            IStream var2 = this.field_146297_k.func_152346_Z();
            if (var2.func_152936_l() && var2.func_152928_D()) {
               this.field_146297_k.func_147108_a(new GuiStreamOptions(this, this.field_146443_h));
            } else {
               GuiStreamUnavailable.func_152321_a(this);
            }
         }
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_146442_a, this.field_146294_l / 2, 15, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
