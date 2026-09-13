package net.minecraft.realms;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerStatusPinger {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List connections = Collections.synchronizedList(new ArrayList());

   public RealmsServerStatusPinger() {
      super();
   }

   public void pingServer(String var1, ServerPing var2) {
      if (var1 != null && !var1.startsWith("0.0.0.0") && !var1.isEmpty()) {
         RealmsServerAddress var3 = RealmsServerAddress.parseString(var1);
         NetworkManager var4 = NetworkManager.func_150726_a(InetAddress.getByName(var3.getHost()), var3.getPort());
         this.connections.add(var4);
         var4.func_150719_a(new RealmsServerStatusPinger$1(this, var2, var4, var1));

         try {
            var4.func_150725_a(new C00Handshake(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION, var3.getHost(), var3.getPort(), EnumConnectionState.STATUS));
            var4.func_150725_a(new C00PacketServerQuery());
         } catch (Throwable var6) {
            LOGGER.error(var6);
         }
      }
   }

   public void tick() {
      synchronized(this.connections) {
         Iterator var2 = this.connections.iterator();

         while(var2.hasNext()) {
            NetworkManager var3 = (NetworkManager)var2.next();
            if (var3.func_150724_d()) {
               var3.func_74428_b();
            } else {
               var2.remove();
               if (var3.func_150730_f() != null) {
                  var3.func_150729_e().func_147231_a(var3.func_150730_f());
               }
            }
         }
      }
   }

   public void removeAll() {
      synchronized(this.connections) {
         Iterator var2 = this.connections.iterator();

         while(var2.hasNext()) {
            NetworkManager var3 = (NetworkManager)var2.next();
            if (var3.func_150724_d()) {
               var2.remove();
               var3.func_150718_a(new ChatComponentText("Cancelled"));
            }
         }
      }
   }
}
