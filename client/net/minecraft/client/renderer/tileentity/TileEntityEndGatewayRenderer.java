package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class TileEntityEndGatewayRenderer extends TileEntityEndPortalRenderer {
   private static final ResourceLocation field_188199_f = new ResourceLocation("textures/entity/end_gateway_beam.png");

   public TileEntityEndGatewayRenderer() {
      super();
   }

   public void func_199341_a(TileEntityEndPortal var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.func_179106_n();
      TileEntityEndGateway var10 = (TileEntityEndGateway)var1;
      if (var10.func_195499_c() || var10.func_195500_d()) {
         GlStateManager.func_179092_a(516, 0.1F);
         this.func_147499_a(field_188199_f);
         float var11 = var10.func_195499_c() ? var10.func_195497_a(var8) : var10.func_195491_b(var8);
         double var12 = var10.func_195499_c() ? 256.0D - var4 : 50.0D;
         var11 = MathHelper.func_76126_a(var11 * 3.1415927F);
         int var14 = MathHelper.func_76128_c((double)var11 * var12);
         float[] var15 = var10.func_195499_c() ? EnumDyeColor.MAGENTA.func_193349_f() : EnumDyeColor.PURPLE.func_193349_f();
         TileEntityBeaconRenderer.func_188205_a(var2, var4, var6, (double)var8, (double)var11, var10.func_145831_w().func_82737_E(), 0, var14, var15, 0.15D, 0.175D);
         TileEntityBeaconRenderer.func_188205_a(var2, var4, var6, (double)var8, (double)var11, var10.func_145831_w().func_82737_E(), 0, -var14, var15, 0.15D, 0.175D);
      }

      super.func_199341_a(var1, var2, var4, var6, var8, var9);
      GlStateManager.func_179127_m();
   }

   protected int func_191286_a(double var1) {
      return super.func_191286_a(var1) + 1;
   }

   protected float func_191287_c() {
      return 1.0F;
   }
}
