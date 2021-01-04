package net.minecraft.realms;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;

public class RealmsBufferBuilder {
   private BufferBuilder b;

   public RealmsBufferBuilder(BufferBuilder var1) {
      super();
      this.b = var1;
   }

   public RealmsBufferBuilder from(BufferBuilder var1) {
      this.b = var1;
      return this;
   }

   public void sortQuads(float var1, float var2, float var3) {
      this.b.sortQuads(var1, var2, var3);
   }

   public void fixupQuadColor(int var1) {
      this.b.fixupQuadColor(var1);
   }

   public ByteBuffer getBuffer() {
      return this.b.getBuffer();
   }

   public void postNormal(float var1, float var2, float var3) {
      this.b.postNormal(var1, var2, var3);
   }

   public int getDrawMode() {
      return this.b.getDrawMode();
   }

   public void offset(double var1, double var3, double var5) {
      this.b.offset(var1, var3, var5);
   }

   public void restoreState(BufferBuilder.State var1) {
      this.b.restoreState(var1);
   }

   public void endVertex() {
      this.b.endVertex();
   }

   public RealmsBufferBuilder normal(float var1, float var2, float var3) {
      return this.from(this.b.normal(var1, var2, var3));
   }

   public void end() {
      this.b.end();
   }

   public void begin(int var1, VertexFormat var2) {
      this.b.begin(var1, var2);
   }

   public RealmsBufferBuilder color(int var1, int var2, int var3, int var4) {
      return this.from(this.b.color(var1, var2, var3, var4));
   }

   public void faceTex2(int var1, int var2, int var3, int var4) {
      this.b.faceTex2(var1, var2, var3, var4);
   }

   public void postProcessFacePosition(double var1, double var3, double var5) {
      this.b.postProcessFacePosition(var1, var3, var5);
   }

   public void fixupVertexColor(float var1, float var2, float var3, int var4) {
      this.b.fixupVertexColor(var1, var2, var3, var4);
   }

   public RealmsBufferBuilder color(float var1, float var2, float var3, float var4) {
      return this.from(this.b.color(var1, var2, var3, var4));
   }

   public RealmsVertexFormat getVertexFormat() {
      return new RealmsVertexFormat(this.b.getVertexFormat());
   }

   public void faceTint(float var1, float var2, float var3, int var4) {
      this.b.faceTint(var1, var2, var3, var4);
   }

   public RealmsBufferBuilder tex2(int var1, int var2) {
      return this.from(this.b.uv2(var1, var2));
   }

   public void putBulkData(int[] var1) {
      this.b.putBulkData(var1);
   }

   public RealmsBufferBuilder tex(double var1, double var3) {
      return this.from(this.b.uv(var1, var3));
   }

   public int getVertexCount() {
      return this.b.getVertexCount();
   }

   public void clear() {
      this.b.clear();
   }

   public RealmsBufferBuilder vertex(double var1, double var3, double var5) {
      return this.from(this.b.vertex(var1, var3, var5));
   }

   public void fixupQuadColor(float var1, float var2, float var3) {
      this.b.fixupQuadColor(var1, var2, var3);
   }

   public void noColor() {
      this.b.noColor();
   }
}
