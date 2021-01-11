package net.minecraft.realms;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RealmsBufferBuilder {
   private WorldRenderer b;

   public RealmsBufferBuilder(WorldRenderer var1) {
      super();
      this.b = var1;
   }

   public RealmsBufferBuilder from(WorldRenderer var1) {
      this.b = var1;
      return this;
   }

   public void sortQuads(float var1, float var2, float var3) {
      this.b.func_181674_a(var1, var2, var3);
   }

   public void fixupQuadColor(int var1) {
      this.b.func_178968_d(var1);
   }

   public ByteBuffer getBuffer() {
      return this.b.func_178966_f();
   }

   public void postNormal(float var1, float var2, float var3) {
      this.b.func_178975_e(var1, var2, var3);
   }

   public int getDrawMode() {
      return this.b.func_178979_i();
   }

   public void offset(double var1, double var3, double var5) {
      this.b.func_178969_c(var1, var3, var5);
   }

   public void restoreState(WorldRenderer.State var1) {
      this.b.func_178993_a(var1);
   }

   public void endVertex() {
      this.b.func_181675_d();
   }

   public RealmsBufferBuilder normal(float var1, float var2, float var3) {
      return this.from(this.b.func_181663_c(var1, var2, var3));
   }

   public void end() {
      this.b.func_178977_d();
   }

   public void begin(int var1, VertexFormat var2) {
      this.b.func_181668_a(var1, var2);
   }

   public RealmsBufferBuilder color(int var1, int var2, int var3, int var4) {
      return this.from(this.b.func_181669_b(var1, var2, var3, var4));
   }

   public void faceTex2(int var1, int var2, int var3, int var4) {
      this.b.func_178962_a(var1, var2, var3, var4);
   }

   public void postProcessFacePosition(double var1, double var3, double var5) {
      this.b.func_178987_a(var1, var3, var5);
   }

   public void fixupVertexColor(float var1, float var2, float var3, int var4) {
      this.b.func_178994_b(var1, var2, var3, var4);
   }

   public RealmsBufferBuilder color(float var1, float var2, float var3, float var4) {
      return this.from(this.b.func_181666_a(var1, var2, var3, var4));
   }

   public RealmsVertexFormat getVertexFormat() {
      return new RealmsVertexFormat(this.b.func_178973_g());
   }

   public void faceTint(float var1, float var2, float var3, int var4) {
      this.b.func_178978_a(var1, var2, var3, var4);
   }

   public RealmsBufferBuilder tex2(int var1, int var2) {
      return this.from(this.b.func_181671_a(var1, var2));
   }

   public void putBulkData(int[] var1) {
      this.b.func_178981_a(var1);
   }

   public RealmsBufferBuilder tex(double var1, double var3) {
      return this.from(this.b.func_181673_a(var1, var3));
   }

   public int getVertexCount() {
      return this.b.func_178989_h();
   }

   public void clear() {
      this.b.func_178965_a();
   }

   public RealmsBufferBuilder vertex(double var1, double var3, double var5) {
      return this.from(this.b.func_181662_b(var1, var3, var5));
   }

   public void fixupQuadColor(float var1, float var2, float var3) {
      this.b.func_178990_f(var1, var2, var3);
   }

   public void noColor() {
      this.b.func_78914_f();
   }
}
