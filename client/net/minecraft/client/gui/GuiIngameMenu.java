package net.minecraft.client.gui;

import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;

public class GuiIngameMenu extends GuiScreen {
   public GuiIngameMenu() {
      super();
   }

   protected void func_73866_w_() {
      boolean var1 = true;
      boolean var2 = true;
      GuiButton var3 = this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + -16, I18n.func_135052_a("menu.returnToMenu")) {
         public void func_194829_a(double var1, double var3) {
            boolean var5 = GuiIngameMenu.this.field_146297_k.func_71387_A();
            boolean var6 = GuiIngameMenu.this.field_146297_k.func_181540_al();
            this.field_146124_l = false;
            GuiIngameMenu.this.field_146297_k.field_71441_e.func_72882_A();
            if (var5) {
               GuiIngameMenu.this.field_146297_k.func_205055_a((WorldClient)null, new GuiDirtMessageScreen(I18n.func_135052_a("menu.savingLevel")));
            } else {
               GuiIngameMenu.this.field_146297_k.func_71403_a((WorldClient)null);
            }

            if (var5) {
               GuiIngameMenu.this.field_146297_k.func_147108_a(new GuiMainMenu());
            } else if (var6) {
               RealmsBridge var7 = new RealmsBridge();
               var7.switchToRealms(new GuiMainMenu());
            } else {
               GuiIngameMenu.this.field_146297_k.func_147108_a(new GuiMultiplayer(new GuiMainMenu()));
            }

         }
      });
      if (!this.field_146297_k.func_71387_A()) {
         var3.field_146126_j = I18n.func_135052_a("menu.disconnect");
      }

      this.func_189646_b(new GuiButton(4, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 24 + -16, I18n.func_135052_a("menu.returnToGame")) {
         public void func_194829_a(double var1, double var3) {
            GuiIngameMenu.this.field_146297_k.func_147108_a((GuiScreen)null);
            GuiIngameMenu.this.field_146297_k.field_71417_B.func_198034_i();
         }
      });
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + -16, 98, 20, I18n.func_135052_a("menu.options")) {
         public void func_194829_a(double var1, double var3) {
            GuiIngameMenu.this.field_146297_k.func_147108_a(new GuiOptions(GuiIngameMenu.this, GuiIngameMenu.this.field_146297_k.field_71474_y));
         }
      });
      GuiButton var4 = this.func_189646_b(new GuiButton(7, this.field_146294_l / 2 + 2, this.field_146295_m / 4 + 96 + -16, 98, 20, I18n.func_135052_a("menu.shareToLan")) {
         public void func_194829_a(double var1, double var3) {
            GuiIngameMenu.this.field_146297_k.func_147108_a(new GuiShareToLan(GuiIngameMenu.this));
         }
      });
      var4.field_146124_l = this.field_146297_k.func_71356_B() && !this.field_146297_k.func_71401_C().func_71344_c();
      this.func_189646_b(new GuiButton(5, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 48 + -16, 98, 20, I18n.func_135052_a("gui.advancements")) {
         public void func_194829_a(double var1, double var3) {
            GuiIngameMenu.this.field_146297_k.func_147108_a(new GuiScreenAdvancements(GuiIngameMenu.this.field_146297_k.field_71439_g.field_71174_a.func_191982_f()));
         }
      });
      this.func_189646_b(new GuiButton(6, this.field_146294_l / 2 + 2, this.field_146295_m / 4 + 48 + -16, 98, 20, I18n.func_135052_a("gui.stats")) {
         public void func_194829_a(double var1, double var3) {
            GuiIngameMenu.this.field_146297_k.func_147108_a(new GuiStats(GuiIngameMenu.this, GuiIngameMenu.this.field_146297_k.field_71439_g.func_146107_m()));
         }
      });
   }

   public void func_73876_c() {
      super.func_73876_c();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("menu.game"), this.field_146294_l / 2, 40, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
