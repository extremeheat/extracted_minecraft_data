package net.minecraft.client.color.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.core.Registry;

public class ItemTintSources {
   public static final Codec<ItemTintSource> CODEC;

   public ItemTintSources() {
      super();
   }

   public static Object bootstrap(final Registry<MapCodec<? extends ItemTintSource>> registry) {
      Registry.register(registry, (String)"custom_model_data", CustomModelDataSource.MAP_CODEC);
      Registry.register(registry, (String)"constant", Constant.MAP_CODEC);
      Registry.register(registry, (String)"dye", Dye.MAP_CODEC);
      Registry.register(registry, (String)"grass", GrassColorSource.MAP_CODEC);
      Registry.register(registry, (String)"firework", Firework.MAP_CODEC);
      Registry.register(registry, (String)"potion", Potion.MAP_CODEC);
      return Registry.register(registry, (String)"team", TeamColor.MAP_CODEC);
   }

   static {
      CODEC = BuiltInClientRegistries.ITEM_TINT_SOURCE_TYPE.byNameCodec().dispatch(ItemTintSource::type, (c) -> c);
   }
}
