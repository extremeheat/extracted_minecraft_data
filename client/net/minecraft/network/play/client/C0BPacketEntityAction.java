package net.minecraft.network.play.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0BPacketEntityAction extends Packet {
   private int field_149517_a;
   private int field_149515_b;
   private int field_149516_c;

   public C0BPacketEntityAction() {
      super();
   }

   public C0BPacketEntityAction(Entity var1, int var2) {
      this(var1, var2, 0);
   }

   public C0BPacketEntityAction(Entity var1, int var2, int var3) {
      super();
      this.field_149517_a = var1.func_145782_y();
      this.field_149515_b = var2;
      this.field_149516_c = var3;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149517_a = var1.readInt();
      this.field_149515_b = var1.readByte();
      this.field_149516_c = var1.readInt();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149517_a);
      var1.writeByte(this.field_149515_b);
      var1.writeInt(this.field_149516_c);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147357_a(this);
   }

   public int func_149513_d() {
      return this.field_149515_b;
   }

   public int func_149512_e() {
      return this.field_149516_c;
   }
}
