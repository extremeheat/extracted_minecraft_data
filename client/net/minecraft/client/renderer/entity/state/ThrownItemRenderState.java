package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class ThrownItemRenderState extends EntityRenderState {
   @Nullable
   public BakedModel itemModel;
   public ItemStack item;

   public ThrownItemRenderState() {
      super();
      this.item = ItemStack.EMPTY;
   }
}
