package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class OminousItemSpawnerRenderState extends EntityRenderState {
   @Nullable
   public BakedModel itemModel;
   public ItemStack item;

   public OminousItemSpawnerRenderState() {
      super();
      this.item = ItemStack.EMPTY;
   }
}
