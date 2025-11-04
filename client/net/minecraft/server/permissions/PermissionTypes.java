package net.minecraft.server.permissions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class PermissionTypes {
   public PermissionTypes() {
      super();
   }

   public static MapCodec<? extends Permission> bootstrap(Registry<MapCodec<? extends Permission>> var0) {
      Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("atom"), Permission.Atom.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("command_level"), Permission.HasCommandLevel.MAP_CODEC);
   }
}
