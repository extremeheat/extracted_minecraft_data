package net.minecraft.server.permissions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class PermissionCheckTypes {
   public PermissionCheckTypes() {
      super();
   }

   public static MapCodec<? extends PermissionCheck> bootstrap(final Registry<MapCodec<? extends PermissionCheck>> registry) {
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("always_pass"), PermissionCheck.AlwaysPass.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("require"), PermissionCheck.Require.MAP_CODEC);
   }
}
