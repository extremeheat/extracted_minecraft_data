package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class CompiledShader implements AutoCloseable {
   private static final int NOT_ALLOCATED = -1;
   private final ResourceLocation id;
   private int shaderId;

   private CompiledShader(int var1, ResourceLocation var2) {
      super();
      this.id = var2;
      this.shaderId = var1;
   }

   public static CompiledShader compile(ResourceLocation var0, Type var1, String var2) throws ShaderManager.CompilationException {
      RenderSystem.assertOnRenderThread();
      int var3 = GlStateManager.glCreateShader(var1.glType());
      GlStateManager.glShaderSource(var3, var2);
      GlStateManager.glCompileShader(var3);
      if (GlStateManager.glGetShaderi(var3, 35713) == 0) {
         String var4 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(var3, 32768));
         String var10002 = var1.getName();
         throw new ShaderManager.CompilationException("Couldn't compile " + var10002 + " shader (" + String.valueOf(var0) + ") : " + var4);
      } else {
         return new CompiledShader(var3, var0);
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

   public static enum Type {
      VERTEX("vertex", ".vsh", 35633),
      FRAGMENT("fragment", ".fsh", 35632);

      private static final Type[] TYPES = values();
      private final String name;
      private final String extension;
      private final int glType;

      private Type(final String var3, final String var4, final int var5) {
         this.name = var3;
         this.extension = var4;
         this.glType = var5;
      }

      @Nullable
      public static Type byLocation(ResourceLocation var0) {
         Type[] var1 = TYPES;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Type var4 = var1[var3];
            if (var0.getPath().endsWith(var4.extension)) {
               return var4;
            }
         }

         return null;
      }

      public String getName() {
         return this.name;
      }

      public int glType() {
         return this.glType;
      }

      public FileToIdConverter idConverter() {
         return new FileToIdConverter("shaders", this.extension);
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{VERTEX, FRAGMENT};
      }
   }
}
