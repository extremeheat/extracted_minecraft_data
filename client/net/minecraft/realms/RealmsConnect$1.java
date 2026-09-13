package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentTranslation;

public class RealmsConnect$1 extends Thread {
   public RealmsConnect$1(RealmsConnect var1, String var2, String var3, int var4) {
      super(var2);
      this.field_154357_c = var1;
      this.field_154355_a = var3;
      this.field_154356_b = var4;
   }

   @Override
   public void run() {
      InetAddress var1 = null;

      try {
         var1 = InetAddress.getByName(this.field_154355_a);
         if (RealmsConnect.access$000(this.field_154357_c)) {
            return;
         }

         RealmsConnect.access$102(this.field_154357_c, NetworkManager.func_150726_a(var1, this.field_154356_b));
         if (RealmsConnect.access$000(this.field_154357_c)) {
            return;
         }

         RealmsConnect.access$100(this.field_154357_c)
            .func_150719_a(
               new NetHandlerLoginClient(
                  RealmsConnect.access$100(this.field_154357_c), Minecraft.func_71410_x(), RealmsConnect.access$200(this.field_154357_c).getProxy()
               )
            );
         if (RealmsConnect.access$000(this.field_154357_c)) {
            return;
         }

         RealmsConnect.access$100(this.field_154357_c).func_150725_a(new C00Handshake(5, this.field_154355_a, this.field_154356_b, EnumConnectionState.LOGIN));
         if (RealmsConnect.access$000(this.field_154357_c)) {
            return;
         }

         RealmsConnect.access$100(this.field_154357_c).func_150725_a(new C00PacketLoginStart(Minecraft.func_71410_x().func_110432_I().func_148256_e()));
      } catch (UnknownHostException var5) {
         if (RealmsConnect.access$000(this.field_154357_c)) {
            return;
         }

         RealmsConnect.access$300().error("Couldn't connect to world", var5);
         Realms.setScreen(
            new DisconnectedOnlineScreen(
               RealmsConnect.access$200(this.field_154357_c),
               "connect.failed",
               new ChatComponentTranslation("disconnect.genericReason", "Unknown host '" + this.field_154355_a + "'")
            )
         );
      } catch (Exception var6) {
         if (RealmsConnect.access$000(this.field_154357_c)) {
            return;
         }

         RealmsConnect.access$300().error("Couldn't connect to world", var6);
         String var3 = var6.toString();
         if (var1 != null) {
            String var4 = var1.toString() + ":" + this.field_154356_b;
            var3 = var3.replaceAll(var4, "");
         }

         Realms.setScreen(
            new DisconnectedOnlineScreen(
               RealmsConnect.access$200(this.field_154357_c), "connect.failed", new ChatComponentTranslation("disconnect.genericReason", var3)
            )
         );
      }
   }
}
