package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.core.Registry;

public class SelectItemModelProperties {
   public static final Codec<SelectItemModelProperty.Type<?, ?>> CODEC;

   public SelectItemModelProperties() {
      super();
   }

   public static Object bootstrap(final Registry<SelectItemModelProperty.Type<?, ?>> registry) {
      Registry.register(registry, (String)"custom_model_data", CustomModelDataProperty.TYPE);
      Registry.register(registry, (String)"main_hand", MainHand.TYPE);
      Registry.register(registry, (String)"charge_type", Charge.TYPE);
      Registry.register(registry, (String)"trim_material", TrimMaterialProperty.TYPE);
      Registry.register(registry, (String)"block_state", ItemBlockState.TYPE);
      Registry.register(registry, (String)"display_context", DisplayContext.TYPE);
      Registry.register(registry, (String)"local_time", LocalTime.TYPE);
      Registry.register(registry, (String)"context_entity_type", ContextEntityType.TYPE);
      Registry.register(registry, (String)"context_dimension", ContextDimension.TYPE);
      return Registry.register(registry, (String)"component", ComponentContents.castType());
   }

   static {
      CODEC = BuiltInClientRegistries.SELECT_ITEM_MODEL_PROPERTY_TYPE.byNameCodec();
   }
}
