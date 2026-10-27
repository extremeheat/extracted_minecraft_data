package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.core.Registry;

public class ConditionalItemModelProperties {
   public static final MapCodec<ConditionalItemModelProperty> MAP_CODEC;

   public ConditionalItemModelProperties() {
      super();
   }

   public static Object bootstrap(final Registry<MapCodec<? extends ConditionalItemModelProperty>> registry) {
      Registry.register(registry, (String)"custom_model_data", CustomModelDataProperty.MAP_CODEC);
      Registry.register(registry, (String)"using_item", IsUsingItem.MAP_CODEC);
      Registry.register(registry, (String)"broken", Broken.MAP_CODEC);
      Registry.register(registry, (String)"damaged", Damaged.MAP_CODEC);
      Registry.register(registry, (String)"fishing_rod/cast", FishingRodCast.MAP_CODEC);
      Registry.register(registry, (String)"has_component", HasComponent.MAP_CODEC);
      Registry.register(registry, (String)"bundle/has_selected_item", BundleHasSelectedItem.MAP_CODEC);
      Registry.register(registry, (String)"selected", IsSelected.MAP_CODEC);
      Registry.register(registry, (String)"carried", IsCarried.MAP_CODEC);
      Registry.register(registry, (String)"extended_view", ExtendedView.MAP_CODEC);
      Registry.register(registry, (String)"keybind_down", IsKeybindDown.MAP_CODEC);
      Registry.register(registry, (String)"view_entity", IsViewEntity.MAP_CODEC);
      return Registry.register(registry, (String)"component", ComponentMatches.MAP_CODEC);
   }

   static {
      MAP_CODEC = BuiltInClientRegistries.CONDITIONAL_ITEM_MODEL_PROPERTY_TYPE.byNameCodec().dispatchMap("property", ConditionalItemModelProperty::type, (c) -> c);
   }
}
