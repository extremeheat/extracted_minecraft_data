package net.minecraft.client.renderer.blockentity.state;

import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.SignText;

public class SignRenderState extends BlockEntityRenderState {
   @Nullable
   public SignText frontText;
   @Nullable
   public SignText backText;
   public int textLineHeight;
   public int maxTextLineWidth;
   public boolean isTextFilteringEnabled;
   public boolean drawOutline;

   public SignRenderState() {
      super();
   }
}
