package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.StringRepresentable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class Uniform extends AbstractUniform implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private int location;
   private final Type type;
   private final IntBuffer intValues;
   private final FloatBuffer floatValues;
   private final String name;

   public Uniform(String var1, Type var2) {
      super();
      this.name = var1;
      this.type = var2;
      if (var2.isIntStorage()) {
         this.intValues = MemoryUtil.memAllocInt(var2.count);
         this.floatValues = null;
      } else {
         this.intValues = null;
         this.floatValues = MemoryUtil.memAllocFloat(var2.count);
      }

      this.location = -1;
   }

   public static int glGetUniformLocation(int var0, CharSequence var1) {
      return GlStateManager._glGetUniformLocation(var0, var1);
   }

   public static void uploadInteger(int var0, int var1) {
      RenderSystem.glUniform1i(var0, var1);
   }

   public void setFromConfig(List<Float> var1, int var2) {
      float[] var3 = new float[Math.max(var2, 16)];
      if (var1.size() == 1) {
         Arrays.fill(var3, (Float)var1.getFirst());
      } else {
         for(int var4 = 0; var4 < var1.size(); ++var4) {
            var3[var4] = (Float)var1.get(var4);
         }
      }

      if (this.type.isIntStorage()) {
         this.setSafe((int)var3[0], (int)var3[1], (int)var3[2]);
      } else {
         this.set(Arrays.copyOfRange(var3, 0, var2));
      }

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

   public Type getType() {
      return this.type;
   }

   public final void set(float var1) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
   }

   public final void set(float var1, float var2) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
   }

   public final void set(float var1, float var2, float var3) {
      this.floatValues.position(0);
      this.floatValues.put(0, var1);
      this.floatValues.put(1, var2);
      this.floatValues.put(2, var3);
   }

   public final void set(Vector3f var1) {
      this.floatValues.position(0);
      var1.get(this.floatValues);
   }

   public final void set(float var1, float var2, float var3, float var4) {
      this.floatValues.position(0);
      this.floatValues.put(var1);
      this.floatValues.put(var2);
      this.floatValues.put(var3);
      this.floatValues.put(var4);
      this.floatValues.flip();
   }

   private void setSafe(int var1, int var2, int var3) {
      this.intValues.position(0);
      if (this.type == Uniform.Type.INT || this.type == Uniform.Type.IVEC3) {
         this.intValues.put(0, var1);
      }

      if (this.type == Uniform.Type.IVEC3) {
         this.intValues.put(1, var2);
         this.intValues.put(2, var3);
      }

   }

   public final void set(int var1) {
      this.intValues.position(0);
      this.intValues.put(0, var1);
   }

   public final void set(int var1, int var2, int var3) {
      this.intValues.position(0);
      this.intValues.put(0, var1);
      this.intValues.put(1, var2);
      this.intValues.put(2, var3);
   }

   public final void set(float[] var1) {
      if (var1.length < this.type.count) {
         LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.type.count, var1.length);
      } else {
         this.floatValues.position(0);
         this.floatValues.put(var1);
         this.floatValues.position(0);
      }
   }

   public final void set(Matrix4f var1) {
      this.floatValues.position(0);
      var1.get(this.floatValues);
   }

   public void upload() {
      if (this.type.isIntStorage()) {
         this.type.uploadIntBuffer(this.location, this.intValues);
      } else {
         this.type.uploadFloatBuffer(this.location, this.floatValues);
      }

   }

   public static enum Type implements StringRepresentable {
      INT(1, "int"),
      IVEC3(3, "ivec3"),
      FLOAT(1, "float"),
      VEC2(2, "vec2"),
      VEC3(3, "vec3"),
      VEC4(4, "vec4"),
      MATRIX4X4(16, "matrix4x4");

      public static final StringRepresentable.EnumCodec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      final int count;
      final String name;

      private Type(final int var3, final String var4) {
         this.count = var3;
         this.name = var4;
      }

      boolean isIntStorage() {
         return this == INT || this == IVEC3;
      }

      void uploadIntBuffer(int var1, IntBuffer var2) {
         switch (this.ordinal()) {
            case 0 -> RenderSystem.glUniform1(var1, var2);
            case 1 -> RenderSystem.glUniform3(var1, var2);
         }

      }

      void uploadFloatBuffer(int var1, FloatBuffer var2) {
         switch (this.ordinal()) {
            case 2 -> RenderSystem.glUniform1(var1, var2);
            case 3 -> RenderSystem.glUniform2(var1, var2);
            case 4 -> RenderSystem.glUniform3(var1, var2);
            case 5 -> RenderSystem.glUniform4(var1, var2);
            case 6 -> RenderSystem.glUniformMatrix4(var1, var2);
         }

      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4};
      }
   }
}
