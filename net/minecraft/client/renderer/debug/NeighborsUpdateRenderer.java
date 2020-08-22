package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map lastUpdate = Maps.newTreeMap(Ordering.natural().reverse());

   NeighborsUpdateRenderer(Minecraft var1) {
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      long var9 = this.minecraft.level.getGameTime();
      boolean var11 = true;
      double var12 = 0.0025D;
      HashSet var14 = Sets.newHashSet();
      HashMap var15 = Maps.newHashMap();
      VertexConsumer var16 = var2.getBuffer(RenderType.lines());
      Iterator var17 = this.lastUpdate.entrySet().iterator();

      while(true) {
         Entry var18;
         while(var17.hasNext()) {
            var18 = (Entry)var17.next();
            Long var19 = (Long)var18.getKey();
            Map var20 = (Map)var18.getValue();
            long var21 = var9 - var19;
            if (var21 > 200L) {
               var17.remove();
            } else {
               Iterator var23 = var20.entrySet().iterator();

               while(var23.hasNext()) {
                  Entry var24 = (Entry)var23.next();
                  BlockPos var25 = (BlockPos)var24.getKey();
                  Integer var26 = (Integer)var24.getValue();
                  if (var14.add(var25)) {
                     AABB var27 = (new AABB(BlockPos.ZERO)).inflate(0.002D).deflate(0.0025D * (double)var21).move((double)var25.getX(), (double)var25.getY(), (double)var25.getZ()).move(-var3, -var5, -var7);
                     LevelRenderer.renderLineBox(var16, var27.minX, var27.minY, var27.minZ, var27.maxX, var27.maxY, var27.maxZ, 1.0F, 1.0F, 1.0F, 1.0F);
                     var15.put(var25, var26);
                  }
               }
            }
         }

         var17 = var15.entrySet().iterator();

         while(var17.hasNext()) {
            var18 = (Entry)var17.next();
            BlockPos var28 = (BlockPos)var18.getKey();
            Integer var29 = (Integer)var18.getValue();
            DebugRenderer.renderFloatingText(String.valueOf(var29), var28.getX(), var28.getY(), var28.getZ(), -1);
         }

         return;
      }
   }
}
