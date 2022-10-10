package net.minecraft.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
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
   private final String field_147334_j;
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
         EntityPlayerMP var1 = this.field_147327_f.func_184103_al().func_177451_a(this.field_147337_i.getId());
         if (var1 == null) {
            this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
            this.field_147327_f.func_184103_al().func_72355_a(this.field_147333_a, this.field_181025_l);
            this.field_181025_l = null;
         }
      }

      if (this.field_147336_h++ == 600) {
         this.func_194026_b(new TextComponentTranslation("multiplayer.disconnect.slow_login", new Object[0]));
      }

   }

   public void func_194026_b(ITextComponent var1) {
      try {
         field_147332_c.info("Disconnecting {}: {}", this.func_147317_d(), var1.getString());
         this.field_147333_a.func_179290_a(new SPacketDisconnectLogin(var1));
         this.field_147333_a.func_150718_a(var1);
      } catch (Exception var3) {
         field_147332_c.error("Error whilst disconnecting player", var3);
      }

   }

   public void func_147326_c() {
      if (!this.field_147337_i.isComplete()) {
         this.field_147337_i = this.func_152506_a(this.field_147337_i);
      }

      ITextComponent var1 = this.field_147327_f.func_184103_al().func_206258_a(this.field_147333_a.func_74430_c(), this.field_147337_i);
      if (var1 != null) {
         this.func_194026_b(var1);
      } else {
         this.field_147328_g = NetHandlerLoginServer.LoginState.ACCEPTED;
         if (this.field_147327_f.func_175577_aI() >= 0 && !this.field_147333_a.func_150731_c()) {
            this.field_147333_a.func_201058_a(new SPacketEnableCompression(this.field_147327_f.func_175577_aI()), (var1x) -> {
               this.field_147333_a.func_179289_a(this.field_147327_f.func_175577_aI());
            });
         }

         this.field_147333_a.func_179290_a(new SPacketLoginSuccess(this.field_147337_i));
         EntityPlayerMP var2 = this.field_147327_f.func_184103_al().func_177451_a(this.field_147337_i.getId());
         if (var2 != null) {
            this.field_147328_g = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
            this.field_181025_l = this.field_147327_f.func_184103_al().func_148545_a(this.field_147337_i);
         } else {
            this.field_147327_f.func_184103_al().func_72355_a(this.field_147333_a, this.field_147327_f.func_184103_al().func_148545_a(this.field_147337_i));
         }
      }

   }

   public void func_147231_a(ITextComponent var1) {
      field_147332_c.info("{} lost connection: {}", this.func_147317_d(), var1.getString());
   }

   public String func_147317_d() {
      return this.field_147337_i != null ? this.field_147337_i + " (" + this.field_147333_a.func_74430_c() + ")" : String.valueOf(this.field_147333_a.func_74430_c());
   }

   public void func_147316_a(CPacketLoginStart var1) {
      Validate.validState(this.field_147328_g == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet", new Object[0]);
      this.field_147337_i = var1.func_149304_c();
      if (this.field_147327_f.func_71266_T() && !this.field_147333_a.func_150731_c()) {
         this.field_147328_g = NetHandlerLoginServer.LoginState.KEY;
         this.field_147333_a.func_179290_a(new SPacketEncryptionRequest("", this.field_147327_f.func_71250_E().getPublic(), this.field_147330_e));
      } else {
         this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
      }

   }

   public void func_147315_a(CPacketEncryptionResponse var1) {
      Validate.validState(this.field_147328_g == NetHandlerLoginServer.LoginState.KEY, "Unexpected key packet", new Object[0]);
      PrivateKey var2 = this.field_147327_f.func_71250_E().getPrivate();
      if (!Arrays.equals(this.field_147330_e, var1.func_149299_b(var2))) {
         throw new IllegalStateException("Invalid nonce!");
      } else {
         this.field_147335_k = var1.func_149300_a(var2);
         this.field_147328_g = NetHandlerLoginServer.LoginState.AUTHENTICATING;
         this.field_147333_a.func_150727_a(this.field_147335_k);
         Thread var3 = new Thread("User Authenticator #" + field_147331_b.incrementAndGet()) {
            public void run() {
               GameProfile var1 = NetHandlerLoginServer.this.field_147337_i;

               try {
                  String var2 = (new BigInteger(CryptManager.func_75895_a("", NetHandlerLoginServer.this.field_147327_f.func_71250_E().getPublic(), NetHandlerLoginServer.this.field_147335_k))).toString(16);
                  NetHandlerLoginServer.this.field_147337_i = NetHandlerLoginServer.this.field_147327_f.func_147130_as().hasJoinedServer(new GameProfile((UUID)null, var1.getName()), var2, this.func_191235_a());
                  if (NetHandlerLoginServer.this.field_147337_i != null) {
                     NetHandlerLoginServer.field_147332_c.info("UUID of player {} is {}", NetHandlerLoginServer.this.field_147337_i.getName(), NetHandlerLoginServer.this.field_147337_i.getId());
                     NetHandlerLoginServer.this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else if (NetHandlerLoginServer.this.field_147327_f.func_71264_H()) {
                     NetHandlerLoginServer.field_147332_c.warn("Failed to verify username but will let them in anyway!");
                     NetHandlerLoginServer.this.field_147337_i = NetHandlerLoginServer.this.func_152506_a(var1);
                     NetHandlerLoginServer.this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else {
                     NetHandlerLoginServer.this.func_194026_b(new TextComponentTranslation("multiplayer.disconnect.unverified_username", new Object[0]));
                     NetHandlerLoginServer.field_147332_c.error("Username '{}' tried to join with an invalid session", var1.getName());
                  }
               } catch (AuthenticationUnavailableException var3) {
                  if (NetHandlerLoginServer.this.field_147327_f.func_71264_H()) {
                     NetHandlerLoginServer.field_147332_c.warn("Authentication servers are down but will let them in anyway!");
                     NetHandlerLoginServer.this.field_147337_i = NetHandlerLoginServer.this.func_152506_a(var1);
                     NetHandlerLoginServer.this.field_147328_g = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else {
                     NetHandlerLoginServer.this.func_194026_b(new TextComponentTranslation("multiplayer.disconnect.authservers_down", new Object[0]));
                     NetHandlerLoginServer.field_147332_c.error("Couldn't verify username because servers are unavailable");
                  }
               }

            }

            @Nullable
            private InetAddress func_191235_a() {
               SocketAddress var1 = NetHandlerLoginServer.this.field_147333_a.func_74430_c();
               return NetHandlerLoginServer.this.field_147327_f.func_190518_ac() && var1 instanceof InetSocketAddress ? ((InetSocketAddress)var1).getAddress() : null;
            }
         };
         var3.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(field_147332_c));
         var3.start();
      }
   }

   public void func_209526_a(CPacketCustomPayloadLogin var1) {
      this.func_194026_b(new TextComponentTranslation("multiplayer.disconnect.unexpected_query_response", new Object[0]));
   }

   protected GameProfile func_152506_a(GameProfile var1) {
      UUID var2 = EntityPlayer.func_175147_b(var1.getName());
      return new GameProfile(var2, var1.getName());
   }

   static enum LoginState {
      HELLO,
      KEY,
      AUTHENTICATING,
      NEGOTIATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;

      private LoginState() {
      }
   }
}
