package net.minecraft.client.resources.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
   Collection<ResourceLocation> getDependencies();

   Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> var1, Set<Pair<String, String>> var2);

   @Nullable
   BakedModel bake(ModelBakery var1, Function<Material, TextureAtlasSprite> var2, ModelState var3, ResourceLocation var4);
}
