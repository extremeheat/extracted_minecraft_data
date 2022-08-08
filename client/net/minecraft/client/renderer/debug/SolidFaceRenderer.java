package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Iterator;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Level var9 = this.minecraft.player.level;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.lineWidth(2.0F);
      RenderSystem.disableTexture();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BlockPos var10 = new BlockPos(var3, var5, var7);
      Iterator var11 = BlockPos.betweenClosed(var10.offset(-6, -6, -6), var10.offset(6, 6, 6)).iterator();

      while(true) {
         BlockPos var12;
         BlockState var13;
         do {
            if (!var11.hasNext()) {
               RenderSystem.depthMask(true);
               RenderSystem.enableTexture();
               RenderSystem.disableBlend();
               return;
            }

            var12 = (BlockPos)var11.next();
            var13 = var9.getBlockState(var12);
         } while(var13.is(Blocks.AIR));

         VoxelShape var14 = var13.getShape(var9, var12);
         Iterator var15 = var14.toAabbs().iterator();

         while(var15.hasNext()) {
            AABB var16 = (AABB)var15.next();
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
            Tesselator var34;
            BufferBuilder var35;
            if (var13.isFaceSturdy(var9, var12, Direction.WEST)) {
               var34 = Tesselator.getInstance();
               var35 = var34.getBuilder();
               var35.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
               var35.vertex(var18, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var18, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var18, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var18, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var34.end();
            }

            if (var13.isFaceSturdy(var9, var12, Direction.SOUTH)) {
               var34 = Tesselator.getInstance();
               var35 = var34.getBuilder();
               var35.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
               var35.vertex(var18, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var18, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var34.end();
            }

            if (var13.isFaceSturdy(var9, var12, Direction.EAST)) {
               var34 = Tesselator.getInstance();
               var35 = var34.getBuilder();
               var35.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
               var35.vertex(var24, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var34.end();
            }

            if (var13.isFaceSturdy(var9, var12, Direction.NORTH)) {
               var34 = Tesselator.getInstance();
               var35 = var34.getBuilder();
               var35.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
               var35.vertex(var24, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var18, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var18, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var34.end();
            }

            if (var13.isFaceSturdy(var9, var12, Direction.DOWN)) {
               var34 = Tesselator.getInstance();
               var35 = var34.getBuilder();
               var35.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
               var35.vertex(var18, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var20, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var18, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var20, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var34.end();
            }

            if (var13.isFaceSturdy(var9, var12, Direction.UP)) {
               var34 = Tesselator.getInstance();
               var35 = var34.getBuilder();
               var35.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
               var35.vertex(var18, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var18, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var26, var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var35.vertex(var24, var26, var28).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               var34.end();
            }
         }
      }
   }
}
