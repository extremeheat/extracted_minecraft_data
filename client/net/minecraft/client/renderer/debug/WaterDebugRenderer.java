package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

public class WaterDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public WaterDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      BlockPos var9 = this.minecraft.player.blockPosition();
      Level var10 = this.minecraft.player.level();

      for(BlockPos var12 : BlockPos.betweenClosed(var9.offset(-10, -10, -10), var9.offset(10, 10, 10))) {
         FluidState var13 = var10.getFluidState(var12);
         if (var13.is(FluidTags.WATER)) {
            double var14 = (double)((float)var12.getY() + var13.getHeight(var10, var12));
            DebugRenderer.renderFilledBox(var1, var2, (new AABB((double)((float)var12.getX() + 0.01F), (double)((float)var12.getY() + 0.01F), (double)((float)var12.getZ() + 0.01F), (double)((float)var12.getX() + 0.99F), var14, (double)((float)var12.getZ() + 0.99F))).move(-var3, -var5, -var7), 0.0F, 1.0F, 0.0F, 0.15F);
         }
      }

      for(BlockPos var17 : BlockPos.betweenClosed(var9.offset(-10, -10, -10), var9.offset(10, 10, 10))) {
         FluidState var18 = var10.getFluidState(var17);
         if (var18.is(FluidTags.WATER)) {
            DebugRenderer.renderFloatingText(var1, var2, String.valueOf(var18.getAmount()), (double)var17.getX() + 0.5, (double)((float)var17.getY() + var18.getHeight(var10, var17)), (double)var17.getZ() + 0.5, -16777216);
         }
      }

   }
}
