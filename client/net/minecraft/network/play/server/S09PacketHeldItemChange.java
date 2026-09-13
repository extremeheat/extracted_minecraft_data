package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S09PacketHeldItemChange extends Packet {
   private int field_149387_a;

   public S09PacketHeldItemChange() {
      super();
   }

   public S09PacketHeldItemChange(int var1) {
      super();
      this.field_149387_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149387_a = var1.readByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeByte(this.field_149387_a);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147257_a(this);
   }

   public int func_149385_c() {
      return this.field_149387_a;
   }
}
