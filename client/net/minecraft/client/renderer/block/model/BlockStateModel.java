package net.minecraft.client.renderer.block.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateModel {
   List<BakedQuad> getQuads(BlockState var1, @Nullable Direction var2, RandomSource var3);

   boolean useAmbientOcclusion();

   TextureAtlasSprite particleIcon();

   public interface Unbaked extends ResolvableModel {
      BlockStateModel bake(ModelBaker var1);

      Object visualEqualityGroup(BlockState var1);
   }
}
