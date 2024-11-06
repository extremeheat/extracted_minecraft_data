package net.minecraft.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class ItemModels {
   private static final ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends ItemModel.Unbaked>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper();
   public static final Codec<ItemModel.Unbaked> CODEC;

   public ItemModels() {
      super();
   }

   public static void bootstrap() {
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("model"), BlockModelWrapper.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("range_dispatch"), RangeSelectItemModel.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("special"), SpecialModelWrapper.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("composite"), CompositeModel.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("bundle/selected_item"), BundleSelectedItemSpecialRenderer.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("select"), SelectItemModel.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("condition"), ConditionalItemModel.Unbaked.MAP_CODEC);
   }

   static {
      CODEC = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(ItemModel.Unbaked::type, (var0) -> {
         return var0;
      });
   }
}
