package net.minecraft.gametest.framework;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface GameTestInstances {
   ResourceKey<GameTestInstance> ALWAYS_PASS = create("always_pass");

   static void bootstrap(BootstrapContext<GameTestInstance> var0) {
      HolderGetter var1 = var0.lookup(Registries.TEST_FUNCTION);
      HolderGetter var2 = var0.lookup(Registries.TEST_ENVIRONMENT);
      var0.register(ALWAYS_PASS, new FunctionGameTestInstance(BuiltinTestFunctions.ALWAYS_PASS, new TestData(var2.getOrThrow(GameTestEnvironments.DEFAULT_KEY), Identifier.withDefaultNamespace("empty"), 1, 1, false)));
   }

   private static ResourceKey<GameTestInstance> create(String var0) {
      return ResourceKey.create(Registries.TEST_INSTANCE, Identifier.withDefaultNamespace(var0));
   }
}
