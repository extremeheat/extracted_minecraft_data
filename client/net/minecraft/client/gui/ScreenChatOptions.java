package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.I18n;

public class ScreenChatOptions extends GuiScreen {
   private static final GameSettings.Options[] field_146399_a;
   private final GuiScreen field_146396_g;
   private final GameSettings field_146400_h;
   private String field_146401_i;
   private GuiOptionButton field_193025_i;

   public ScreenChatOptions(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146396_g = var1;
      this.field_146400_h = var2;
   }

   protected void func_73866_w_() {
      this.field_146401_i = I18n.func_135052_a("options.chat.title");
      int var1 = 0;
      GameSettings.Options[] var2 = field_146399_a;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameSettings.Options var5 = var2[var4];
         if (var5.func_74380_a()) {
            this.func_189646_b(new GuiOptionSlider(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 + 24 * (var1 >> 1), var5));
         } else {
            GuiOptionButton var6 = new GuiOptionButton(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 + 24 * (var1 >> 1), var5, this.field_146400_h.func_74297_c(var5)) {
               public void func_194829_a(double var1, double var3) {
                  ScreenChatOptions.this.field_146400_h.func_74306_a(this.func_146136_c(), 1);
                  this.field_146126_j = ScreenChatOptions.this.field_146400_h.func_74297_c(GameSettings.Options.func_74379_a(this.field_146127_k));
               }
            };
            this.func_189646_b(var6);
            if (var5 == GameSettings.Options.NARRATOR) {
               this.field_193025_i = var6;
               var6.field_146124_l = NarratorChatListener.field_193643_a.func_193640_a();
            }
         }

         ++var1;
      }

      this.func_189646_b(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 144, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            ScreenChatOptions.this.field_146297_k.field_71474_y.func_74303_b();
            ScreenChatOptions.this.field_146297_k.func_147108_a(ScreenChatOptions.this.field_146396_g);
         }
      });
   }

   public void func_195122_V_() {
      this.field_146297_k.field_71474_y.func_74303_b();
      super.func_195122_V_();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_146401_i, this.field_146294_l / 2, 20, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   public void func_193024_a() {
      this.field_193025_i.field_146126_j = this.field_146400_h.func_74297_c(GameSettings.Options.func_74379_a(this.field_193025_i.field_146127_k));
   }

   static {
      field_146399_a = new GameSettings.Options[]{GameSettings.Options.CHAT_VISIBILITY, GameSettings.Options.CHAT_COLOR, GameSettings.Options.CHAT_LINKS, GameSettings.Options.CHAT_OPACITY, GameSettings.Options.CHAT_LINKS_PROMPT, GameSettings.Options.CHAT_SCALE, GameSettings.Options.CHAT_HEIGHT_FOCUSED, GameSettings.Options.CHAT_HEIGHT_UNFOCUSED, GameSettings.Options.CHAT_WIDTH, GameSettings.Options.REDUCED_DEBUG_INFO, GameSettings.Options.NARRATOR, GameSettings.Options.AUTO_SUGGESTIONS};
   }
}
