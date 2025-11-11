package net.minecraft.client.renderer.block.model;

import com.google.common.collect.HashMultimap;
import com.mojang.logging.LogUtils;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record SimpleModelWrapper(QuadCollection quads, boolean useAmbientOcclusion, TextureAtlasSprite particleIcon) implements BlockModelPart {
   private static final Logger LOGGER = LogUtils.getLogger();

   public SimpleModelWrapper(QuadCollection var1, boolean var2, TextureAtlasSprite var3) {
      super();
      this.quads = var1;
      this.useAmbientOcclusion = var2;
      this.particleIcon = var3;
   }

   public static BlockModelPart bake(ModelBaker var0, Identifier var1, ModelState var2) {
      ResolvedModel var3 = var0.getModel(var1);
      TextureSlots var4 = var3.getTopTextureSlots();
      boolean var5 = var3.getTopAmbientOcclusion();
      TextureAtlasSprite var6 = var3.resolveParticleSprite(var4, var0);
      QuadCollection var7 = var3.bakeTopGeometry(var4, var0, var2);
      HashMultimap var8 = null;

      for(BakedQuad var10 : var7.getAll()) {
         TextureAtlasSprite var11 = var10.sprite();
         if (!var11.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS)) {
            if (var8 == null) {
               var8 = HashMultimap.create();
            }

            var8.put(var11.atlasLocation(), var11.contents().name());
         }
      }

      if (var8 != null) {
         LOGGER.warn("Rejecting block model {}, since it contains sprites from outside of supported atlas: {}", var1, var8);
         return var0.missingBlockModelPart();
      } else {
         return new SimpleModelWrapper(var7, var5, var6);
      }
   }

   public List<BakedQuad> getQuads(@Nullable Direction var1) {
      return this.quads.getQuads(var1);
   }
}
