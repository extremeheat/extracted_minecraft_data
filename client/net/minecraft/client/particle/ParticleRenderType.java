package net.minecraft.client.particle;

public record ParticleRenderType(String name) {
   public static final ParticleRenderType SINGLE_QUADS = new ParticleRenderType("SINGLE_QUADS");
   public static final ParticleRenderType ITEM_PICKUP = new ParticleRenderType("ITEM_PICKUP");
   public static final ParticleRenderType ELDER_GUARDIANS = new ParticleRenderType("ELDER_GUARDIANS");
   public static final ParticleRenderType NO_RENDER = new ParticleRenderType("NO_RENDER");

   public ParticleRenderType {
      super();
   }
}
