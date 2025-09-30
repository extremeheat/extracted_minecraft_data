package net.minecraft.core.particles;

public record ParticleLimit(int limit) {
   public static final ParticleLimit SPORE_BLOSSOM = new ParticleLimit(1000);

   public ParticleLimit(int var1) {
      super();
      this.limit = var1;
   }
}
