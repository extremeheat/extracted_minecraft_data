package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumLightType;

public class DebugRendererLight implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_201728_a;

   public DebugRendererLight(Minecraft var1) {
      super();
      this.field_201728_a = var1;
   }

   public void func_190060_a(float var1, long var2) {
      EntityPlayerSP var4 = this.field_201728_a.field_71439_g;
      WorldClient var5 = this.field_201728_a.field_71441_e;
      double var10000 = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var1;
      var10000 = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var1;
      var10000 = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var1;
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179090_x();
      BlockPos var12 = new BlockPos(var4.field_70165_t, var4.field_70163_u, var4.field_70161_v);
      Iterable var13 = BlockPos.func_177980_a(var12.func_177982_a(-5, -5, -5), var12.func_177982_a(5, 5, 5));
      Iterator var14 = var13.iterator();

      while(var14.hasNext()) {
         BlockPos var15 = (BlockPos)var14.next();
         int var16 = var5.func_175642_b(EnumLightType.SKY, var15);
         float var17 = (float)(15 - var16) / 15.0F * 0.5F + 0.16F;
         int var18 = MathHelper.func_181758_c(var17, 0.9F, 0.9F);
         if (var16 != 15) {
            DebugRenderer.func_190076_a(String.valueOf(var16), (double)var15.func_177958_n() + 0.5D, (double)var15.func_177956_o() + 0.25D, (double)var15.func_177952_p() + 0.5D, 1.0F, var18);
         }
      }

      GlStateManager.func_179098_w();
      GlStateManager.func_179121_F();
   }
}
