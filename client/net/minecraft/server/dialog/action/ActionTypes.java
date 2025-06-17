package net.minecraft.server.dialog.action;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class ActionTypes {
   public ActionTypes() {
      super();
   }

   public static MapCodec<? extends Action> bootstrap(Registry<MapCodec<? extends Action>> var0) {
      StaticAction.WRAPPED_CODECS.forEach((var1, var2) -> Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace(var1.getSerializedName()), var2));
      Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace("dynamic/run_command"), CommandTemplate.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (ResourceLocation)ResourceLocation.withDefaultNamespace("dynamic/custom"), CustomAll.MAP_CODEC);
   }
}
