package net.minecraft.server.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.status.INetHandlerStatusServer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class NetHandlerStatusServer implements INetHandlerStatusServer {
   private static final IChatComponent field_183007_a = new ChatComponentText("Status request has been handled.");
   private final MinecraftServer field_147314_a;
   private final NetworkManager field_147313_b;
   private boolean field_183008_d;

   public NetHandlerStatusServer(MinecraftServer var1, NetworkManager var2) {
      super();
      this.field_147314_a = var1;
      this.field_147313_b = var2;
   }

   public void func_147231_a(IChatComponent var1) {
   }

   public void func_147312_a(C00PacketServerQuery var1) {
      if (this.field_183008_d) {
         this.field_147313_b.func_150718_a(field_183007_a);
      } else {
         this.field_183008_d = true;
         this.field_147313_b.func_179290_a(new S00PacketServerInfo(this.field_147314_a.func_147134_at()));
      }
   }

   public void func_147311_a(C01PacketPing var1) {
      this.field_147313_b.func_179290_a(new S01PacketPong(var1.func_149289_c()));
      this.field_147313_b.func_150718_a(field_183007_a);
   }
}
