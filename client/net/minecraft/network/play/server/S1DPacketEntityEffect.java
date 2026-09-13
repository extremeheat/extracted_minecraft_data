package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1DPacketEntityEffect extends Packet {
   private int field_149434_a;
   private byte field_149432_b;
   private byte field_149433_c;
   private short field_149431_d;

   public S1DPacketEntityEffect() {
      super();
   }

   public S1DPacketEntityEffect(int var1, PotionEffect var2) {
      super();
      this.field_149434_a = var1;
      this.field_149432_b = (byte)(var2.func_76456_a() & 0xFF);
      this.field_149433_c = (byte)(var2.func_76458_c() & 0xFF);
      if (var2.func_76459_b() > 32767) {
         this.field_149431_d = 32767;
      } else {
         this.field_149431_d = (short)var2.func_76459_b();
      }
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149434_a = var1.readInt();
      this.field_149432_b = var1.readByte();
      this.field_149433_c = var1.readByte();
      this.field_149431_d = var1.readShort();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149434_a);
      var1.writeByte(this.field_149432_b);
      var1.writeByte(this.field_149433_c);
      var1.writeShort(this.field_149431_d);
   }

   public boolean func_149429_c() {
      return this.field_149431_d == 32767;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147260_a(this);
   }

   public int func_149426_d() {
      return this.field_149434_a;
   }

   public byte func_149427_e() {
      return this.field_149432_b;
   }

   public byte func_149428_f() {
      return this.field_149433_c;
   }

   public short func_149425_g() {
      return this.field_149431_d;
   }
}
