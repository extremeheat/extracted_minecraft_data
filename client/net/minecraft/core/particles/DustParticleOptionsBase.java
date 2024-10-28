package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public abstract class DustParticleOptionsBase implements ParticleOptions {
   public static final float MIN_SCALE = 0.01F;
   public static final float MAX_SCALE = 4.0F;
   protected final Vector3f color;
   protected final float scale;

   public DustParticleOptionsBase(Vector3f var1, float var2) {
      super();
      this.color = var1;
      this.scale = Mth.clamp(var2, 0.01F, 4.0F);
   }

   public static Vector3f readVector3f(StringReader var0) throws CommandSyntaxException {
      var0.expect(' ');
      float var1 = var0.readFloat();
      var0.expect(' ');
      float var2 = var0.readFloat();
      var0.expect(' ');
      float var3 = var0.readFloat();
      return new Vector3f(var1, var2, var3);
   }

   public String writeToString(HolderLookup.Provider var1) {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale);
   }

   public Vector3f getColor() {
      return this.color;
   }

   public float getScale() {
      return this.scale;
   }
}
