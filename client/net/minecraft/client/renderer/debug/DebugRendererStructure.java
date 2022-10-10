package net.minecraft.client.renderer.debug;

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
import net.minecraft.util.math.MutableBoundingBox;

public class DebugRendererStructure implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_201730_a;
   private final Map<Integer, Map<String, MutableBoundingBox>> field_201731_b = Maps.newHashMap();
   private final Map<Integer, Map<String, MutableBoundingBox>> field_201732_c = Maps.newHashMap();
   private final Map<Integer, Map<String, Boolean>> field_201733_d = Maps.newHashMap();

   public DebugRendererStructure(Minecraft var1) {
      super();
      this.field_201730_a = var1;
   }

   public void func_190060_a(float var1, long var2) {
      EntityPlayerSP var4 = this.field_201730_a.field_71439_g;
      WorldClient var5 = this.field_201730_a.field_71441_e;
      int var6 = var5.func_72912_H().func_202836_i();
      double var7 = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var1;
      double var9 = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var1;
      double var11 = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var1;
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179090_x();
      GlStateManager.func_179097_i();
      BlockPos var13 = new BlockPos(var4.field_70165_t, 0.0D, var4.field_70161_v);
      Tessellator var14 = Tessellator.func_178181_a();
      BufferBuilder var15 = var14.func_178180_c();
      var15.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      GlStateManager.func_187441_d(1.0F);
      Iterator var16;
      if (this.field_201731_b.containsKey(var6)) {
         var16 = ((Map)this.field_201731_b.get(var6)).values().iterator();

         while(var16.hasNext()) {
            MutableBoundingBox var17 = (MutableBoundingBox)var16.next();
            if (var13.func_185332_f(var17.field_78897_a, var17.field_78895_b, var17.field_78896_c) < 500.0D) {
               WorldRenderer.func_189698_a(var15, (double)var17.field_78897_a - var7, (double)var17.field_78895_b - var9, (double)var17.field_78896_c - var11, (double)(var17.field_78893_d + 1) - var7, (double)(var17.field_78894_e + 1) - var9, (double)(var17.field_78892_f + 1) - var11, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      if (this.field_201732_c.containsKey(var6)) {
         var16 = ((Map)this.field_201732_c.get(var6)).entrySet().iterator();

         while(var16.hasNext()) {
            Entry var21 = (Entry)var16.next();
            String var18 = (String)var21.getKey();
            MutableBoundingBox var19 = (MutableBoundingBox)var21.getValue();
            Boolean var20 = (Boolean)((Map)this.field_201733_d.get(var6)).get(var18);
            if (var13.func_185332_f(var19.field_78897_a, var19.field_78895_b, var19.field_78896_c) < 500.0D) {
               if (var20) {
                  WorldRenderer.func_189698_a(var15, (double)var19.field_78897_a - var7, (double)var19.field_78895_b - var9, (double)var19.field_78896_c - var11, (double)(var19.field_78893_d + 1) - var7, (double)(var19.field_78894_e + 1) - var9, (double)(var19.field_78892_f + 1) - var11, 0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  WorldRenderer.func_189698_a(var15, (double)var19.field_78897_a - var7, (double)var19.field_78895_b - var9, (double)var19.field_78896_c - var11, (double)(var19.field_78893_d + 1) - var7, (double)(var19.field_78894_e + 1) - var9, (double)(var19.field_78892_f + 1) - var11, 0.0F, 0.0F, 1.0F, 1.0F);
               }
            }
         }
      }

      var14.func_78381_a();
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179121_F();
   }

   public void func_201729_a(MutableBoundingBox var1, List<MutableBoundingBox> var2, List<Boolean> var3, int var4) {
      if (!this.field_201731_b.containsKey(var4)) {
         this.field_201731_b.put(var4, Maps.newHashMap());
      }

      if (!this.field_201732_c.containsKey(var4)) {
         this.field_201732_c.put(var4, Maps.newHashMap());
         this.field_201733_d.put(var4, Maps.newHashMap());
      }

      ((Map)this.field_201731_b.get(var4)).put(var1.toString(), var1);

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         MutableBoundingBox var6 = (MutableBoundingBox)var2.get(var5);
         Boolean var7 = (Boolean)var3.get(var5);
         ((Map)this.field_201732_c.get(var4)).put(var6.toString(), var6);
         ((Map)this.field_201733_d.get(var4)).put(var6.toString(), var7);
      }

   }
}
