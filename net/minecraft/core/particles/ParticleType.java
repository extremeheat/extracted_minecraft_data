package net.minecraft.core.particles;

public class ParticleType {
   private final boolean overrideLimiter;
   private final ParticleOptions.Deserializer deserializer;

   protected ParticleType(boolean var1, ParticleOptions.Deserializer var2) {
      this.overrideLimiter = var1;
      this.deserializer = var2;
   }

   public boolean getOverrideLimiter() {
      return this.overrideLimiter;
   }

   public ParticleOptions.Deserializer getDeserializer() {
      return this.deserializer;
   }
}
