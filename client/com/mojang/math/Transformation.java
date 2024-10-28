package com.mojang.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class Transformation {
   private final Matrix4f matrix;
   public static final Codec<Transformation> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.VECTOR3F.fieldOf("translation").forGetter((var0x) -> {
         return var0x.translation;
      }), ExtraCodecs.QUATERNIONF.fieldOf("left_rotation").forGetter((var0x) -> {
         return var0x.leftRotation;
      }), ExtraCodecs.VECTOR3F.fieldOf("scale").forGetter((var0x) -> {
         return var0x.scale;
      }), ExtraCodecs.QUATERNIONF.fieldOf("right_rotation").forGetter((var0x) -> {
         return var0x.rightRotation;
      })).apply(var0, Transformation::new);
   });
   public static final Codec<Transformation> EXTENDED_CODEC;
   private boolean decomposed;
   @Nullable
   private Vector3f translation;
   @Nullable
   private Quaternionf leftRotation;
   @Nullable
   private Vector3f scale;
   @Nullable
   private Quaternionf rightRotation;
   private static final Transformation IDENTITY;

   public Transformation(@Nullable Matrix4f var1) {
      super();
      if (var1 == null) {
         this.matrix = new Matrix4f();
      } else {
         this.matrix = var1;
      }

   }

   public Transformation(@Nullable Vector3f var1, @Nullable Quaternionf var2, @Nullable Vector3f var3, @Nullable Quaternionf var4) {
      super();
      this.matrix = compose(var1, var2, var3, var4);
      this.translation = var1 != null ? var1 : new Vector3f();
      this.leftRotation = var2 != null ? var2 : new Quaternionf();
      this.scale = var3 != null ? var3 : new Vector3f(1.0F, 1.0F, 1.0F);
      this.rightRotation = var4 != null ? var4 : new Quaternionf();
      this.decomposed = true;
   }

   public static Transformation identity() {
      return IDENTITY;
   }

   public Transformation compose(Transformation var1) {
      Matrix4f var2 = this.getMatrix();
      var2.mul(var1.getMatrix());
      return new Transformation(var2);
   }

   @Nullable
   public Transformation inverse() {
      if (this == IDENTITY) {
         return this;
      } else {
         Matrix4f var1 = this.getMatrix().invert();
         return var1.isFinite() ? new Transformation(var1) : null;
      }
   }

   private void ensureDecomposed() {
      if (!this.decomposed) {
         float var1 = 1.0F / this.matrix.m33();
         Triple var2 = MatrixUtil.svdDecompose((new Matrix3f(this.matrix)).scale(var1));
         this.translation = this.matrix.getTranslation(new Vector3f()).mul(var1);
         this.leftRotation = new Quaternionf((Quaternionfc)var2.getLeft());
         this.scale = new Vector3f((Vector3fc)var2.getMiddle());
         this.rightRotation = new Quaternionf((Quaternionfc)var2.getRight());
         this.decomposed = true;
      }

   }

   private static Matrix4f compose(@Nullable Vector3f var0, @Nullable Quaternionf var1, @Nullable Vector3f var2, @Nullable Quaternionf var3) {
      Matrix4f var4 = new Matrix4f();
      if (var0 != null) {
         var4.translation(var0);
      }

      if (var1 != null) {
         var4.rotate(var1);
      }

      if (var2 != null) {
         var4.scale(var2);
      }

      if (var3 != null) {
         var4.rotate(var3);
      }

      return var4;
   }

   public Matrix4f getMatrix() {
      return new Matrix4f(this.matrix);
   }

   public Vector3f getTranslation() {
      this.ensureDecomposed();
      return new Vector3f(this.translation);
   }

   public Quaternionf getLeftRotation() {
      this.ensureDecomposed();
      return new Quaternionf(this.leftRotation);
   }

   public Vector3f getScale() {
      this.ensureDecomposed();
      return new Vector3f(this.scale);
   }

   public Quaternionf getRightRotation() {
      this.ensureDecomposed();
      return new Quaternionf(this.rightRotation);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Transformation var2 = (Transformation)var1;
         return Objects.equals(this.matrix, var2.matrix);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.matrix});
   }

   public Transformation slerp(Transformation var1, float var2) {
      Vector3f var3 = this.getTranslation();
      Quaternionf var4 = this.getLeftRotation();
      Vector3f var5 = this.getScale();
      Quaternionf var6 = this.getRightRotation();
      var3.lerp(var1.getTranslation(), var2);
      var4.slerp(var1.getLeftRotation(), var2);
      var5.lerp(var1.getScale(), var2);
      var6.slerp(var1.getRightRotation(), var2);
      return new Transformation(var3, var4, var5, var6);
   }

   static {
      EXTENDED_CODEC = Codec.withAlternative(CODEC, ExtraCodecs.MATRIX4F.xmap(Transformation::new, Transformation::getMatrix));
      IDENTITY = (Transformation)Util.make(() -> {
         Transformation var0 = new Transformation(new Matrix4f());
         var0.translation = new Vector3f();
         var0.leftRotation = new Quaternionf();
         var0.scale = new Vector3f(1.0F, 1.0F, 1.0F);
         var0.rightRotation = new Quaternionf();
         var0.decomposed = true;
         return var0;
      });
   }
}
