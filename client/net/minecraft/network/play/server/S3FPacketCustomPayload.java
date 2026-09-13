package net.minecraft.network.play.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload extends Packet {
   private String field_149172_a;
   private byte[] field_149171_b;

   public S3FPacketCustomPayload() {
      super();
   }

   public S3FPacketCustomPayload(String var1, ByteBuf var2) {
      this(var1, var2.array());
   }

   public S3FPacketCustomPayload(String var1, byte[] var2) {
      super();
      this.field_149172_a = var1;
      this.field_149171_b = var2;
      if (var2.length >= 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149172_a = var1.func_150789_c(20);
      this.field_149171_b = new byte[var1.readUnsignedShort()];
      var1.readBytes(this.field_149171_b);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(this.field_149172_a);
      var1.writeShort(this.field_149171_b.length);
      var1.writeBytes(this.field_149171_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147240_a(this);
   }

   public String func_149169_c() {
      return this.field_149172_a;
   }

   public byte[] func_149168_d() {
      return this.field_149171_b;
   }
}
