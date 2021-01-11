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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen onlineScreen;
   private volatile boolean aborted = false;
   private NetworkManager connection;

   public RealmsConnect(RealmsScreen var1) {
      super();
      this.onlineScreen = var1;
   }

   public void connect(final String var1, final int var2) {
      Realms.setConnectedToRealms(true);
      (new Thread("Realms-connect-task") {
         public void run() {
            InetAddress var1x = null;

            try {
               var1x = InetAddress.getByName(var1);
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection = NetworkManager.func_181124_a(var1x, var2, Minecraft.func_71410_x().field_71474_y.func_181148_f());
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.func_150719_a(new NetHandlerLoginClient(RealmsConnect.this.connection, Minecraft.func_71410_x(), RealmsConnect.this.onlineScreen.getProxy()));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.func_179290_a(new C00Handshake(47, var1, var2, EnumConnectionState.LOGIN));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.func_179290_a(new C00PacketLoginStart(Minecraft.func_71410_x().func_110432_I().func_148256_e()));
            } catch (UnknownHostException var5) {
               Realms.clearResourcePack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", var5);
               Minecraft.func_71410_x().func_110438_M().func_148529_f();
               Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host '" + var1 + "'"})));
            } catch (Exception var6) {
               Realms.clearResourcePack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", var6);
               String var3 = var6.toString();
               if (var1x != null) {
                  String var4 = var1x.toString() + ":" + var2;
                  var3 = var3.replaceAll(var4, "");
               }

               Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[]{var3})));
            }

         }
      }).start();
   }

   public void abort() {
      this.aborted = true;
   }

   public void tick() {
      if (this.connection != null) {
         if (this.connection.func_150724_d()) {
            this.connection.func_74428_b();
         } else {
            this.connection.func_179293_l();
         }
      }

   }
}
