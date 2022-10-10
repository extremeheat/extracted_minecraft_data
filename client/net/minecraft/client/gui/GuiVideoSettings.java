package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;

public class GuiVideoSettings extends GuiScreen {
   private final GuiScreen field_146498_f;
   protected String field_146500_a = "Video Settings";
   private final GameSettings field_146499_g;
   private GuiOptionsRowList field_146501_h;
   private static final GameSettings.Options[] field_146502_i;

   public GuiVideoSettings(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146498_f = var1;
      this.field_146499_g = var2;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.field_146501_h;
   }

   protected void func_73866_w_() {
      this.field_146500_a = I18n.func_135052_a("options.videoTitle");
      this.func_189646_b(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m - 27, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiVideoSettings.this.field_146297_k.field_71474_y.func_74303_b();
            GuiVideoSettings.this.field_146297_k.field_195558_d.func_198097_f();
            GuiVideoSettings.this.field_146297_k.func_147108_a(GuiVideoSettings.this.field_146498_f);
         }
      });
      if (OpenGlHelper.field_176083_O) {
         this.field_146501_h = new GuiOptionsRowList(this.field_146297_k, this.field_146294_l, this.field_146295_m, 32, this.field_146295_m - 32, 25, field_146502_i);
      } else {
         GameSettings.Options[] var1 = new GameSettings.Options[field_146502_i.length - 1];
         int var2 = 0;
         GameSettings.Options[] var3 = field_146502_i;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            GameSettings.Options var6 = var3[var5];
            if (var6 == GameSettings.Options.USE_VBO) {
               break;
            }

            var1[var2] = var6;
            ++var2;
         }

         this.field_146501_h = new GuiOptionsRowList(this.field_146297_k, this.field_146294_l, this.field_146295_m, 32, this.field_146295_m - 32, 25, var1);
      }

      this.field_195124_j.add(this.field_146501_h);
   }

   public void func_195122_V_() {
      this.field_146297_k.field_71474_y.func_74303_b();
      super.func_195122_V_();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = this.field_146499_g.field_74335_Z;
      if (super.mouseClicked(var1, var3, var5)) {
         if (this.field_146499_g.field_74335_Z != var6) {
            this.field_146297_k.field_195558_d.func_198098_h();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      int var6 = this.field_146499_g.field_74335_Z;
      if (super.mouseReleased(var1, var3, var5)) {
         return true;
      } else if (this.field_146501_h.mouseReleased(var1, var3, var5)) {
         if (this.field_146499_g.field_74335_Z != var6) {
            this.field_146297_k.field_195558_d.func_198098_h();
         }

         return true;
      } else {
         return false;
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146501_h.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146500_a, this.field_146294_l / 2, 5, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   static {
      field_146502_i = new GameSettings.Options[]{GameSettings.Options.GRAPHICS, GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION, GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.ENABLE_VSYNC, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE, GameSettings.Options.ATTACK_INDICATOR, GameSettings.Options.GAMMA, GameSettings.Options.RENDER_CLOUDS, GameSettings.Options.USE_FULLSCREEN, GameSettings.Options.PARTICLES, GameSettings.Options.MIPMAP_LEVELS, GameSettings.Options.USE_VBO, GameSettings.Options.ENTITY_SHADOWS, GameSettings.Options.BIOME_BLEND_RADIUS};
   }
}
