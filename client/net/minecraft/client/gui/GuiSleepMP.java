package net.minecraft.client.gui;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketEntityAction;

public class GuiSleepMP extends GuiChat {
   public GuiSleepMP() {
      super();
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m - 40, I18n.func_135052_a("multiplayer.stopSleeping")) {
         public void func_194829_a(double var1, double var3) {
            GuiSleepMP.this.func_146418_g();
         }
      });
   }

   public void func_195122_V_() {
      this.func_146418_g();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.func_146418_g();
      } else if (var1 == 257 || var1 == 335) {
         String var4 = this.field_146415_a.func_146179_b().trim();
         if (!var4.isEmpty()) {
            this.field_146297_k.field_71439_g.func_71165_d(var4);
         }

         this.field_146415_a.func_146180_a("");
         this.field_146297_k.field_71456_v.func_146158_b().func_146240_d();
         return true;
      }

      return super.keyPressed(var1, var2, var3);
   }

   private void func_146418_g() {
      NetHandlerPlayClient var1 = this.field_146297_k.field_71439_g.field_71174_a;
      var1.func_147297_a(new CPacketEntityAction(this.field_146297_k.field_71439_g, CPacketEntityAction.Action.STOP_SLEEPING));
   }
}
