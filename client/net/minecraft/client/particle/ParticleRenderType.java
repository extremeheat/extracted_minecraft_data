package net.minecraft.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public interface ParticleRenderType {
   ParticleRenderType TERRAIN_SHEET = new ParticleRenderType() {
      public BufferBuilder begin(Tesselator var1, TextureManager var2) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
         return var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public String toString() {
         return "TERRAIN_SHEET";
      }
   };
   ParticleRenderType PARTICLE_SHEET_OPAQUE = new ParticleRenderType() {
      public BufferBuilder begin(Tesselator var1, TextureManager var2) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.setShader(GameRenderer::getParticleShader);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         return var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public String toString() {
         return "PARTICLE_SHEET_OPAQUE";
      }
   };
   ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
      public BufferBuilder begin(Tesselator var1, TextureManager var2) {
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         return var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public String toString() {
         return "PARTICLE_SHEET_TRANSLUCENT";
      }
   };
   ParticleRenderType PARTICLE_SHEET_LIT = new ParticleRenderType() {
      public BufferBuilder begin(Tesselator var1, TextureManager var2) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         return var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public String toString() {
         return "PARTICLE_SHEET_LIT";
      }
   };
   ParticleRenderType CUSTOM = new ParticleRenderType() {
      public BufferBuilder begin(Tesselator var1, TextureManager var2) {
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         return var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public String toString() {
         return "CUSTOM";
      }
   };
   ParticleRenderType NO_RENDER = new ParticleRenderType() {
      @Nullable
      public BufferBuilder begin(Tesselator var1, TextureManager var2) {
         return null;
      }

      public String toString() {
         return "NO_RENDER";
      }
   };

   @Nullable
   BufferBuilder begin(Tesselator var1, TextureManager var2);
}
