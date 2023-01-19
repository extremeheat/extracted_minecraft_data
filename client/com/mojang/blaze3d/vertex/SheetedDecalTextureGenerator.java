package com.mojang.blaze3d.vertex;

import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SheetedDecalTextureGenerator extends DefaultedVertexConsumer {
   private final VertexConsumer delegate;
   private final Matrix4f cameraInversePose;
   private final Matrix3f normalInversePose;
   private final float textureScale;
   private float x;
   private float y;
   private float z;
   private int overlayU;
   private int overlayV;
   private int lightCoords;
   private float nx;
   private float ny;
   private float nz;

   public SheetedDecalTextureGenerator(VertexConsumer var1, Matrix4f var2, Matrix3f var3, float var4) {
      super();
      this.delegate = var1;
      this.cameraInversePose = new Matrix4f(var2).invert();
      this.normalInversePose = new Matrix3f(var3).invert();
      this.textureScale = var4;
      this.resetState();
   }

   private void resetState() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
      this.overlayU = 0;
      this.overlayV = 10;
      this.lightCoords = 15728880;
      this.nx = 0.0F;
      this.ny = 1.0F;
      this.nz = 0.0F;
   }

   @Override
   public void endVertex() {
      Vector3f var1 = this.normalInversePose.transform(new Vector3f(this.nx, this.ny, this.nz));
      Direction var2 = Direction.getNearest(var1.x(), var1.y(), var1.z());
      Vector4f var3 = this.cameraInversePose.transform(new Vector4f(this.x, this.y, this.z, 1.0F));
      var3.rotateY(3.1415927F);
      var3.rotateX(-1.5707964F);
      var3.rotate(var2.getRotation());
      float var4 = -var3.x() * this.textureScale;
      float var5 = -var3.y() * this.textureScale;
      this.delegate
         .vertex((double)this.x, (double)this.y, (double)this.z)
         .color(1.0F, 1.0F, 1.0F, 1.0F)
         .uv(var4, var5)
         .overlayCoords(this.overlayU, this.overlayV)
         .uv2(this.lightCoords)
         .normal(this.nx, this.ny, this.nz)
         .endVertex();
      this.resetState();
   }

   @Override
   public VertexConsumer vertex(double var1, double var3, double var5) {
      this.x = (float)var1;
      this.y = (float)var3;
      this.z = (float)var5;
      return this;
   }

   @Override
   public VertexConsumer color(int var1, int var2, int var3, int var4) {
      return this;
   }

   @Override
   public VertexConsumer uv(float var1, float var2) {
      return this;
   }

   @Override
   public VertexConsumer overlayCoords(int var1, int var2) {
      this.overlayU = var1;
      this.overlayV = var2;
      return this;
   }

   @Override
   public VertexConsumer uv2(int var1, int var2) {
      this.lightCoords = var1 | var2 << 16;
      return this;
   }

   @Override
   public VertexConsumer normal(float var1, float var2, float var3) {
      this.nx = var1;
      this.ny = var2;
      this.nz = var3;
      return this;
   }
}
