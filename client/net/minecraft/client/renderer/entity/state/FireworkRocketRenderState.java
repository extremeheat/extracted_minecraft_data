package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class FireworkRocketRenderState extends EntityRenderState {
   public boolean isShotAtAngle;
   @Nullable
   public BakedModel itemModel;
   public ItemStack item;

   public FireworkRocketRenderState() {
      super();
      this.item = ItemStack.EMPTY;
   }
}
