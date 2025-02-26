package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

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

   public static GlShaderModule compile(ResourceLocation var0, ShaderType var1, String var2) throws ShaderManager.CompilationException {
      RenderSystem.assertOnRenderThread();
      int var3 = GlStateManager.glCreateShader(GlConst.toGl(var1));
      GlStateManager.glShaderSource(var3, var2);
      GlStateManager.glCompileShader(var3);
      if (GlStateManager.glGetShaderi(var3, 35713) == 0) {
         String var4 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(var3, 32768));
         String var10002 = var1.getName();
         throw new ShaderManager.CompilationException("Couldn't compile " + var10002 + " shader (" + String.valueOf(var0) + ") : " + var4);
      } else {
         return new GlShaderModule(var3, var0, var1);
      }
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
