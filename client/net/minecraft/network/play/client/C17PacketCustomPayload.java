package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload extends Packet {
   private String field_149562_a;
   private int field_149560_b;
   private byte[] field_149561_c;

   public C17PacketCustomPayload() {
      super();
   }

   public C17PacketCustomPayload(String var1, ByteBuf var2) {
      this(var1, var2.array());
   }

   public C17PacketCustomPayload(String var1, byte[] var2) {
      super();
      this.field_149562_a = var1;
      this.field_149561_c = var2;
      if (var2 != null) {
         this.field_149560_b = var2.length;
         if (this.field_149560_b >= 32767) {
            throw new IllegalArgumentException("Payload may not be larger than 32k");
         }
      }
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149562_a = var1.func_150789_c(20);
      this.field_149560_b = var1.readShort();
      if (this.field_149560_b > 0 && this.field_149560_b < 32767) {
         this.field_149561_c = new byte[this.field_149560_b];
         var1.readBytes(this.field_149561_c);
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(this.field_149562_a);
      var1.writeShort((short)this.field_149560_b);
      if (this.field_149561_c != null) {
         var1.writeBytes(this.field_149561_c);
      }
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147349_a(this);
   }

   public String func_149559_c() {
      return this.field_149562_a;
   }

   public byte[] func_149558_e() {
      return this.field_149561_c;
   }
}
