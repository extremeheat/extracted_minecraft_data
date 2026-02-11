package net.minecraft.client.resources.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public record SpriteId(Identifier atlasLocation, Identifier texture) {
   public SpriteId {
      super();
   }

   public RenderType renderType(final Function<Identifier, RenderType> renderType) {
      return (RenderType)renderType.apply(this.atlasLocation);
   }

   public VertexConsumer buffer(final SpriteGetter sprites, final MultiBufferSource bufferSource, final Function<Identifier, RenderType> renderType) {
      return sprites.get(this).wrap(bufferSource.getBuffer(this.renderType(renderType)));
   }

   public VertexConsumer buffer(final SpriteGetter sprites, final MultiBufferSource bufferSource, final Function<Identifier, RenderType> renderType, final boolean sheeted, final boolean hasFoil) {
      return sprites.get(this).wrap(ItemRenderer.getFoilBuffer(bufferSource, this.renderType(renderType), sheeted, hasFoil));
   }
}
