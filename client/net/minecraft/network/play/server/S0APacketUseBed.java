package net.minecraft.network.play.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S0APacketUseBed extends Packet {
   private int field_149097_a;
   private int field_149095_b;
   private int field_149096_c;
   private int field_149094_d;

   public S0APacketUseBed() {
      super();
   }

   public S0APacketUseBed(EntityPlayer var1, int var2, int var3, int var4) {
      super();
      this.field_149095_b = var2;
      this.field_149096_c = var3;
      this.field_149094_d = var4;
      this.field_149097_a = var1.func_145782_y();
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149097_a = var1.readInt();
      this.field_149095_b = var1.readInt();
      this.field_149096_c = var1.readByte();
      this.field_149094_d = var1.readInt();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149097_a);
      var1.writeInt(this.field_149095_b);
      var1.writeByte(this.field_149096_c);
      var1.writeInt(this.field_149094_d);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147278_a(this);
   }

   public EntityPlayer func_149091_a(World var1) {
      return (EntityPlayer)var1.func_73045_a(this.field_149097_a);
   }

   public int func_149092_c() {
      return this.field_149095_b;
   }

   public int func_149090_d() {
      return this.field_149096_c;
   }

   public int func_149089_e() {
      return this.field_149094_d;
   }
}
