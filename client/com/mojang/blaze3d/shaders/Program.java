package com.mojang.blaze3d.shaders;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class Program {
   private final Program.Type type;
   private final String name;
   private final int id;
   private int references;

   private Program(Program.Type var1, int var2, String var3) {
      super();
      this.type = var1;
      this.id = var2;
      this.name = var3;
   }

   public void attachToEffect(Effect var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      ++this.references;
      GlStateManager.glAttachShader(var1.getId(), this.id);
   }

   public void close() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      --this.references;
      if (this.references <= 0) {
         GlStateManager.glDeleteShader(this.id);
         this.type.getPrograms().remove(this.name);
      }

   }

   public String getName() {
      return this.name;
   }

   public static Program compileShader(Program.Type var0, String var1, InputStream var2, String var3) throws IOException {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      String var4 = TextureUtil.readResourceAsString(var2);
      if (var4 == null) {
         throw new IOException("Could not load program " + var0.getName());
      } else {
         int var5 = GlStateManager.glCreateShader(var0.getGlType());
         GlStateManager.glShaderSource(var5, var4);
         GlStateManager.glCompileShader(var5);
         if (GlStateManager.glGetShaderi(var5, 35713) == 0) {
            String var7 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(var5, 32768));
            throw new IOException("Couldn't compile " + var0.getName() + " program (" + var3 + ", " + var1 + ") : " + var7);
         } else {
            Program var6 = new Program(var0, var5, var1);
            var0.getPrograms().put(var1, var6);
            return var6;
         }
      }
   }

   public static enum Type {
      VERTEX("vertex", ".vsh", 35633),
      FRAGMENT("fragment", ".fsh", 35632);

      private final String name;
      private final String extension;
      private final int glType;
      private final Map<String, Program> programs = Maps.newHashMap();

      private Type(String var3, String var4, int var5) {
         this.name = var3;
         this.extension = var4;
         this.glType = var5;
      }

      public String getName() {
         return this.name;
      }

      public String getExtension() {
         return this.extension;
      }

      private int getGlType() {
         return this.glType;
      }

      public Map<String, Program> getPrograms() {
         return this.programs;
      }
   }
}
