package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class OminousItemSpawnerRenderState extends EntityRenderState {
   @Nullable
   public BakedModel itemModel;
   public ItemStack item = ItemStack.EMPTY;

   public OminousItemSpawnerRenderState() {
      super();
   }
}
