package net.minecraft.client.renderer.entity.state;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

public class CatRenderState extends FelineRenderState {
   private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/cat/tabby.png");
   public ResourceLocation texture;
   public boolean isLyingOnTopOfSleepingPlayer;
   public @Nullable DyeColor collarColor;

   public CatRenderState() {
      super();
      this.texture = DEFAULT_TEXTURE;
   }
}
