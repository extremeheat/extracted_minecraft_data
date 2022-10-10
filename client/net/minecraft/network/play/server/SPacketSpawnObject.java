package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SPacketSpawnObject implements Packet<INetHandlerPlayClient> {
   private int field_149018_a;
   private UUID field_186883_b;
   private double field_149016_b;
   private double field_149017_c;
   private double field_149014_d;
   private int field_149015_e;
   private int field_149012_f;
   private int field_149013_g;
   private int field_149021_h;
   private int field_149022_i;
   private int field_149019_j;
   private int field_149020_k;

   public SPacketSpawnObject() {
      super();
   }

   public SPacketSpawnObject(Entity var1, int var2) {
      this(var1, var2, 0);
   }

   public SPacketSpawnObject(Entity var1, int var2, int var3) {
      super();
      this.field_149018_a = var1.func_145782_y();
      this.field_186883_b = var1.func_110124_au();
      this.field_149016_b = var1.field_70165_t;
      this.field_149017_c = var1.field_70163_u;
      this.field_149014_d = var1.field_70161_v;
      this.field_149021_h = MathHelper.func_76141_d(var1.field_70125_A * 256.0F / 360.0F);
      this.field_149022_i = MathHelper.func_76141_d(var1.field_70177_z * 256.0F / 360.0F);
      this.field_149019_j = var2;
      this.field_149020_k = var3;
      double var4 = 3.9D;
      this.field_149015_e = (int)(MathHelper.func_151237_a(var1.field_70159_w, -3.9D, 3.9D) * 8000.0D);
      this.field_149012_f = (int)(MathHelper.func_151237_a(var1.field_70181_x, -3.9D, 3.9D) * 8000.0D);
      this.field_149013_g = (int)(MathHelper.func_151237_a(var1.field_70179_y, -3.9D, 3.9D) * 8000.0D);
   }

   public SPacketSpawnObject(Entity var1, int var2, int var3, BlockPos var4) {
      this(var1, var2, var3);
      this.field_149016_b = (double)var4.func_177958_n();
      this.field_149017_c = (double)var4.func_177956_o();
      this.field_149014_d = (double)var4.func_177952_p();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149018_a = var1.func_150792_a();
      this.field_186883_b = var1.func_179253_g();
      this.field_149019_j = var1.readByte();
      this.field_149016_b = var1.readDouble();
      this.field_149017_c = var1.readDouble();
      this.field_149014_d = var1.readDouble();
      this.field_149021_h = var1.readByte();
      this.field_149022_i = var1.readByte();
      this.field_149020_k = var1.readInt();
      this.field_149015_e = var1.readShort();
      this.field_149012_f = var1.readShort();
      this.field_149013_g = var1.readShort();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149018_a);
      var1.func_179252_a(this.field_186883_b);
      var1.writeByte(this.field_149019_j);
      var1.writeDouble(this.field_149016_b);
      var1.writeDouble(this.field_149017_c);
      var1.writeDouble(this.field_149014_d);
      var1.writeByte(this.field_149021_h);
      var1.writeByte(this.field_149022_i);
      var1.writeInt(this.field_149020_k);
      var1.writeShort(this.field_149015_e);
      var1.writeShort(this.field_149012_f);
      var1.writeShort(this.field_149013_g);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147235_a(this);
   }

   public int func_149001_c() {
      return this.field_149018_a;
   }

   public UUID func_186879_b() {
      return this.field_186883_b;
   }

   public double func_186880_c() {
      return this.field_149016_b;
   }

   public double func_186882_d() {
      return this.field_149017_c;
   }

   public double func_186881_e() {
      return this.field_149014_d;
   }

   public int func_149010_g() {
      return this.field_149015_e;
   }

   public int func_149004_h() {
      return this.field_149012_f;
   }

   public int func_148999_i() {
      return this.field_149013_g;
   }

   public int func_149008_j() {
      return this.field_149021_h;
   }

   public int func_149006_k() {
      return this.field_149022_i;
   }

   public int func_148993_l() {
      return this.field_149019_j;
   }

   public int func_149009_m() {
      return this.field_149020_k;
   }

   public void func_149003_d(int var1) {
      this.field_149015_e = var1;
   }

   public void func_149000_e(int var1) {
      this.field_149012_f = var1;
   }

   public void func_149007_f(int var1) {
      this.field_149013_g = var1;
   }

   public void func_149002_g(int var1) {
      this.field_149020_k = var1;
   }
}
