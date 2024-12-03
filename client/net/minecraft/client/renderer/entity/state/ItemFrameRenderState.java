package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.saveddata.maps.MapId;

public class ItemFrameRenderState extends EntityRenderState {
   public Direction direction;
   public final ItemStackRenderState item;
   public int rotation;
   public boolean isGlowFrame;
   @Nullable
   public MapId mapId;
   public final MapRenderState mapRenderState;

   public ItemFrameRenderState() {
      super();
      this.direction = Direction.NORTH;
      this.item = new ItemStackRenderState();
      this.mapRenderState = new MapRenderState();
   }
}
