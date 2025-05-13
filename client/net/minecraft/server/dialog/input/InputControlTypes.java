package net.minecraft.server.dialog.input;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class InputControlTypes {
   public InputControlTypes() {
      super();
   }

   public static MapCodec<? extends InputControl> bootstrap(Registry<MapCodec<? extends InputControl>> var0) {
      Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace("boolean"), BooleanInput.MAP_CODEC);
      Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace("number_range"), NumberRangeInput.MAP_CODEC);
      Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace("single_option"), SingleOptionInput.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace("text"), TextInput.MAP_CODEC);
   }
}
