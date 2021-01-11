package net.minecraft.client.gui;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;

public class GuiDownloadTerrain extends GuiScreen {
   private NetHandlerPlayClient field_146594_a;
   private int field_146593_f;

   public GuiDownloadTerrain(NetHandlerPlayClient var1) {
      super();
      this.field_146594_a = var1;
   }

   protected void func_73869_a(char var1, int var2) {
   }

   public void func_73866_w_() {
      this.field_146292_n.clear();
   }

   public void func_73876_c() {
      ++this.field_146593_f;
      if (this.field_146593_f % 20 == 0) {
         this.field_146594_a.func_147297_a(new C00PacketKeepAlive());
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146278_c(0);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("multiplayer.downloadingTerrain"), this.field_146294_l / 2, this.field_146295_m / 2 - 50, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   public boolean func_73868_f() {
      return false;
   }
}
