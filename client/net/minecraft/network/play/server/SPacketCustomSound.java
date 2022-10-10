package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

public class SPacketCustomSound implements Packet<INetHandlerPlayClient> {
   private ResourceLocation field_149219_a;
   private SoundCategory field_186933_b;
   private int field_186934_c;
   private int field_186935_d = 2147483647;
   private int field_186936_e;
   private float field_186937_f;
   private float field_186938_g;

   public SPacketCustomSound() {
      super();
   }

   public SPacketCustomSound(ResourceLocation var1, SoundCategory var2, Vec3d var3, float var4, float var5) {
      super();
      this.field_149219_a = var1;
      this.field_186933_b = var2;
      this.field_186934_c = (int)(var3.field_72450_a * 8.0D);
      this.field_186935_d = (int)(var3.field_72448_b * 8.0D);
      this.field_186936_e = (int)(var3.field_72449_c * 8.0D);
      this.field_186937_f = var4;
      this.field_186938_g = var5;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149219_a = var1.func_192575_l();
      this.field_186933_b = (SoundCategory)var1.func_179257_a(SoundCategory.class);
      this.field_186934_c = var1.readInt();
      this.field_186935_d = var1.readInt();
      this.field_186936_e = var1.readInt();
      this.field_186937_f = var1.readFloat();
      this.field_186938_g = var1.readFloat();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_192572_a(this.field_149219_a);
      var1.func_179249_a(this.field_186933_b);
      var1.writeInt(this.field_186934_c);
      var1.writeInt(this.field_186935_d);
      var1.writeInt(this.field_186936_e);
      var1.writeFloat(this.field_186937_f);
      var1.writeFloat(this.field_186938_g);
   }

   public ResourceLocation func_197698_a() {
      return this.field_149219_a;
   }

   public SoundCategory func_186929_b() {
      return this.field_186933_b;
   }

   public double func_186932_c() {
      return (double)((float)this.field_186934_c / 8.0F);
   }

   public double func_186926_d() {
      return (double)((float)this.field_186935_d / 8.0F);
   }

   public double func_186925_e() {
      return (double)((float)this.field_186936_e / 8.0F);
   }

   public float func_186927_f() {
      return this.field_186937_f;
   }

   public float func_186928_g() {
      return this.field_186938_g;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_184329_a(this);
   }
}
