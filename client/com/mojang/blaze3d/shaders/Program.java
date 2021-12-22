package com.mojang.blaze3d.shaders;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Program {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int MAX_LOG_LENGTH = 32768;
   private final Program.Type type;
   private final String name;
   // $FF: renamed from: id int
   private int field_421;

   protected Program(Program.Type var1, int var2, String var3) {
      super();
      this.type = var1;
      this.field_421 = var2;
      this.name = var3;
   }

   public void attachToShader(Shader var1) {
      RenderSystem.assertOnRenderThread();
      GlStateManager.glAttachShader(var1.getId(), this.getId());
   }

   public void close() {
      if (this.field_421 != -1) {
         RenderSystem.assertOnRenderThread();
         GlStateManager.glDeleteShader(this.field_421);
         this.field_421 = -1;
         this.type.getPrograms().remove(this.name);
      }
   }

   public String getName() {
      return this.name;
   }

   public static Program compileShader(Program.Type var0, String var1, InputStream var2, String var3, GlslPreprocessor var4) throws IOException {
      RenderSystem.assertOnRenderThread();
      int var5 = compileShaderInternal(var0, var1, var2, var3, var4);
      Program var6 = new Program(var0, var5, var1);
      var0.getPrograms().put(var1, var6);
      return var6;
   }

   protected static int compileShaderInternal(Program.Type var0, String var1, InputStream var2, String var3, GlslPreprocessor var4) throws IOException {
      String var5 = TextureUtil.readResourceAsString(var2);
      if (var5 == null) {
         throw new IOException("Could not load program " + var0.getName());
      } else {
         int var6 = GlStateManager.glCreateShader(var0.getGlType());
         GlStateManager.glShaderSource(var6, var4.process(var5));
         GlStateManager.glCompileShader(var6);
         if (GlStateManager.glGetShaderi(var6, 35713) == 0) {
            String var7 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(var6, 32768));
            throw new IOException("Couldn't compile " + var0.getName() + " program (" + var3 + ", " + var1 + ") : " + var7);
         } else {
            return var6;
         }
      }
   }

   private static Program createProgram(Program.Type var0, String var1, int var2) {
      return new Program(var0, var2, var1);
   }

   protected int getId() {
      return this.field_421;
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

      int getGlType() {
         return this.glType;
      }

      public Map<String, Program> getPrograms() {
         return this.programs;
      }

      // $FF: synthetic method
      private static Program.Type[] $values() {
         return new Program.Type[]{VERTEX, FRAGMENT};
      }
   }
}
