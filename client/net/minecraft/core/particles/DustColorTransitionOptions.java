package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustColorTransitionOptions extends DustParticleOptionsBase {
   public static final Vector3f SCULK_PARTICLE_COLOR = Vec3.fromRGB24(3790560).toVector3f();
   public static final DustColorTransitionOptions SCULK_TO_REDSTONE;
   public static final MapCodec<DustColorTransitionOptions> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, DustColorTransitionOptions> STREAM_CODEC;
   public static final ParticleOptions.Deserializer<DustColorTransitionOptions> DESERIALIZER;
   private final Vector3f toColor;

   public DustColorTransitionOptions(Vector3f var1, Vector3f var2, float var3) {
      super(var1, var3);
      this.toColor = var2;
   }

   public Vector3f getFromColor() {
      return this.color;
   }

   public Vector3f getToColor() {
      return this.toColor;
   }

   public String writeToString(HolderLookup.Provider var1) {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale, this.toColor.x(), this.toColor.y(), this.toColor.z());
   }

   public ParticleType<DustColorTransitionOptions> getType() {
      return ParticleTypes.DUST_COLOR_TRANSITION;
   }

   static {
      SCULK_TO_REDSTONE = new DustColorTransitionOptions(SCULK_PARTICLE_COLOR, DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0F);
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ExtraCodecs.VECTOR3F.fieldOf("fromColor").forGetter((var0x) -> {
            return var0x.color;
         }), ExtraCodecs.VECTOR3F.fieldOf("toColor").forGetter((var0x) -> {
            return var0x.toColor;
         }), Codec.FLOAT.fieldOf("scale").forGetter((var0x) -> {
            return var0x.scale;
         })).apply(var0, DustColorTransitionOptions::new);
      });
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VECTOR3F, (var0) -> {
         return var0.color;
      }, ByteBufCodecs.VECTOR3F, (var0) -> {
         return var0.toColor;
      }, ByteBufCodecs.FLOAT, (var0) -> {
         return var0.scale;
      }, DustColorTransitionOptions::new);
      DESERIALIZER = new ParticleOptions.Deserializer<DustColorTransitionOptions>() {
         public DustColorTransitionOptions fromCommand(ParticleType<DustColorTransitionOptions> var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
            Vector3f var4 = DustParticleOptionsBase.readVector3f(var2);
            var2.expect(' ');
            float var5 = var2.readFloat();
            Vector3f var6 = DustParticleOptionsBase.readVector3f(var2);
            return new DustColorTransitionOptions(var4, var6, var5);
         }

         // $FF: synthetic method
         public ParticleOptions fromCommand(ParticleType var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
            return this.fromCommand(var1, var2, var3);
         }
      };
   }
}
