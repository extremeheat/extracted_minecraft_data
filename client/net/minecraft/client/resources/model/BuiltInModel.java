package net.minecraft.client.resources.model;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BuiltInModel implements BakedModel {
   private final ItemTransforms itemTransforms;
   private final TextureAtlasSprite particleTexture;
   private final boolean usesBlockLight;

   public BuiltInModel(ItemTransforms var1, TextureAtlasSprite var2, boolean var3) {
      super();
      this.itemTransforms = var1;
      this.particleTexture = var2;
      this.usesBlockLight = var3;
   }

   @Override
   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
      return Collections.emptyList();
   }

   @Override
   public boolean useAmbientOcclusion() {
      return false;
   }

   @Override
   public boolean isGui3d() {
      return true;
   }

   @Override
   public boolean usesBlockLight() {
      return this.usesBlockLight;
   }

   @Override
   public boolean isCustomRenderer() {
      return true;
   }

   @Override
   public TextureAtlasSprite getParticleIcon() {
      return this.particleTexture;
   }

   @Override
   public ItemTransforms getTransforms() {
      return this.itemTransforms;
   }
}
