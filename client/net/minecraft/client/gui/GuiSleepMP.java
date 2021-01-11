package net.minecraft.client.gui;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class GuiSleepMP extends GuiChat {
   public GuiSleepMP() {
      super();
   }

   public void func_73866_w_() {
      super.func_73866_w_();
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m - 40, I18n.func_135052_a("multiplayer.stopSleeping")));
   }

   protected void func_73869_a(char var1, int var2) {
      if (var2 == 1) {
         this.func_146418_g();
      } else if (var2 != 28 && var2 != 156) {
         super.func_73869_a(var1, var2);
      } else {
         String var3 = this.field_146415_a.func_146179_b().trim();
         if (!var3.isEmpty()) {
            this.field_146297_k.field_71439_g.func_71165_d(var3);
         }

         this.field_146415_a.func_146180_a("");
         this.field_146297_k.field_71456_v.func_146158_b().func_146240_d();
      }

   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == 1) {
         this.func_146418_g();
      } else {
         super.func_146284_a(var1);
      }

   }

   private void func_146418_g() {
      NetHandlerPlayClient var1 = this.field_146297_k.field_71439_g.field_71174_a;
      var1.func_147297_a(new C0BPacketEntityAction(this.field_146297_k.field_71439_g, C0BPacketEntityAction.Action.STOP_SLEEPING));
   }
}
