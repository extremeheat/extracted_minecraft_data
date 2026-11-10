package net.minecraft.server.permissions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.Identifier;

public class PermissionCheckTypes {
   public PermissionCheckTypes() {
      super();
   }

   public static void bootstrap(final WritableRegistry<MapCodec<? extends PermissionCheck>> registry) {
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("always_pass"), PermissionCheck.AlwaysPass.MAP_CODEC);
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("require"), PermissionCheck.Require.MAP_CODEC);
   }
}
