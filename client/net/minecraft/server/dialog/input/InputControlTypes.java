package net.minecraft.server.dialog.input;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class InputControlTypes {
   public InputControlTypes() {
      super();
   }

   public static MapCodec<? extends InputControl> bootstrap(Registry<MapCodec<? extends InputControl>> var0) {
      Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("boolean"), BooleanInput.MAP_CODEC);
      Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("number_range"), NumberRangeInput.MAP_CODEC);
      Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("single_option"), SingleOptionInput.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("text"), TextInput.MAP_CODEC);
   }
}
