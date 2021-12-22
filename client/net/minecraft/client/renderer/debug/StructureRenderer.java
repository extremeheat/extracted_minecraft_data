package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map<DimensionType, Map<String, BoundingBox>> postMainBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, BoundingBox>> postPiecesBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, Boolean>> startPiecesMap = Maps.newIdentityHashMap();
   private static final int MAX_RENDER_DIST = 500;

   public StructureRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Camera var9 = this.minecraft.gameRenderer.getMainCamera();
      ClientLevel var10 = this.minecraft.level;
      DimensionType var11 = var10.dimensionType();
      BlockPos var12 = new BlockPos(var9.getPosition().field_414, 0.0D, var9.getPosition().field_416);
      VertexConsumer var13 = var2.getBuffer(RenderType.lines());
      Iterator var14;
      if (this.postMainBoxes.containsKey(var11)) {
         var14 = ((Map)this.postMainBoxes.get(var11)).values().iterator();

         while(var14.hasNext()) {
            BoundingBox var15 = (BoundingBox)var14.next();
            if (var12.closerThan(var15.getCenter(), 500.0D)) {
               LevelRenderer.renderLineBox(var1, var13, (double)var15.minX() - var3, (double)var15.minY() - var5, (double)var15.minZ() - var7, (double)(var15.maxX() + 1) - var3, (double)(var15.maxY() + 1) - var5, (double)(var15.maxZ() + 1) - var7, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      if (this.postPiecesBoxes.containsKey(var11)) {
         var14 = ((Map)this.postPiecesBoxes.get(var11)).entrySet().iterator();

         while(var14.hasNext()) {
            Entry var19 = (Entry)var14.next();
            String var16 = (String)var19.getKey();
            BoundingBox var17 = (BoundingBox)var19.getValue();
            Boolean var18 = (Boolean)((Map)this.startPiecesMap.get(var11)).get(var16);
            if (var12.closerThan(var17.getCenter(), 500.0D)) {
               if (var18) {
                  LevelRenderer.renderLineBox(var1, var13, (double)var17.minX() - var3, (double)var17.minY() - var5, (double)var17.minZ() - var7, (double)(var17.maxX() + 1) - var3, (double)(var17.maxY() + 1) - var5, (double)(var17.maxZ() + 1) - var7, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F);
               } else {
                  LevelRenderer.renderLineBox(var1, var13, (double)var17.minX() - var3, (double)var17.minY() - var5, (double)var17.minZ() - var7, (double)(var17.maxX() + 1) - var3, (double)(var17.maxY() + 1) - var5, (double)(var17.maxZ() + 1) - var7, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
               }
            }
         }
      }

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
