package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.init.Particles;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.IRegistry;

public class SPacketParticles implements Packet<INetHandlerPlayClient> {
   private float field_149234_b;
   private float field_149235_c;
   private float field_149232_d;
   private float field_149233_e;
   private float field_149230_f;
   private float field_149231_g;
   private float field_149237_h;
   private int field_149238_i;
   private boolean field_179752_j;
   private IParticleData field_197700_j;

   public SPacketParticles() {
      super();
   }

   public <T extends IParticleData> SPacketParticles(T var1, boolean var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10) {
      super();
      this.field_197700_j = var1;
      this.field_179752_j = var2;
      this.field_149234_b = var3;
      this.field_149235_c = var4;
      this.field_149232_d = var5;
      this.field_149233_e = var6;
      this.field_149230_f = var7;
      this.field_149231_g = var8;
      this.field_149237_h = var9;
      this.field_149238_i = var10;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      Object var2 = (ParticleType)IRegistry.field_212632_u.func_148754_a(var1.readInt());
      if (var2 == null) {
         var2 = Particles.field_197610_c;
      }

      this.field_179752_j = var1.readBoolean();
      this.field_149234_b = var1.readFloat();
      this.field_149235_c = var1.readFloat();
      this.field_149232_d = var1.readFloat();
      this.field_149233_e = var1.readFloat();
      this.field_149230_f = var1.readFloat();
      this.field_149231_g = var1.readFloat();
      this.field_149237_h = var1.readFloat();
      this.field_149238_i = var1.readInt();
      this.field_197700_j = this.func_199855_a(var1, (ParticleType)var2);
   }

   private <T extends IParticleData> T func_199855_a(PacketBuffer var1, ParticleType<T> var2) {
      return var2.func_197571_g().func_197543_b(var2, var1);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeInt(IRegistry.field_212632_u.func_148757_b(this.field_197700_j.func_197554_b()));
      var1.writeBoolean(this.field_179752_j);
      var1.writeFloat(this.field_149234_b);
      var1.writeFloat(this.field_149235_c);
      var1.writeFloat(this.field_149232_d);
      var1.writeFloat(this.field_149233_e);
      var1.writeFloat(this.field_149230_f);
      var1.writeFloat(this.field_149231_g);
      var1.writeFloat(this.field_149237_h);
      var1.writeInt(this.field_149238_i);
      this.field_197700_j.func_197553_a(var1);
   }

   public boolean func_179750_b() {
      return this.field_179752_j;
   }

   public double func_149220_d() {
      return (double)this.field_149234_b;
   }

   public double func_149226_e() {
      return (double)this.field_149235_c;
   }

   public double func_149225_f() {
      return (double)this.field_149232_d;
   }

   public float func_149221_g() {
      return this.field_149233_e;
   }

   public float func_149224_h() {
      return this.field_149230_f;
   }

   public float func_149223_i() {
      return this.field_149231_g;
   }

   public float func_149227_j() {
      return this.field_149237_h;
   }

   public int func_149222_k() {
      return this.field_149238_i;
   }

   public IParticleData func_197699_j() {
      return this.field_197700_j;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147289_a(this);
   }
}
