package net.minecraft.gametest.framework;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;

public class FunctionGameTestInstance extends GameTestInstance {
   public static final MapCodec<FunctionGameTestInstance> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceKey.codec(Registries.TEST_FUNCTION).fieldOf("function").forGetter(FunctionGameTestInstance::function), TestData.CODEC.forGetter(GameTestInstance::info)).apply(var0, FunctionGameTestInstance::new));
   private final ResourceKey<Consumer<GameTestHelper>> function;

   public FunctionGameTestInstance(ResourceKey<Consumer<GameTestHelper>> var1, TestData<Holder<TestEnvironmentDefinition>> var2) {
      super(var2);
      this.function = var1;
   }

   public void run(GameTestHelper var1) {
      ((Consumer)var1.getLevel().registryAccess().get(this.function).map(Holder.Reference::value).orElseThrow(() -> new IllegalStateException("Trying to access missing test function: " + String.valueOf(this.function.identifier())))).accept(var1);
   }

   private ResourceKey<Consumer<GameTestHelper>> function() {
      return this.function;
   }

   public MapCodec<FunctionGameTestInstance> codec() {
      return CODEC;
   }

   protected MutableComponent typeDescription() {
      return Component.translatable("test_instance.type.function");
   }

   public Component describe() {
      return this.describeType().append((Component)this.descriptionRow("test_instance.description.function", this.function.identifier().toString())).append(this.describeInfo());
   }
}
