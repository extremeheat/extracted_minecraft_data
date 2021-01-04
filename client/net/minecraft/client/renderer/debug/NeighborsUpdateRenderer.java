package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Long, Map<BlockPos, Integer>> lastUpdate = Maps.newTreeMap(Ordering.natural().reverse());

   NeighborsUpdateRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void addUpdate(long var1, BlockPos var3) {
      Object var4 = (Map)this.lastUpdate.get(var1);
      if (var4 == null) {
         var4 = Maps.newHashMap();
         this.lastUpdate.put(var1, var4);
      }

      Integer var5 = (Integer)((Map)var4).get(var3);
      if (var5 == null) {
         var5 = 0;
      }

      ((Map)var4).put(var3, var5 + 1);
   }

   public void render(long var1) {
      long var3 = this.minecraft.level.getGameTime();
      Camera var5 = this.minecraft.gameRenderer.getMainCamera();
      double var6 = var5.getPosition().x;
      double var8 = var5.getPosition().y;
      double var10 = var5.getPosition().z;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.lineWidth(2.0F);
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);
      boolean var12 = true;
      double var13 = 0.0025D;
      HashSet var15 = Sets.newHashSet();
      HashMap var16 = Maps.newHashMap();
      Iterator var17 = this.lastUpdate.entrySet().iterator();

      while(true) {
         Entry var18;
         while(var17.hasNext()) {
            var18 = (Entry)var17.next();
            Long var19 = (Long)var18.getKey();
            Map var20 = (Map)var18.getValue();
            long var21 = var3 - var19;
            if (var21 > 200L) {
               var17.remove();
            } else {
               Iterator var23 = var20.entrySet().iterator();

               while(var23.hasNext()) {
                  Entry var24 = (Entry)var23.next();
                  BlockPos var25 = (BlockPos)var24.getKey();
                  Integer var26 = (Integer)var24.getValue();
                  if (var15.add(var25)) {
                     LevelRenderer.renderLineBox((new AABB(BlockPos.ZERO)).inflate(0.002D).deflate(0.0025D * (double)var21).move((double)var25.getX(), (double)var25.getY(), (double)var25.getZ()).move(-var6, -var8, -var10), 1.0F, 1.0F, 1.0F, 1.0F);
                     var16.put(var25, var26);
                  }
               }
            }
         }

         var17 = var16.entrySet().iterator();

         while(var17.hasNext()) {
            var18 = (Entry)var17.next();
            BlockPos var27 = (BlockPos)var18.getKey();
            Integer var28 = (Integer)var18.getValue();
            DebugRenderer.renderFloatingText(String.valueOf(var28), var27.getX(), var27.getY(), var27.getZ(), -1);
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture();
         GlStateManager.disableBlend();
         return;
      }
   }
}
