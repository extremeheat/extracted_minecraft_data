package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4fc;

public interface UniformValue {
   Codec<UniformValue> CODEC = UniformValue.Type.CODEC.dispatch(UniformValue::type, (t) -> t.valueCodec);

   void writeTo(Std140Builder builder);

   void addSize(Std140SizeCalculator calculator);

   Type type();

   public static enum Type implements StringRepresentable {
      INT("int", UniformValue.IntUniform.CODEC),
      IVEC3("ivec3", UniformValue.IVec3Uniform.CODEC),
      FLOAT("float", UniformValue.FloatUniform.CODEC),
      VEC2("vec2", UniformValue.Vec2Uniform.CODEC),
      VEC3("vec3", UniformValue.Vec3Uniform.CODEC),
      VEC4("vec4", UniformValue.Vec4Uniform.CODEC),
      MATRIX4X4("matrix4x4", UniformValue.Matrix4x4Uniform.CODEC);

      public static final StringRepresentable.EnumCodec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      private final String name;
      private final MapCodec<? extends UniformValue> valueCodec;

      private Type(final String name, final Codec<? extends UniformValue> valueCodec) {
         this.name = name;
         this.valueCodec = valueCodec.fieldOf("value");
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4};
      }
   }

   public static record IntUniform(int value) implements UniformValue {
      public static final Codec<IntUniform> CODEC;

      public IntUniform {
         super();
      }

      public void writeTo(final Std140Builder builder) {
         builder.putInt(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putInt();
      }

      public Type type() {
         return UniformValue.Type.INT;
      }

      static {
         CODEC = Codec.INT.xmap(IntUniform::new, IntUniform::value);
      }
   }

   public static record IVec3Uniform(Vector3ic value) implements UniformValue {
      public static final Codec<IVec3Uniform> CODEC;

      public IVec3Uniform {
         super();
      }

      public void writeTo(final Std140Builder builder) {
         builder.putIVec3(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putIVec3();
      }

      public Type type() {
         return UniformValue.Type.IVEC3;
      }

      static {
         CODEC = ExtraCodecs.VECTOR3I.xmap(IVec3Uniform::new, IVec3Uniform::value);
      }
   }

   public static record FloatUniform(float value) implements UniformValue {
      public static final Codec<FloatUniform> CODEC;

      public FloatUniform {
         super();
      }

      public void writeTo(final Std140Builder builder) {
         builder.putFloat(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putFloat();
      }

      public Type type() {
         return UniformValue.Type.FLOAT;
      }

      static {
         CODEC = Codec.FLOAT.xmap(FloatUniform::new, FloatUniform::value);
      }
   }

   public static record Vec2Uniform(Vector2fc value) implements UniformValue {
      public static final Codec<Vec2Uniform> CODEC;

      public Vec2Uniform {
         super();
      }

      public void writeTo(final Std140Builder builder) {
         builder.putVec2(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putVec2();
      }

      public Type type() {
         return UniformValue.Type.VEC2;
      }

      static {
         CODEC = ExtraCodecs.VECTOR2F.xmap(Vec2Uniform::new, Vec2Uniform::value);
      }
   }

   public static record Vec3Uniform(Vector3fc value) implements UniformValue {
      public static final Codec<Vec3Uniform> CODEC;

      public Vec3Uniform {
         super();
      }

      public void writeTo(final Std140Builder builder) {
         builder.putVec3(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putVec3();
      }

      public Type type() {
         return UniformValue.Type.VEC3;
      }

      static {
         CODEC = ExtraCodecs.VECTOR3F.xmap(Vec3Uniform::new, Vec3Uniform::value);
      }
   }

   public static record Vec4Uniform(Vector4fc value) implements UniformValue {
      public static final Codec<Vec4Uniform> CODEC;

      public Vec4Uniform {
         super();
      }

      public void writeTo(final Std140Builder builder) {
         builder.putVec4(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putVec4();
      }

      public Type type() {
         return UniformValue.Type.VEC4;
      }

      static {
         CODEC = ExtraCodecs.VECTOR4F.xmap(Vec4Uniform::new, Vec4Uniform::value);
      }
   }

   public static record Matrix4x4Uniform(Matrix4fc value) implements UniformValue {
      public static final Codec<Matrix4x4Uniform> CODEC;

      public Matrix4x4Uniform {
         super();
      }

      public void writeTo(final Std140Builder builder) {
         builder.putMat4f(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putMat4f();
      }

      public Type type() {
         return UniformValue.Type.MATRIX4X4;
      }

      static {
         CODEC = ExtraCodecs.MATRIX4F.xmap(Matrix4x4Uniform::new, Matrix4x4Uniform::value);
      }
   }
}
