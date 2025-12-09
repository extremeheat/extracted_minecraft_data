package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.llama.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LlamaSpitRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.LlamaSpit;
import org.joml.Quaternionfc;

public class LlamaSpitRenderer extends EntityRenderer<LlamaSpit, LlamaSpitRenderState> {
   private static final Identifier LLAMA_SPIT_LOCATION = Identifier.withDefaultNamespace("textures/entity/llama/spit.png");
   private final LlamaSpitModel model;

   public LlamaSpitRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new LlamaSpitModel(var1.bakeLayer(ModelLayers.LLAMA_SPIT));
   }

   public void submit(LlamaSpitRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      var2.translate(0.0F, 0.15F, 0.0F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var1.yRot - 90.0F));
      var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(var1.xRot));
      var3.submitModel(this.model, var1, var2, this.model.renderType(LLAMA_SPIT_LOCATION), var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      var2.popPose();
      super.submit(var1, var2, var3, var4);
   }

   public LlamaSpitRenderState createRenderState() {
      return new LlamaSpitRenderState();
   }

   public void extractRenderState(LlamaSpit var1, LlamaSpitRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.xRot = var1.getXRot(var3);
      var2.yRot = var1.getYRot(var3);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
