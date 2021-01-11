package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable {
   private static final AtomicInteger field_147331_b = new AtomicInteger(0);
   private static final Logger field_147332_c = LogManager.getLogger();
   private static final Random field_147329_d = new Random();
   private final byte[] field_147330_e = new byte[4];
   private final MinecraftServer field_147327_f;
   public final NetworkManager field_147333_a;
   private NetHandlerLoginServer.LoginState field_147328_g;
   private int field_147336_h;
   private GameProfile field_147337_i;
   private String field_147334_j;
   private SecretKey field_147335_k;
   private EntityPlayerMP field_181025_l;

   public NetHandlerLoginServer(MinecraftServer var1, NetworkManager var2) {
      super();
      this.field_147328_g = NetHandlerLoginServer.LoginState.HELLO;
      this.field_147334_j = "";
      this.field_147327_f = var1;
      this.field_147333_a = var2;
      field_147329_d.nextBytes(this.field_147330_e);
   }

   public void func_73660_a() {
      if (this.field_147328_g == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT) {
         this.func_147326_c();
      } else if (this.field_147328_g == NetHandlerLoginServer.LoginState.DELAY_ACCEPT) {
         EntityPlayerMP var1 = this.field_147327_f.func_71203_ab().func_177451_a(this.field_147337_i.getId());
         if (var1 == null) {
            this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
            this.field_147327_f.func_71203_ab().func_72355_a(this.field_147333_a, this.field_181025_l);
            this.field_181025_l = null;
         }
      }

      if (this.field_147336_h++ == 600) {
         this.func_147322_a("Took too long to log in");
      }

   }

   public void func_147322_a(String var1) {
      try {
         field_147332_c.info("Disconnecting " + this.func_147317_d() + ": " + var1);
         ChatComponentText var2 = new ChatComponentText(var1);
         this.field_147333_a.func_179290_a(new S00PacketDisconnect(var2));
         this.field_147333_a.func_150718_a(var2);
      } catch (Exception var3) {
         field_147332_c.error("Error whilst disconnecting player", var3);
      }

   }

   public void func_147326_c() {
      if (!this.field_147337_i.isComplete()) {
         this.field_147337_i = this.func_152506_a(this.field_147337_i);
      }

      String var1 = this.field_147327_f.func_71203_ab().func_148542_a(this.field_147333_a.func_74430_c(), this.field_147337_i);
      if (var1 != null) {
         this.func_147322_a(var1);
      } else {
         this.field_147328_g = NetHandlerLoginServer.LoginState.ACCEPTED;
         if (this.field_147327_f.func_175577_aI() >= 0 && !this.field_147333_a.func_150731_c()) {
            this.field_147333_a.func_179288_a(new S03PacketEnableCompression(this.field_147327_f.func_175577_aI()), new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1) throws Exception {
                  NetHandlerLoginServer.this.field_147333_a.func_179289_a(NetHandlerLoginServer.this.field_147327_f.func_175577_aI());
               }

               // $FF: synthetic method
               public void operationComplete(Future var1) throws Exception {
                  this.operationComplete((ChannelFuture)var1);
               }
            });
         }

         this.field_147333_a.func_179290_a(new S02PacketLoginSuccess(this.field_147337_i));
         EntityPlayerMP var2 = this.field_147327_f.func_71203_ab().func_177451_a(this.field_147337_i.getId());
         if (var2 != null) {
            this.field_147328_g = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
            this.field_181025_l = this.field_147327_f.func_71203_ab().func_148545_a(this.field_147337_i);
         } else {
            this.field_147327_f.func_71203_ab().func_72355_a(this.field_147333_a, this.field_147327_f.func_71203_ab().func_148545_a(this.field_147337_i));
         }
      }

   }

   public void func_147231_a(IChatComponent var1) {
      field_147332_c.info(this.func_147317_d() + " lost connection: " + var1.func_150260_c());
   }

   public String func_147317_d() {
      return this.field_147337_i != null ? this.field_147337_i.toString() + " (" + this.field_147333_a.func_74430_c().toString() + ")" : String.valueOf(this.field_147333_a.func_74430_c());
   }

   public void func_147316_a(C00PacketLoginStart var1) {
      Validate.validState(this.field_147328_g == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet", new Object[0]);
      this.field_147337_i = var1.func_149304_c();
      if (this.field_147327_f.func_71266_T() && !this.field_147333_a.func_150731_c()) {
         this.field_147328_g = NetHandlerLoginServer.LoginState.KEY;
         this.field_147333_a.func_179290_a(new S01PacketEncryptionRequest(this.field_147334_j, this.field_147327_f.func_71250_E().getPublic(), this.field_147330_e));
      } else {
         this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
      }

   }

   public void func_147315_a(C01PacketEncryptionResponse var1) {
      Validate.validState(this.field_147328_g == NetHandlerLoginServer.LoginState.KEY, "Unexpected key packet", new Object[0]);
      PrivateKey var2 = this.field_147327_f.func_71250_E().getPrivate();
      if (!Arrays.equals(this.field_147330_e, var1.func_149299_b(var2))) {
         throw new IllegalStateException("Invalid nonce!");
      } else {
         this.field_147335_k = var1.func_149300_a(var2);
         this.field_147328_g = NetHandlerLoginServer.LoginState.AUTHENTICATING;
         this.field_147333_a.func_150727_a(this.field_147335_k);
         (new Thread("User Authenticator #" + field_147331_b.incrementAndGet()) {
            public void run() {
               GameProfile var1 = NetHandlerLoginServer.this.field_147337_i;

               try {
                  String var2 = (new BigInteger(CryptManager.func_75895_a(NetHandlerLoginServer.this.field_147334_j, NetHandlerLoginServer.this.field_147327_f.func_71250_E().getPublic(), NetHandlerLoginServer.this.field_147335_k))).toString(16);
                  NetHandlerLoginServer.this.field_147337_i = NetHandlerLoginServer.this.field_147327_f.func_147130_as().hasJoinedServer(new GameProfile((UUID)null, var1.getName()), var2);
                  if (NetHandlerLoginServer.this.field_147337_i != null) {
                     NetHandlerLoginServer.field_147332_c.info("UUID of player " + NetHandlerLoginServer.this.field_147337_i.getName() + " is " + NetHandlerLoginServer.this.field_147337_i.getId());
                     NetHandlerLoginServer.this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else if (NetHandlerLoginServer.this.field_147327_f.func_71264_H()) {
                     NetHandlerLoginServer.field_147332_c.warn("Failed to verify username but will let them in anyway!");
                     NetHandlerLoginServer.this.field_147337_i = NetHandlerLoginServer.this.func_152506_a(var1);
                     NetHandlerLoginServer.this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else {
                     NetHandlerLoginServer.this.func_147322_a("Failed to verify username!");
                     NetHandlerLoginServer.field_147332_c.error("Username '" + NetHandlerLoginServer.this.field_147337_i.getName() + "' tried to join with an invalid session");
                  }
               } catch (AuthenticationUnavailableException var3) {
                  if (NetHandlerLoginServer.this.field_147327_f.func_71264_H()) {
                     NetHandlerLoginServer.field_147332_c.warn("Authentication servers are down but will let them in anyway!");
                     NetHandlerLoginServer.this.field_147337_i = NetHandlerLoginServer.this.func_152506_a(var1);
                     NetHandlerLoginServer.this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else {
                     NetHandlerLoginServer.this.func_147322_a("Authentication servers are down. Please try again later, sorry!");
                     NetHandlerLoginServer.field_147332_c.error("Couldn't verify username because servers are unavailable");
                  }
               }

            }
         }).start();
      }
   }

   protected GameProfile func_152506_a(GameProfile var1) {
      UUID var2 = UUID.nameUUIDFromBytes(("OfflinePlayer:" + var1.getName()).getBytes(Charsets.UTF_8));
      return new GameProfile(var2, var1.getName());
   }

   static enum LoginState {
      HELLO,
      KEY,
      AUTHENTICATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;

      private LoginState() {
      }
   }
}
