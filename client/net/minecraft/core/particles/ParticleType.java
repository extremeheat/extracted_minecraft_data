package net.minecraft.core.particles;

public class ParticleType<T extends ParticleOptions> {
   private final boolean overrideLimiter;
   private final ParticleOptions.Deserializer<T> deserializer;

   protected ParticleType(boolean var1, ParticleOptions.Deserializer<T> var2) {
      super();
      this.overrideLimiter = var1;
      this.deserializer = var2;
   }

   public boolean getOverrideLimiter() {
      return this.overrideLimiter;
   }

   public ParticleOptions.Deserializer<T> getDeserializer() {
      return this.deserializer;
   }
}
