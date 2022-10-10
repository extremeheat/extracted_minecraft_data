package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;

public class DebugRendererHeightMap implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_190061_a;

   public DebugRendererHeightMap(Minecraft var1) {
      super();
      this.field_190061_a = var1;
   }

   public void func_190060_a(float var1, long var2) {
      EntityPlayerSP var4 = this.field_190061_a.field_71439_g;
      WorldClient var5 = this.field_190061_a.field_71441_e;
      double var6 = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var1;
      double var8 = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var1;
      double var10 = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var1;
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179090_x();
      BlockPos var12 = new BlockPos(var4.field_70165_t, 0.0D, var4.field_70161_v);
      Iterable var13 = BlockPos.func_177980_a(var12.func_177982_a(-40, 0, -40), var12.func_177982_a(40, 0, 40));
      Tessellator var14 = Tessellator.func_178181_a();
      BufferBuilder var15 = var14.func_178180_c();
      var15.func_181668_a(5, DefaultVertexFormats.field_181706_f);
      Iterator var16 = var13.iterator();

      while(var16.hasNext()) {
         BlockPos var17 = (BlockPos)var16.next();
         int var18 = var5.func_201676_a(Heightmap.Type.WORLD_SURFACE_WG, var17.func_177958_n(), var17.func_177952_p());
         if (var5.func_180495_p(var17.func_177982_a(0, var18, 0).func_177977_b()).func_196958_f()) {
            WorldRenderer.func_189693_b(var15, (double)((float)var17.func_177958_n() + 0.25F) - var6, (double)var18 - var8, (double)((float)var17.func_177952_p() + 0.25F) - var10, (double)((float)var17.func_177958_n() + 0.75F) - var6, (double)var18 + 0.09375D - var8, (double)((float)var17.func_177952_p() + 0.75F) - var10, 0.0F, 0.0F, 1.0F, 0.5F);
         } else {
            WorldRenderer.func_189693_b(var15, (double)((float)var17.func_177958_n() + 0.25F) - var6, (double)var18 - var8, (double)((float)var17.func_177952_p() + 0.25F) - var10, (double)((float)var17.func_177958_n() + 0.75F) - var6, (double)var18 + 0.09375D - var8, (double)((float)var17.func_177952_p() + 0.75F) - var10, 0.0F, 1.0F, 0.0F, 0.5F);
         }
      }

      var14.func_78381_a();
      GlStateManager.func_179098_w();
      GlStateManager.func_179121_F();
   }
}
