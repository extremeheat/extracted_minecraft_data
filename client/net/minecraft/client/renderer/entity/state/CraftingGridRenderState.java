package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.item.ItemStackRenderState;

public class CraftingGridRenderState extends EntityRenderState {
   public int size;
   public float rotation;
   public ItemStackRenderState[] ghosts = new ItemStackRenderState[9];

   public CraftingGridRenderState() {
      super();
   }
}
