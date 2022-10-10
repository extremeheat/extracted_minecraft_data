package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class TileEntityBeaconRenderer extends TileEntityRenderer<TileEntityBeacon> {
   private static final ResourceLocation field_147523_b = new ResourceLocation("textures/entity/beacon_beam.png");

   public TileEntityBeaconRenderer() {
      super();
   }

   public void func_199341_a(TileEntityBeacon var1, double var2, double var4, double var6, float var8, int var9) {
      this.func_188206_a(var2, var4, var6, (double)var8, (double)var1.func_146002_i(), var1.func_174907_n(), var1.func_145831_w().func_82737_E());
   }

   private void func_188206_a(double var1, double var3, double var5, double var7, double var9, List<TileEntityBeacon.BeamSegment> var11, long var12) {
      GlStateManager.func_179092_a(516, 0.1F);
      this.func_147499_a(field_147523_b);
      if (var9 > 0.0D) {
         GlStateManager.func_179106_n();
         int var14 = 0;

         for(int var15 = 0; var15 < var11.size(); ++var15) {
            TileEntityBeacon.BeamSegment var16 = (TileEntityBeacon.BeamSegment)var11.get(var15);
            func_188204_a(var1, var3, var5, var7, var9, var12, var14, var16.func_177264_c(), var16.func_177263_b());
            var14 += var16.func_177264_c();
         }

         GlStateManager.func_179127_m();
      }

   }

   private static void func_188204_a(double var0, double var2, double var4, double var6, double var8, long var10, int var12, int var13, float[] var14) {
      func_188205_a(var0, var2, var4, var6, var8, var10, var12, var13, var14, 0.2D, 0.25D);
   }

   public static void func_188205_a(double var0, double var2, double var4, double var6, double var8, long var10, int var12, int var13, float[] var14, double var15, double var17) {
      int var19 = var12 + var13;
      GlStateManager.func_187421_b(3553, 10242, 10497);
      GlStateManager.func_187421_b(3553, 10243, 10497);
      GlStateManager.func_179140_f();
      GlStateManager.func_179129_p();
      GlStateManager.func_179084_k();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(var0 + 0.5D, var2, var4 + 0.5D);
      Tessellator var20 = Tessellator.func_178181_a();
      BufferBuilder var21 = var20.func_178180_c();
      double var22 = (double)Math.floorMod(var10, 40L) + var6;
      double var24 = var13 < 0 ? var22 : -var22;
      double var26 = MathHelper.func_181162_h(var24 * 0.2D - (double)MathHelper.func_76128_c(var24 * 0.1D));
      float var28 = var14[0];
      float var29 = var14[1];
      float var30 = var14[2];
      GlStateManager.func_179094_E();
      GlStateManager.func_212477_a(var22 * 2.25D - 45.0D, 0.0D, 1.0D, 0.0D);
      double var31 = 0.0D;
      double var37 = 0.0D;
      double var39 = -var15;
      double var41 = 0.0D;
      double var43 = 0.0D;
      double var45 = -var15;
      double var47 = 0.0D;
      double var49 = 1.0D;
      double var51 = -1.0D + var26;
      double var53 = (double)var13 * var8 * (0.5D / var15) + var51;
      var21.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var21.func_181662_b(0.0D, (double)var19, var15).func_187315_a(1.0D, var53).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(0.0D, (double)var12, var15).func_187315_a(1.0D, var51).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(var15, (double)var12, 0.0D).func_187315_a(0.0D, var51).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(var15, (double)var19, 0.0D).func_187315_a(0.0D, var53).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(0.0D, (double)var19, var45).func_187315_a(1.0D, var53).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(0.0D, (double)var12, var45).func_187315_a(1.0D, var51).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(var39, (double)var12, 0.0D).func_187315_a(0.0D, var51).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(var39, (double)var19, 0.0D).func_187315_a(0.0D, var53).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(var15, (double)var19, 0.0D).func_187315_a(1.0D, var53).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(var15, (double)var12, 0.0D).func_187315_a(1.0D, var51).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(0.0D, (double)var12, var45).func_187315_a(0.0D, var51).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(0.0D, (double)var19, var45).func_187315_a(0.0D, var53).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(var39, (double)var19, 0.0D).func_187315_a(1.0D, var53).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(var39, (double)var12, 0.0D).func_187315_a(1.0D, var51).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(0.0D, (double)var12, var15).func_187315_a(0.0D, var51).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var21.func_181662_b(0.0D, (double)var19, var15).func_187315_a(0.0D, var53).func_181666_a(var28, var29, var30, 1.0F).func_181675_d();
      var20.func_78381_a();
      GlStateManager.func_179121_F();
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179132_a(false);
      var31 = -var17;
      double var33 = -var17;
      var37 = -var17;
      var39 = -var17;
      var47 = 0.0D;
      var49 = 1.0D;
      var51 = -1.0D + var26;
      var53 = (double)var13 * var8 + var51;
      var21.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var21.func_181662_b(var31, (double)var19, var33).func_187315_a(1.0D, var53).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var31, (double)var12, var33).func_187315_a(1.0D, var51).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var17, (double)var12, var37).func_187315_a(0.0D, var51).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var17, (double)var19, var37).func_187315_a(0.0D, var53).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var17, (double)var19, var17).func_187315_a(1.0D, var53).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var17, (double)var12, var17).func_187315_a(1.0D, var51).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var39, (double)var12, var17).func_187315_a(0.0D, var51).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var39, (double)var19, var17).func_187315_a(0.0D, var53).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var17, (double)var19, var37).func_187315_a(1.0D, var53).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var17, (double)var12, var37).func_187315_a(1.0D, var51).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var17, (double)var12, var17).func_187315_a(0.0D, var51).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var17, (double)var19, var17).func_187315_a(0.0D, var53).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var39, (double)var19, var17).func_187315_a(1.0D, var53).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var39, (double)var12, var17).func_187315_a(1.0D, var51).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var31, (double)var12, var33).func_187315_a(0.0D, var51).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var21.func_181662_b(var31, (double)var19, var33).func_187315_a(0.0D, var53).func_181666_a(var28, var29, var30, 0.125F).func_181675_d();
      var20.func_78381_a();
      GlStateManager.func_179121_F();
      GlStateManager.func_179145_e();
      GlStateManager.func_179098_w();
      GlStateManager.func_179132_a(true);
   }

   public boolean func_188185_a(TileEntityBeacon var1) {
      return true;
   }
}
