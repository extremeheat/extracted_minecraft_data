package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustParticleOptions extends DustParticleOptionsBase {
   public static final Vector3f REDSTONE_PARTICLE_COLOR = Vec3.fromRGB24(16711680).toVector3f();
   public static final DustParticleOptions REDSTONE = new DustParticleOptions(REDSTONE_PARTICLE_COLOR, 1.0F);
   public static final MapCodec<DustParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(var0x -> var0x.color), Codec.FLOAT.fieldOf("scale").forGetter(var0x -> var0x.scale))
            .apply(var0, DustParticleOptions::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, DustParticleOptions> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VECTOR3F, var0 -> var0.color, ByteBufCodecs.FLOAT, var0 -> var0.scale, DustParticleOptions::new
   );
   public static final ParticleOptions.Deserializer<DustParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustParticleOptions>() {
      public DustParticleOptions fromCommand(ParticleType<DustParticleOptions> var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
         Vector3f var4 = DustParticleOptionsBase.readVector3f(var2);
         var2.expect(' ');
         float var5 = var2.readFloat();
         return new DustParticleOptions(var4, var5);
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
