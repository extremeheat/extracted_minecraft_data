package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.world.level.block.entity.SignText;
import org.jspecify.annotations.Nullable;

public class SignRenderState extends BlockEntityRenderState {
   public @Nullable SignText frontText;
   public @Nullable SignText backText;
   public int textLineHeight;
   public int maxTextLineWidth;
   public boolean isTextFilteringEnabled;
   public boolean drawOutline;

   public SignRenderState() {
      super();
   }
}
