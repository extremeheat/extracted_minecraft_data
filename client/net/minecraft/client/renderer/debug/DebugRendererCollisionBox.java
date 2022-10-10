package net.minecraft.client.renderer.debug;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.shapes.VoxelShape;

public class DebugRendererCollisionBox implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_191312_a;
   private double field_195631_b = 4.9E-324D;
   private List<VoxelShape> field_195632_c = Collections.emptyList();

   public DebugRendererCollisionBox(Minecraft var1) {
      super();
      this.field_191312_a = var1;
   }

   public void func_190060_a(float var1, long var2) {
      EntityPlayerSP var4 = this.field_191312_a.field_71439_g;
      double var5 = (double)Util.func_211178_c();
      if (var5 - this.field_195631_b > 1.0E8D) {
         this.field_195631_b = var5;
         this.field_195632_c = (List)var4.field_70170_p.func_212388_b(var4, var4.func_174813_aQ().func_186662_g(6.0D)).collect(Collectors.toList());
      }

      double var7 = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var1;
      double var9 = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var1;
      double var11 = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var1;
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_187441_d(2.0F);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      Iterator var13 = this.field_195632_c.iterator();

      while(var13.hasNext()) {
         VoxelShape var14 = (VoxelShape)var13.next();
         WorldRenderer.func_195470_a(var14, -var7, -var9, -var11, 1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }
}
