package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class GuiEntityRenderer extends PictureInPictureRenderer<GuiEntityRenderState> {
   private final EntityRenderDispatcher entityRenderDispatcher;

   public GuiEntityRenderer(MultiBufferSource.BufferSource var1, EntityRenderDispatcher var2) {
      super(var1);
      this.entityRenderDispatcher = var2;
   }

   public Class<GuiEntityRenderState> getRenderStateClass() {
      return GuiEntityRenderState.class;
   }

   protected void renderToTexture(GuiEntityRenderState var1, PoseStack var2) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
      Vector3f var3 = var1.translation();
      var2.translate(var3.x, var3.y, var3.z);
      var2.mulPose((Quaternionfc)var1.rotation());
      Quaternionf var4 = var1.overrideCameraAngle();
      FeatureRenderDispatcher var5 = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
      CameraRenderState var6 = new CameraRenderState();
      if (var4 != null) {
         var6.orientation = var4.conjugate(new Quaternionf()).rotateY(3.1415927F);
      }

      this.entityRenderDispatcher.submit(var1.renderState(), var6, 0.0, 0.0, 0.0, var2, var5.getSubmitNodeStorage());
      var5.renderAllFeatures();
   }

   protected float getTranslateY(int var1, int var2) {
      return (float)var1 / 2.0F;
   }

   protected String getTextureLabel() {
      return "entity";
   }
}
