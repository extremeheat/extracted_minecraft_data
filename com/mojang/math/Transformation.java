package com.mojang.math;

import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.apache.commons.lang3.tuple.Triple;

public final class Transformation {
   private final Matrix4f matrix;
   private boolean decomposed;
   @Nullable
   private Vector3f translation;
   @Nullable
   private Quaternion leftRotation;
   @Nullable
   private Vector3f scale;
   @Nullable
   private Quaternion rightRotation;
   private static final Transformation IDENTITY = (Transformation)Util.make(() -> {
      Matrix4f var0 = new Matrix4f();
      var0.setIdentity();
      Transformation var1 = new Transformation(var0);
      var1.getLeftRotation();
      return var1;
   });

   public Transformation(@Nullable Matrix4f var1) {
      if (var1 == null) {
         this.matrix = IDENTITY.matrix;
      } else {
         this.matrix = var1;
      }

   }

   public Transformation(@Nullable Vector3f var1, @Nullable Quaternion var2, @Nullable Vector3f var3, @Nullable Quaternion var4) {
      this.matrix = compose(var1, var2, var3, var4);
      this.translation = var1 != null ? var1 : new Vector3f();
      this.leftRotation = var2 != null ? var2 : Quaternion.ONE.copy();
      this.scale = var3 != null ? var3 : new Vector3f(1.0F, 1.0F, 1.0F);
      this.rightRotation = var4 != null ? var4 : Quaternion.ONE.copy();
      this.decomposed = true;
   }

   public static Transformation identity() {
      return IDENTITY;
   }

   public Transformation compose(Transformation var1) {
      Matrix4f var2 = this.getMatrix();
      var2.multiply(var1.getMatrix());
      return new Transformation(var2);
   }

   @Nullable
   public Transformation inverse() {
      if (this == IDENTITY) {
         return this;
      } else {
         Matrix4f var1 = this.getMatrix();
         return var1.invert() ? new Transformation(var1) : null;
      }
   }

   private void ensureDecomposed() {
      if (!this.decomposed) {
         Pair var1 = toAffine(this.matrix);
         Triple var2 = ((Matrix3f)var1.getFirst()).svdDecompose();
         this.translation = (Vector3f)var1.getSecond();
         this.leftRotation = (Quaternion)var2.getLeft();
         this.scale = (Vector3f)var2.getMiddle();
         this.rightRotation = (Quaternion)var2.getRight();
         this.decomposed = true;
      }

   }

   private static Matrix4f compose(@Nullable Vector3f var0, @Nullable Quaternion var1, @Nullable Vector3f var2, @Nullable Quaternion var3) {
      Matrix4f var4 = new Matrix4f();
      var4.setIdentity();
      if (var1 != null) {
         var4.multiply(new Matrix4f(var1));
      }

      if (var2 != null) {
         var4.multiply(Matrix4f.createScaleMatrix(var2.x(), var2.y(), var2.z()));
      }

      if (var3 != null) {
         var4.multiply(new Matrix4f(var3));
      }

      if (var0 != null) {
         var4.m03 = var0.x();
         var4.m13 = var0.y();
         var4.m32 = var0.z();
      }

      return var4;
   }

   public static Pair toAffine(Matrix4f var0) {
      var0.multiply(1.0F / var0.m33);
      Vector3f var1 = new Vector3f(var0.m03, var0.m13, var0.m23);
      Matrix3f var2 = new Matrix3f(var0);
      return Pair.of(var2, var1);
   }

   public Matrix4f getMatrix() {
      return this.matrix.copy();
   }

   public Quaternion getLeftRotation() {
      this.ensureDecomposed();
      return this.leftRotation.copy();
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
}
