package net.minecraft.server.dialog.input;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class InputControlTypes {
   public InputControlTypes() {
      super();
   }

   public static MapCodec<? extends InputControl> bootstrap(final Registry<MapCodec<? extends InputControl>> registry) {
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("boolean"), BooleanInput.MAP_CODEC);
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("number_range"), NumberRangeInput.MAP_CODEC);
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("single_option"), SingleOptionInput.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("text"), TextInput.MAP_CODEC);
   }
}
