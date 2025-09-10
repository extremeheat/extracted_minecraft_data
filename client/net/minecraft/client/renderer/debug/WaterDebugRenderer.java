package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      BlockPos var10 = this.minecraft.player.blockPosition();
      Level var11 = this.minecraft.player.level();

      for(BlockPos var13 : BlockPos.betweenClosed(var10.offset(-10, -10, -10), var10.offset(10, 10, 10))) {
         FluidState var14 = var11.getFluidState(var13);
         if (var14.is(FluidTags.WATER)) {
            double var15 = (double)((float)var13.getY() + var14.getHeight(var11, var13));
            DebugRenderer.renderFilledBox(var1, var2, (new AABB((double)((float)var13.getX() + 0.01F), (double)((float)var13.getY() + 0.01F), (double)((float)var13.getZ() + 0.01F), (double)((float)var13.getX() + 0.99F), var15, (double)((float)var13.getZ() + 0.99F))).move(-var3, -var5, -var7), 0.0F, 1.0F, 0.0F, 0.15F);
         }
      }

      for(BlockPos var18 : BlockPos.betweenClosed(var10.offset(-10, -10, -10), var10.offset(10, 10, 10))) {
         FluidState var19 = var11.getFluidState(var18);
         if (var19.is(FluidTags.WATER)) {
            DebugRenderer.renderFloatingText(var1, var2, String.valueOf(var19.getAmount()), (double)var18.getX() + 0.5, (double)((float)var18.getY() + var19.getHeight(var11, var18)), (double)var18.getZ() + 0.5, -16777216);
         }
      }

   }
}
