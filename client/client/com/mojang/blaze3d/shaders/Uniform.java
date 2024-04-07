package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class Uniform extends AbstractUniform implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int UT_INT1 = 0;
   public static final int UT_INT2 = 1;
   public static final int UT_INT3 = 2;
   public static final int UT_INT4 = 3;
   public static final int UT_FLOAT1 = 4;
   public static final int UT_FLOAT2 = 5;
   public static final int UT_FLOAT3 = 6;
   public static final int UT_FLOAT4 = 7;
   public static final int UT_MAT2 = 8;
   public static final int UT_MAT3 = 9;
   public static final int UT_MAT4 = 10;
   private static final boolean TRANSPOSE_MATRICIES = false;
   private int location;
   private final int count;
   private final int type;
   private final IntBuffer intValues;
   private final FloatBuffer floatValues;
   private final String name;
   private boolean dirty;
   private final Shader parent;

   public Uniform(String var1, int var2, int var3, Shader var4) {
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

   public static void glBindAttribLocation(int var0, int var1, CharSequence var2) {
      GlStateManager._glBindAttribLocation(var0, var1, var2);
   }

   @Override
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

   @Override
   public final void set(float var1) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.markDirty();
   }

   @Override
   public final void set(float var1, float var2) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.markDirty();
   }

   public final void set(int var1, float var2) {
      this.floatValues.position(0);
      this.floatValues.put(var1, var2);
      this.markDirty();
   }

   @Override
   public final void set(float var1, float var2, float var3) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.markDirty();
   }

   @Override
   public final void set(Vector3f var1) {
      this.floatValues.position(0);
      var1.get(this.floatValues);
      this.markDirty();
   }

   @Override
   public final void set(float var1, float var2, float var3, float var4) {
      this.floatValues.position(0);
      this.floatValues.put(var1);
      this.floatValues.put(var2);
      this.floatValues.put(var3);
      this.floatValues.put(var4);
      this.floatValues.flip();
      this.markDirty();
   }

   @Override
   public final void set(Vector4f var1) {
      this.floatValues.position(0);
      var1.get(this.floatValues);
      this.markDirty();
   }

   @Override
   public final void setSafe(float var1, float var2, float var3, float var4) {
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

   @Override
   public final void setSafe(int var1, int var2, int var3, int var4) {
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

   @Override
   public final void set(int var1) {
      this.intValues.position(0);
      this.intValues.put(0, var1);
      this.markDirty();
   }

   @Override
   public final void set(int var1, int var2) {
      this.intValues.position(0);
      this.intValues.put(0, var1);
      this.intValues.put(1, var2);
      this.markDirty();
   }

   @Override
   public final void set(int var1, int var2, int var3) {
      this.intValues.position(0);
      this.intValues.put(0, var1);
      this.intValues.put(1, var2);
      this.intValues.put(2, var3);
      this.markDirty();
   }

   @Override
   public final void set(int var1, int var2, int var3, int var4) {
      this.intValues.position(0);
      this.intValues.put(0, var1);
      this.intValues.put(1, var2);
      this.intValues.put(2, var3);
      this.intValues.put(3, var4);
      this.markDirty();
   }

   @Override
   public final void set(float[] var1) {
      if (var1.length < this.count) {
         LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, var1.length);
      } else {
         this.floatValues.position(0);
         this.floatValues.put(var1);
         this.floatValues.position(0);
         this.markDirty();
      }
   }

   @Override
   public final void setMat2x2(float var1, float var2, float var3, float var4) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.floatValues.put(3, var4);
      this.markDirty();
   }

   @Override
   public final void setMat2x3(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.floatValues.put(3, var4);
      this.floatValues.put(4, var5);
      this.floatValues.put(5, var6);
      this.markDirty();
   }

   @Override
   public final void setMat2x4(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.floatValues.put(3, var4);
      this.floatValues.put(4, var5);
      this.floatValues.put(5, var6);
      this.floatValues.put(6, var7);
      this.floatValues.put(7, var8);
      this.markDirty();
   }

   @Override
   public final void setMat3x2(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.floatValues.put(3, var4);
      this.floatValues.put(4, var5);
      this.floatValues.put(5, var6);
      this.markDirty();
   }

   @Override
   public final void setMat3x3(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.floatValues.put(3, var4);
      this.floatValues.put(4, var5);
      this.floatValues.put(5, var6);
      this.floatValues.put(6, var7);
      this.floatValues.put(7, var8);
      this.floatValues.put(8, var9);
      this.markDirty();
   }

   @Override
   public final void setMat3x4(
      float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12
   ) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.floatValues.put(3, var4);
      this.floatValues.put(4, var5);
      this.floatValues.put(5, var6);
      this.floatValues.put(6, var7);
      this.floatValues.put(7, var8);
      this.floatValues.put(8, var9);
      this.floatValues.put(9, var10);
      this.floatValues.put(10, var11);
      this.floatValues.put(11, var12);
      this.markDirty();
   }

   @Override
   public final void setMat4x2(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.floatValues.put(3, var4);
      this.floatValues.put(4, var5);
      this.floatValues.put(5, var6);
      this.floatValues.put(6, var7);
      this.floatValues.put(7, var8);
      this.markDirty();
   }

   @Override
   public final void setMat4x3(
      float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12
   ) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.floatValues.put(3, var4);
      this.floatValues.put(4, var5);
      this.floatValues.put(5, var6);
      this.floatValues.put(6, var7);
      this.floatValues.put(7, var8);
      this.floatValues.put(8, var9);
      this.floatValues.put(9, var10);
      this.floatValues.put(10, var11);
      this.floatValues.put(11, var12);
      this.markDirty();
   }

   @Override
   public final void setMat4x4(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16
   ) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
      this.floatValues.put(3, var4);
      this.floatValues.put(4, var5);
      this.floatValues.put(5, var6);
      this.floatValues.put(6, var7);
      this.floatValues.put(7, var8);
      this.floatValues.put(8, var9);
      this.floatValues.put(9, var10);
      this.floatValues.put(10, var11);
      this.floatValues.put(11, var12);
      this.floatValues.put(12, var13);
      this.floatValues.put(13, var14);
      this.floatValues.put(14, var15);
      this.floatValues.put(15, var16);
      this.markDirty();
   }

   @Override
   public final void set(Matrix4f var1) {
      this.floatValues.position(0);
      var1.get(this.floatValues);
      this.markDirty();
   }

   @Override
   public final void set(Matrix3f var1) {
      this.floatValues.position(0);
      var1.get(this.floatValues);
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
      this.intValues.rewind();
      switch (this.type) {
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
      this.floatValues.rewind();
      switch (this.type) {
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
      switch (this.type) {
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

   public int getLocation() {
      return this.location;
   }

   public int getCount() {
      return this.count;
   }

   public int getType() {
      return this.type;
   }

   public IntBuffer getIntBuffer() {
      return this.intValues;
   }

   public FloatBuffer getFloatBuffer() {
      return this.floatValues;
   }
}
