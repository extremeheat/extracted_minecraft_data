package net.minecraft.client.gui.stream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;

public class GuiStreamOptions extends GuiScreen {
   private static final GameSettings.Options[] field_152312_a;
   private static final GameSettings.Options[] field_152316_f;
   private final GuiScreen field_152317_g;
   private final GameSettings field_152318_h;
   private String field_152319_i;
   private String field_152313_r;
   private int field_152314_s;
   private boolean field_152315_t = false;

   public GuiStreamOptions(GuiScreen var1, GameSettings var2) {
      super();
      this.field_152317_g = var1;
      this.field_152318_h = var2;
   }

   public void func_73866_w_() {
      int var1 = 0;
      this.field_152319_i = I18n.func_135052_a("options.stream.title");
      this.field_152313_r = I18n.func_135052_a("options.stream.chat.title");
      GameSettings.Options[] var2 = field_152312_a;
      int var3 = var2.length;

      int var4;
      GameSettings.Options var5;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5.func_74380_a()) {
            this.field_146292_n.add(new GuiOptionSlider(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 + 24 * (var1 >> 1), var5));
         } else {
            this.field_146292_n.add(new GuiOptionButton(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 + 24 * (var1 >> 1), var5, this.field_152318_h.func_74297_c(var5)));
         }

         ++var1;
      }

      if (var1 % 2 == 1) {
         ++var1;
      }

      this.field_152314_s = this.field_146295_m / 6 + 24 * (var1 >> 1) + 6;
      var1 += 2;
      var2 = field_152316_f;
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5.func_74380_a()) {
            this.field_146292_n.add(new GuiOptionSlider(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 + 24 * (var1 >> 1), var5));
         } else {
            this.field_146292_n.add(new GuiOptionButton(var5.func_74381_c(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 + 24 * (var1 >> 1), var5, this.field_152318_h.func_74297_c(var5)));
         }

         ++var1;
      }

      this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 168, 150, 20, I18n.func_135052_a("gui.done")));
      GuiButton var6 = new GuiButton(201, this.field_146294_l / 2 + 5, this.field_146295_m / 6 + 168, 150, 20, I18n.func_135052_a("options.stream.ingestSelection"));
      var6.field_146124_l = this.field_146297_k.func_152346_Z().func_152924_m() && this.field_146297_k.func_152346_Z().func_152925_v().length > 0 || this.field_146297_k.func_152346_Z().func_152908_z();
      this.field_146292_n.add(var6);
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k < 100 && var1 instanceof GuiOptionButton) {
            GameSettings.Options var2 = ((GuiOptionButton)var1).func_146136_c();
            this.field_152318_h.func_74306_a(var2, 1);
            var1.field_146126_j = this.field_152318_h.func_74297_c(GameSettings.Options.func_74379_a(var1.field_146127_k));
            if (this.field_146297_k.func_152346_Z().func_152934_n() && var2 != GameSettings.Options.STREAM_CHAT_ENABLED && var2 != GameSettings.Options.STREAM_CHAT_USER_FILTER) {
               this.field_152315_t = true;
            }
         } else if (var1 instanceof GuiOptionSlider) {
            if (var1.field_146127_k == GameSettings.Options.STREAM_VOLUME_MIC.func_74381_c()) {
               this.field_146297_k.func_152346_Z().func_152915_s();
            } else if (var1.field_146127_k == GameSettings.Options.STREAM_VOLUME_SYSTEM.func_74381_c()) {
               this.field_146297_k.func_152346_Z().func_152915_s();
            } else if (this.field_146297_k.func_152346_Z().func_152934_n()) {
               this.field_152315_t = true;
            }
         }

         if (var1.field_146127_k == 200) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(this.field_152317_g);
         } else if (var1.field_146127_k == 201) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(new GuiIngestServers(this));
         }

      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_152319_i, this.field_146294_l / 2, 20, 16777215);
      this.func_73732_a(this.field_146289_q, this.field_152313_r, this.field_146294_l / 2, this.field_152314_s, 16777215);
      if (this.field_152315_t) {
         this.func_73732_a(this.field_146289_q, EnumChatFormatting.RED + I18n.func_135052_a("options.stream.changes"), this.field_146294_l / 2, 20 + this.field_146289_q.field_78288_b, 16777215);
      }

      super.func_73863_a(var1, var2, var3);
   }

   static {
      field_152312_a = new GameSettings.Options[]{GameSettings.Options.STREAM_BYTES_PER_PIXEL, GameSettings.Options.STREAM_FPS, GameSettings.Options.STREAM_KBPS, GameSettings.Options.STREAM_SEND_METADATA, GameSettings.Options.STREAM_VOLUME_MIC, GameSettings.Options.STREAM_VOLUME_SYSTEM, GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR, GameSettings.Options.STREAM_COMPRESSION};
      field_152316_f = new GameSettings.Options[]{GameSettings.Options.STREAM_CHAT_ENABLED, GameSettings.Options.STREAM_CHAT_USER_FILTER};
   }
}
