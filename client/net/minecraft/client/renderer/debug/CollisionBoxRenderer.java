package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollisionBoxRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = 4.9E-324D;
   private List<VoxelShape> shapes = Collections.emptyList();

   public CollisionBoxRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(long var1) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      double var4 = (double)Util.getNanos();
      if (var4 - this.lastUpdateTime > 1.0E8D) {
         this.lastUpdateTime = var4;
         this.shapes = (List)var3.getEntity().level.getCollisions(var3.getEntity(), var3.getEntity().getBoundingBox().inflate(6.0D), Collections.emptySet()).collect(Collectors.toList());
      }

      double var6 = var3.getPosition().x;
      double var8 = var3.getPosition().y;
      double var10 = var3.getPosition().z;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.lineWidth(2.0F);
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);
      Iterator var12 = this.shapes.iterator();

      while(var12.hasNext()) {
         VoxelShape var13 = (VoxelShape)var12.next();
         LevelRenderer.renderVoxelShape(var13, -var6, -var8, -var10, 1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
   }
}
