package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustParticleOptions extends DustParticleOptionsBase {
   public static final Vector3f REDSTONE_PARTICLE_COLOR = Vec3.fromRGB24(16711680).toVector3f();
   public static final DustParticleOptions REDSTONE = new DustParticleOptions(REDSTONE_PARTICLE_COLOR, 1.0F);
   public static final Codec<DustParticleOptions> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(var0x -> var0x.color), Codec.FLOAT.fieldOf("scale").forGetter(var0x -> var0x.scale))
            .apply(var0, DustParticleOptions::new)
   );
   public static final ParticleOptions.Deserializer<DustParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustParticleOptions>() {
      public DustParticleOptions fromCommand(ParticleType<DustParticleOptions> var1, StringReader var2) throws CommandSyntaxException {
         Vector3f var3 = DustParticleOptionsBase.readVector3f(var2);
         var2.expect(' ');
         float var4 = var2.readFloat();
         return new DustParticleOptions(var3, var4);
      }

      public DustParticleOptions fromNetwork(ParticleType<DustParticleOptions> var1, FriendlyByteBuf var2) {
         return new DustParticleOptions(DustParticleOptionsBase.readVector3f(var2), var2.readFloat());
      }
   };

   public DustParticleOptions(Vector3f var1, float var2) {
      super(var1, var2);
   }

   @Override
   public ParticleType<DustParticleOptions> getType() {
      return ParticleTypes.DUST;
   }
}
