package net.minecraft.client.renderer.blockentity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;

public class VaultRenderState extends BlockEntityRenderState {
   @Nullable
   public ItemClusterRenderState displayItem;
   public float spin;

   public VaultRenderState() {
      super();
   }
}
