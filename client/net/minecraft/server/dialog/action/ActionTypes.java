package net.minecraft.server.dialog.action;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.Identifier;

public class ActionTypes {
   public ActionTypes() {
      super();
   }

   public static void bootstrap(final WritableRegistry<MapCodec<? extends Action>> registry) {
      StaticAction.WRAPPED_CODECS.forEach((action, codec) -> Registry.register(registry, (Identifier)Identifier.withDefaultNamespace(action.getSerializedName()), codec));
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("dynamic/run_command"), CommandTemplate.MAP_CODEC);
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("dynamic/custom"), CustomAll.MAP_CODEC);
   }
}
