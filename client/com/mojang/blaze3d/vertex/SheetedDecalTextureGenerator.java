package com.mojang.blaze3d.vertex;

import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public class SheetedDecalTextureGenerator implements VertexConsumer {
   private final VertexConsumer delegate;
   private final Matrix4f cameraInversePose;
   private final Matrix3f normalInversePose;
   private final float textureScale;
   private final Vector3f worldPos = new Vector3f();
   private final Vector3f normal = new Vector3f();
   private float x;
   private float y;
   private float z;

   public SheetedDecalTextureGenerator(final VertexConsumer delegate, final PoseStack.Pose cameraPose, final float textureScale) {
      super();
      this.delegate = delegate;
      this.cameraInversePose = (new Matrix4f(cameraPose.pose())).invert();
      this.normalInversePose = (new Matrix3f(cameraPose.normal())).invert();
      this.textureScale = textureScale;
   }

   public VertexConsumer addVertex(final float x, final float y, final float z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.delegate.addVertex(x, y, z);
      return this;
   }

   public VertexConsumer setColor(final int r, final int g, final int b, final int a) {
      this.delegate.setColor(-1);
      return this;
   }

   public VertexConsumer setColor(final int color) {
      this.delegate.setColor(-1);
      return this;
   }

   public VertexConsumer setUv(final float u, final float v) {
      return this;
   }

   public VertexConsumer setUv1(final int u, final int v) {
      this.delegate.setUv1(u, v);
      return this;
   }

   public VertexConsumer setUv2(final int u, final int v) {
      this.delegate.setUv2(u, v);
      return this;
   }

   public VertexConsumer setUv3(final float u, final float v) {
      this.delegate.setUv3(u, v);
      return this;
   }

   public VertexConsumer setNormal(final float x, final float y, final float z) {
      this.delegate.setNormal(x, y, z);
      Vector3f normal = this.normalInversePose.transform(x, y, z, this.normal);
      Direction direction = Direction.getApproximateNearest(normal.x(), normal.y(), normal.z());
      Vector3f worldPos = this.cameraInversePose.transformPosition(this.x, this.y, this.z, this.worldPos);
      worldPos.rotateY(3.1415927F);
      worldPos.rotateX(-1.5707964F);
      worldPos.rotate(direction.getRotation());
      this.delegate.setUv(-worldPos.x() * this.textureScale, -worldPos.y() * this.textureScale);
      return this;
   }

   public static void setSheetedDecalUv(final Vector3f position, final Vector3f normal, final Matrix4fc cameraInversePose, final Matrix3fc normalInversePose, final float textureScale, final VertexConsumer delegate) {
      Vector3f transformedNormal = normalInversePose.transform(normal.x(), normal.y(), normal.z(), new Vector3f());
      Direction direction = Direction.getApproximateNearest(transformedNormal.x(), transformedNormal.y(), transformedNormal.z());
      Vector3f worldPos = cameraInversePose.transformPosition(position.x, position.y, position.z, new Vector3f());
      worldPos.rotateY(3.1415927F);
      worldPos.rotateX(-1.5707964F);
      worldPos.rotate(direction.getRotation());
      delegate.setUv3(-worldPos.x() * textureScale, -worldPos.y() * textureScale);
   }

   public VertexConsumer setLineWidth(final float width) {
      this.delegate.setLineWidth(width);
      return this;
   }
}
