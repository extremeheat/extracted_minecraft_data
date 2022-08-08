package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class DustColorTransitionOptions extends DustParticleOptionsBase {
   public static final Vector3f SCULK_PARTICLE_COLOR = new Vector3f(Vec3.fromRGB24(3790560));
   public static final DustColorTransitionOptions SCULK_TO_REDSTONE;
   public static final Codec<DustColorTransitionOptions> CODEC;
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

   public void writeToNetwork(FriendlyByteBuf var1) {
      super.writeToNetwork(var1);
      var1.writeFloat(this.toColor.x());
      var1.writeFloat(this.toColor.y());
      var1.writeFloat(this.toColor.z());
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale, this.toColor.x(), this.toColor.y(), this.toColor.z());
   }

   public ParticleType<DustColorTransitionOptions> getType() {
      return ParticleTypes.DUST_COLOR_TRANSITION;
   }

   static {
      SCULK_TO_REDSTONE = new DustColorTransitionOptions(SCULK_PARTICLE_COLOR, DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0F);
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Vector3f.CODEC.fieldOf("fromColor").forGetter((var0x) -> {
            return var0x.color;
         }), Vector3f.CODEC.fieldOf("toColor").forGetter((var0x) -> {
            return var0x.toColor;
         }), Codec.FLOAT.fieldOf("scale").forGetter((var0x) -> {
            return var0x.scale;
         })).apply(var0, DustColorTransitionOptions::new);
      });
      DESERIALIZER = new ParticleOptions.Deserializer<DustColorTransitionOptions>() {
         public DustColorTransitionOptions fromCommand(ParticleType<DustColorTransitionOptions> var1, StringReader var2) throws CommandSyntaxException {
            Vector3f var3 = DustParticleOptionsBase.readVector3f(var2);
            var2.expect(' ');
            float var4 = var2.readFloat();
            Vector3f var5 = DustParticleOptionsBase.readVector3f(var2);
            return new DustColorTransitionOptions(var3, var5, var4);
         }

         public DustColorTransitionOptions fromNetwork(ParticleType<DustColorTransitionOptions> var1, FriendlyByteBuf var2) {
            Vector3f var3 = DustParticleOptionsBase.readVector3f(var2);
            float var4 = var2.readFloat();
            Vector3f var5 = DustParticleOptionsBase.readVector3f(var2);
            return new DustColorTransitionOptions(var3, var5, var4);
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
