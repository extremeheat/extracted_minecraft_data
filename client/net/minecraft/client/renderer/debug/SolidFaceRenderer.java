package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Matrix4f var9 = var1.last().pose();
      Level var10 = this.minecraft.player.level();
      BlockPos var11 = BlockPos.containing(var3, var5, var7);
      Iterator var12 = BlockPos.betweenClosed(var11.offset(-6, -6, -6), var11.offset(6, 6, 6)).iterator();

      while(true) {
         BlockPos var13;
         BlockState var14;
         do {
            if (!var12.hasNext()) {
               return;
            }

            var13 = (BlockPos)var12.next();
            var14 = var10.getBlockState(var13);
         } while(var14.is(Blocks.AIR));

         VoxelShape var15 = var14.getShape(var10, var13);
         Iterator var16 = var15.toAabbs().iterator();

         while(var16.hasNext()) {
            AABB var17 = (AABB)var16.next();
            AABB var18 = var17.move(var13).inflate(0.002);
            float var19 = (float)(var18.minX - var3);
            float var20 = (float)(var18.minY - var5);
            float var21 = (float)(var18.minZ - var7);
            float var22 = (float)(var18.maxX - var3);
            float var23 = (float)(var18.maxY - var5);
            float var24 = (float)(var18.maxZ - var7);
            int var25 = -2130771968;
            VertexConsumer var26;
            if (var14.isFaceSturdy(var10, var13, Direction.WEST)) {
               var26 = var2.getBuffer(RenderType.debugFilledBox());
               var26.addVertex(var9, var19, var20, var21).setColor(-2130771968);
               var26.addVertex(var9, var19, var20, var24).setColor(-2130771968);
               var26.addVertex(var9, var19, var23, var21).setColor(-2130771968);
               var26.addVertex(var9, var19, var23, var24).setColor(-2130771968);
            }

            if (var14.isFaceSturdy(var10, var13, Direction.SOUTH)) {
               var26 = var2.getBuffer(RenderType.debugFilledBox());
               var26.addVertex(var9, var19, var23, var24).setColor(-2130771968);
               var26.addVertex(var9, var19, var20, var24).setColor(-2130771968);
               var26.addVertex(var9, var22, var23, var24).setColor(-2130771968);
               var26.addVertex(var9, var22, var20, var24).setColor(-2130771968);
            }

            if (var14.isFaceSturdy(var10, var13, Direction.EAST)) {
               var26 = var2.getBuffer(RenderType.debugFilledBox());
               var26.addVertex(var9, var22, var20, var24).setColor(-2130771968);
               var26.addVertex(var9, var22, var20, var21).setColor(-2130771968);
               var26.addVertex(var9, var22, var23, var24).setColor(-2130771968);
               var26.addVertex(var9, var22, var23, var21).setColor(-2130771968);
            }

            if (var14.isFaceSturdy(var10, var13, Direction.NORTH)) {
               var26 = var2.getBuffer(RenderType.debugFilledBox());
               var26.addVertex(var9, var22, var23, var21).setColor(-2130771968);
               var26.addVertex(var9, var22, var20, var21).setColor(-2130771968);
               var26.addVertex(var9, var19, var23, var21).setColor(-2130771968);
               var26.addVertex(var9, var19, var20, var21).setColor(-2130771968);
            }

            if (var14.isFaceSturdy(var10, var13, Direction.DOWN)) {
               var26 = var2.getBuffer(RenderType.debugFilledBox());
               var26.addVertex(var9, var19, var20, var21).setColor(-2130771968);
               var26.addVertex(var9, var22, var20, var21).setColor(-2130771968);
               var26.addVertex(var9, var19, var20, var24).setColor(-2130771968);
               var26.addVertex(var9, var22, var20, var24).setColor(-2130771968);
            }

            if (var14.isFaceSturdy(var10, var13, Direction.UP)) {
               var26 = var2.getBuffer(RenderType.debugFilledBox());
               var26.addVertex(var9, var19, var23, var21).setColor(-2130771968);
               var26.addVertex(var9, var19, var23, var24).setColor(-2130771968);
               var26.addVertex(var9, var22, var23, var21).setColor(-2130771968);
               var26.addVertex(var9, var22, var23, var24).setColor(-2130771968);
            }
         }
      }
   }
}
