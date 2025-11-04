package net.minecraft.gametest.framework;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record GeneratedTest(Map<Identifier, TestData<ResourceKey<TestEnvironmentDefinition>>> tests, ResourceKey<Consumer<GameTestHelper>> functionKey, Consumer<GameTestHelper> function) {
   public GeneratedTest(Map<Identifier, TestData<ResourceKey<TestEnvironmentDefinition>>> var1, Identifier var2, Consumer<GameTestHelper> var3) {
      this(var1, ResourceKey.create(Registries.TEST_FUNCTION, var2), var3);
   }

   public GeneratedTest(Identifier var1, TestData<ResourceKey<TestEnvironmentDefinition>> var2, Consumer<GameTestHelper> var3) {
      this(Map.of(var1, var2), var1, var3);
   }

   public GeneratedTest(Map<Identifier, TestData<ResourceKey<TestEnvironmentDefinition>>> var1, ResourceKey<Consumer<GameTestHelper>> var2, Consumer<GameTestHelper> var3) {
      super();
      this.tests = var1;
      this.functionKey = var2;
      this.function = var3;
   }
}
