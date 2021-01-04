package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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

   public void render(long var1) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      double var4 = var3.getPosition().x;
      double var6 = var3.getPosition().y;
      double var8 = var3.getPosition().z;
      BlockPos var10 = this.minecraft.player.getCommandSenderBlockPosition();
      Level var11 = this.minecraft.player.level;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      GlStateManager.disableTexture();
      GlStateManager.lineWidth(6.0F);
      Iterator var12 = BlockPos.betweenClosed(var10.offset(-10, -10, -10), var10.offset(10, 10, 10)).iterator();

      BlockPos var13;
      FluidState var14;
      while(var12.hasNext()) {
         var13 = (BlockPos)var12.next();
         var14 = var11.getFluidState(var13);
         if (var14.is(FluidTags.WATER)) {
            double var15 = (double)((float)var13.getY() + var14.getHeight(var11, var13));
            DebugRenderer.renderFilledBox((new AABB((double)((float)var13.getX() + 0.01F), (double)((float)var13.getY() + 0.01F), (double)((float)var13.getZ() + 0.01F), (double)((float)var13.getX() + 0.99F), var15, (double)((float)var13.getZ() + 0.99F))).move(-var4, -var6, -var8), 1.0F, 1.0F, 1.0F, 0.2F);
         }
      }

      var12 = BlockPos.betweenClosed(var10.offset(-10, -10, -10), var10.offset(10, 10, 10)).iterator();

      while(var12.hasNext()) {
         var13 = (BlockPos)var12.next();
         var14 = var11.getFluidState(var13);
         if (var14.is(FluidTags.WATER)) {
            DebugRenderer.renderFloatingText(String.valueOf(var14.getAmount()), (double)var13.getX() + 0.5D, (double)((float)var13.getY() + var14.getHeight(var11, var13)), (double)var13.getZ() + 0.5D, -16777216);
         }
      }

      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
   }
}
