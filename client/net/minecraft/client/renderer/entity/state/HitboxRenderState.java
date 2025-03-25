package net.minecraft.client.renderer.entity.state;

public record HitboxRenderState(double x0, double y0, double z0, double x1, double y1, double z1, float offsetX, float offsetY, float offsetZ, float red, float green, float blue) {
   public HitboxRenderState(double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15) {
      this(var1, var3, var5, var7, var9, var11, 0.0F, 0.0F, 0.0F, var13, var14, var15);
   }

   public HitboxRenderState(double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15, float var16, float var17, float var18) {
      super();
      this.x0 = var1;
      this.y0 = var3;
      this.z0 = var5;
      this.x1 = var7;
      this.y1 = var9;
      this.z1 = var11;
      this.offsetX = var13;
      this.offsetY = var14;
      this.offsetZ = var15;
      this.red = var16;
      this.green = var17;
      this.blue = var18;
   }
}
