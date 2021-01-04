package com.mojang.blaze3d.shaders;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.TextureUtil;
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
      ++this.references;
      GLX.glAttachShader(var1.getId(), this.id);
   }

   public void close() {
      --this.references;
      if (this.references <= 0) {
         GLX.glDeleteShader(this.id);
         this.type.getPrograms().remove(this.name);
      }

   }

   public String getName() {
      return this.name;
   }

   public static Program compileShader(Program.Type var0, String var1, InputStream var2) throws IOException {
      String var3 = TextureUtil.readResourceAsString(var2);
      if (var3 == null) {
         throw new IOException("Could not load program " + var0.getName());
      } else {
         int var4 = GLX.glCreateShader(var0.getGlType());
         GLX.glShaderSource(var4, var3);
         GLX.glCompileShader(var4);
         if (GLX.glGetShaderi(var4, GLX.GL_COMPILE_STATUS) == 0) {
            String var6 = StringUtils.trim(GLX.glGetShaderInfoLog(var4, 32768));
            throw new IOException("Couldn't compile " + var0.getName() + " program: " + var6);
         } else {
            Program var5 = new Program(var0, var4, var1);
            var0.getPrograms().put(var1, var5);
            return var5;
         }
      }
   }

   public static enum Type {
      VERTEX("vertex", ".vsh", GLX.GL_VERTEX_SHADER),
      FRAGMENT("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);

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
