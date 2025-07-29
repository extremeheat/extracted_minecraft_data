package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;
import org.joml.Quaternionfc;

public class TntRenderer extends EntityRenderer<PrimedTnt, TntRenderState> {
   public TntRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
   }

   public void submit(TntRenderState var1, PoseStack var2, SubmitNodeCollector var3) {
      var2.pushPose();
      var2.translate(0.0F, 0.5F, 0.0F);
      float var4 = var1.fuseRemainingInTicks;
      if (var1.fuseRemainingInTicks < 10.0F) {
         float var5 = 1.0F - var1.fuseRemainingInTicks / 10.0F;
         var5 = Mth.clamp(var5, 0.0F, 1.0F);
         var5 *= var5;
         var5 *= var5;
         float var6 = 1.0F + var5 * 0.3F;
         var2.scale(var6, var6, var6);
      }

      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-90.0F));
      var2.translate(-0.5F, -0.5F, 0.5F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0F));
      if (var1.blockState != null) {
         TntMinecartRenderer.submitWhiteSolidBlock(var1.blockState, var2, var3, var1.lightCoords, (int)var4 / 5 % 2 == 0);
      }

      var2.popPose();
      super.submit(var1, var2, var3);
   }

   public TntRenderState createRenderState() {
      return new TntRenderState();
   }

   public void extractRenderState(PrimedTnt var1, TntRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.fuseRemainingInTicks = (float)var1.getFuse() - var3 + 1.0F;
      var2.blockState = var1.getBlockState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
