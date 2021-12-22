package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;

public class EffectProgram extends Program {
   private static final GlslPreprocessor PREPROCESSOR = new GlslPreprocessor() {
      public String applyImport(boolean var1, String var2) {
         return "#error Import statement not supported";
      }
   };
   private int references;

   private EffectProgram(Program.Type var1, int var2, String var3) {
      super(var1, var2, var3);
   }

   public void attachToEffect(Effect var1) {
      RenderSystem.assertOnRenderThread();
      ++this.references;
      this.attachToShader(var1);
   }

   public void close() {
      RenderSystem.assertOnRenderThread();
      --this.references;
      if (this.references <= 0) {
         super.close();
      }

   }

   public static EffectProgram compileShader(Program.Type var0, String var1, InputStream var2, String var3) throws IOException {
      RenderSystem.assertOnRenderThread();
      int var4 = compileShaderInternal(var0, var1, var2, var3, PREPROCESSOR);
      EffectProgram var5 = new EffectProgram(var0, var4, var1);
      var0.getPrograms().put(var1, var5);
      return var5;
   }
}
