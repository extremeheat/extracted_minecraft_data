package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Display;

public class ItemDisplayEntityRenderState extends DisplayEntityRenderState {
   @Nullable
   public Display.ItemDisplay.ItemRenderState itemRenderState;
   @Nullable
   public BakedModel itemModel;

   public ItemDisplayEntityRenderState() {
      super();
   }

   @Override
   public boolean hasSubState() {
      return this.itemRenderState != null && this.itemModel != null;
   }
}
