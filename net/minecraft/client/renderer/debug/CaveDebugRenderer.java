package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

public class CaveDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Map tunnelsList = Maps.newHashMap();
   private final Map thicknessMap = Maps.newHashMap();
   private final List startPoses = Lists.newArrayList();

   public void addTunnel(BlockPos var1, List var2, List var3) {
      for(int var4 = 0; var4 < var2.size(); ++var4) {
         this.tunnelsList.put(var2.get(var4), var1);
         this.thicknessMap.put(var2.get(var4), var3.get(var4));
      }

      this.startPoses.add(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos var9 = new BlockPos(var3, 0.0D, var7);
      Tesselator var10 = Tesselator.getInstance();
      BufferBuilder var11 = var10.getBuilder();
      var11.begin(5, DefaultVertexFormat.POSITION_COLOR);
      Iterator var12 = this.tunnelsList.entrySet().iterator();

      while(var12.hasNext()) {
         Entry var13 = (Entry)var12.next();
         BlockPos var14 = (BlockPos)var13.getKey();
         BlockPos var15 = (BlockPos)var13.getValue();
         float var16 = (float)(var15.getX() * 128 % 256) / 256.0F;
         float var17 = (float)(var15.getY() * 128 % 256) / 256.0F;
         float var18 = (float)(var15.getZ() * 128 % 256) / 256.0F;
         float var19 = (Float)this.thicknessMap.get(var14);
         if (var9.closerThan(var14, 160.0D)) {
            LevelRenderer.addChainedFilledBoxVertices(var11, (double)((float)var14.getX() + 0.5F) - var3 - (double)var19, (double)((float)var14.getY() + 0.5F) - var5 - (double)var19, (double)((float)var14.getZ() + 0.5F) - var7 - (double)var19, (double)((float)var14.getX() + 0.5F) - var3 + (double)var19, (double)((float)var14.getY() + 0.5F) - var5 + (double)var19, (double)((float)var14.getZ() + 0.5F) - var7 + (double)var19, var16, var17, var18, 0.5F);
         }
      }

      var12 = this.startPoses.iterator();

      while(var12.hasNext()) {
         BlockPos var20 = (BlockPos)var12.next();
         if (var9.closerThan(var20, 160.0D)) {
            LevelRenderer.addChainedFilledBoxVertices(var11, (double)var20.getX() - var3, (double)var20.getY() - var5, (double)var20.getZ() - var7, (double)((float)var20.getX() + 1.0F) - var3, (double)((float)var20.getY() + 1.0F) - var5, (double)((float)var20.getZ() + 1.0F) - var7, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      var10.end();
      RenderSystem.enableDepthTest();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
