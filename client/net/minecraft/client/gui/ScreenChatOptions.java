package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings$Options;

public class ScreenChatOptions extends GuiScreen {
   private static final GameSettings$Options[] field_146399_a = new GameSettings$Options[]{
      GameSettings$Options.CHAT_VISIBILITY,
      GameSettings$Options.CHAT_COLOR,
      GameSettings$Options.CHAT_LINKS,
      GameSettings$Options.CHAT_OPACITY,
      GameSettings$Options.CHAT_LINKS_PROMPT,
      GameSettings$Options.CHAT_SCALE,
      GameSettings$Options.CHAT_HEIGHT_FOCUSED,
      GameSettings$Options.CHAT_HEIGHT_UNFOCUSED,
      GameSettings$Options.CHAT_WIDTH
   };
   private static final GameSettings$Options[] field_146395_f = new GameSettings$Options[]{GameSettings$Options.SHOW_CAPE};
   private final GuiScreen field_146396_g;
   private final GameSettings field_146400_h;
   private String field_146401_i;
   private String field_146398_r;
   private int field_146397_s;

   public ScreenChatOptions(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146396_g = var1;
      this.field_146400_h = var2;
   }

   @Override
   public void func_73866_w_() {
      int var1 = 0;
      this.field_146401_i = I18n.func_135052_a("options.chat.title");
      this.field_146398_r = I18n.func_135052_a("options.multiplayer.title");

      for(GameSettings$Options var5 : field_146399_a) {
         if (var5.func_74380_a()) {
            this.field_146292_n
               .add(new GuiOptionSlider(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 + 24 * (var1 >> 1), var5));
         } else {
            this.field_146292_n
               .add(
                  new GuiOptionButton(
                     var5.func_74381_c(),
                     this.field_146294_l / 2 - 155 + var1 % 2 * 160,
                     this.field_146295_m / 6 + 24 * (var1 >> 1),
                     var5,
                     this.field_146400_h.func_74297_c(var5)
                  )
               );
         }

         ++var1;
      }

      if (var1 % 2 == 1) {
         ++var1;
      }

      this.field_146397_s = this.field_146295_m / 6 + 24 * (var1 >> 1);
      var1 += 2;

      for(GameSettings$Options var10 : field_146395_f) {
         if (var10.func_74380_a()) {
            this.field_146292_n
               .add(
                  new GuiOptionSlider(var10.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 + 24 * (var1 >> 1), var10)
               );
         } else {
            this.field_146292_n
               .add(
                  new GuiOptionButton(
                     var10.func_74381_c(),
                     this.field_146294_l / 2 - 155 + var1 % 2 * 160,
                     this.field_146295_m / 6 + 24 * (var1 >> 1),
                     var10,
                     this.field_146400_h.func_74297_c(var10)
                  )
               );
         }

         ++var1;
      }

      this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 168, I18n.func_135052_a("gui.done")));
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k < 100 && var1 instanceof GuiOptionButton) {
            this.field_146400_h.func_74306_a(((GuiOptionButton)var1).func_146136_c(), 1);
            var1.field_146126_j = this.field_146400_h.func_74297_c(GameSettings$Options.func_74379_a(var1.field_146127_k));
         }

         if (var1.field_146127_k == 200) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(this.field_146396_g);
         }
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_146401_i, this.field_146294_l / 2, 20, 16777215);
      this.func_73732_a(this.field_146289_q, this.field_146398_r, this.field_146294_l / 2, this.field_146397_s + 7, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
