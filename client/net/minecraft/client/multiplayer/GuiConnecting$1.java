package net.minecraft.client.multiplayer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentTranslation;

class GuiConnecting$1 extends Thread {
   GuiConnecting$1(GuiConnecting var1, String var2, String var3, int var4) {
      super(var2);
      this.field_148230_c = var1;
      this.field_148231_a = var3;
      this.field_148229_b = var4;
   }

   @Override
   public void run() {
      InetAddress var1 = null;

      try {
         if (GuiConnecting.access$000(this.field_148230_c)) {
            return;
         }

         var1 = InetAddress.getByName(this.field_148231_a);
         GuiConnecting.access$102(this.field_148230_c, NetworkManager.func_150726_a(var1, this.field_148229_b));
         GuiConnecting.access$100(this.field_148230_c)
            .func_150719_a(
               new NetHandlerLoginClient(
                  GuiConnecting.access$100(this.field_148230_c), this.field_148230_c.field_146297_k, GuiConnecting.access$200(this.field_148230_c)
               )
            );
         GuiConnecting.access$100(this.field_148230_c).func_150725_a(new C00Handshake(5, this.field_148231_a, this.field_148229_b, EnumConnectionState.LOGIN));
         GuiConnecting.access$100(this.field_148230_c)
            .func_150725_a(new C00PacketLoginStart(this.field_148230_c.field_146297_k.func_110432_I().func_148256_e()));
      } catch (UnknownHostException var5) {
         if (GuiConnecting.access$000(this.field_148230_c)) {
            return;
         }

         GuiConnecting.access$300().error("Couldn't connect to server", var5);
         this.field_148230_c
            .field_146297_k
            .func_147108_a(
               new GuiDisconnected(
                  GuiConnecting.access$200(this.field_148230_c), "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")
               )
            );
      } catch (Exception var6) {
         if (GuiConnecting.access$000(this.field_148230_c)) {
            return;
         }

         GuiConnecting.access$300().error("Couldn't connect to server", var6);
         String var3 = var6.toString();
         if (var1 != null) {
            String var4 = var1.toString() + ":" + this.field_148229_b;
            var3 = var3.replaceAll(var4, "");
         }

         this.field_148230_c
            .field_146297_k
            .func_147108_a(
               new GuiDisconnected(
                  GuiConnecting.access$200(this.field_148230_c), "connect.failed", new ChatComponentTranslation("disconnect.genericReason", var3)
               )
            );
      }
   }
}
