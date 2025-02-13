package net.minecraft.client.renderer.block.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public record SimpleModelWrapper(QuadCollection quads, boolean useAmbientOcclusion, TextureAtlasSprite particleIcon) implements BlockStateModel {
   public SimpleModelWrapper(QuadCollection var1, boolean var2, TextureAtlasSprite var3) {
      super();
      this.quads = var1;
      this.useAmbientOcclusion = var2;
      this.particleIcon = var3;
   }

   public static SimpleModelWrapper bake(ModelBaker var0, ResourceLocation var1, ModelState var2) {
      ResolvedModel var3 = var0.getModel(var1);
      TextureSlots var4 = var3.getTopTextureSlots();
      boolean var5 = var3.getTopAmbientOcclusion();
      TextureAtlasSprite var6 = var3.resolveParticleSprite(var4, var0);
      QuadCollection var7 = var3.bakeTopGeometry(var4, var0, var2);
      return new SimpleModelWrapper(var7, var5, var6);
   }

   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
      return this.quads.getQuads(var2);
   }
}
