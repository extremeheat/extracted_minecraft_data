package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Iterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SolidFaceRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public SolidFaceRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(long var1) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      double var4 = var3.getPosition().x;
      double var6 = var3.getPosition().y;
      double var8 = var3.getPosition().z;
      Level var10 = this.minecraft.player.level;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.lineWidth(2.0F);
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);
      BlockPos var11 = new BlockPos(var3.getPosition());
      Iterator var12 = BlockPos.betweenClosed(var11.offset(-6, -6, -6), var11.offset(6, 6, 6)).iterator();

      while(true) {
         BlockPos var13;
         BlockState var14;
         do {
            if (!var12.hasNext()) {
               GlStateManager.depthMask(true);
               GlStateManager.enableTexture();
               GlStateManager.disableBlend();
               return;
            }

            var13 = (BlockPos)var12.next();
            var14 = var10.getBlockState(var13);
         } while(var14.getBlock() == Blocks.AIR);

         VoxelShape var15 = var14.getShape(var10, var13);
         Iterator var16 = var15.toAabbs().iterator();

         while(var16.hasNext()) {
            AABB var17 = (AABB)var16.next();
            AABB var18 = var17.move(var13).inflate(0.002D).move(-var4, -var6, -var8);
            double var19 = var18.minX;
            double var21 = var18.minY;
            double var23 = var18.minZ;
            double var25 = var18.maxX;
            double var27 = var18.maxY;
            double var29 = var18.maxZ;
            float var31 = 1.0F;
            float var32 = 0.0F;
            float var33 = 0.0F;
            float var34 = 0.5F;
            Tesselator var35;
            BufferBuilder var36;
            if (var14.isFaceSturdy(var10, var13, Direction.WEST)) {
               var35 = Tesselator.getInstance();
               var36 = var35.getBuilder();
               var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
               var36.vertex(var19, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var19, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var19, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var19, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.end();
            }

            if (var14.isFaceSturdy(var10, var13, Direction.SOUTH)) {
               var35 = Tesselator.getInstance();
               var36 = var35.getBuilder();
               var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
               var36.vertex(var19, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var19, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.end();
            }

            if (var14.isFaceSturdy(var10, var13, Direction.EAST)) {
               var35 = Tesselator.getInstance();
               var36 = var35.getBuilder();
               var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
               var36.vertex(var25, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.end();
            }

            if (var14.isFaceSturdy(var10, var13, Direction.NORTH)) {
               var35 = Tesselator.getInstance();
               var36 = var35.getBuilder();
               var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
               var36.vertex(var25, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var19, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var19, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.end();
            }

            if (var14.isFaceSturdy(var10, var13, Direction.DOWN)) {
               var35 = Tesselator.getInstance();
               var36 = var35.getBuilder();
               var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
               var36.vertex(var19, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var19, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.end();
            }

            if (var14.isFaceSturdy(var10, var13, Direction.UP)) {
               var35 = Tesselator.getInstance();
               var36 = var35.getBuilder();
               var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
               var36.vertex(var19, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var19, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var36.vertex(var25, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.end();
            }
         }
      }
   }
}
