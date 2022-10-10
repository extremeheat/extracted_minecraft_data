package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class CPacketServerQuery implements Packet<INetHandlerStatusServer> {
   public CPacketServerQuery() {
      super();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
   }

   public void func_148833_a(INetHandlerStatusServer var1) {
      var1.func_147312_a(this);
   }
}
