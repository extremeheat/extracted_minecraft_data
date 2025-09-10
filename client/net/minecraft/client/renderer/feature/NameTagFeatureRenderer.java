package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

public class NameTagFeatureRenderer {
   public NameTagFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2, Font var3) {
      Storage var4 = var1.getNameTagSubmits();
      var4.nameTagSubmitsSeethrough.sort(Comparator.comparing(SubmitNodeStorage.NameTagSubmit::distanceToCameraSq).reversed());

      for(SubmitNodeStorage.NameTagSubmit var6 : var4.nameTagSubmitsSeethrough) {
         var3.drawInBatch((Component)var6.text(), var6.x(), var6.y(), var6.color(), false, var6.pose(), var2, Font.DisplayMode.SEE_THROUGH, var6.backgroundColor(), var6.lightCoords());
      }

      for(SubmitNodeStorage.NameTagSubmit var8 : var4.nameTagSubmitsNormal) {
         var3.drawInBatch((Component)var8.text(), var8.x(), var8.y(), var8.color(), false, var8.pose(), var2, Font.DisplayMode.NORMAL, var8.backgroundColor(), var8.lightCoords());
      }

   }

   public static class Storage {
      final List<SubmitNodeStorage.NameTagSubmit> nameTagSubmitsSeethrough = new ArrayList();
      final List<SubmitNodeStorage.NameTagSubmit> nameTagSubmitsNormal = new ArrayList();

      public Storage() {
         super();
      }

      public void add(PoseStack var1, @Nullable Vec3 var2, Component var3, boolean var4, int var5, double var6) {
         if (var2 != null) {
            int var8 = "deadmau5".equals(var3.getString()) ? -10 : 0;
            Minecraft var9 = Minecraft.getInstance();
            var1.pushPose();
            var1.translate(var2.x, var2.y + 0.5, var2.z);
            var1.mulPose((Quaternionfc)var9.getEntityRenderDispatcher().cameraOrientation());
            var1.scale(0.025F, -0.025F, 0.025F);
            Matrix4f var10 = new Matrix4f(var1.last().pose());
            float var11 = (float)(-var9.font.width((FormattedText)var3)) / 2.0F;
            int var12 = (int)(var9.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
            if (var4) {
               this.nameTagSubmitsNormal.add(new SubmitNodeStorage.NameTagSubmit(var10, var11, (float)var8, var3, LightTexture.lightCoordsWithEmission(var5, 2), -1, 0, var6));
               this.nameTagSubmitsSeethrough.add(new SubmitNodeStorage.NameTagSubmit(var10, var11, (float)var8, var3, var5, -2130706433, var12, var6));
            } else {
               this.nameTagSubmitsNormal.add(new SubmitNodeStorage.NameTagSubmit(var10, var11, (float)var8, var3, var5, -2130706433, var12, var6));
            }

            var1.popPose();
         }
      }

      public void clear() {
         this.nameTagSubmitsNormal.clear();
         this.nameTagSubmitsSeethrough.clear();
      }
   }
}
