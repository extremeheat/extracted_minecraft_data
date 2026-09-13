package net.minecraft.client.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings$Options;

public class GuiVideoSettings extends GuiScreen {
   private GuiScreen field_146498_f;
   protected String field_146500_a = "Video Settings";
   private GameSettings field_146499_g;
   private GuiListExtended field_146501_h;
   private static final GameSettings$Options[] field_146502_i = new GameSettings$Options[]{
      GameSettings$Options.GRAPHICS,
      GameSettings$Options.RENDER_DISTANCE,
      GameSettings$Options.AMBIENT_OCCLUSION,
      GameSettings$Options.FRAMERATE_LIMIT,
      GameSettings$Options.ANAGLYPH,
      GameSettings$Options.VIEW_BOBBING,
      GameSettings$Options.GUI_SCALE,
      GameSettings$Options.ADVANCED_OPENGL,
      GameSettings$Options.GAMMA,
      GameSettings$Options.RENDER_CLOUDS,
      GameSettings$Options.PARTICLES,
      GameSettings$Options.USE_FULLSCREEN,
      GameSettings$Options.ENABLE_VSYNC,
      GameSettings$Options.MIPMAP_LEVELS,
      GameSettings$Options.ANISOTROPIC_FILTERING
   };

   public GuiVideoSettings(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146498_f = var1;
      this.field_146499_g = var2;
   }

   @Override
   public void func_73866_w_() {
      this.field_146500_a = I18n.func_135052_a("options.videoTitle");
      this.field_146292_n.clear();
      this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m - 27, I18n.func_135052_a("gui.done")));
      if (OpenGlHelper.field_153197_d) {
         this.field_146501_h = new GuiOptionsRowList(
            this.field_146297_k, this.field_146294_l, this.field_146295_m, 32, this.field_146295_m - 32, 25, field_146502_i
         );
      } else {
         GameSettings$Options[] var1 = new GameSettings$Options[field_146502_i.length - 1];
         int var2 = 0;

         for(GameSettings$Options var6 : field_146502_i) {
            if (var6 != GameSettings$Options.ADVANCED_OPENGL) {
               var1[var2] = var6;
               ++var2;
            }
         }

         this.field_146501_h = new GuiOptionsRowList(this.field_146297_k, this.field_146294_l, this.field_146295_m, 32, this.field_146295_m - 32, 25, var1);
      }
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 200) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(this.field_146498_f);
         }
      }
   }

   @Override
   protected void func_73864_a(int var1, int var2, int var3) {
      int var4 = this.field_146499_g.field_74335_Z;
      super.func_73864_a(var1, var2, var3);
      this.field_146501_h.func_148179_a(var1, var2, var3);
      if (this.field_146499_g.field_74335_Z != var4) {
         ScaledResolution var5 = new ScaledResolution(this.field_146297_k, this.field_146297_k.field_71443_c, this.field_146297_k.field_71440_d);
         int var6 = var5.func_78326_a();
         int var7 = var5.func_78328_b();
         this.func_146280_a(this.field_146297_k, var6, var7);
      }
   }

   @Override
   protected void func_146286_b(int var1, int var2, int var3) {
      int var4 = this.field_146499_g.field_74335_Z;
      super.func_146286_b(var1, var2, var3);
      this.field_146501_h.func_148181_b(var1, var2, var3);
      if (this.field_146499_g.field_74335_Z != var4) {
         ScaledResolution var5 = new ScaledResolution(this.field_146297_k, this.field_146297_k.field_71443_c, this.field_146297_k.field_71440_d);
         int var6 = var5.func_78326_a();
         int var7 = var5.func_78328_b();
         this.func_146280_a(this.field_146297_k, var6, var7);
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146501_h.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146500_a, this.field_146294_l / 2, 5, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
