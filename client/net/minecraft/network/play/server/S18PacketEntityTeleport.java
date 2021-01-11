package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S18PacketEntityTeleport implements Packet<INetHandlerPlayClient> {
   private int field_149458_a;
   private int field_149456_b;
   private int field_149457_c;
   private int field_149454_d;
   private byte field_149455_e;
   private byte field_149453_f;
   private boolean field_179698_g;

   public S18PacketEntityTeleport() {
      super();
   }

   public S18PacketEntityTeleport(Entity var1) {
      super();
      this.field_149458_a = var1.func_145782_y();
      this.field_149456_b = MathHelper.func_76128_c(var1.field_70165_t * 32.0D);
      this.field_149457_c = MathHelper.func_76128_c(var1.field_70163_u * 32.0D);
      this.field_149454_d = MathHelper.func_76128_c(var1.field_70161_v * 32.0D);
      this.field_149455_e = (byte)((int)(var1.field_70177_z * 256.0F / 360.0F));
      this.field_149453_f = (byte)((int)(var1.field_70125_A * 256.0F / 360.0F));
      this.field_179698_g = var1.field_70122_E;
   }

   public S18PacketEntityTeleport(int var1, int var2, int var3, int var4, byte var5, byte var6, boolean var7) {
      super();
      this.field_149458_a = var1;
      this.field_149456_b = var2;
      this.field_149457_c = var3;
      this.field_149454_d = var4;
      this.field_149455_e = var5;
      this.field_149453_f = var6;
      this.field_179698_g = var7;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149458_a = var1.func_150792_a();
      this.field_149456_b = var1.readInt();
      this.field_149457_c = var1.readInt();
      this.field_149454_d = var1.readInt();
      this.field_149455_e = var1.readByte();
      this.field_149453_f = var1.readByte();
      this.field_179698_g = var1.readBoolean();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149458_a);
      var1.writeInt(this.field_149456_b);
      var1.writeInt(this.field_149457_c);
      var1.writeInt(this.field_149454_d);
      var1.writeByte(this.field_149455_e);
      var1.writeByte(this.field_149453_f);
      var1.writeBoolean(this.field_179698_g);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147275_a(this);
   }

   public int func_149451_c() {
      return this.field_149458_a;
   }

   public int func_149449_d() {
      return this.field_149456_b;
   }

   public int func_149448_e() {
      return this.field_149457_c;
   }

   public int func_149446_f() {
      return this.field_149454_d;
   }

   public byte func_149450_g() {
      return this.field_149455_e;
   }

   public byte func_149447_h() {
      return this.field_149453_f;
   }

   public boolean func_179697_g() {
      return this.field_179698_g;
   }
}
