package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class GuiScreenOptionsSounds extends GuiScreen {
   private final GuiScreen field_146505_f;
   private final GameSettings field_146506_g;
   protected String field_146507_a = "Options";
   private String field_146508_h;

   public GuiScreenOptionsSounds(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146505_f = var1;
      this.field_146506_g = var2;
   }

   protected void func_73866_w_() {
      this.field_146507_a = I18n.func_135052_a("options.sounds.title");
      this.field_146508_h = I18n.func_135052_a("options.off");
      byte var1 = 0;
      this.func_189646_b(new GuiScreenOptionsSounds.Button(SoundCategory.MASTER.ordinal(), this.field_146294_l / 2 - 155 + var1 % 2 * 160, this.field_146295_m / 6 - 12 + 24 * (var1 >> 1), SoundCategory.MASTER, true));
      int var6 = var1 + 2;
      SoundCategory[] var2 = SoundCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundCategory var5 = var2[var4];
         if (var5 != SoundCategory.MASTER) {
            this.func_189646_b(new GuiScreenOptionsSounds.Button(var5.ordinal(), this.field_146294_l / 2 - 155 + var6 % 2 * 160, this.field_146295_m / 6 - 12 + 24 * (var6 >> 1), var5, false));
            ++var6;
         }
      }

      int var10005 = this.field_146294_l / 2 - 75;
      int var10006 = this.field_146295_m / 6 - 12;
      ++var6;
      this.func_189646_b(new GuiOptionButton(201, var10005, var10006 + 24 * (var6 >> 1), GameSettings.Options.SHOW_SUBTITLES, this.field_146506_g.func_74297_c(GameSettings.Options.SHOW_SUBTITLES)) {
         public void func_194829_a(double var1, double var3) {
            GuiScreenOptionsSounds.this.field_146297_k.field_71474_y.func_74306_a(GameSettings.Options.SHOW_SUBTITLES, 1);
            this.field_146126_j = GuiScreenOptionsSounds.this.field_146297_k.field_71474_y.func_74297_c(GameSettings.Options.SHOW_SUBTITLES);
            GuiScreenOptionsSounds.this.field_146297_k.field_71474_y.func_74303_b();
         }
      });
      this.func_189646_b(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 168, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiScreenOptionsSounds.this.field_146297_k.field_71474_y.func_74303_b();
            GuiScreenOptionsSounds.this.field_146297_k.func_147108_a(GuiScreenOptionsSounds.this.field_146505_f);
         }
      });
   }

   public void func_195122_V_() {
      this.field_146297_k.field_71474_y.func_74303_b();
      super.func_195122_V_();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_146507_a, this.field_146294_l / 2, 15, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   protected String func_184097_a(SoundCategory var1) {
      float var2 = this.field_146506_g.func_186711_a(var1);
      return var2 == 0.0F ? this.field_146508_h : (int)(var2 * 100.0F) + "%";
   }

   class Button extends GuiButton {
      private final SoundCategory field_184063_r;
      private final String field_146152_s;
      public double field_146156_o;
      public boolean field_146155_p;

      public Button(int var2, int var3, int var4, SoundCategory var5, boolean var6) {
         super(var2, var3, var4, var6 ? 310 : 150, 20, "");
         this.field_184063_r = var5;
         this.field_146152_s = I18n.func_135052_a("soundCategory." + var5.func_187948_a());
         this.field_146126_j = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.func_184097_a(var5);
         this.field_146156_o = (double)GuiScreenOptionsSounds.this.field_146506_g.func_186711_a(var5);
      }

      protected int func_146114_a(boolean var1) {
         return 0;
      }

      protected void func_146119_b(Minecraft var1, int var2, int var3) {
         if (this.field_146125_m) {
            if (this.field_146155_p) {
               this.field_146156_o = (double)((float)(var2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8));
               this.field_146156_o = MathHelper.func_151237_a(this.field_146156_o, 0.0D, 1.0D);
               var1.field_71474_y.func_186712_a(this.field_184063_r, (float)this.field_146156_o);
               var1.field_71474_y.func_74303_b();
               this.field_146126_j = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.func_184097_a(this.field_184063_r);
            }

            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            this.func_73729_b(this.field_146128_h + (int)(this.field_146156_o * (double)(this.field_146120_f - 8)), this.field_146129_i, 0, 66, 4, 20);
            this.func_73729_b(this.field_146128_h + (int)(this.field_146156_o * (double)(this.field_146120_f - 8)) + 4, this.field_146129_i, 196, 66, 4, 20);
         }
      }

      public void func_194829_a(double var1, double var3) {
         this.field_146156_o = (var1 - (double)(this.field_146128_h + 4)) / (double)(this.field_146120_f - 8);
         this.field_146156_o = MathHelper.func_151237_a(this.field_146156_o, 0.0D, 1.0D);
         GuiScreenOptionsSounds.this.field_146297_k.field_71474_y.func_186712_a(this.field_184063_r, (float)this.field_146156_o);
         GuiScreenOptionsSounds.this.field_146297_k.field_71474_y.func_74303_b();
         this.field_146126_j = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.func_184097_a(this.field_184063_r);
         this.field_146155_p = true;
      }

      public void func_146113_a(SoundHandler var1) {
      }

      public void func_194831_b(double var1, double var3) {
         if (this.field_146155_p) {
            GuiScreenOptionsSounds.this.field_146297_k.func_147118_V().func_147682_a(SimpleSound.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
         }

         this.field_146155_p = false;
      }
   }
}
