package net.minecraft.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.multiplayer.ServerData$ServerResourceMode;
import net.minecraft.client.multiplayer.ServerList;

class NetHandlerPlayClient$1 implements GuiYesNoCallback {
   NetHandlerPlayClient$1(NetHandlerPlayClient var1, String var2) {
      super();
      this.field_146299_f = var1;
      this.field_146300_a = var2;
   }

   @Override
   public void func_73878_a(boolean var1, int var2) {
      NetHandlerPlayClient.access$002(this.field_146299_f, Minecraft.func_71410_x());
      if (NetHandlerPlayClient.access$000(this.field_146299_f).func_147104_D() != null) {
         NetHandlerPlayClient.access$000(this.field_146299_f).func_147104_D().func_152584_a(ServerData$ServerResourceMode.ENABLED);
         ServerList.func_147414_b(NetHandlerPlayClient.access$000(this.field_146299_f).func_147104_D());
      }

      if (var1) {
         NetHandlerPlayClient.access$000(this.field_146299_f).func_110438_M().func_148526_a(this.field_146300_a);
      }

      NetHandlerPlayClient.access$000(this.field_146299_f).func_147108_a(null);
   }
}
