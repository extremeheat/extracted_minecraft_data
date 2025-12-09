package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.TridentModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import org.joml.Quaternionfc;

public class ThrownTridentRenderer extends EntityRenderer<ThrownTrident, ThrownTridentRenderState> {
   public static final Identifier TRIDENT_LOCATION = Identifier.withDefaultNamespace("textures/entity/trident.png");
   private final TridentModel model;

   public ThrownTridentRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new TridentModel(var1.bakeLayer(ModelLayers.TRIDENT));
   }

   public void submit(ThrownTridentRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var1.yRot - 90.0F));
      var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(var1.xRot + 90.0F));
      List var5 = ItemRenderer.getFoilRenderTypes(this.model.renderType(TRIDENT_LOCATION), false, var1.isFoil);

      for(int var6 = 0; var6 < var5.size(); ++var6) {
         var3.order(var6).submitModel(this.model, Unit.INSTANCE, var2, (RenderType)var5.get(var6), var1.lightCoords, OverlayTexture.NO_OVERLAY, -1, (TextureAtlasSprite)null, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }

      var2.popPose();
      super.submit(var1, var2, var3, var4);
   }

   public ThrownTridentRenderState createRenderState() {
      return new ThrownTridentRenderState();
   }

   public void extractRenderState(ThrownTrident var1, ThrownTridentRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.yRot = var1.getYRot(var3);
      var2.xRot = var1.getXRot(var3);
      var2.isFoil = var1.isFoil();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
