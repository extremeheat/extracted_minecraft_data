package net.minecraft.gametest.framework;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class FunctionGameTestInstance extends GameTestInstance {
   public static final MapCodec<FunctionGameTestInstance> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BuiltInRegistries.TEST_FUNCTION.holderByNameCodec().fieldOf("function").forGetter(FunctionGameTestInstance::function), TestData.CODEC.forGetter(GameTestInstance::info)).apply(var0, FunctionGameTestInstance::new));
   private final Holder<Consumer<GameTestHelper>> function;

   public FunctionGameTestInstance(Holder<Consumer<GameTestHelper>> var1, TestData<Holder<TestEnvironmentDefinition>> var2) {
      super(var2);
      this.function = var1;
   }

   public void run(GameTestHelper var1) {
      ((Consumer)this.function.value()).accept(var1);
   }

   private Holder<Consumer<GameTestHelper>> function() {
      return this.function;
   }

   public MapCodec<FunctionGameTestInstance> codec() {
      return CODEC;
   }

   protected MutableComponent typeDescription() {
      return Component.translatable("test_instance.type.function");
   }

   public Component describe() {
      return this.describeType().append((Component)this.descriptionRow("test_instance.description.function", this.function.getRegisteredName())).append(this.describeInfo());
   }
}
