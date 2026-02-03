package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;

public class TheEndPortalRenderer extends AbstractEndPortalRenderer<TheEndPortalBlockEntity, EndPortalRenderState> {
   public TheEndPortalRenderer() {
      super();
   }

   public EndPortalRenderState createRenderState() {
      return new EndPortalRenderState();
   }
}
