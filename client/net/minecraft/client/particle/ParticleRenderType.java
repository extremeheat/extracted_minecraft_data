package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public interface ParticleRenderType {
   ParticleRenderType TERRAIN_SHEET = new ParticleRenderType() {
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.depthMask(true);
         var2.bind(TextureAtlas.LOCATION_BLOCKS);
         var1.begin(7, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator var1) {
         var1.end();
      }

      public String toString() {
         return "TERRAIN_SHEET";
      }
   };
   ParticleRenderType PARTICLE_SHEET_OPAQUE = new ParticleRenderType() {
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         var2.bind(TextureAtlas.LOCATION_PARTICLES);
         var1.begin(7, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator var1) {
         var1.end();
      }

      public String toString() {
         return "PARTICLE_SHEET_OPAQUE";
      }
   };
   ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.depthMask(true);
         var2.bind(TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.alphaFunc(516, 0.003921569F);
         var1.begin(7, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator var1) {
         var1.end();
      }

      public String toString() {
         return "PARTICLE_SHEET_TRANSLUCENT";
      }
   };
   ParticleRenderType PARTICLE_SHEET_LIT = new ParticleRenderType() {
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         var2.bind(TextureAtlas.LOCATION_PARTICLES);
         var1.begin(7, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator var1) {
         var1.end();
      }

      public String toString() {
         return "PARTICLE_SHEET_LIT";
      }
   };
   ParticleRenderType CUSTOM = new ParticleRenderType() {
      public void begin(BufferBuilder var1, TextureManager var2) {
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
      }

      public void end(Tesselator var1) {
      }

      public String toString() {
         return "CUSTOM";
      }
   };
   ParticleRenderType NO_RENDER = new ParticleRenderType() {
      public void begin(BufferBuilder var1, TextureManager var2) {
      }

      public void end(Tesselator var1) {
      }

      public String toString() {
         return "NO_RENDER";
      }
   };

   void begin(BufferBuilder var1, TextureManager var2);

   void end(Tesselator var1);
}
