package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
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

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Level var9 = this.minecraft.player.level;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.lineWidth(2.0F);
      RenderSystem.depthMask(false);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BlockPos var10 = new BlockPos(var3, var5, var7);

      for(BlockPos var12 : BlockPos.betweenClosed(var10.offset(-6, -6, -6), var10.offset(6, 6, 6))) {
         BlockState var13 = var9.getBlockState(var12);
         if (!var13.is(Blocks.AIR)) {
            VoxelShape var14 = var13.getShape(var9, var12);

            for(AABB var16 : var14.toAabbs()) {
               AABB var17 = var16.move(var12).inflate(0.002).move(-var3, -var5, -var7);
               double var18 = var17.minX;
               double var20 = var17.minY;
               double var22 = var17.minZ;
               double var24 = var17.maxX;
               double var26 = var17.maxY;
               double var28 = var17.maxZ;
               float var30 = 1.0F;
               float var31 = 0.0F;
               float var32 = 0.0F;
               float var33 = 0.5F;
               if (var13.isFaceSturdy(var9, var12, Direction.WEST)) {
                  Tesselator var34 = Tesselator.getInstance();
                  BufferBuilder var35 = var34.getBuilder();
                  var35.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  var35.vertex(var18, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var35.vertex(var18, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var35.vertex(var18, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var35.vertex(var18, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var34.end();
               }

               if (var13.isFaceSturdy(var9, var12, Direction.SOUTH)) {
                  Tesselator var36 = Tesselator.getInstance();
                  BufferBuilder var41 = var36.getBuilder();
                  var41.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  var41.vertex(var18, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var41.vertex(var18, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var41.vertex(var24, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var41.vertex(var24, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.end();
               }

               if (var13.isFaceSturdy(var9, var12, Direction.EAST)) {
                  Tesselator var37 = Tesselator.getInstance();
                  BufferBuilder var42 = var37.getBuilder();
                  var42.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  var42.vertex(var24, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var42.vertex(var24, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var42.vertex(var24, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var42.vertex(var24, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var37.end();
               }

               if (var13.isFaceSturdy(var9, var12, Direction.NORTH)) {
                  Tesselator var38 = Tesselator.getInstance();
                  BufferBuilder var43 = var38.getBuilder();
                  var43.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  var43.vertex(var24, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var43.vertex(var24, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var43.vertex(var18, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var43.vertex(var18, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var38.end();
               }

               if (var13.isFaceSturdy(var9, var12, Direction.DOWN)) {
                  Tesselator var39 = Tesselator.getInstance();
                  BufferBuilder var44 = var39.getBuilder();
                  var44.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  var44.vertex(var18, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var44.vertex(var24, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var44.vertex(var18, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var44.vertex(var24, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var39.end();
               }

               if (var13.isFaceSturdy(var9, var12, Direction.UP)) {
                  Tesselator var40 = Tesselator.getInstance();
                  BufferBuilder var45 = var40.getBuilder();
                  var45.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                  var45.vertex(var18, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var45.vertex(var18, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var45.vertex(var24, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var45.vertex(var24, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var40.end();
               }
            }
         }
      }

      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
   }
}
