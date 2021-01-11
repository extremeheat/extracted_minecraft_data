package net.minecraft.client.gui;

import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;

public class GuiIngameMenu extends GuiScreen {
   private int field_146445_a;
   private int field_146444_f;

   public GuiIngameMenu() {
      super();
   }

   public void func_73866_w_() {
      this.field_146445_a = 0;
      this.field_146292_n.clear();
      byte var1 = -16;
      boolean var2 = true;
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + var1, I18n.func_135052_a("menu.returnToMenu")));
      if (!this.field_146297_k.func_71387_A()) {
         ((GuiButton)this.field_146292_n.get(0)).field_146126_j = I18n.func_135052_a("menu.disconnect");
      }

      this.field_146292_n.add(new GuiButton(4, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 24 + var1, I18n.func_135052_a("menu.returnToGame")));
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + var1, 98, 20, I18n.func_135052_a("menu.options")));
      GuiButton var3;
      this.field_146292_n.add(var3 = new GuiButton(7, this.field_146294_l / 2 + 2, this.field_146295_m / 4 + 96 + var1, 98, 20, I18n.func_135052_a("menu.shareToLan")));
      this.field_146292_n.add(new GuiButton(5, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 48 + var1, 98, 20, I18n.func_135052_a("gui.achievements")));
      this.field_146292_n.add(new GuiButton(6, this.field_146294_l / 2 + 2, this.field_146295_m / 4 + 48 + var1, 98, 20, I18n.func_135052_a("gui.stats")));
      var3.field_146124_l = this.field_146297_k.func_71356_B() && !this.field_146297_k.func_71401_C().func_71344_c();
   }

   protected void func_146284_a(GuiButton var1) {
      switch(var1.field_146127_k) {
      case 0:
         this.field_146297_k.func_147108_a(new GuiOptions(this, this.field_146297_k.field_71474_y));
         break;
      case 1:
         boolean var2 = this.field_146297_k.func_71387_A();
         boolean var3 = this.field_146297_k.func_181540_al();
         var1.field_146124_l = false;
         this.field_146297_k.field_71441_e.func_72882_A();
         this.field_146297_k.func_71403_a((WorldClient)null);
         if (var2) {
            this.field_146297_k.func_147108_a(new GuiMainMenu());
         } else if (var3) {
            RealmsBridge var4 = new RealmsBridge();
            var4.switchToRealms(new GuiMainMenu());
         } else {
            this.field_146297_k.func_147108_a(new GuiMultiplayer(new GuiMainMenu()));
         }
      case 2:
      case 3:
      default:
         break;
      case 4:
         this.field_146297_k.func_147108_a((GuiScreen)null);
         this.field_146297_k.func_71381_h();
         break;
      case 5:
         this.field_146297_k.func_147108_a(new GuiAchievements(this, this.field_146297_k.field_71439_g.func_146107_m()));
         break;
      case 6:
         this.field_146297_k.func_147108_a(new GuiStats(this, this.field_146297_k.field_71439_g.func_146107_m()));
         break;
      case 7:
         this.field_146297_k.func_147108_a(new GuiShareToLan(this));
      }

   }

   public void func_73876_c() {
      super.func_73876_c();
      ++this.field_146444_f;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("menu.game"), this.field_146294_l / 2, 40, 16777215);
      super.func_73863_a(var1, var2, var3);
   }
}
