package net.minecraft.client.gui.screens.social;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityPortraitWidget extends AbstractWidget {
   private final EntityRenderState renderState;
   private final float rotationScale;

   public EntityPortraitWidget(final Component message, final int width, final int height, final float rotationScale, final EntityRenderState renderState) {
      super(0, 0, width, height, message);
      this.renderState = renderState;
      this.rotationScale = rotationScale;
      this.active = false;
   }

   public EntityPortraitWidget(final int width, final int height, final float rotationScale, final EntityRenderState renderState) {
      this(CommonComponents.GUI_CANCEL, width, height, rotationScale, renderState);
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      int x = this.getX();
      int y = this.getY();
      float size = (float)this.height * 0.45F;
      extractEntityInInventoryFollowsMouse(graphics, x, y, x + this.width, y + this.height, size, 0.0625F, this.rotationScale, (float)mouseX, (float)mouseY, this.renderState);
   }

   public static void extractEntityInInventoryFollowsMouse(final GuiGraphicsExtractor graphics, final int x0, final int y0, final int x1, final int y1, final float size, final float offsetY, final float rotationScale, final float mouseX, final float mouseY, final EntityRenderState renderState) {
      float translateY = renderState.boundingBoxHeight / 2.0F + offsetY;
      float centerX = (float)(x0 + x1) / 2.0F;
      float centerY = (float)(y0 + y1) / 2.0F + (translateY - renderState.eyeHeight) * size;
      float xAngle = (float)Math.atan((double)((centerX - mouseX) * rotationScale));
      float yAngle = (float)Math.atan((double)((centerY - mouseY) * rotationScale));
      Quaternionf rotation = (new Quaternionf()).rotateZ(3.1415927F);
      Quaternionf xRotation = (new Quaternionf()).rotateX(yAngle * 20.0F * 0.017453292F);
      rotation.mul(xRotation);
      if (renderState instanceof LivingEntityRenderState livingRenderState) {
         livingRenderState.bodyRot = 180.0F + xAngle * 20.0F;
         livingRenderState.yRot = xAngle * 20.0F;
         if (livingRenderState.pose != Pose.FALL_FLYING) {
            livingRenderState.xRot = -yAngle * 20.0F;
         } else {
            livingRenderState.xRot = 0.0F;
         }
      }

      Vector3f translation = new Vector3f(0.0F, translateY, 0.0F);
      graphics.entity(renderState, size, translation, rotation, xRotation, x0, y0, x1, y1);
   }

   public static void extractEntityInInventoryFollowsMouse(final GuiGraphicsExtractor graphics, final int x0, final int y0, final int x1, final int y1, final float size, final float offsetY, final float mouseX, final float mouseY, final Entity entity) {
      EntityRenderState renderState = extractRenderState(entity);
      extractEntityInInventoryFollowsMouse(graphics, x0, y0, x1, y1, size, offsetY, 0.025F, mouseX, mouseY, renderState);
   }

   private static EntityRenderState extractRenderState(final Entity entity) {
      EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      EntityRenderer<? super Entity, ?> renderer = entityRenderDispatcher.getRenderer(entity);
      EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
      renderState.shadowPieces.clear();
      renderState.outlineColor = 0;
      if (renderState instanceof LivingEntityRenderState livingRenderState) {
         livingRenderState.boundingBoxWidth /= livingRenderState.scale;
         livingRenderState.boundingBoxHeight /= livingRenderState.scale;
         livingRenderState.scale = 1.0F;
      }

      return renderState;
   }
}
