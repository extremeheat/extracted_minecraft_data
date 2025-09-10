package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      Matrix4f var10 = var1.last().pose();
      Level var11 = this.minecraft.player.level();
      BlockPos var12 = BlockPos.containing(var3, var5, var7);

      for(BlockPos var14 : BlockPos.betweenClosed(var12.offset(-6, -6, -6), var12.offset(6, 6, 6))) {
         BlockState var15 = var11.getBlockState(var14);
         if (!var15.is(Blocks.AIR)) {
            VoxelShape var16 = var15.getShape(var11, var14);

            for(AABB var18 : var16.toAabbs()) {
               AABB var19 = var18.move(var14).inflate(0.002);
               float var20 = (float)(var19.minX - var3);
               float var21 = (float)(var19.minY - var5);
               float var22 = (float)(var19.minZ - var7);
               float var23 = (float)(var19.maxX - var3);
               float var24 = (float)(var19.maxY - var5);
               float var25 = (float)(var19.maxZ - var7);
               int var26 = -2130771968;
               if (var15.isFaceSturdy(var11, var14, Direction.WEST)) {
                  VertexConsumer var27 = var2.getBuffer(RenderType.debugFilledBox());
                  var27.addVertex(var10, var20, var21, var22).setColor(-2130771968);
                  var27.addVertex(var10, var20, var21, var25).setColor(-2130771968);
                  var27.addVertex(var10, var20, var24, var22).setColor(-2130771968);
                  var27.addVertex(var10, var20, var24, var25).setColor(-2130771968);
               }

               if (var15.isFaceSturdy(var11, var14, Direction.SOUTH)) {
                  VertexConsumer var28 = var2.getBuffer(RenderType.debugFilledBox());
                  var28.addVertex(var10, var20, var24, var25).setColor(-2130771968);
                  var28.addVertex(var10, var20, var21, var25).setColor(-2130771968);
                  var28.addVertex(var10, var23, var24, var25).setColor(-2130771968);
                  var28.addVertex(var10, var23, var21, var25).setColor(-2130771968);
               }

               if (var15.isFaceSturdy(var11, var14, Direction.EAST)) {
                  VertexConsumer var29 = var2.getBuffer(RenderType.debugFilledBox());
                  var29.addVertex(var10, var23, var21, var25).setColor(-2130771968);
                  var29.addVertex(var10, var23, var21, var22).setColor(-2130771968);
                  var29.addVertex(var10, var23, var24, var25).setColor(-2130771968);
                  var29.addVertex(var10, var23, var24, var22).setColor(-2130771968);
               }

               if (var15.isFaceSturdy(var11, var14, Direction.NORTH)) {
                  VertexConsumer var30 = var2.getBuffer(RenderType.debugFilledBox());
                  var30.addVertex(var10, var23, var24, var22).setColor(-2130771968);
                  var30.addVertex(var10, var23, var21, var22).setColor(-2130771968);
                  var30.addVertex(var10, var20, var24, var22).setColor(-2130771968);
                  var30.addVertex(var10, var20, var21, var22).setColor(-2130771968);
               }

               if (var15.isFaceSturdy(var11, var14, Direction.DOWN)) {
                  VertexConsumer var31 = var2.getBuffer(RenderType.debugFilledBox());
                  var31.addVertex(var10, var20, var21, var22).setColor(-2130771968);
                  var31.addVertex(var10, var23, var21, var22).setColor(-2130771968);
                  var31.addVertex(var10, var20, var21, var25).setColor(-2130771968);
                  var31.addVertex(var10, var23, var21, var25).setColor(-2130771968);
               }

               if (var15.isFaceSturdy(var11, var14, Direction.UP)) {
                  VertexConsumer var32 = var2.getBuffer(RenderType.debugFilledBox());
                  var32.addVertex(var10, var20, var24, var22).setColor(-2130771968);
                  var32.addVertex(var10, var20, var24, var25).setColor(-2130771968);
                  var32.addVertex(var10, var23, var24, var22).setColor(-2130771968);
                  var32.addVertex(var10, var23, var24, var25).setColor(-2130771968);
               }
            }
         }
      }

   }
}
