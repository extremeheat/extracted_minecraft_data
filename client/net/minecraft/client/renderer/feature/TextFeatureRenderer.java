package net.minecraft.client.renderer.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.util.FormattedCharSequence;

public class TextFeatureRenderer {
   public TextFeatureRenderer() {
      super();
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource) {
      Font font = Minecraft.getInstance().font;

      for(SubmitNodeStorage.TextSubmit textSubmit : nodeCollection.getTextSubmits()) {
         if (textSubmit.outlineColor() == 0) {
            font.drawInBatch((FormattedCharSequence)textSubmit.string(), textSubmit.x(), textSubmit.y(), textSubmit.color(), textSubmit.dropShadow(), textSubmit.pose(), bufferSource, textSubmit.displayMode(), textSubmit.backgroundColor(), textSubmit.lightCoords());
         } else {
            font.drawInBatch8xOutline(textSubmit.string(), textSubmit.x(), textSubmit.y(), textSubmit.color(), textSubmit.outlineColor(), textSubmit.pose(), bufferSource, textSubmit.lightCoords());
         }
      }

   }
}
