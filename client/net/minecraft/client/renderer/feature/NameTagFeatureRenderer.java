package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

public class NameTagFeatureRenderer {
   public NameTagFeatureRenderer() {
      super();
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final Font font) {
      Storage storage = nodeCollection.getNameTagSubmits();
      storage.nameTagSubmitsSeethrough.sort(Comparator.comparing(SubmitNodeStorage.NameTagSubmit::distanceToCameraSq).reversed());

      for(SubmitNodeStorage.NameTagSubmit nameTag : storage.nameTagSubmitsSeethrough) {
         font.drawInBatch((Component)nameTag.text(), nameTag.x(), nameTag.y(), nameTag.color(), false, nameTag.pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, nameTag.backgroundColor(), nameTag.lightCoords());
      }

      for(SubmitNodeStorage.NameTagSubmit nameTag : storage.nameTagSubmitsNormal) {
         font.drawInBatch((Component)nameTag.text(), nameTag.x(), nameTag.y(), nameTag.color(), false, nameTag.pose(), bufferSource, Font.DisplayMode.NORMAL, nameTag.backgroundColor(), nameTag.lightCoords());
      }

   }

   public static class Storage {
      private final List<SubmitNodeStorage.NameTagSubmit> nameTagSubmitsSeethrough = new ArrayList();
      private final List<SubmitNodeStorage.NameTagSubmit> nameTagSubmitsNormal = new ArrayList();

      public Storage() {
         super();
      }

      public void add(final PoseStack poseStack, final @Nullable Vec3 nameTagAttachment, final int offset, final Component name, final boolean seeThrough, final int lightCoords, final double distanceToCameraSq, final CameraRenderState camera) {
         if (nameTagAttachment != null) {
            Minecraft minecraft = Minecraft.getInstance();
            poseStack.pushPose();
            poseStack.translate(nameTagAttachment.x, nameTagAttachment.y + 0.5, nameTagAttachment.z);
            poseStack.mulPose((Quaternionfc)camera.orientation);
            poseStack.scale(0.025F, -0.025F, 0.025F);
            Matrix4f pose = new Matrix4f(poseStack.last().pose());
            float x = (float)(-minecraft.font.width((FormattedText)name)) / 2.0F;
            int backgroundColor = (int)(minecraft.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
            if (seeThrough) {
               this.nameTagSubmitsNormal.add(new SubmitNodeStorage.NameTagSubmit(pose, x, (float)offset, name, LightCoordsUtil.lightCoordsWithEmission(lightCoords, 2), -1, 0, distanceToCameraSq));
               this.nameTagSubmitsSeethrough.add(new SubmitNodeStorage.NameTagSubmit(pose, x, (float)offset, name, lightCoords, -2130706433, backgroundColor, distanceToCameraSq));
            } else {
               this.nameTagSubmitsNormal.add(new SubmitNodeStorage.NameTagSubmit(pose, x, (float)offset, name, lightCoords, -2130706433, backgroundColor, distanceToCameraSq));
            }

            poseStack.popPose();
         }
      }

      public void clear() {
         this.nameTagSubmitsNormal.clear();
         this.nameTagSubmitsSeethrough.clear();
      }
   }
}
