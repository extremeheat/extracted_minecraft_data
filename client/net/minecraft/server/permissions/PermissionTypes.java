package net.minecraft.server.permissions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.Identifier;

public class PermissionTypes {
   public PermissionTypes() {
      super();
   }

   public static void bootstrap(final WritableRegistry<MapCodec<? extends Permission>> registry) {
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("atom"), Permission.Atom.MAP_CODEC);
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("command_level"), Permission.HasCommandLevel.MAP_CODEC);
   }
}
