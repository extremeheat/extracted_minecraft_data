package net.minecraft.server.permissions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class PermissionCheckTypes {
   public PermissionCheckTypes() {
      super();
   }

   public static MapCodec<? extends PermissionCheck> bootstrap(Registry<MapCodec<? extends PermissionCheck>> var0) {
      Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("always_pass"), PermissionCheck.AlwaysPass.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("require"), PermissionCheck.Require.MAP_CODEC);
   }
}
