package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0DPacketCloseWindow extends Packet {
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

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149556_a = var1.readByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeByte(this.field_149556_a);
   }
}
