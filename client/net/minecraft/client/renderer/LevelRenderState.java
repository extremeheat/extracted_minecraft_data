package net.minecraft.client.renderer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class LevelRenderState {
   public final List<EntityRenderState> entityRenderStates = new ArrayList();
   public final List<BlockEntityRenderState> blockEntityRenderStates = new ArrayList();
   public boolean haveGlowingEntities;

   public LevelRenderState() {
      super();
   }

   public void reset() {
      this.entityRenderStates.clear();
      this.blockEntityRenderStates.clear();
      this.haveGlowingEntities = false;
   }
}
