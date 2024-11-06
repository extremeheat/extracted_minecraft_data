package net.minecraft.client.resources.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public abstract class DelegateBakedModel implements BakedModel {
   protected final BakedModel parent;

   public DelegateBakedModel(BakedModel var1) {
      super();
      this.parent = var1;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
      return this.parent.getQuads(var1, var2, var3);
   }

   public boolean useAmbientOcclusion() {
      return this.parent.useAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.parent.isGui3d();
   }

   public boolean usesBlockLight() {
      return this.parent.usesBlockLight();
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.parent.getParticleIcon();
   }

   public ItemTransforms getTransforms() {
      return this.parent.getTransforms();
   }
}