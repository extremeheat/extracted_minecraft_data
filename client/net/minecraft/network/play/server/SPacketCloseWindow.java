package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketCloseWindow implements Packet<INetHandlerPlayClient> {
   private int field_148896_a;

   public SPacketCloseWindow() {
      super();
   }

   public SPacketCloseWindow(int var1) {
      super();
      this.field_148896_a = var1;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147276_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148896_a = var1.readUnsignedByte();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_148896_a);
   }
}
