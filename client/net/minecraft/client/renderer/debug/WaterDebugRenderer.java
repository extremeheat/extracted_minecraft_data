package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

public class WaterDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public WaterDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      BlockPos var11 = this.minecraft.player.blockPosition();
      Level var12 = this.minecraft.player.level();

      for(BlockPos var14 : BlockPos.betweenClosed(var11.offset(-10, -10, -10), var11.offset(10, 10, 10))) {
         FluidState var15 = var12.getFluidState(var14);
         if (var15.is(FluidTags.WATER)) {
            double var16 = (double)((float)var14.getY() + var15.getHeight(var12, var14));
            DebugRenderer.renderFilledBox(var1, var2, (new AABB((double)((float)var14.getX() + 0.01F), (double)((float)var14.getY() + 0.01F), (double)((float)var14.getZ() + 0.01F), (double)((float)var14.getX() + 0.99F), var16, (double)((float)var14.getZ() + 0.99F))).move(-var3, -var5, -var7), 0.0F, 1.0F, 0.0F, 0.15F);
         }
      }

      for(BlockPos var19 : BlockPos.betweenClosed(var11.offset(-10, -10, -10), var11.offset(10, 10, 10))) {
         FluidState var20 = var12.getFluidState(var19);
         if (var20.is(FluidTags.WATER)) {
            DebugRenderer.renderFloatingText(var1, var2, String.valueOf(var20.getAmount()), (double)var19.getX() + 0.5, (double)((float)var19.getY() + var20.getHeight(var12, var19)), (double)var19.getZ() + 0.5, -16777216);
         }
      }

   }
}
