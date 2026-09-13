package net.minecraft.network.play.server;

import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S23PacketBlockChange extends Packet {
   private int field_148887_a;
   private int field_148885_b;
   private int field_148886_c;
   private Block field_148883_d;
   private int field_148884_e;

   public S23PacketBlockChange() {
      super();
   }

   public S23PacketBlockChange(int var1, int var2, int var3, World var4) {
      super();
      this.field_148887_a = var1;
      this.field_148885_b = var2;
      this.field_148886_c = var3;
      this.field_148883_d = var4.func_147439_a(var1, var2, var3);
      this.field_148884_e = var4.func_72805_g(var1, var2, var3);
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_148887_a = var1.readInt();
      this.field_148885_b = var1.readUnsignedByte();
      this.field_148886_c = var1.readInt();
      this.field_148883_d = Block.func_149729_e(var1.func_150792_a());
      this.field_148884_e = var1.readUnsignedByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_148887_a);
      var1.writeByte(this.field_148885_b);
      var1.writeInt(this.field_148886_c);
      var1.func_150787_b(Block.func_149682_b(this.field_148883_d));
      var1.writeByte(this.field_148884_e);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147234_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format(
         "type=%d, data=%d, x=%d, y=%d, z=%d",
         Block.func_149682_b(this.field_148883_d),
         this.field_148884_e,
         this.field_148887_a,
         this.field_148885_b,
         this.field_148886_c
      );
   }

   public Block func_148880_c() {
      return this.field_148883_d;
   }

   public int func_148879_d() {
      return this.field_148887_a;
   }

   public int func_148878_e() {
      return this.field_148885_b;
   }

   public int func_148877_f() {
      return this.field_148886_c;
   }

   public int func_148881_g() {
      return this.field_148884_e;
   }
}
