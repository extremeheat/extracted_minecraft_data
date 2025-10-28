package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.Display;
import org.jspecify.annotations.Nullable;

public class TextDisplayEntityRenderState extends DisplayEntityRenderState {
   public Display.TextDisplay.@Nullable TextRenderState textRenderState;
   public Display.TextDisplay.@Nullable CachedInfo cachedInfo;

   public TextDisplayEntityRenderState() {
      super();
   }

   public boolean hasSubState() {
      return this.textRenderState != null;
   }
}
