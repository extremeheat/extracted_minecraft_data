package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S34PacketMaps extends Packet {
   private int field_149191_a;
   private byte[] field_149190_b;

   public S34PacketMaps() {
      super();
   }

   public S34PacketMaps(int var1, byte[] var2) {
      super();
      this.field_149191_a = var1;
      this.field_149190_b = var2;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149191_a = var1.func_150792_a();
      this.field_149190_b = new byte[var1.readUnsignedShort()];
      var1.readBytes(this.field_149190_b);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150787_b(this.field_149191_a);
      var1.writeShort(this.field_149190_b.length);
      var1.writeBytes(this.field_149190_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147264_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format("id=%d, length=%d", this.field_149191_a, this.field_149190_b.length);
   }

   public int func_149188_c() {
      return this.field_149191_a;
   }

   public byte[] func_149187_d() {
      return this.field_149190_b;
   }
}
