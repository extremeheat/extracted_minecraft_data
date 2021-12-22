package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollisionBoxRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = 4.9E-324D;
   private List<VoxelShape> shapes = Collections.emptyList();

   public CollisionBoxRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      double var9 = (double)Util.getNanos();
      if (var9 - this.lastUpdateTime > 1.0E8D) {
         this.lastUpdateTime = var9;
         Entity var11 = this.minecraft.gameRenderer.getMainCamera().getEntity();
         this.shapes = ImmutableList.copyOf(var11.level.getCollisions(var11, var11.getBoundingBox().inflate(6.0D)));
      }

      VertexConsumer var14 = var2.getBuffer(RenderType.lines());
      Iterator var12 = this.shapes.iterator();

      while(var12.hasNext()) {
         VoxelShape var13 = (VoxelShape)var12.next();
         LevelRenderer.renderVoxelShape(var1, var14, var13, -var3, -var5, -var7, 1.0F, 1.0F, 1.0F, 1.0F);
      }

   }
}
