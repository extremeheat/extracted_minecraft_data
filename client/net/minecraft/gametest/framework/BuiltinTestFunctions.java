package net.minecraft.gametest.framework;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class BuiltinTestFunctions extends TestFunctionLoader {
   public static final ResourceKey<Consumer<GameTestHelper>> ALWAYS_PASS = create("always_pass");
   public static final Consumer<GameTestHelper> ALWAYS_PASS_INSTANCE = GameTestHelper::succeed;

   public BuiltinTestFunctions() {
      super();
   }

   private static ResourceKey<Consumer<GameTestHelper>> create(String var0) {
      return ResourceKey.create(Registries.TEST_FUNCTION, Identifier.withDefaultNamespace(var0));
   }

   public static Consumer<GameTestHelper> bootstrap(Registry<Consumer<GameTestHelper>> var0) {
      registerLoader(new BuiltinTestFunctions());
      runLoaders(var0);
      return ALWAYS_PASS_INSTANCE;
   }

   public void load(BiConsumer<ResourceKey<Consumer<GameTestHelper>>, Consumer<GameTestHelper>> var1) {
      var1.accept(ALWAYS_PASS, ALWAYS_PASS_INSTANCE);
   }
}
