package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2EPacketCloseWindow extends Packet {
   private int field_148896_a;

   public S2EPacketCloseWindow() {
      super();
   }

   public S2EPacketCloseWindow(int var1) {
      super();
      this.field_148896_a = var1;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147276_a(this);
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_148896_a = var1.readUnsignedByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeByte(this.field_148896_a);
   }
}
