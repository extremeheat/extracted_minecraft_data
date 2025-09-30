package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;

public class GlShaderModule implements AutoCloseable {
   private static final int NOT_ALLOCATED = -1;
   public static final GlShaderModule INVALID_SHADER;
   private final ResourceLocation id;
   private int shaderId;
   private final ShaderType type;

   public GlShaderModule(int var1, ResourceLocation var2, ShaderType var3) {
      super();
      this.id = var2;
      this.shaderId = var1;
      this.type = var3;
   }

   public void close() {
      if (this.shaderId == -1) {
         throw new IllegalStateException("Already closed");
      } else {
         RenderSystem.assertOnRenderThread();
         GlStateManager.glDeleteShader(this.shaderId);
         this.shaderId = -1;
      }
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public int getShaderId() {
      return this.shaderId;
   }

   public String getDebugLabel() {
      return this.type.idConverter().idToFile(this.id).toString();
   }

   static {
      INVALID_SHADER = new GlShaderModule(-1, ResourceLocation.withDefaultNamespace("invalid"), ShaderType.VERTEX);
   }
}
