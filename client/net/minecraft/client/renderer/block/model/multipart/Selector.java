package net.minecraft.client.renderer.block.model.multipart;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

public record Selector(Optional<Condition> condition, BlockStateModel.Unbaked variant) {
   public static final Codec<Selector> CODEC = RecordCodecBuilder.create((i) -> i.group(Condition.CODEC.optionalFieldOf("when").forGetter(Selector::condition), BlockStateModel.Unbaked.CODEC.fieldOf("apply").forGetter(Selector::variant)).apply(i, Selector::new));

   public Selector {
      super();
   }

   public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(final StateDefinition<O, S> definition) {
      return (Predicate)this.condition.map((c) -> c.instantiate(definition)).orElse((Predicate)(state) -> true);
   }
}
