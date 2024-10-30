package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

public record ParticleRenderType(String name, @Nullable RenderType renderType) {
   public static final ParticleRenderType TERRAIN_SHEET;
   public static final ParticleRenderType PARTICLE_SHEET_OPAQUE;
   public static final ParticleRenderType PARTICLE_SHEET_TRANSLUCENT;
   public static final ParticleRenderType CUSTOM;
   public static final ParticleRenderType NO_RENDER;

   public ParticleRenderType(String var1, @Nullable RenderType var2) {
      super();
      this.name = var1;
      this.renderType = var2;
   }

   public String name() {
      return this.name;
   }

   @Nullable
   public RenderType renderType() {
      return this.renderType;
   }

   static {
      TERRAIN_SHEET = new ParticleRenderType("TERRAIN_SHEET", RenderType.translucentParticle(TextureAtlas.LOCATION_BLOCKS));
      PARTICLE_SHEET_OPAQUE = new ParticleRenderType("PARTICLE_SHEET_OPAQUE", RenderType.opaqueParticle(TextureAtlas.LOCATION_PARTICLES));
      PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType("PARTICLE_SHEET_TRANSLUCENT", RenderType.translucentParticle(TextureAtlas.LOCATION_PARTICLES));
      CUSTOM = new ParticleRenderType("CUSTOM", (RenderType)null);
      NO_RENDER = new ParticleRenderType("NO_RENDER", (RenderType)null);
   }
}
