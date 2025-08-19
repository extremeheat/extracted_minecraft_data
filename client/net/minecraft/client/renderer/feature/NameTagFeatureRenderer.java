package net.minecraft.client.renderer.feature;

import java.util.Comparator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.network.chat.Component;

public class NameTagFeatureRenderer {
   public NameTagFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2, Font var3) {
      var1.getNameTagSubmitsSeethrough().sort(Comparator.comparing(SubmitNodeStorage.NameTagSubmit::distanceToCameraSq).reversed());

      for(SubmitNodeStorage.NameTagSubmit var5 : var1.getNameTagSubmitsSeethrough()) {
         var3.drawInBatch((Component)var5.text(), var5.x(), var5.y(), var5.color(), false, var5.pose(), var2, Font.DisplayMode.SEE_THROUGH, var5.backgroundColor(), var5.lightCoords());
      }

      for(SubmitNodeStorage.NameTagSubmit var7 : var1.getNameTagSubmitsNormal()) {
         var3.drawInBatch((Component)var7.text(), var7.x(), var7.y(), var7.color(), false, var7.pose(), var2, Font.DisplayMode.NORMAL, var7.backgroundColor(), var7.lightCoords());
      }

   }
}
