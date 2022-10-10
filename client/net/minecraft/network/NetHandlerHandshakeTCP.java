package net.minecraft.network;

import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class NetHandlerHandshakeTCP implements INetHandlerHandshakeServer {
   private final MinecraftServer field_147387_a;
   private final NetworkManager field_147386_b;

   public NetHandlerHandshakeTCP(MinecraftServer var1, NetworkManager var2) {
      super();
      this.field_147387_a = var1;
      this.field_147386_b = var2;
   }

   public void func_147383_a(CPacketHandshake var1) {
      switch(var1.func_149594_c()) {
      case LOGIN:
         this.field_147386_b.func_150723_a(EnumConnectionState.LOGIN);
         TextComponentTranslation var2;
         if (var1.func_149595_d() > 404) {
            var2 = new TextComponentTranslation("multiplayer.disconnect.outdated_server", new Object[]{"1.13.2"});
            this.field_147386_b.func_179290_a(new SPacketDisconnectLogin(var2));
            this.field_147386_b.func_150718_a(var2);
         } else if (var1.func_149595_d() < 404) {
            var2 = new TextComponentTranslation("multiplayer.disconnect.outdated_client", new Object[]{"1.13.2"});
            this.field_147386_b.func_179290_a(new SPacketDisconnectLogin(var2));
            this.field_147386_b.func_150718_a(var2);
         } else {
            this.field_147386_b.func_150719_a(new NetHandlerLoginServer(this.field_147387_a, this.field_147386_b));
         }
         break;
      case STATUS:
         this.field_147386_b.func_150723_a(EnumConnectionState.STATUS);
         this.field_147386_b.func_150719_a(new NetHandlerStatusServer(this.field_147387_a, this.field_147386_b));
         break;
      default:
         throw new UnsupportedOperationException("Invalid intention " + var1.func_149594_c());
      }

   }

   public void func_147231_a(ITextComponent var1) {
   }
}
