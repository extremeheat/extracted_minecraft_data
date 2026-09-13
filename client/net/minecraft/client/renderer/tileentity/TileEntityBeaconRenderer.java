package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer {
   private static final ResourceLocation field_147523_b = new ResourceLocation("textures/entity/beacon_beam.png");

   public TileEntityBeaconRenderer() {
      super();
   }

   public void func_147500_a(TileEntityBeacon var1, double var2, double var4, double var6, float var8) {
      float var9 = var1.func_146002_i();
      GL11.glAlphaFunc(516, 0.1F);
      if (var9 > 0.0F) {
         Tessellator var10 = Tessellator.field_78398_a;
         this.func_147499_a(field_147523_b);
         GL11.glTexParameterf(3553, 10242, 10497.0F);
         GL11.glTexParameterf(3553, 10243, 10497.0F);
         GL11.glDisable(2896);
         GL11.glDisable(2884);
         GL11.glDisable(3042);
         GL11.glDepthMask(true);
         OpenGlHelper.func_148821_a(770, 1, 1, 0);
         float var11 = (float)var1.func_145831_w().func_82737_E() + var8;
         float var12 = -var11 * 0.2F - (float)MathHelper.func_76141_d(-var11 * 0.1F);
         byte var13 = 1;
         double var14 = (double)var11 * 0.025 * (1.0 - (double)(var13 & 1) * 2.5);
         var10.func_78382_b();
         var10.func_78370_a(255, 255, 255, 32);
         double var16 = (double)var13 * 0.2;
         double var18 = 0.5 + Math.cos(var14 + 2.356194490192345) * var16;
         double var20 = 0.5 + Math.sin(var14 + 2.356194490192345) * var16;
         double var22 = 0.5 + Math.cos(var14 + 0.7853981633974483) * var16;
         double var24 = 0.5 + Math.sin(var14 + 0.7853981633974483) * var16;
         double var26 = 0.5 + Math.cos(var14 + 3.9269908169872414) * var16;
         double var28 = 0.5 + Math.sin(var14 + 3.9269908169872414) * var16;
         double var30 = 0.5 + Math.cos(var14 + 5.497787143782138) * var16;
         double var32 = 0.5 + Math.sin(var14 + 5.497787143782138) * var16;
         double var34 = (double)(256.0F * var9);
         double var36 = 0.0;
         double var38 = 1.0;
         double var40 = (double)(-1.0F + var12);
         double var42 = (double)(256.0F * var9) * (0.5 / var16) + var40;
         var10.func_78374_a(var2 + var18, var4 + var34, var6 + var20, var38, var42);
         var10.func_78374_a(var2 + var18, var4, var6 + var20, var38, var40);
         var10.func_78374_a(var2 + var22, var4, var6 + var24, var36, var40);
         var10.func_78374_a(var2 + var22, var4 + var34, var6 + var24, var36, var42);
         var10.func_78374_a(var2 + var30, var4 + var34, var6 + var32, var38, var42);
         var10.func_78374_a(var2 + var30, var4, var6 + var32, var38, var40);
         var10.func_78374_a(var2 + var26, var4, var6 + var28, var36, var40);
         var10.func_78374_a(var2 + var26, var4 + var34, var6 + var28, var36, var42);
         var10.func_78374_a(var2 + var22, var4 + var34, var6 + var24, var38, var42);
         var10.func_78374_a(var2 + var22, var4, var6 + var24, var38, var40);
         var10.func_78374_a(var2 + var30, var4, var6 + var32, var36, var40);
         var10.func_78374_a(var2 + var30, var4 + var34, var6 + var32, var36, var42);
         var10.func_78374_a(var2 + var26, var4 + var34, var6 + var28, var38, var42);
         var10.func_78374_a(var2 + var26, var4, var6 + var28, var38, var40);
         var10.func_78374_a(var2 + var18, var4, var6 + var20, var36, var40);
         var10.func_78374_a(var2 + var18, var4 + var34, var6 + var20, var36, var42);
         var10.func_78381_a();
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         GL11.glDepthMask(false);
         var10.func_78382_b();
         var10.func_78370_a(255, 255, 255, 32);
         double var44 = 0.2;
         double var15 = 0.2;
         double var17 = 0.8;
         double var19 = 0.2;
         double var21 = 0.2;
         double var23 = 0.8;
         double var25 = 0.8;
         double var27 = 0.8;
         double var29 = (double)(256.0F * var9);
         double var31 = 0.0;
         double var33 = 1.0;
         double var35 = (double)(-1.0F + var12);
         double var37 = (double)(256.0F * var9) + var35;
         var10.func_78374_a(var2 + var44, var4 + var29, var6 + var15, var33, var37);
         var10.func_78374_a(var2 + var44, var4, var6 + var15, var33, var35);
         var10.func_78374_a(var2 + var17, var4, var6 + var19, var31, var35);
         var10.func_78374_a(var2 + var17, var4 + var29, var6 + var19, var31, var37);
         var10.func_78374_a(var2 + var25, var4 + var29, var6 + var27, var33, var37);
         var10.func_78374_a(var2 + var25, var4, var6 + var27, var33, var35);
         var10.func_78374_a(var2 + var21, var4, var6 + var23, var31, var35);
         var10.func_78374_a(var2 + var21, var4 + var29, var6 + var23, var31, var37);
         var10.func_78374_a(var2 + var17, var4 + var29, var6 + var19, var33, var37);
         var10.func_78374_a(var2 + var17, var4, var6 + var19, var33, var35);
         var10.func_78374_a(var2 + var25, var4, var6 + var27, var31, var35);
         var10.func_78374_a(var2 + var25, var4 + var29, var6 + var27, var31, var37);
         var10.func_78374_a(var2 + var21, var4 + var29, var6 + var23, var33, var37);
         var10.func_78374_a(var2 + var21, var4, var6 + var23, var33, var35);
         var10.func_78374_a(var2 + var44, var4, var6 + var15, var31, var35);
         var10.func_78374_a(var2 + var44, var4 + var29, var6 + var15, var31, var37);
         var10.func_78381_a();
         GL11.glEnable(2896);
         GL11.glEnable(3553);
         GL11.glDepthMask(true);
      }
   }
}
