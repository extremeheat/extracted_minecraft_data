package net.minecraft.server;

import java.util.List;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public enum RegistryLayer {
   STATIC,
   WORLDGEN,
   DIMENSIONS,
   RELOADABLE;

   private static final List<RegistryLayer> VALUES = List.of(values());
   private static final RegistryAccess.Frozen STATIC_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

   private RegistryLayer() {
   }

   public static LayeredRegistryAccess<RegistryLayer> createRegistryAccess() {
      return (new LayeredRegistryAccess(VALUES)).replaceFrom(STATIC, (RegistryAccess.Frozen[])(STATIC_ACCESS));
   }

   // $FF: synthetic method
   private static RegistryLayer[] $values() {
      return new RegistryLayer[]{STATIC, WORLDGEN, DIMENSIONS, RELOADABLE};
   }
}
