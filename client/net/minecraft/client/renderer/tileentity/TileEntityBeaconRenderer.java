package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer<TileEntityBeacon> {
   private static final ResourceLocation field_147523_b = new ResourceLocation("textures/entity/beacon_beam.png");

   public TileEntityBeaconRenderer() {
      super();
   }

   public void func_180535_a(TileEntityBeacon var1, double var2, double var4, double var6, float var8, int var9) {
      float var10 = var1.func_146002_i();
      GlStateManager.func_179092_a(516, 0.1F);
      if (var10 > 0.0F) {
         Tessellator var11 = Tessellator.func_178181_a();
         WorldRenderer var12 = var11.func_178180_c();
         GlStateManager.func_179106_n();
         List var13 = var1.func_174907_n();
         int var14 = 0;

         for(int var15 = 0; var15 < var13.size(); ++var15) {
            TileEntityBeacon.BeamSegment var16 = (TileEntityBeacon.BeamSegment)var13.get(var15);
            int var17 = var14 + var16.func_177264_c();
            this.func_147499_a(field_147523_b);
            GL11.glTexParameterf(3553, 10242, 10497.0F);
            GL11.glTexParameterf(3553, 10243, 10497.0F);
            GlStateManager.func_179140_f();
            GlStateManager.func_179129_p();
            GlStateManager.func_179084_k();
            GlStateManager.func_179132_a(true);
            GlStateManager.func_179120_a(770, 1, 1, 0);
            double var18 = (double)var1.func_145831_w().func_82737_E() + (double)var8;
            double var20 = MathHelper.func_181162_h(-var18 * 0.2D - (double)MathHelper.func_76128_c(-var18 * 0.1D));
            float var22 = var16.func_177263_b()[0];
            float var23 = var16.func_177263_b()[1];
            float var24 = var16.func_177263_b()[2];
            double var25 = var18 * 0.025D * -1.5D;
            double var27 = 0.2D;
            double var29 = 0.5D + Math.cos(var25 + 2.356194490192345D) * 0.2D;
            double var31 = 0.5D + Math.sin(var25 + 2.356194490192345D) * 0.2D;
            double var33 = 0.5D + Math.cos(var25 + 0.7853981633974483D) * 0.2D;
            double var35 = 0.5D + Math.sin(var25 + 0.7853981633974483D) * 0.2D;
            double var37 = 0.5D + Math.cos(var25 + 3.9269908169872414D) * 0.2D;
            double var39 = 0.5D + Math.sin(var25 + 3.9269908169872414D) * 0.2D;
            double var41 = 0.5D + Math.cos(var25 + 5.497787143782138D) * 0.2D;
            double var43 = 0.5D + Math.sin(var25 + 5.497787143782138D) * 0.2D;
            double var45 = 0.0D;
            double var47 = 1.0D;
            double var49 = -1.0D + var20;
            double var51 = (double)((float)var16.func_177264_c() * var10) * 2.5D + var49;
            var12.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var12.func_181662_b(var2 + var29, var4 + (double)var17, var6 + var31).func_181673_a(1.0D, var51).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var29, var4 + (double)var14, var6 + var31).func_181673_a(1.0D, var49).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var33, var4 + (double)var14, var6 + var35).func_181673_a(0.0D, var49).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var33, var4 + (double)var17, var6 + var35).func_181673_a(0.0D, var51).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var41, var4 + (double)var17, var6 + var43).func_181673_a(1.0D, var51).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var41, var4 + (double)var14, var6 + var43).func_181673_a(1.0D, var49).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var37, var4 + (double)var14, var6 + var39).func_181673_a(0.0D, var49).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var37, var4 + (double)var17, var6 + var39).func_181673_a(0.0D, var51).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var33, var4 + (double)var17, var6 + var35).func_181673_a(1.0D, var51).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var33, var4 + (double)var14, var6 + var35).func_181673_a(1.0D, var49).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var41, var4 + (double)var14, var6 + var43).func_181673_a(0.0D, var49).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var41, var4 + (double)var17, var6 + var43).func_181673_a(0.0D, var51).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var37, var4 + (double)var17, var6 + var39).func_181673_a(1.0D, var51).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var37, var4 + (double)var14, var6 + var39).func_181673_a(1.0D, var49).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var29, var4 + (double)var14, var6 + var31).func_181673_a(0.0D, var49).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var12.func_181662_b(var2 + var29, var4 + (double)var17, var6 + var31).func_181673_a(0.0D, var51).func_181666_a(var22, var23, var24, 1.0F).func_181675_d();
            var11.func_78381_a();
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            GlStateManager.func_179132_a(false);
            var25 = 0.2D;
            var27 = 0.2D;
            var29 = 0.8D;
            var31 = 0.2D;
            var33 = 0.2D;
            var35 = 0.8D;
            var37 = 0.8D;
            var39 = 0.8D;
            var41 = 0.0D;
            var43 = 1.0D;
            var45 = -1.0D + var20;
            var47 = (double)((float)var16.func_177264_c() * var10) + var45;
            var12.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            var12.func_181662_b(var2 + 0.2D, var4 + (double)var17, var6 + 0.2D).func_181673_a(1.0D, var47).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.2D, var4 + (double)var14, var6 + 0.2D).func_181673_a(1.0D, var45).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.8D, var4 + (double)var14, var6 + 0.2D).func_181673_a(0.0D, var45).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.8D, var4 + (double)var17, var6 + 0.2D).func_181673_a(0.0D, var47).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.8D, var4 + (double)var17, var6 + 0.8D).func_181673_a(1.0D, var47).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.8D, var4 + (double)var14, var6 + 0.8D).func_181673_a(1.0D, var45).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.2D, var4 + (double)var14, var6 + 0.8D).func_181673_a(0.0D, var45).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.2D, var4 + (double)var17, var6 + 0.8D).func_181673_a(0.0D, var47).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.8D, var4 + (double)var17, var6 + 0.2D).func_181673_a(1.0D, var47).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.8D, var4 + (double)var14, var6 + 0.2D).func_181673_a(1.0D, var45).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.8D, var4 + (double)var14, var6 + 0.8D).func_181673_a(0.0D, var45).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.8D, var4 + (double)var17, var6 + 0.8D).func_181673_a(0.0D, var47).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.2D, var4 + (double)var17, var6 + 0.8D).func_181673_a(1.0D, var47).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.2D, var4 + (double)var14, var6 + 0.8D).func_181673_a(1.0D, var45).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.2D, var4 + (double)var14, var6 + 0.2D).func_181673_a(0.0D, var45).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var12.func_181662_b(var2 + 0.2D, var4 + (double)var17, var6 + 0.2D).func_181673_a(0.0D, var47).func_181666_a(var22, var23, var24, 0.125F).func_181675_d();
            var11.func_78381_a();
            GlStateManager.func_179145_e();
            GlStateManager.func_179098_w();
            GlStateManager.func_179132_a(true);
            var14 = var17;
         }

         GlStateManager.func_179127_m();
      }

   }

   public boolean func_181055_a() {
      return true;
   }
}
