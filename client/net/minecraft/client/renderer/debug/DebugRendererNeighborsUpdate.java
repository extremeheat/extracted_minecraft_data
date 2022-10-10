package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class DebugRendererNeighborsUpdate implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_191554_a;
   private final Map<Long, Map<BlockPos, Integer>> field_191555_b = Maps.newTreeMap(Ordering.natural().reverse());

   DebugRendererNeighborsUpdate(Minecraft var1) {
      super();
      this.field_191554_a = var1;
   }

   public void func_191553_a(long var1, BlockPos var3) {
      Object var4 = (Map)this.field_191555_b.get(var1);
      if (var4 == null) {
         var4 = Maps.newHashMap();
         this.field_191555_b.put(var1, var4);
      }

      Integer var5 = (Integer)((Map)var4).get(var3);
      if (var5 == null) {
         var5 = 0;
      }

      ((Map)var4).put(var3, var5 + 1);
   }

   public void func_190060_a(float var1, long var2) {
      long var4 = this.field_191554_a.field_71441_e.func_82737_E();
      EntityPlayerSP var6 = this.field_191554_a.field_71439_g;
      double var7 = var6.field_70142_S + (var6.field_70165_t - var6.field_70142_S) * (double)var1;
      double var9 = var6.field_70137_T + (var6.field_70163_u - var6.field_70137_T) * (double)var1;
      double var11 = var6.field_70136_U + (var6.field_70161_v - var6.field_70136_U) * (double)var1;
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_187441_d(2.0F);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      boolean var13 = true;
      double var14 = 0.0025D;
      HashSet var16 = Sets.newHashSet();
      HashMap var17 = Maps.newHashMap();
      Iterator var18 = this.field_191555_b.entrySet().iterator();

      while(true) {
         Entry var19;
         while(var18.hasNext()) {
            var19 = (Entry)var18.next();
            Long var20 = (Long)var19.getKey();
            Map var21 = (Map)var19.getValue();
            long var22 = var4 - var20;
            if (var22 > 200L) {
               var18.remove();
            } else {
               Iterator var24 = var21.entrySet().iterator();

               while(var24.hasNext()) {
                  Entry var25 = (Entry)var24.next();
                  BlockPos var26 = (BlockPos)var25.getKey();
                  Integer var27 = (Integer)var25.getValue();
                  if (var16.add(var26)) {
                     WorldRenderer.func_189697_a((new AxisAlignedBB(BlockPos.field_177992_a)).func_186662_g(0.002D).func_186664_h(0.0025D * (double)var22).func_72317_d((double)var26.func_177958_n(), (double)var26.func_177956_o(), (double)var26.func_177952_p()).func_72317_d(-var7, -var9, -var11), 1.0F, 1.0F, 1.0F, 1.0F);
                     var17.put(var26, var27);
                  }
               }
            }
         }

         var18 = var17.entrySet().iterator();

         while(var18.hasNext()) {
            var19 = (Entry)var18.next();
            BlockPos var29 = (BlockPos)var19.getKey();
            Integer var28 = (Integer)var19.getValue();
            DebugRenderer.func_191556_a(String.valueOf(var28), var29.func_177958_n(), var29.func_177956_o(), var29.func_177952_p(), var1, -1);
         }

         GlStateManager.func_179132_a(true);
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
         return;
      }
   }
}
