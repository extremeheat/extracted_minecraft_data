package com.mojang.blaze3d.vertex;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.core.Direction;

public class SheetedDecalTextureGenerator extends DefaultedVertexConsumer {
   private final VertexConsumer delegate;
   private final Matrix4f cameraInversePose;
   private final Matrix3f normalInversePose;
   // $FF: renamed from: x float
   private float field_169;
   // $FF: renamed from: y float
   private float field_170;
   // $FF: renamed from: z float
   private float field_171;
   private int overlayU;
   private int overlayV;
   private int lightCoords;
   // $FF: renamed from: nx float
   private float field_172;
   // $FF: renamed from: ny float
   private float field_173;
   // $FF: renamed from: nz float
   private float field_174;

   public SheetedDecalTextureGenerator(VertexConsumer var1, Matrix4f var2, Matrix3f var3) {
      super();
      this.delegate = var1;
      this.cameraInversePose = var2.copy();
      this.cameraInversePose.invert();
      this.normalInversePose = var3.copy();
      this.normalInversePose.invert();
      this.resetState();
   }

   private void resetState() {
      this.field_169 = 0.0F;
      this.field_170 = 0.0F;
      this.field_171 = 0.0F;
      this.overlayU = 0;
      this.overlayV = 10;
      this.lightCoords = 15728880;
      this.field_172 = 0.0F;
      this.field_173 = 1.0F;
      this.field_174 = 0.0F;
   }

   public void endVertex() {
      Vector3f var1 = new Vector3f(this.field_172, this.field_173, this.field_174);
      var1.transform(this.normalInversePose);
      Direction var2 = Direction.getNearest(var1.method_82(), var1.method_83(), var1.method_84());
      Vector4f var3 = new Vector4f(this.field_169, this.field_170, this.field_171, 1.0F);
      var3.transform(this.cameraInversePose);
      var3.transform(Vector3f.field_292.rotationDegrees(180.0F));
      var3.transform(Vector3f.field_290.rotationDegrees(-90.0F));
      var3.transform(var2.getRotation());
      float var4 = -var3.method_66();
      float var5 = -var3.method_67();
      this.delegate.vertex((double)this.field_169, (double)this.field_170, (double)this.field_171).color(1.0F, 1.0F, 1.0F, 1.0F).method_7(var4, var5).overlayCoords(this.overlayU, this.overlayV).uv2(this.lightCoords).normal(this.field_172, this.field_173, this.field_174).endVertex();
      this.resetState();
   }

   public VertexConsumer vertex(double var1, double var3, double var5) {
      this.field_169 = (float)var1;
      this.field_170 = (float)var3;
      this.field_171 = (float)var5;
      return this;
   }

   public VertexConsumer color(int var1, int var2, int var3, int var4) {
      return this;
   }

   // $FF: renamed from: uv (float, float) com.mojang.blaze3d.vertex.VertexConsumer
   public VertexConsumer method_7(float var1, float var2) {
      return this;
   }

   public VertexConsumer overlayCoords(int var1, int var2) {
      this.overlayU = var1;
      this.overlayV = var2;
      return this;
   }

   public VertexConsumer uv2(int var1, int var2) {
      this.lightCoords = var1 | var2 << 16;
      return this;
   }

   public VertexConsumer normal(float var1, float var2, float var3) {
      this.field_172 = var1;
      this.field_173 = var2;
      this.field_174 = var3;
      return this;
   }
}
