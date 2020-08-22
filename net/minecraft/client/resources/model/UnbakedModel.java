package net.minecraft.client.resources.model;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
   Collection getDependencies();

   Collection getMaterials(Function var1, Set var2);

   @Nullable
   BakedModel bake(ModelBakery var1, Function var2, ModelState var3, ResourceLocation var4);
}
