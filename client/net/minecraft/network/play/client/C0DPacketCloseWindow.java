package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0DPacketCloseWindow implements Packet<INetHandlerPlayServer> {
   private int field_149556_a;

   public C0DPacketCloseWindow() {
      super();
   }

   public C0DPacketCloseWindow(int var1) {
      super();
      this.field_149556_a = var1;
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147356_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149556_a = var1.readByte();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_149556_a);
   }
}
