package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
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
      Lighting.setupForEntityInInventory();
      Vector3f var3 = var1.translation();
      var2.translate(var3.x, var3.y, var3.z);
      var2.mulPose((Quaternionfc)var1.rotation());
      Quaternionf var4 = var1.overrideCameraAngle();
      if (var4 != null) {
         this.entityRenderDispatcher.overrideCameraOrientation(var4.conjugate(new Quaternionf()).rotateY(3.1415927F));
      }

      this.entityRenderDispatcher.setRenderShadow(false);
      this.entityRenderDispatcher.render(var1.renderState(), 0.0, 0.0, 0.0, var2, this.bufferSource, 15728880);
      this.entityRenderDispatcher.setRenderShadow(true);
   }

   protected float getTranslateY(int var1, int var2) {
      return (float)var1 / 2.0F;
   }

   protected String getTextureLabel() {
      return "entity";
   }
}
