package com.mojang.blaze3d.vertex;

import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
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

   public SheetedDecalTextureGenerator(VertexConsumer var1, PoseStack.Pose var2, float var3) {
      super();
      this.delegate = var1;
      this.cameraInversePose = (new Matrix4f(var2.pose())).invert();
      this.normalInversePose = (new Matrix3f(var2.normal())).invert();
      this.textureScale = var3;
   }

   public VertexConsumer addVertex(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.delegate.addVertex(var1, var2, var3);
      return this;
   }

   public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
      this.delegate.setColor(-1);
      return this;
   }

   public VertexConsumer setUv(float var1, float var2) {
      return this;
   }

   public VertexConsumer setUv1(int var1, int var2) {
      this.delegate.setUv1(var1, var2);
      return this;
   }

   public VertexConsumer setUv2(int var1, int var2) {
      this.delegate.setUv2(var1, var2);
      return this;
   }

   public VertexConsumer setNormal(float var1, float var2, float var3) {
      this.delegate.setNormal(var1, var2, var3);
      Vector3f var4 = this.normalInversePose.transform(var1, var2, var3, this.normal);
      Direction var5 = Direction.getNearest(var4.x(), var4.y(), var4.z());
      Vector3f var6 = this.cameraInversePose.transformPosition(this.x, this.y, this.z, this.worldPos);
      var6.rotateY(3.1415927F);
      var6.rotateX(-1.5707964F);
      var6.rotate(var5.getRotation());
      this.delegate.setUv(-var6.x() * this.textureScale, -var6.y() * this.textureScale);
      return this;
   }
}
