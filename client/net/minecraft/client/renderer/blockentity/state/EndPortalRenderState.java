package net.minecraft.client.renderer.blockentity.state;

import java.util.EnumSet;
import net.minecraft.core.Direction;

public class EndPortalRenderState extends BlockEntityRenderState {
   public EnumSet<Direction> facesToShow = EnumSet.noneOf(Direction.class);

   public EndPortalRenderState() {
      super();
   }
}
