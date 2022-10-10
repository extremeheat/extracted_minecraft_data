package net.minecraft.network.status.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class SPacketPong implements Packet<INetHandlerStatusClient> {
   private long field_149293_a;

   public SPacketPong() {
      super();
   }

   public SPacketPong(long var1) {
      super();
      this.field_149293_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149293_a = var1.readLong();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeLong(this.field_149293_a);
   }

   public void func_148833_a(INetHandlerStatusClient var1) {
      var1.func_147398_a(this);
   }
}
