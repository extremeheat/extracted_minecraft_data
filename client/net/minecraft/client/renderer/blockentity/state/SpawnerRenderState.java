package net.minecraft.client.renderer.blockentity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SpawnerRenderState extends BlockEntityRenderState {
   @Nullable
   public EntityRenderState displayEntity;
   public float spin;
   public float scale;

   public SpawnerRenderState() {
      super();
   }
}
