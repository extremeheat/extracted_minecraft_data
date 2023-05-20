package net.minecraft.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public interface ParticleRenderType {
   ParticleRenderType TERRAIN_SHEET = new ParticleRenderType() {
      @Override
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
         var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public void end(Tesselator var1) {
         var1.end();
      }

      @Override
      public String toString() {
         return "TERRAIN_SHEET";
      }
   };
   ParticleRenderType PARTICLE_SHEET_OPAQUE = new ParticleRenderType() {
      @Override
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.setShader(GameRenderer::getParticleShader);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public void end(Tesselator var1) {
         var1.end();
      }

      @Override
      public String toString() {
         return "PARTICLE_SHEET_OPAQUE";
      }
   };
   ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
      @Override
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public void end(Tesselator var1) {
         var1.end();
      }

      @Override
      public String toString() {
         return "PARTICLE_SHEET_TRANSLUCENT";
      }
   };
   ParticleRenderType PARTICLE_SHEET_LIT = new ParticleRenderType() {
      @Override
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public void end(Tesselator var1) {
         var1.end();
      }

      @Override
      public String toString() {
         return "PARTICLE_SHEET_LIT";
      }
   };
   ParticleRenderType CUSTOM = new ParticleRenderType() {
      @Override
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
      }

      @Override
      public void end(Tesselator var1) {
      }

      @Override
      public String toString() {
         return "CUSTOM";
      }
   };
   ParticleRenderType NO_RENDER = new ParticleRenderType() {
      @Override
      public void begin(BufferBuilder var1, TextureManager var2) {
      }

      @Override
      public void end(Tesselator var1) {
      }

      @Override
      public String toString() {
         return "NO_RENDER";
      }
   };

   void begin(BufferBuilder var1, TextureManager var2);

   void end(Tesselator var1);
}
