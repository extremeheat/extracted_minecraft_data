package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.logging.LogUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class Uniform extends AbstractUniform implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private int location;
   private final UniformType type;
   private final IntBuffer intValues;
   private final FloatBuffer floatValues;
   private final String name;
   private boolean dirty;

   public Uniform(String var1, UniformType var2) {
      super();
      this.name = var1;
      this.type = var2;
      if (var2.isIntStorage()) {
         this.intValues = MemoryUtil.memAllocInt(var2.count());
         this.floatValues = null;
      } else {
         this.intValues = null;
         this.floatValues = MemoryUtil.memAllocFloat(var2.count());
      }

      this.location = -1;
   }

   public static int glGetUniformLocation(int var0, CharSequence var1) {
      return GlStateManager._glGetUniformLocation(var0, var1);
   }

   public static void uploadInteger(int var0, int var1) {
      GlStateManager._glUniform1i(var0, var1);
   }

   public void close() {
      if (this.intValues != null) {
         MemoryUtil.memFree(this.intValues);
      }

      if (this.floatValues != null) {
         MemoryUtil.memFree(this.floatValues);
      }

   }

   public void setLocation(int var1) {
      this.location = var1;
   }

   public String getName() {
      return this.name;
   }

   public UniformType getType() {
      return this.type;
   }

   public final void set(float var1) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.dirty = true;
   }

   public final void set(float var1, float var2) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.dirty = true;
   }

   public final void set(float var1, float var2, float var3) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.dirty = true;
   }

   public final void set(Vector3f var1) {
      this.floatValues.position(0);
      var1.get(this.floatValues);
      this.dirty = true;
   }

   public final void set(float var1, float var2, float var3, float var4) {
      this.floatValues.position(0);
      this.floatValues.put(var1);
      this.floatValues.put(var2);
      this.floatValues.put(var3);
      this.floatValues.put(var4);
      this.floatValues.flip();
      this.dirty = true;
   }

   public final void set(int var1) {
      this.intValues.position(0);
      this.intValues.put(0, var1);
      this.dirty = true;
   }

   public final void set(int var1, int var2, int var3) {
      this.intValues.position(0);
      this.intValues.put(0, var1);
      this.intValues.put(1, var2);
      this.intValues.put(2, var3);
      this.dirty = true;
   }

   public final void set(float[] var1) {
      if (var1.length < this.type.count()) {
         LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.type.count(), var1.length);
      } else {
         this.floatValues.position(0);
         this.floatValues.put(var1);
         this.floatValues.position(0);
         this.dirty = true;
      }
   }

   public final void set(int[] var1) {
      if (var1.length < this.type.count()) {
         LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.type.count(), var1.length);
      } else {
         this.intValues.position(0);
         this.intValues.put(var1);
         this.intValues.position(0);
         this.dirty = true;
      }
   }

   public final void set(Matrix4f var1) {
      this.floatValues.position(0);
      var1.get(this.floatValues);
      this.dirty = true;
   }

   public void upload() {
      if (this.dirty) {
         if (this.type.isIntStorage()) {
            switch (this.type) {
               case INT -> GlStateManager._glUniform1(this.location, this.intValues);
               case IVEC3 -> GlStateManager._glUniform3(this.location, this.intValues);
            }
         } else {
            switch (this.type) {
               case FLOAT -> GlStateManager._glUniform1(this.location, this.floatValues);
               case VEC2 -> GlStateManager._glUniform2(this.location, this.floatValues);
               case VEC3 -> GlStateManager._glUniform3(this.location, this.floatValues);
               case VEC4 -> GlStateManager._glUniform4(this.location, this.floatValues);
               case MATRIX4X4 -> GlStateManager._glUniformMatrix4(this.location, this.floatValues);
            }
         }

         this.dirty = false;
      }

   }
}
