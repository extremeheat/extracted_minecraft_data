package net.minecraft.client.renderer.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.util.FormattedCharSequence;

public class TextFeatureRenderer {
   public TextFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeStorage var1, MultiBufferSource.BufferSource var2) {
      Font var3 = Minecraft.getInstance().font;

      for(SubmitNodeStorage.TextSubmit var5 : var1.getTextSubmits()) {
         var3.drawInBatch((FormattedCharSequence)var5.string(), var5.x(), var5.y(), var5.color(), var5.dropShadow(), var5.pose(), var2, var5.displayMode(), var5.backgroundColor(), var5.lightCoords());
      }

   }
}
