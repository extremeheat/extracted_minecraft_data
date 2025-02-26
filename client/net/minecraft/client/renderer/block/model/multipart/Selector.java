package net.minecraft.client.renderer.block.model.multipart;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

public record Selector(Optional<Condition> condition, BlockStateModel.Unbaked variant) {
   public static final Codec<Selector> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Condition.CODEC.optionalFieldOf("when").forGetter(Selector::condition), BlockStateModel.Unbaked.CODEC.fieldOf("apply").forGetter(Selector::variant)).apply(var0, Selector::new));

   public Selector(Optional<Condition> var1, BlockStateModel.Unbaked var2) {
      super();
      this.condition = var1;
      this.variant = var2;
   }

   public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> var1) {
      return (Predicate)this.condition.map((var1x) -> var1x.instantiate(var1)).orElse((Predicate)(var0) -> true);
   }
}
