package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.core.Registry;

public class RangeSelectItemModelProperties {
   public static final MapCodec<RangeSelectItemModelProperty> MAP_CODEC;

   public RangeSelectItemModelProperties() {
      super();
   }

   public static Object bootstrap(final Registry<MapCodec<? extends RangeSelectItemModelProperty>> registry) {
      Registry.register(registry, (String)"custom_model_data", CustomModelDataProperty.MAP_CODEC);
      Registry.register(registry, (String)"bundle/fullness", BundleFullness.MAP_CODEC);
      Registry.register(registry, (String)"damage", Damage.MAP_CODEC);
      Registry.register(registry, (String)"cooldown", Cooldown.MAP_CODEC);
      Registry.register(registry, (String)"time", Time.MAP_CODEC);
      Registry.register(registry, (String)"compass", CompassAngle.MAP_CODEC);
      Registry.register(registry, (String)"crossbow/pull", CrossbowPull.MAP_CODEC);
      Registry.register(registry, (String)"use_cycle", UseCycle.MAP_CODEC);
      Registry.register(registry, (String)"use_duration", UseDuration.MAP_CODEC);
      return Registry.register(registry, (String)"count", Count.MAP_CODEC);
   }

   static {
      MAP_CODEC = BuiltInClientRegistries.RANGE_SELECT_ITEM_MODEL_PROPERTY_TYPE.byNameCodec().dispatchMap("property", RangeSelectItemModelProperty::type, (c) -> c);
   }
}
