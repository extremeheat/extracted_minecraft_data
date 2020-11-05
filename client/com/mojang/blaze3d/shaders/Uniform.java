package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

public class Uniform extends AbstractUniform implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private int location;
   private final int count;
   private final int type;
   private final IntBuffer intValues;
   private final FloatBuffer floatValues;
   private final String name;
   private boolean dirty;
   private final Effect parent;

   public Uniform(String var1, int var2, int var3, Effect var4) {
      super();
      this.name = var1;
      this.count = var3;
      this.type = var2;
      this.parent = var4;
      if (var2 <= 3) {
         this.intValues = MemoryUtil.memAllocInt(var3);
         this.floatValues = null;
      } else {
         this.intValues = null;
         this.floatValues = MemoryUtil.memAllocFloat(var3);
      }

      this.location = -1;
      this.markDirty();
   }

   public static int glGetUniformLocation(int var0, CharSequence var1) {
      return GlStateManager._glGetUniformLocation(var0, var1);
   }

   public static void uploadInteger(int var0, int var1) {
      RenderSystem.glUniform1i(var0, var1);
   }

   public static int glGetAttribLocation(int var0, CharSequence var1) {
      return GlStateManager._glGetAttribLocation(var0, var1);
   }

   public void close() {
      if (this.intValues != null) {
         MemoryUtil.memFree(this.intValues);
      }

      if (this.floatValues != null) {
         MemoryUtil.memFree(this.floatValues);
      }

   }

   private void markDirty() {
      this.dirty = true;
      if (this.parent != null) {
         this.parent.markDirty();
      }

   }

   public static int getTypeFromString(String var0) {
      byte var1 = -1;
      if ("int".equals(var0)) {
         var1 = 0;
      } else if ("float".equals(var0)) {
         var1 = 4;
      } else if (var0.startsWith("matrix")) {
         if (var0.endsWith("2x2")) {
            var1 = 8;
         } else if (var0.endsWith("3x3")) {
            var1 = 9;
         } else if (var0.endsWith("4x4")) {
            var1 = 10;
         }
      }

      return var1;
   }

   public void setLocation(int var1) {
      this.location = var1;
   }

   public String getName() {
      return this.name;
   }

   public void set(float var1) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.markDirty();
   }

   public void set(float var1, float var2) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.markDirty();
   }

   public void set(float var1, float var2, float var3) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.markDirty();
   }

   public void set(float var1, float var2, float var3, float var4) {
      this.floatValues.position(0);
      this.floatValues.put(var1);
      this.floatValues.put(var2);
      this.floatValues.put(var3);
      this.floatValues.put(var4);
      this.floatValues.flip();
      this.markDirty();
   }

   public void setSafe(float var1, float var2, float var3, float var4) {
      this.floatValues.position(0);
      if (this.type >= 4) {
         this.floatValues.put(0, var1);
      }

      if (this.type >= 5) {
         this.floatValues.put(1, var2);
      }

      if (this.type >= 6) {
         this.floatValues.put(2, var3);
      }

      if (this.type >= 7) {
         this.floatValues.put(3, var4);
      }

      this.markDirty();
   }

   public void setSafe(int var1, int var2, int var3, int var4) {
      this.intValues.position(0);
      if (this.type >= 0) {
         this.intValues.put(0, var1);
      }

      if (this.type >= 1) {
         this.intValues.put(1, var2);
      }

      if (this.type >= 2) {
         this.intValues.put(2, var3);
      }

      if (this.type >= 3) {
         this.intValues.put(3, var4);
      }

      this.markDirty();
   }

   public void set(float[] var1) {
      if (var1.length < this.count) {
         LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, var1.length);
      } else {
         this.floatValues.position(0);
         this.floatValues.put(var1);
         this.floatValues.position(0);
         this.markDirty();
      }
   }

   public void set(Matrix4f var1) {
      this.floatValues.position(0);
      var1.store(this.floatValues);
      this.markDirty();
   }

   public void upload() {
      if (!this.dirty) {
      }

      this.dirty = false;
      if (this.type <= 3) {
         this.uploadAsInteger();
      } else if (this.type <= 7) {
         this.uploadAsFloat();
      } else {
         if (this.type > 10) {
            LOGGER.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", this.type);
            return;
         }

         this.uploadAsMatrix();
      }

   }

   private void uploadAsInteger() {
      this.floatValues.clear();
      switch(this.type) {
      case 0:
         RenderSystem.glUniform1(this.location, this.intValues);
         break;
      case 1:
         RenderSystem.glUniform2(this.location, this.intValues);
         break;
      case 2:
         RenderSystem.glUniform3(this.location, this.intValues);
         break;
      case 3:
         RenderSystem.glUniform4(this.location, this.intValues);
         break;
      default:
         LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", this.count);
      }

   }

   private void uploadAsFloat() {
      this.floatValues.clear();
      switch(this.type) {
      case 4:
         RenderSystem.glUniform1(this.location, this.floatValues);
         break;
      case 5:
         RenderSystem.glUniform2(this.location, this.floatValues);
         break;
      case 6:
         RenderSystem.glUniform3(this.location, this.floatValues);
         break;
      case 7:
         RenderSystem.glUniform4(this.location, this.floatValues);
         break;
      default:
         LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", this.count);
      }

   }

   private void uploadAsMatrix() {
      this.floatValues.clear();
      switch(this.type) {
      case 8:
         RenderSystem.glUniformMatrix2(this.location, false, this.floatValues);
         break;
      case 9:
         RenderSystem.glUniformMatrix3(this.location, false, this.floatValues);
         break;
      case 10:
         RenderSystem.glUniformMatrix4(this.location, false, this.floatValues);
      }

   }
}
