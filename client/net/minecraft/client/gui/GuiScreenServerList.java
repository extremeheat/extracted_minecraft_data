package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

public class GuiScreenServerList extends GuiScreen {
   private GuiButton field_195170_a;
   private final GuiScreen field_146303_a;
   private final ServerData field_146301_f;
   private GuiTextField field_146302_g;

   public GuiScreenServerList(GuiScreen var1, ServerData var2) {
      super();
      this.field_146303_a = var1;
      this.field_146301_f = var2;
   }

   public void func_73876_c() {
      this.field_146302_g.func_146178_a();
   }

   protected void func_73866_w_() {
      this.field_146297_k.field_195559_v.func_197967_a(true);
      this.field_195170_a = this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 12, I18n.func_135052_a("selectServer.select")) {
         public void func_194829_a(double var1, double var3) {
            GuiScreenServerList.this.func_195167_h();
         }
      });
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiScreenServerList.this.field_146303_a.confirmResult(false, 0);
         }
      });
      this.field_146302_g = new GuiTextField(2, this.field_146289_q, this.field_146294_l / 2 - 100, 116, 200, 20);
      this.field_146302_g.func_146203_f(128);
      this.field_146302_g.func_146195_b(true);
      this.field_146302_g.func_146180_a(this.field_146297_k.field_71474_y.field_74332_R);
      this.field_195124_j.add(this.field_146302_g);
      this.func_195073_a(this.field_146302_g);
      this.func_195168_i();
   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      String var4 = this.field_146302_g.func_146179_b();
      this.func_146280_a(var1, var2, var3);
      this.field_146302_g.func_146180_a(var4);
   }

   private void func_195167_h() {
      this.field_146301_f.field_78845_b = this.field_146302_g.func_146179_b();
      this.field_146303_a.confirmResult(true, 0);
   }

   public void func_146281_b() {
      this.field_146297_k.field_195559_v.func_197967_a(false);
      this.field_146297_k.field_71474_y.field_74332_R = this.field_146302_g.func_146179_b();
      this.field_146297_k.field_71474_y.func_74303_b();
   }

   public boolean charTyped(char var1, int var2) {
      if (this.field_146302_g.charTyped(var1, var2)) {
         this.func_195168_i();
         return true;
      } else {
         return false;
      }
   }

   private void func_195168_i() {
      this.field_195170_a.field_146124_l = !this.field_146302_g.func_146179_b().isEmpty() && this.field_146302_g.func_146179_b().split(":").length > 0;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 != 257 && var1 != 335) {
         if (super.keyPressed(var1, var2, var3)) {
            this.func_195168_i();
            return true;
         } else {
            return false;
         }
      } else {
         this.func_195167_h();
         return true;
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("selectServer.direct"), this.field_146294_l / 2, 20, 16777215);
      this.func_73731_b(this.field_146289_q, I18n.func_135052_a("addServer.enterIp"), this.field_146294_l / 2 - 100, 100, 10526880);
      this.field_146302_g.func_195608_a(var1, var2, var3);
      super.func_73863_a(var1, var2, var3);
   }
}
