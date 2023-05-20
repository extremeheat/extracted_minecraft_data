package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

public class SolidFaceRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public SolidFaceRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Matrix4f var9 = var1.last().pose();
      Level var10 = this.minecraft.player.level;
      BlockPos var11 = BlockPos.containing(var3, var5, var7);

      for(BlockPos var13 : BlockPos.betweenClosed(var11.offset(-6, -6, -6), var11.offset(6, 6, 6))) {
         BlockState var14 = var10.getBlockState(var13);
         if (!var14.is(Blocks.AIR)) {
            VoxelShape var15 = var14.getShape(var10, var13);

            for(AABB var17 : var15.toAabbs()) {
               AABB var18 = var17.move(var13).inflate(0.002);
               float var19 = (float)(var18.minX - var3);
               float var20 = (float)(var18.minY - var5);
               float var21 = (float)(var18.minZ - var7);
               float var22 = (float)(var18.maxX - var3);
               float var23 = (float)(var18.maxY - var5);
               float var24 = (float)(var18.maxZ - var7);
               float var25 = 1.0F;
               float var26 = 0.0F;
               float var27 = 0.0F;
               float var28 = 0.5F;
               if (var14.isFaceSturdy(var10, var13, Direction.WEST)) {
                  VertexConsumer var29 = var2.getBuffer(RenderType.debugFilledBox());
                  var29.vertex(var9, var19, var20, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var29.vertex(var9, var19, var20, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var29.vertex(var9, var19, var23, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var29.vertex(var9, var19, var23, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (var14.isFaceSturdy(var10, var13, Direction.SOUTH)) {
                  VertexConsumer var30 = var2.getBuffer(RenderType.debugFilledBox());
                  var30.vertex(var9, var19, var23, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var30.vertex(var9, var19, var20, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var30.vertex(var9, var22, var23, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var30.vertex(var9, var22, var20, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (var14.isFaceSturdy(var10, var13, Direction.EAST)) {
                  VertexConsumer var31 = var2.getBuffer(RenderType.debugFilledBox());
                  var31.vertex(var9, var22, var20, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var31.vertex(var9, var22, var20, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var31.vertex(var9, var22, var23, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var31.vertex(var9, var22, var23, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (var14.isFaceSturdy(var10, var13, Direction.NORTH)) {
                  VertexConsumer var32 = var2.getBuffer(RenderType.debugFilledBox());
                  var32.vertex(var9, var22, var23, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var32.vertex(var9, var22, var20, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var32.vertex(var9, var19, var23, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var32.vertex(var9, var19, var20, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (var14.isFaceSturdy(var10, var13, Direction.DOWN)) {
                  VertexConsumer var33 = var2.getBuffer(RenderType.debugFilledBox());
                  var33.vertex(var9, var19, var20, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var33.vertex(var9, var22, var20, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var33.vertex(var9, var19, var20, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var33.vertex(var9, var22, var20, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (var14.isFaceSturdy(var10, var13, Direction.UP)) {
                  VertexConsumer var34 = var2.getBuffer(RenderType.debugFilledBox());
                  var34.vertex(var9, var19, var23, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var34.vertex(var9, var19, var23, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var34.vertex(var9, var22, var23, var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var34.vertex(var9, var22, var23, var24).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }
            }
         }
      }
   }
}
