package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class ItemEntityRenderState extends EntityRenderState {
   public float bobOffset;
   @Nullable
   public BakedModel itemModel;
   public ItemStack item;

   public ItemEntityRenderState() {
      super();
      this.item = ItemStack.EMPTY;
   }
}
