package net.minecraft.client.renderer.entity.state;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class CushionRenderState extends EntityRenderState {
   private static final Identifier DEFAULT_TEXTURE = Identifier.withDefaultNamespace("textures/entity/cushion/white_cushion.png");
   public Direction direction;
   public Identifier texture;

   public CushionRenderState() {
      super();
      this.direction = Direction.NORTH;
      this.texture = DEFAULT_TEXTURE;
   }
}
