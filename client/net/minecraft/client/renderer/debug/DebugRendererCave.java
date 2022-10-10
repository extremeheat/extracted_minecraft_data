package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;

public class DebugRendererCave implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_201743_a;
   private final Map<BlockPos, BlockPos> field_201744_b = Maps.newHashMap();
   private final Map<BlockPos, Float> field_201745_c = Maps.newHashMap();
   private final List<BlockPos> field_201746_d = Lists.newArrayList();

   public DebugRendererCave(Minecraft var1) {
      super();
      this.field_201743_a = var1;
   }

   public void func_201742_a(BlockPos var1, List<BlockPos> var2, List<Float> var3) {
      for(int var4 = 0; var4 < var2.size(); ++var4) {
         this.field_201744_b.put(var2.get(var4), var1);
         this.field_201745_c.put(var2.get(var4), var3.get(var4));
      }

      this.field_201746_d.add(var1);
   }

   public void func_190060_a(float var1, long var2) {
      EntityPlayerSP var4 = this.field_201743_a.field_71439_g;
      WorldClient var5 = this.field_201743_a.field_71441_e;
      double var6 = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var1;
      double var8 = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var1;
      double var10 = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var1;
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179090_x();
      BlockPos var12 = new BlockPos(var4.field_70165_t, 0.0D, var4.field_70161_v);
      Tessellator var13 = Tessellator.func_178181_a();
      BufferBuilder var14 = var13.func_178180_c();
      var14.func_181668_a(5, DefaultVertexFormats.field_181706_f);
      Iterator var15 = this.field_201744_b.entrySet().iterator();

      while(var15.hasNext()) {
         Entry var16 = (Entry)var15.next();
         BlockPos var17 = (BlockPos)var16.getKey();
         BlockPos var18 = (BlockPos)var16.getValue();
         float var19 = (float)(var18.func_177958_n() * 128 % 256) / 256.0F;
         float var20 = (float)(var18.func_177956_o() * 128 % 256) / 256.0F;
         float var21 = (float)(var18.func_177952_p() * 128 % 256) / 256.0F;
         float var22 = (Float)this.field_201745_c.get(var17);
         if (var12.func_196233_m(var17) < 160.0D) {
            WorldRenderer.func_189693_b(var14, (double)((float)var17.func_177958_n() + 0.5F) - var6 - (double)var22, (double)((float)var17.func_177956_o() + 0.5F) - var8 - (double)var22, (double)((float)var17.func_177952_p() + 0.5F) - var10 - (double)var22, (double)((float)var17.func_177958_n() + 0.5F) - var6 + (double)var22, (double)((float)var17.func_177956_o() + 0.5F) - var8 + (double)var22, (double)((float)var17.func_177952_p() + 0.5F) - var10 + (double)var22, var19, var20, var21, 0.5F);
         }
      }

      var15 = this.field_201746_d.iterator();

      while(var15.hasNext()) {
         BlockPos var23 = (BlockPos)var15.next();
         if (var12.func_196233_m(var23) < 160.0D) {
            WorldRenderer.func_189693_b(var14, (double)var23.func_177958_n() - var6, (double)var23.func_177956_o() - var8, (double)var23.func_177952_p() - var10, (double)((float)var23.func_177958_n() + 1.0F) - var6, (double)((float)var23.func_177956_o() + 1.0F) - var8, (double)((float)var23.func_177952_p() + 1.0F) - var10, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      var13.func_78381_a();
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179121_F();
   }
}
