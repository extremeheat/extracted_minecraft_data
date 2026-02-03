package com.mojang.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public final class Transformation {
   private final Matrix4fc matrix;
   public static final Codec<Transformation> CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.VECTOR3F.fieldOf("translation").forGetter((e) -> e.translation), ExtraCodecs.QUATERNIONF.fieldOf("left_rotation").forGetter((e) -> e.leftRotation), ExtraCodecs.VECTOR3F.fieldOf("scale").forGetter((e) -> e.scale), ExtraCodecs.QUATERNIONF.fieldOf("right_rotation").forGetter((e) -> e.rightRotation)).apply(i, Transformation::new));
   public static final Codec<Transformation> EXTENDED_CODEC;
   private boolean decomposed;
   private @Nullable Vector3fc translation;
   private @Nullable Quaternionfc leftRotation;
   private @Nullable Vector3fc scale;
   private @Nullable Quaternionfc rightRotation;
   private static final Transformation IDENTITY;

   public Transformation(final @Nullable Matrix4fc matrix) {
      super();
      if (matrix == null) {
         this.matrix = new Matrix4f();
      } else {
         this.matrix = matrix;
      }

   }

   public Transformation(final @Nullable Vector3fc translation, final @Nullable Quaternionfc leftRotation, final @Nullable Vector3fc scale, final @Nullable Quaternionfc rightRotation) {
      super();
      this.matrix = compose(translation, leftRotation, scale, rightRotation);
      this.translation = (Vector3fc)(translation != null ? translation : new Vector3f());
      this.leftRotation = (Quaternionfc)(leftRotation != null ? leftRotation : new Quaternionf());
      this.scale = (Vector3fc)(scale != null ? scale : new Vector3f(1.0F, 1.0F, 1.0F));
      this.rightRotation = (Quaternionfc)(rightRotation != null ? rightRotation : new Quaternionf());
      this.decomposed = true;
   }

   public static Transformation identity() {
      return IDENTITY;
   }

   public Transformation compose(final Transformation that) {
      Matrix4f matrix = this.getMatrixCopy();
      matrix.mul(that.getMatrix());
      return new Transformation(matrix);
   }

   public @Nullable Transformation inverse() {
      if (this == IDENTITY) {
         return this;
      } else {
         Matrix4f matrix = this.getMatrixCopy().invertAffine();
         return matrix.isFinite() ? new Transformation(matrix) : null;
      }
   }

   private void ensureDecomposed() {
      if (!this.decomposed) {
         float scaleFactor = 1.0F / this.matrix.m33();
         Triple<Quaternionf, Vector3f, Quaternionf> triple = MatrixUtil.svdDecompose((new Matrix3f(this.matrix)).scale(scaleFactor));
         this.translation = this.matrix.getTranslation(new Vector3f()).mul(scaleFactor);
         this.leftRotation = new Quaternionf((Quaternionfc)triple.getLeft());
         this.scale = new Vector3f((Vector3fc)triple.getMiddle());
         this.rightRotation = new Quaternionf((Quaternionfc)triple.getRight());
         this.decomposed = true;
      }

   }

   private static Matrix4f compose(final @Nullable Vector3fc translation, final @Nullable Quaternionfc leftRotation, final @Nullable Vector3fc scale, final @Nullable Quaternionfc rightRotation) {
      Matrix4f result = new Matrix4f();
      if (translation != null) {
         result.translation(translation);
      }

      if (leftRotation != null) {
         result.rotate(leftRotation);
      }

      if (scale != null) {
         result.scale(scale);
      }

      if (rightRotation != null) {
         result.rotate(rightRotation);
      }

      return result;
   }

   public Matrix4fc getMatrix() {
      return this.matrix;
   }

   public Matrix4f getMatrixCopy() {
      return new Matrix4f(this.matrix);
   }

   public Vector3fc getTranslation() {
      this.ensureDecomposed();
      return this.translation;
   }

   public Quaternionfc getLeftRotation() {
      this.ensureDecomposed();
      return this.leftRotation;
   }

   public Vector3fc getScale() {
      this.ensureDecomposed();
      return this.scale;
   }

   public Quaternionfc getRightRotation() {
      this.ensureDecomposed();
      return this.rightRotation;
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Transformation that = (Transformation)o;
         return Objects.equals(this.matrix, that.matrix);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.matrix});
   }

   public Transformation slerp(final Transformation that, final float progress) {
      return new Transformation(this.getTranslation().lerp(that.getTranslation(), progress, new Vector3f()), this.getLeftRotation().slerp(that.getLeftRotation(), progress, new Quaternionf()), this.getScale().lerp(that.getScale(), progress, new Vector3f()), this.getRightRotation().slerp(that.getRightRotation(), progress, new Quaternionf()));
   }

   static {
      EXTENDED_CODEC = Codec.withAlternative(CODEC, ExtraCodecs.MATRIX4F.xmap(Transformation::new, Transformation::getMatrix));
      IDENTITY = (Transformation)Util.make(() -> {
         Transformation identity = new Transformation(new Matrix4f());
         identity.translation = new Vector3f();
         identity.leftRotation = new Quaternionf();
         identity.scale = new Vector3f(1.0F, 1.0F, 1.0F);
         identity.rightRotation = new Quaternionf();
         identity.decomposed = true;
         return identity;
      });
   }
}
