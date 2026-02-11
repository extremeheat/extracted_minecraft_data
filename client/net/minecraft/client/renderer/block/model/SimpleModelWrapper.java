package net.minecraft.client.renderer.block.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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

public record SimpleModelWrapper(QuadCollection quads, boolean useAmbientOcclusion, Material.Baked particleMaterial, boolean hasTranslucency) implements BlockModelPart {
   private static final Logger LOGGER = LogUtils.getLogger();

   public SimpleModelWrapper {
      super();
   }

   public static BlockModelPart bake(final ModelBaker modelBakery, final Identifier location, final ModelState state) {
      ResolvedModel model = modelBakery.getModel(location);
      TextureSlots textureSlots = model.getTopTextureSlots();
      boolean hasAmbientOcclusion = model.getTopAmbientOcclusion();
      Material.Baked particleMaterial = model.resolveParticleMaterial(textureSlots, modelBakery);
      QuadCollection geometry = model.bakeTopGeometry(textureSlots, modelBakery, state);
      boolean hasTranslucency = false;
      Multimap<Identifier, Identifier> forbiddenSprites = null;

      for(BakedQuad bakedQuad : geometry.getAll()) {
         TextureAtlasSprite sprite = bakedQuad.spriteInfo().sprite();
         if (!sprite.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS)) {
            if (forbiddenSprites == null) {
               forbiddenSprites = HashMultimap.create();
            }

            forbiddenSprites.put(sprite.atlasLocation(), sprite.contents().name());
         }

         hasTranslucency |= bakedQuad.spriteInfo().layer().translucent();
      }

      if (forbiddenSprites != null) {
         LOGGER.warn("Rejecting block model {}, since it contains sprites from outside of supported atlas: {}", location, forbiddenSprites);
         return modelBakery.missingBlockModelPart();
      } else {
         return new SimpleModelWrapper(geometry, hasAmbientOcclusion, particleMaterial, hasTranslucency);
      }
   }

   public List<BakedQuad> getQuads(final @Nullable Direction direction) {
      return this.quads.getQuads(direction);
   }
}
