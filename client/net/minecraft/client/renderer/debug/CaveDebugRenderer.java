package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;

public class CaveDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map<BlockPos, BlockPos> tunnelsList = Maps.newHashMap();
   private final Map<BlockPos, Float> thicknessMap = Maps.newHashMap();
   private final List<BlockPos> startPoses = Lists.newArrayList();

   public CaveDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void addTunnel(BlockPos var1, List<BlockPos> var2, List<Float> var3) {
      for(int var4 = 0; var4 < var2.size(); ++var4) {
         this.tunnelsList.put(var2.get(var4), var1);
         this.thicknessMap.put(var2.get(var4), var3.get(var4));
      }

      this.startPoses.add(var1);
   }

   public void render(long var1) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      double var4 = var3.getPosition().x;
      double var6 = var3.getPosition().y;
      double var8 = var3.getPosition().z;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      BlockPos var10 = new BlockPos(var3.getPosition().x, 0.0D, var3.getPosition().z);
      Tesselator var11 = Tesselator.getInstance();
      BufferBuilder var12 = var11.getBuilder();
      var12.begin(5, DefaultVertexFormat.POSITION_COLOR);
      Iterator var13 = this.tunnelsList.entrySet().iterator();

      while(var13.hasNext()) {
         Entry var14 = (Entry)var13.next();
         BlockPos var15 = (BlockPos)var14.getKey();
         BlockPos var16 = (BlockPos)var14.getValue();
         float var17 = (float)(var16.getX() * 128 % 256) / 256.0F;
         float var18 = (float)(var16.getY() * 128 % 256) / 256.0F;
         float var19 = (float)(var16.getZ() * 128 % 256) / 256.0F;
         float var20 = (Float)this.thicknessMap.get(var15);
         if (var10.closerThan(var15, 160.0D)) {
            LevelRenderer.addChainedFilledBoxVertices(var12, (double)((float)var15.getX() + 0.5F) - var4 - (double)var20, (double)((float)var15.getY() + 0.5F) - var6 - (double)var20, (double)((float)var15.getZ() + 0.5F) - var8 - (double)var20, (double)((float)var15.getX() + 0.5F) - var4 + (double)var20, (double)((float)var15.getY() + 0.5F) - var6 + (double)var20, (double)((float)var15.getZ() + 0.5F) - var8 + (double)var20, var17, var18, var19, 0.5F);
         }
      }

      var13 = this.startPoses.iterator();

      while(var13.hasNext()) {
         BlockPos var21 = (BlockPos)var13.next();
         if (var10.closerThan(var21, 160.0D)) {
            LevelRenderer.addChainedFilledBoxVertices(var12, (double)var21.getX() - var4, (double)var21.getY() - var6, (double)var21.getZ() - var8, (double)((float)var21.getX() + 1.0F) - var4, (double)((float)var21.getY() + 1.0F) - var6, (double)((float)var21.getZ() + 1.0F) - var8, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      var11.end();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }
}
