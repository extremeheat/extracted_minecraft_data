package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Display;

public class TextDisplayEntityRenderState extends DisplayEntityRenderState {
   @Nullable
   public Display.TextDisplay.TextRenderState textRenderState;
   @Nullable
   public Display.TextDisplay.CachedInfo cachedInfo;

   public TextDisplayEntityRenderState() {
      super();
   }

   @Override
   public boolean hasSubState() {
      return this.textRenderState != null;
   }
}
