package net.minecraft.server.dialog.submit;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class SubmitMethodTypes {
   public SubmitMethodTypes() {
      super();
   }

   public static MapCodec<? extends SubmitMethod> bootstrap(Registry<MapCodec<? extends SubmitMethod>> var0) {
      Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace("command_template"), CommandTemplate.MAP_CODEC);
      Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace("custom_template"), CustomTemplate.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace("custom_form"), CustomForm.MAP_CODEC);
   }
}
