package net.minecraft.client.resources.model.sprite;

import java.util.function.Function;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public record SpriteId(Identifier atlasLocation, Identifier texture) {
   public SpriteId {
      super();
   }

   public RenderType renderType(final Function<Identifier, RenderType> renderType) {
      return (RenderType)renderType.apply(this.atlasLocation);
   }
}
