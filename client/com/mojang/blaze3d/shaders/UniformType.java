package com.mojang.blaze3d.shaders;

import net.minecraft.util.StringRepresentable;

public enum UniformType implements StringRepresentable {
   INT(1, "int"),
   IVEC3(3, "ivec3"),
   FLOAT(1, "float"),
   VEC2(2, "vec2"),
   VEC3(3, "vec3"),
   VEC4(4, "vec4"),
   MATRIX4X4(16, "matrix4x4");

   public static final StringRepresentable.EnumCodec<UniformType> CODEC = StringRepresentable.<UniformType>fromEnum(UniformType::values);
   final int count;
   final String name;

   private UniformType(final int var3, final String var4) {
      this.count = var3;
      this.name = var4;
   }

   public int count() {
      return this.count;
   }

   public boolean isIntStorage() {
      return this == INT || this == IVEC3;
   }

   public String getSerializedName() {
      return this.name;
   }

   public int getCount() {
      return this.count;
   }

   // $FF: synthetic method
   private static UniformType[] $values() {
      return new UniformType[]{INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4};
   }
}
