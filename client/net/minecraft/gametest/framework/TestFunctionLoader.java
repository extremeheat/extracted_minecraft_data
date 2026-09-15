package net.minecraft.gametest.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface TestFunctionLoader {
   List<TestFunctionLoader> ALL_LOADERS = new ArrayList();

   static void registerLoader(final TestFunctionLoader loader) {
      ALL_LOADERS.add(loader);
   }

   static void runLoaders(final Registry<Consumer<GameTestHelper>> registry) {
      for(TestFunctionLoader loader : ALL_LOADERS) {
         loader.load((key, function) -> Registry.register(registry, (ResourceKey)key, function));
      }

   }

   void load(BiConsumer<ResourceKey<Consumer<GameTestHelper>>, Consumer<GameTestHelper>> register);
}
