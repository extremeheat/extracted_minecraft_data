package net.minecraft.gametest.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Rotation;

public abstract class GameTestInstance {
   public static final Codec<GameTestInstance> DIRECT_CODEC;
   private final TestData<Holder<TestEnvironmentDefinition>> info;

   public static MapCodec<? extends GameTestInstance> bootstrap(Registry<MapCodec<? extends GameTestInstance>> var0) {
      register(var0, "block_based", BlockBasedTestInstance.CODEC);
      return register(var0, "function", FunctionGameTestInstance.CODEC);
   }

   private static MapCodec<? extends GameTestInstance> register(Registry<MapCodec<? extends GameTestInstance>> var0, String var1, MapCodec<? extends GameTestInstance> var2) {
      return (MapCodec)Registry.register(var0, (ResourceKey)ResourceKey.create(Registries.TEST_INSTANCE_TYPE, Identifier.withDefaultNamespace(var1)), var2);
   }

   protected GameTestInstance(TestData<Holder<TestEnvironmentDefinition>> var1) {
      super();
      this.info = var1;
   }

   public abstract void run(GameTestHelper var1);

   public abstract MapCodec<? extends GameTestInstance> codec();

   public Holder<TestEnvironmentDefinition> batch() {
      return this.info.environment();
   }

   public Identifier structure() {
      return this.info.structure();
   }

   public int maxTicks() {
      return this.info.maxTicks();
   }

   public int setupTicks() {
      return this.info.setupTicks();
   }

   public boolean required() {
      return this.info.required();
   }

   public boolean manualOnly() {
      return this.info.manualOnly();
   }

   public int maxAttempts() {
      return this.info.maxAttempts();
   }

   public int requiredSuccesses() {
      return this.info.requiredSuccesses();
   }

   public boolean skyAccess() {
      return this.info.skyAccess();
   }

   public Rotation rotation() {
      return this.info.rotation();
   }

   protected TestData<Holder<TestEnvironmentDefinition>> info() {
      return this.info;
   }

   protected abstract MutableComponent typeDescription();

   public Component describe() {
      return this.describeType().append(this.describeInfo());
   }

   protected MutableComponent describeType() {
      return this.descriptionRow("test_instance.description.type", this.typeDescription());
   }

   protected Component describeInfo() {
      return this.descriptionRow("test_instance.description.structure", this.info.structure().toString()).append((Component)this.descriptionRow("test_instance.description.batch", ((Holder)this.info.environment()).getRegisteredName()));
   }

   protected MutableComponent descriptionRow(String var1, String var2) {
      return this.descriptionRow(var1, Component.literal(var2));
   }

   protected MutableComponent descriptionRow(String var1, MutableComponent var2) {
      return Component.translatable(var1, var2.withStyle(ChatFormatting.BLUE)).append((Component)Component.literal("\n"));
   }

   static {
      DIRECT_CODEC = BuiltInRegistries.TEST_INSTANCE_TYPE.byNameCodec().dispatch(GameTestInstance::codec, (var0) -> var0);
   }
}
