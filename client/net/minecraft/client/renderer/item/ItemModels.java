package net.minecraft.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.core.Registry;

public class ItemModels {
   public static final Codec<ItemModel.Unbaked> CODEC;

   public ItemModels() {
      super();
   }

   public static Object bootstrap(final Registry<MapCodec<? extends ItemModel.Unbaked>> registry) {
      Registry.register(registry, (String)"empty", EmptyModel.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"model", CuboidItemModelWrapper.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"range_dispatch", RangeSelectItemModel.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"special", SpecialModelWrapper.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"composite", CompositeModel.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"bundle/selected_item", BundleSelectedItemSpecialRenderer.Unbaked.MAP_CODEC);
      Registry.register(registry, (String)"select", SelectItemModel.Unbaked.MAP_CODEC);
      return Registry.register(registry, (String)"condition", ConditionalItemModel.Unbaked.MAP_CODEC);
   }

   static {
      CODEC = BuiltInClientRegistries.ITEM_MODEL_TYPE.byNameCodec().dispatch(ItemModel.Unbaked::type, (c) -> c);
   }
}
