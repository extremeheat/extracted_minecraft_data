package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.init.Particles;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;

public class RedstoneParticleData implements IParticleData {
   public static final RedstoneParticleData field_197564_a = new RedstoneParticleData(1.0F, 0.0F, 0.0F, 1.0F);
   public static final IParticleData.IDeserializer<RedstoneParticleData> field_197565_b = new IParticleData.IDeserializer<RedstoneParticleData>() {
      public RedstoneParticleData func_197544_b(ParticleType<RedstoneParticleData> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         float var3 = (float)var2.readDouble();
         var2.expect(' ');
         float var4 = (float)var2.readDouble();
         var2.expect(' ');
         float var5 = (float)var2.readDouble();
         var2.expect(' ');
         float var6 = (float)var2.readDouble();
         return new RedstoneParticleData(var3, var4, var5, var6);
      }

      public RedstoneParticleData func_197543_b(ParticleType<RedstoneParticleData> var1, PacketBuffer var2) {
         return new RedstoneParticleData(var2.readFloat(), var2.readFloat(), var2.readFloat(), var2.readFloat());
      }

      // $FF: synthetic method
      public IParticleData func_197543_b(ParticleType var1, PacketBuffer var2) {
         return this.func_197543_b(var1, var2);
      }

      // $FF: synthetic method
      public IParticleData func_197544_b(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.func_197544_b(var1, var2);
      }
   };
   private final float field_197566_c;
   private final float field_197567_d;
   private final float field_197568_e;
   private final float field_197569_f;

   public RedstoneParticleData(float var1, float var2, float var3, float var4) {
      super();
      this.field_197566_c = var1;
      this.field_197567_d = var2;
      this.field_197568_e = var3;
      this.field_197569_f = MathHelper.func_76131_a(var4, 0.01F, 4.0F);
   }

   public void func_197553_a(PacketBuffer var1) {
      var1.writeFloat(this.field_197566_c);
      var1.writeFloat(this.field_197567_d);
      var1.writeFloat(this.field_197568_e);
      var1.writeFloat(this.field_197569_f);
   }

   public String func_197555_a() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", this.func_197554_b().func_197570_d(), this.field_197566_c, this.field_197567_d, this.field_197568_e, this.field_197569_f);
   }

   public ParticleType<RedstoneParticleData> func_197554_b() {
      return Particles.field_197619_l;
   }

   public float func_197562_c() {
      return this.field_197566_c;
   }

   public float func_197563_d() {
      return this.field_197567_d;
   }

   public float func_197561_e() {
      return this.field_197568_e;
   }

   public float func_197560_f() {
      return this.field_197569_f;
   }
}
