package net.minecraft.client.network;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.UUID;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginClient implements INetHandlerLoginClient {
   private static final Logger field_147396_a = LogManager.getLogger();
   private final Minecraft field_147394_b;
   private final GuiScreen field_147395_c;
   private final NetworkManager field_147393_d;

   public NetHandlerLoginClient(NetworkManager var1, Minecraft var2, GuiScreen var3) {
      super();
      this.field_147393_d = var1;
      this.field_147394_b = var2;
      this.field_147395_c = var3;
   }

   @Override
   public void func_147389_a(S01PacketEncryptionRequest var1) {
      SecretKey var2 = CryptManager.func_75890_a();
      String var3 = var1.func_149609_c();
      PublicKey var4 = var1.func_149608_d();
      String var5 = new BigInteger(CryptManager.func_75895_a(var3, var4, var2)).toString(16);
      boolean var6 = this.field_147394_b.func_147104_D() == null || !this.field_147394_b.func_147104_D().func_152585_d();

      try {
         this.func_147391_c().joinServer(this.field_147394_b.func_110432_I().func_148256_e(), this.field_147394_b.func_110432_I().func_148254_d(), var5);
      } catch (AuthenticationUnavailableException var8) {
         if (var6) {
            this.field_147393_d
               .func_150718_a(
                  new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.serversUnavailable"))
               );
            return;
         }
      } catch (InvalidCredentialsException var9) {
         if (var6) {
            this.field_147393_d
               .func_150718_a(
                  new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.invalidSession"))
               );
            return;
         }
      } catch (AuthenticationException var10) {
         if (var6) {
            this.field_147393_d.func_150718_a(new ChatComponentTranslation("disconnect.loginFailedInfo", var10.getMessage()));
            return;
         }
      }

      this.field_147393_d.func_150725_a(new C01PacketEncryptionResponse(var2, var4, var1.func_149607_e()), new NetHandlerLoginClient$1(this, var2));
   }

   private MinecraftSessionService func_147391_c() {
      return new YggdrasilAuthenticationService(this.field_147394_b.func_110437_J(), UUID.randomUUID().toString()).createMinecraftSessionService();
   }

   @Override
   public void func_147390_a(S02PacketLoginSuccess var1) {
      this.field_147393_d.func_150723_a(EnumConnectionState.PLAY);
   }

   @Override
   public void func_147231_a(IChatComponent var1) {
      this.field_147394_b.func_147108_a(new GuiDisconnected(this.field_147395_c, "connect.failed", var1));
   }

   @Override
   public void func_147232_a(EnumConnectionState var1, EnumConnectionState var2) {
      field_147396_a.debug("Switching protocol from " + var1 + " to " + var2);
      if (var2 == EnumConnectionState.PLAY) {
         this.field_147393_d.func_150719_a(new NetHandlerPlayClient(this.field_147394_b, this.field_147395_c, this.field_147393_d));
      }
   }

   @Override
   public void func_147233_a() {
   }

   @Override
   public void func_147388_a(S00PacketDisconnect var1) {
      this.field_147393_d.func_150718_a(var1.func_149603_c());
   }
}
