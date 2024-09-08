package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ItemModelShaper {
   private final Map<ResourceLocation, BakedModel> modelToBakedModel = new HashMap<>();
   private final Supplier<BakedModel> missingModel;
   private final Function<ResourceLocation, BakedModel> modelGetter;

   public ItemModelShaper(ModelManager var1) {
      super();
      this.missingModel = var1::getMissingModel;
      this.modelGetter = var1x -> var1.getModel(ModelResourceLocation.inventory(var1x));
   }

   public BakedModel getItemModel(ItemStack var1) {
      ResourceLocation var2 = var1.get(DataComponents.ITEM_MODEL);
      return var2 == null ? this.missingModel.get() : this.getItemModel(var2);
   }

   public BakedModel getItemModel(ResourceLocation var1) {
      return this.modelToBakedModel.computeIfAbsent(var1, this.modelGetter);
   }

   public void invalidateCache() {
      this.modelToBakedModel.clear();
   }
}
