package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer {
   private static final AtomicInteger field_147331_b = new AtomicInteger(0);
   private static final Logger field_147332_c = LogManager.getLogger();
   private static final Random field_147329_d = new Random();
   private final byte[] field_147330_e = new byte[4];
   private final MinecraftServer field_147327_f;
   public final NetworkManager field_147333_a;
   private NetHandlerLoginServer$LoginState field_147328_g = NetHandlerLoginServer$LoginState.HELLO;
   private int field_147336_h;
   private GameProfile field_147337_i;
   private String field_147334_j = "";
   private SecretKey field_147335_k;

   public NetHandlerLoginServer(MinecraftServer var1, NetworkManager var2) {
      super();
      this.field_147327_f = var1;
      this.field_147333_a = var2;
      field_147329_d.nextBytes(this.field_147330_e);
   }

   @Override
   public void func_147233_a() {
      if (this.field_147328_g == NetHandlerLoginServer$LoginState.READY_TO_ACCEPT) {
         this.func_147326_c();
      }

      if (this.field_147336_h++ == 600) {
         this.func_147322_a("Took too long to log in");
      }
   }

   public void func_147322_a(String var1) {
      try {
         field_147332_c.info("Disconnecting " + this.func_147317_d() + ": " + var1);
         ChatComponentText var2 = new ChatComponentText(var1);
         this.field_147333_a.func_150725_a(new S00PacketDisconnect(var2));
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
         this.field_147328_g = NetHandlerLoginServer$LoginState.ACCEPTED;
         this.field_147333_a.func_150725_a(new S02PacketLoginSuccess(this.field_147337_i));
         this.field_147327_f.func_71203_ab().func_72355_a(this.field_147333_a, this.field_147327_f.func_71203_ab().func_148545_a(this.field_147337_i));
      }
   }

   @Override
   public void func_147231_a(IChatComponent var1) {
      field_147332_c.info(this.func_147317_d() + " lost connection: " + var1.func_150260_c());
   }

   public String func_147317_d() {
      return this.field_147337_i != null
         ? this.field_147337_i.toString() + " (" + this.field_147333_a.func_74430_c().toString() + ")"
         : String.valueOf(this.field_147333_a.func_74430_c());
   }

   @Override
   public void func_147232_a(EnumConnectionState var1, EnumConnectionState var2) {
      Validate.validState(
         this.field_147328_g == NetHandlerLoginServer$LoginState.ACCEPTED || this.field_147328_g == NetHandlerLoginServer$LoginState.HELLO,
         "Unexpected change in protocol",
         new Object[0]
      );
      Validate.validState(var2 == EnumConnectionState.PLAY || var2 == EnumConnectionState.LOGIN, "Unexpected protocol " + var2, new Object[0]);
   }

   @Override
   public void func_147316_a(C00PacketLoginStart var1) {
      Validate.validState(this.field_147328_g == NetHandlerLoginServer$LoginState.HELLO, "Unexpected hello packet", new Object[0]);
      this.field_147337_i = var1.func_149304_c();
      if (this.field_147327_f.func_71266_T() && !this.field_147333_a.func_150731_c()) {
         this.field_147328_g = NetHandlerLoginServer$LoginState.KEY;
         this.field_147333_a
            .func_150725_a(new S01PacketEncryptionRequest(this.field_147334_j, this.field_147327_f.func_71250_E().getPublic(), this.field_147330_e));
      } else {
         this.field_147328_g = NetHandlerLoginServer$LoginState.READY_TO_ACCEPT;
      }
   }

   @Override
   public void func_147315_a(C01PacketEncryptionResponse var1) {
      Validate.validState(this.field_147328_g == NetHandlerLoginServer$LoginState.KEY, "Unexpected key packet", new Object[0]);
      PrivateKey var2 = this.field_147327_f.func_71250_E().getPrivate();
      if (!Arrays.equals(this.field_147330_e, var1.func_149299_b(var2))) {
         throw new IllegalStateException("Invalid nonce!");
      } else {
         this.field_147335_k = var1.func_149300_a(var2);
         this.field_147328_g = NetHandlerLoginServer$LoginState.AUTHENTICATING;
         this.field_147333_a.func_150727_a(this.field_147335_k);
         new NetHandlerLoginServer$1(this, "User Authenticator #" + field_147331_b.incrementAndGet()).start();
      }
   }

   protected GameProfile func_152506_a(GameProfile var1) {
      UUID var2 = UUID.nameUUIDFromBytes(("OfflinePlayer:" + var1.getName()).getBytes(Charsets.UTF_8));
      return new GameProfile(var2, var1.getName());
   }
}
