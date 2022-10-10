package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;

public class DebugRendererWorldGenAttempts implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_201735_a;
   private final List<BlockPos> field_201736_b = Lists.newArrayList();
   private final List<Float> field_201737_c = Lists.newArrayList();
   private final List<Float> field_201738_d = Lists.newArrayList();
   private final List<Float> field_201739_e = Lists.newArrayList();
   private final List<Float> field_201740_f = Lists.newArrayList();
   private final List<Float> field_201741_g = Lists.newArrayList();

   public DebugRendererWorldGenAttempts(Minecraft var1) {
      super();
      this.field_201735_a = var1;
   }

   public void func_201734_a(BlockPos var1, float var2, float var3, float var4, float var5, float var6) {
      this.field_201736_b.add(var1);
      this.field_201737_c.add(var2);
      this.field_201738_d.add(var6);
      this.field_201739_e.add(var3);
      this.field_201740_f.add(var4);
      this.field_201741_g.add(var5);
   }

   public void func_190060_a(float var1, long var2) {
      EntityPlayerSP var4 = this.field_201735_a.field_71439_g;
      WorldClient var5 = this.field_201735_a.field_71441_e;
      double var6 = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var1;
      double var8 = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var1;
      double var10 = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var1;
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179090_x();
      new BlockPos(var4.field_70165_t, 0.0D, var4.field_70161_v);
      Tessellator var13 = Tessellator.func_178181_a();
      BufferBuilder var14 = var13.func_178180_c();
      var14.func_181668_a(5, DefaultVertexFormats.field_181706_f);

      for(int var15 = 0; var15 < this.field_201736_b.size(); ++var15) {
         BlockPos var16 = (BlockPos)this.field_201736_b.get(var15);
         Float var17 = (Float)this.field_201737_c.get(var15);
         float var18 = var17 / 2.0F;
         WorldRenderer.func_189693_b(var14, (double)((float)var16.func_177958_n() + 0.5F - var18) - var6, (double)((float)var16.func_177956_o() + 0.5F - var18) - var8, (double)((float)var16.func_177952_p() + 0.5F - var18) - var10, (double)((float)var16.func_177958_n() + 0.5F + var18) - var6, (double)((float)var16.func_177956_o() + 0.5F + var18) - var8, (double)((float)var16.func_177952_p() + 0.5F + var18) - var10, (Float)this.field_201739_e.get(var15), (Float)this.field_201740_f.get(var15), (Float)this.field_201741_g.get(var15), (Float)this.field_201738_d.get(var15));
      }

      var13.func_78381_a();
      GlStateManager.func_179098_w();
      GlStateManager.func_179121_F();
   }
}
