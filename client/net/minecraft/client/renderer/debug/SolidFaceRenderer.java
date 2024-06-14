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
      Level var10 = this.minecraft.player.level();
      BlockPos var11 = BlockPos.containing(var3, var5, var7);

      for (BlockPos var13 : BlockPos.betweenClosed(var11.offset(-6, -6, -6), var11.offset(6, 6, 6))) {
         BlockState var14 = var10.getBlockState(var13);
         if (!var14.is(Blocks.AIR)) {
            VoxelShape var15 = var14.getShape(var10, var13);

            for (AABB var17 : var15.toAabbs()) {
               AABB var18 = var17.move(var13).inflate(0.002);
               float var19 = (float)(var18.minX - var3);
               float var20 = (float)(var18.minY - var5);
               float var21 = (float)(var18.minZ - var7);
               float var22 = (float)(var18.maxX - var3);
               float var23 = (float)(var18.maxY - var5);
               float var24 = (float)(var18.maxZ - var7);
               int var25 = -2130771968;
               if (var14.isFaceSturdy(var10, var13, Direction.WEST)) {
                  VertexConsumer var26 = var2.getBuffer(RenderType.debugFilledBox());
                  var26.addVertex(var9, var19, var20, var21).setColor(-2130771968);
                  var26.addVertex(var9, var19, var20, var24).setColor(-2130771968);
                  var26.addVertex(var9, var19, var23, var21).setColor(-2130771968);
                  var26.addVertex(var9, var19, var23, var24).setColor(-2130771968);
               }

               if (var14.isFaceSturdy(var10, var13, Direction.SOUTH)) {
                  VertexConsumer var27 = var2.getBuffer(RenderType.debugFilledBox());
                  var27.addVertex(var9, var19, var23, var24).setColor(-2130771968);
                  var27.addVertex(var9, var19, var20, var24).setColor(-2130771968);
                  var27.addVertex(var9, var22, var23, var24).setColor(-2130771968);
                  var27.addVertex(var9, var22, var20, var24).setColor(-2130771968);
               }

               if (var14.isFaceSturdy(var10, var13, Direction.EAST)) {
                  VertexConsumer var28 = var2.getBuffer(RenderType.debugFilledBox());
                  var28.addVertex(var9, var22, var20, var24).setColor(-2130771968);
                  var28.addVertex(var9, var22, var20, var21).setColor(-2130771968);
                  var28.addVertex(var9, var22, var23, var24).setColor(-2130771968);
                  var28.addVertex(var9, var22, var23, var21).setColor(-2130771968);
               }

               if (var14.isFaceSturdy(var10, var13, Direction.NORTH)) {
                  VertexConsumer var29 = var2.getBuffer(RenderType.debugFilledBox());
                  var29.addVertex(var9, var22, var23, var21).setColor(-2130771968);
                  var29.addVertex(var9, var22, var20, var21).setColor(-2130771968);
                  var29.addVertex(var9, var19, var23, var21).setColor(-2130771968);
                  var29.addVertex(var9, var19, var20, var21).setColor(-2130771968);
               }

               if (var14.isFaceSturdy(var10, var13, Direction.DOWN)) {
                  VertexConsumer var30 = var2.getBuffer(RenderType.debugFilledBox());
                  var30.addVertex(var9, var19, var20, var21).setColor(-2130771968);
                  var30.addVertex(var9, var22, var20, var21).setColor(-2130771968);
                  var30.addVertex(var9, var19, var20, var24).setColor(-2130771968);
                  var30.addVertex(var9, var22, var20, var24).setColor(-2130771968);
               }

               if (var14.isFaceSturdy(var10, var13, Direction.UP)) {
                  VertexConsumer var31 = var2.getBuffer(RenderType.debugFilledBox());
                  var31.addVertex(var9, var19, var23, var21).setColor(-2130771968);
                  var31.addVertex(var9, var19, var23, var24).setColor(-2130771968);
                  var31.addVertex(var9, var22, var23, var21).setColor(-2130771968);
                  var31.addVertex(var9, var22, var23, var24).setColor(-2130771968);
               }
            }
         }
      }
   }
}
