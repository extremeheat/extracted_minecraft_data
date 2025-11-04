package net.minecraft.gametest.framework;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface GameTestEnvironments {
   String DEFAULT = "default";
   ResourceKey<TestEnvironmentDefinition> DEFAULT_KEY = create("default");

   private static ResourceKey<TestEnvironmentDefinition> create(String var0) {
      return ResourceKey.create(Registries.TEST_ENVIRONMENT, Identifier.withDefaultNamespace(var0));
   }

   static void bootstrap(BootstrapContext<TestEnvironmentDefinition> var0) {
      var0.register(DEFAULT_KEY, new TestEnvironmentDefinition.AllOf(List.of()));
   }
}
