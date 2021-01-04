package net.minecraft.client.renderer.debug;

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
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map<DimensionType, Map<String, BoundingBox>> postMainBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, BoundingBox>> postPiecesBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, Boolean>> startPiecesMap = Maps.newIdentityHashMap();

   public StructureRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(long var1) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      MultiPlayerLevel var4 = this.minecraft.level;
      DimensionType var5 = var4.getDimension().getType();
      double var6 = var3.getPosition().x;
      double var8 = var3.getPosition().y;
      double var10 = var3.getPosition().z;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      GlStateManager.disableDepthTest();
      BlockPos var12 = new BlockPos(var3.getPosition().x, 0.0D, var3.getPosition().z);
      Tesselator var13 = Tesselator.getInstance();
      BufferBuilder var14 = var13.getBuilder();
      var14.begin(3, DefaultVertexFormat.POSITION_COLOR);
      GlStateManager.lineWidth(1.0F);
      Iterator var15;
      if (this.postMainBoxes.containsKey(var5)) {
         var15 = ((Map)this.postMainBoxes.get(var5)).values().iterator();

         while(var15.hasNext()) {
            BoundingBox var16 = (BoundingBox)var15.next();
            if (var12.closerThan(var16.getCenter(), 500.0D)) {
               LevelRenderer.addChainedLineBoxVertices(var14, (double)var16.x0 - var6, (double)var16.y0 - var8, (double)var16.z0 - var10, (double)(var16.x1 + 1) - var6, (double)(var16.y1 + 1) - var8, (double)(var16.z1 + 1) - var10, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      if (this.postPiecesBoxes.containsKey(var5)) {
         var15 = ((Map)this.postPiecesBoxes.get(var5)).entrySet().iterator();

         while(var15.hasNext()) {
            Entry var20 = (Entry)var15.next();
            String var17 = (String)var20.getKey();
            BoundingBox var18 = (BoundingBox)var20.getValue();
            Boolean var19 = (Boolean)((Map)this.startPiecesMap.get(var5)).get(var17);
            if (var12.closerThan(var18.getCenter(), 500.0D)) {
               if (var19) {
                  LevelRenderer.addChainedLineBoxVertices(var14, (double)var18.x0 - var6, (double)var18.y0 - var8, (double)var18.z0 - var10, (double)(var18.x1 + 1) - var6, (double)(var18.y1 + 1) - var8, (double)(var18.z1 + 1) - var10, 0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  LevelRenderer.addChainedLineBoxVertices(var14, (double)var18.x0 - var6, (double)var18.y0 - var8, (double)var18.z0 - var10, (double)(var18.x1 + 1) - var6, (double)(var18.y1 + 1) - var8, (double)(var18.z1 + 1) - var10, 0.0F, 0.0F, 1.0F, 1.0F);
               }
            }
         }
      }

      var13.end();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }

   public void addBoundingBox(BoundingBox var1, List<BoundingBox> var2, List<Boolean> var3, DimensionType var4) {
      if (!this.postMainBoxes.containsKey(var4)) {
         this.postMainBoxes.put(var4, Maps.newHashMap());
      }

      if (!this.postPiecesBoxes.containsKey(var4)) {
         this.postPiecesBoxes.put(var4, Maps.newHashMap());
         this.startPiecesMap.put(var4, Maps.newHashMap());
      }

      ((Map)this.postMainBoxes.get(var4)).put(var1.toString(), var1);

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         BoundingBox var6 = (BoundingBox)var2.get(var5);
         Boolean var7 = (Boolean)var3.get(var5);
         ((Map)this.postPiecesBoxes.get(var4)).put(var6.toString(), var6);
         ((Map)this.startPiecesMap.get(var4)).put(var6.toString(), var7);
      }

   }

   public void clear() {
      this.postMainBoxes.clear();
      this.postPiecesBoxes.clear();
      this.startPiecesMap.clear();
   }
}
