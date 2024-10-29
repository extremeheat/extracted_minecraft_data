package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapId;

public class ItemFrameRenderState extends EntityRenderState {
   public Direction direction;
   public ItemStack itemStack;
   public int rotation;
   public boolean isGlowFrame;
   @Nullable
   public BakedModel itemModel;
   @Nullable
   public MapId mapId;
   public final MapRenderState mapRenderState;

   public ItemFrameRenderState() {
      super();
      this.direction = Direction.NORTH;
      this.itemStack = ItemStack.EMPTY;
      this.mapRenderState = new MapRenderState();
   }
}
