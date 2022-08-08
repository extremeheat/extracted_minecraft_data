package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class DustParticleOptions extends DustParticleOptionsBase {
   public static final Vector3f REDSTONE_PARTICLE_COLOR = new Vector3f(Vec3.fromRGB24(16711680));
   public static final DustParticleOptions REDSTONE;
   public static final Codec<DustParticleOptions> CODEC;
   public static final ParticleOptions.Deserializer<DustParticleOptions> DESERIALIZER;

   public DustParticleOptions(Vector3f var1, float var2) {
      super(var1, var2);
   }

   public ParticleType<DustParticleOptions> getType() {
      return ParticleTypes.DUST;
   }

   static {
      REDSTONE = new DustParticleOptions(REDSTONE_PARTICLE_COLOR, 1.0F);
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Vector3f.CODEC.fieldOf("color").forGetter((var0x) -> {
            return var0x.color;
         }), Codec.FLOAT.fieldOf("scale").forGetter((var0x) -> {
            return var0x.scale;
         })).apply(var0, DustParticleOptions::new);
      });
      DESERIALIZER = new ParticleOptions.Deserializer<DustParticleOptions>() {
         public DustParticleOptions fromCommand(ParticleType<DustParticleOptions> var1, StringReader var2) throws CommandSyntaxException {
            Vector3f var3 = DustParticleOptionsBase.readVector3f(var2);
            var2.expect(' ');
            float var4 = var2.readFloat();
            return new DustParticleOptions(var3, var4);
         }

         public DustParticleOptions fromNetwork(ParticleType<DustParticleOptions> var1, FriendlyByteBuf var2) {
            return new DustParticleOptions(DustParticleOptionsBase.readVector3f(var2), var2.readFloat());
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
   }
}
