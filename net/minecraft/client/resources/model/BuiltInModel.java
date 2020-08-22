package net.minecraft.client.resources.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class BuiltInModel implements BakedModel {
   private final ItemTransforms itemTransforms;
   private final ItemOverrides overrides;
   private final TextureAtlasSprite particleTexture;

   public BuiltInModel(ItemTransforms var1, ItemOverrides var2, TextureAtlasSprite var3) {
      this.itemTransforms = var1;
      this.overrides = var2;
      this.particleTexture = var3;
   }

   public List getQuads(@Nullable BlockState var1, @Nullable Direction var2, Random var3) {
      return Collections.emptyList();
   }

   public boolean useAmbientOcclusion() {
      return false;
   }

   public boolean isGui3d() {
      return true;
   }

   public boolean isCustomRenderer() {
      return true;
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.particleTexture;
   }

   public ItemTransforms getTransforms() {
      return this.itemTransforms;
   }

   public ItemOverrides getOverrides() {
      return this.overrides;
   }
}
