package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C16PacketClientStatus extends Packet {
   private C16PacketClientStatus$EnumState field_149437_a;

   public C16PacketClientStatus() {
      super();
   }

   public C16PacketClientStatus(C16PacketClientStatus$EnumState var1) {
      super();
      this.field_149437_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149437_a = C16PacketClientStatus$EnumState.access$000()[var1.readByte() % C16PacketClientStatus$EnumState.access$000().length];
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeByte(C16PacketClientStatus$EnumState.access$100(this.field_149437_a));
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147342_a(this);
   }

   public C16PacketClientStatus$EnumState func_149435_c() {
      return this.field_149437_a;
   }
}
