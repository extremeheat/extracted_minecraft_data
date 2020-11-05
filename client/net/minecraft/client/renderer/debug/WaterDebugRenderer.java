package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
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
      Level var10 = this.minecraft.player.level;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      RenderSystem.lineWidth(6.0F);
      Iterator var11 = BlockPos.betweenClosed(var9.offset(-10, -10, -10), var9.offset(10, 10, 10)).iterator();

      BlockPos var12;
      FluidState var13;
      while(var11.hasNext()) {
         var12 = (BlockPos)var11.next();
         var13 = var10.getFluidState(var12);
         if (var13.is(FluidTags.WATER)) {
            double var14 = (double)((float)var12.getY() + var13.getHeight(var10, var12));
            DebugRenderer.renderFilledBox((new AABB((double)((float)var12.getX() + 0.01F), (double)((float)var12.getY() + 0.01F), (double)((float)var12.getZ() + 0.01F), (double)((float)var12.getX() + 0.99F), var14, (double)((float)var12.getZ() + 0.99F))).move(-var3, -var5, -var7), 1.0F, 1.0F, 1.0F, 0.2F);
         }
      }

      var11 = BlockPos.betweenClosed(var9.offset(-10, -10, -10), var9.offset(10, 10, 10)).iterator();

      while(var11.hasNext()) {
         var12 = (BlockPos)var11.next();
         var13 = var10.getFluidState(var12);
         if (var13.is(FluidTags.WATER)) {
            DebugRenderer.renderFloatingText(String.valueOf(var13.getAmount()), (double)var12.getX() + 0.5D, (double)((float)var12.getY() + var13.getHeight(var10, var12)), (double)var12.getZ() + 0.5D, -16777216);
         }
      }

      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }
}
