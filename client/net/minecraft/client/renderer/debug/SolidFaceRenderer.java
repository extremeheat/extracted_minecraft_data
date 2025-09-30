package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.debug.DebugValueAccess;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      Matrix4f var11 = var1.last().pose();
      Level var12 = this.minecraft.player.level();
      BlockPos var13 = BlockPos.containing(var3, var5, var7);

      for(BlockPos var15 : BlockPos.betweenClosed(var13.offset(-6, -6, -6), var13.offset(6, 6, 6))) {
         BlockState var16 = var12.getBlockState(var15);
         if (!var16.is(Blocks.AIR)) {
            VoxelShape var17 = var16.getShape(var12, var15);

            for(AABB var19 : var17.toAabbs()) {
               AABB var20 = var19.move(var15).inflate(0.002);
               float var21 = (float)(var20.minX - var3);
               float var22 = (float)(var20.minY - var5);
               float var23 = (float)(var20.minZ - var7);
               float var24 = (float)(var20.maxX - var3);
               float var25 = (float)(var20.maxY - var5);
               float var26 = (float)(var20.maxZ - var7);
               int var27 = -2130771968;
               if (var16.isFaceSturdy(var12, var15, Direction.WEST)) {
                  VertexConsumer var28 = var2.getBuffer(RenderType.debugFilledBox());
                  var28.addVertex(var11, var21, var22, var23).setColor(-2130771968);
                  var28.addVertex(var11, var21, var22, var26).setColor(-2130771968);
                  var28.addVertex(var11, var21, var25, var23).setColor(-2130771968);
                  var28.addVertex(var11, var21, var25, var26).setColor(-2130771968);
               }

               if (var16.isFaceSturdy(var12, var15, Direction.SOUTH)) {
                  VertexConsumer var29 = var2.getBuffer(RenderType.debugFilledBox());
                  var29.addVertex(var11, var21, var25, var26).setColor(-2130771968);
                  var29.addVertex(var11, var21, var22, var26).setColor(-2130771968);
                  var29.addVertex(var11, var24, var25, var26).setColor(-2130771968);
                  var29.addVertex(var11, var24, var22, var26).setColor(-2130771968);
               }

               if (var16.isFaceSturdy(var12, var15, Direction.EAST)) {
                  VertexConsumer var30 = var2.getBuffer(RenderType.debugFilledBox());
                  var30.addVertex(var11, var24, var22, var26).setColor(-2130771968);
                  var30.addVertex(var11, var24, var22, var23).setColor(-2130771968);
                  var30.addVertex(var11, var24, var25, var26).setColor(-2130771968);
                  var30.addVertex(var11, var24, var25, var23).setColor(-2130771968);
               }

               if (var16.isFaceSturdy(var12, var15, Direction.NORTH)) {
                  VertexConsumer var31 = var2.getBuffer(RenderType.debugFilledBox());
                  var31.addVertex(var11, var24, var25, var23).setColor(-2130771968);
                  var31.addVertex(var11, var24, var22, var23).setColor(-2130771968);
                  var31.addVertex(var11, var21, var25, var23).setColor(-2130771968);
                  var31.addVertex(var11, var21, var22, var23).setColor(-2130771968);
               }

               if (var16.isFaceSturdy(var12, var15, Direction.DOWN)) {
                  VertexConsumer var32 = var2.getBuffer(RenderType.debugFilledBox());
                  var32.addVertex(var11, var21, var22, var23).setColor(-2130771968);
                  var32.addVertex(var11, var24, var22, var23).setColor(-2130771968);
                  var32.addVertex(var11, var21, var22, var26).setColor(-2130771968);
                  var32.addVertex(var11, var24, var22, var26).setColor(-2130771968);
               }

               if (var16.isFaceSturdy(var12, var15, Direction.UP)) {
                  VertexConsumer var33 = var2.getBuffer(RenderType.debugFilledBox());
                  var33.addVertex(var11, var21, var25, var23).setColor(-2130771968);
                  var33.addVertex(var11, var21, var25, var26).setColor(-2130771968);
                  var33.addVertex(var11, var24, var25, var23).setColor(-2130771968);
                  var33.addVertex(var11, var24, var25, var26).setColor(-2130771968);
               }
            }
         }
      }

   }
}
