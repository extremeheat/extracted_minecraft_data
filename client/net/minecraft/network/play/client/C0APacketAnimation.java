package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0APacketAnimation implements Packet<INetHandlerPlayServer> {
   public C0APacketAnimation() {
      super();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_175087_a(this);
   }
}
