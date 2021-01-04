package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public class DustParticleOptions implements ParticleOptions {
   public static final DustParticleOptions REDSTONE = new DustParticleOptions(1.0F, 0.0F, 0.0F, 1.0F);
   public static final ParticleOptions.Deserializer<DustParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustParticleOptions>() {
      public DustParticleOptions fromCommand(ParticleType<DustParticleOptions> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         float var3 = (float)var2.readDouble();
         var2.expect(' ');
         float var4 = (float)var2.readDouble();
         var2.expect(' ');
         float var5 = (float)var2.readDouble();
         var2.expect(' ');
         float var6 = (float)var2.readDouble();
         return new DustParticleOptions(var3, var4, var5, var6);
      }

      public DustParticleOptions fromNetwork(ParticleType<DustParticleOptions> var1, FriendlyByteBuf var2) {
         return new DustParticleOptions(var2.readFloat(), var2.readFloat(), var2.readFloat(), var2.readFloat());
      }

      // $FF: synthetic method
      public ParticleOptions fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public ParticleOptions fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.fromCommand(var1, var2);
      }
   };
   private final float r;
   private final float g;
   private final float b;
   private final float scale;

   public DustParticleOptions(float var1, float var2, float var3, float var4) {
      super();
      this.r = var1;
      this.g = var2;
      this.b = var3;
      this.scale = Mth.clamp(var4, 0.01F, 4.0F);
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
      var1.writeFloat(this.r);
      var1.writeFloat(this.g);
      var1.writeFloat(this.b);
      var1.writeFloat(this.scale);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.r, this.g, this.b, this.scale);
   }

   public ParticleType<DustParticleOptions> getType() {
      return ParticleTypes.DUST;
   }

   public float getR() {
      return this.r;
   }

   public float getG() {
      return this.g;
   }

   public float getB() {
      return this.b;
   }

   public float getScale() {
      return this.scale;
   }
}
