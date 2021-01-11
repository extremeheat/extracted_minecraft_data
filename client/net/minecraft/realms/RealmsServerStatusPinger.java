package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerStatusPinger {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<NetworkManager> connections = Collections.synchronizedList(Lists.newArrayList());

   public RealmsServerStatusPinger() {
      super();
   }

   public void pingServer(final String var1, final RealmsServerPing var2) throws UnknownHostException {
      if (var1 != null && !var1.startsWith("0.0.0.0") && !var1.isEmpty()) {
         RealmsServerAddress var3 = RealmsServerAddress.parseString(var1);
         final NetworkManager var4 = NetworkManager.func_181124_a(InetAddress.getByName(var3.getHost()), var3.getPort(), false);
         this.connections.add(var4);
         var4.func_150719_a(new INetHandlerStatusClient() {
            private boolean field_154345_e = false;

            public void func_147397_a(S00PacketServerInfo var1x) {
               ServerStatusResponse var2x = var1x.func_149294_c();
               if (var2x.func_151318_b() != null) {
                  var2.nrOfPlayers = String.valueOf(var2x.func_151318_b().func_151333_b());
                  if (ArrayUtils.isNotEmpty(var2x.func_151318_b().func_151331_c())) {
                     StringBuilder var3 = new StringBuilder();
                     GameProfile[] var4x = var2x.func_151318_b().func_151331_c();
                     int var5 = var4x.length;

                     for(int var6 = 0; var6 < var5; ++var6) {
                        GameProfile var7 = var4x[var6];
                        if (var3.length() > 0) {
                           var3.append("\n");
                        }

                        var3.append(var7.getName());
                     }

                     if (var2x.func_151318_b().func_151331_c().length < var2x.func_151318_b().func_151333_b()) {
                        if (var3.length() > 0) {
                           var3.append("\n");
                        }

                        var3.append("... and ").append(var2x.func_151318_b().func_151333_b() - var2x.func_151318_b().func_151331_c().length).append(" more ...");
                     }

                     var2.playerList = var3.toString();
                  }
               } else {
                  var2.playerList = "";
               }

               var4.func_179290_a(new C01PacketPing(Realms.currentTimeMillis()));
               this.field_154345_e = true;
            }

            public void func_147398_a(S01PacketPong var1x) {
               var4.func_150718_a(new ChatComponentText("Finished"));
            }

            public void func_147231_a(IChatComponent var1x) {
               if (!this.field_154345_e) {
                  RealmsServerStatusPinger.LOGGER.error("Can't ping " + var1 + ": " + var1x.func_150260_c());
               }

            }
         });

         try {
            var4.func_179290_a(new C00Handshake(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION, var3.getHost(), var3.getPort(), EnumConnectionState.STATUS));
            var4.func_179290_a(new C00PacketServerQuery());
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
               var3.func_179293_l();
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
