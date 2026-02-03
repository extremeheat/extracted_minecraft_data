package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EnterBlockTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public EnterBlockTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return EnterBlockTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final BlockState state) {
      this.trigger(player, (t) -> t.matches(state));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block, Optional<StatePropertiesPredicate> state) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("block").forGetter(TriggerInstance::block), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(TriggerInstance::state)).apply(i, TriggerInstance::new)).validate(TriggerInstance::validate);

      public TriggerInstance {
         super();
      }

      private static DataResult<TriggerInstance> validate(final TriggerInstance trigger) {
         return (DataResult)trigger.block.flatMap((block) -> trigger.state.flatMap((state) -> state.checkState(((Block)block.value()).getStateDefinition())).map((property) -> DataResult.error(() -> {
                  String var10000 = String.valueOf(block);
                  return "Block" + var10000 + " has no property " + property;
               }))).orElseGet(() -> DataResult.success(trigger));
      }

      public static Criterion<TriggerInstance> entersBlock(final Block block) {
         return CriteriaTriggers.ENTER_BLOCK.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(block.builtInRegistryHolder()), Optional.empty()));
      }

      public boolean matches(final BlockState state) {
         if (this.block.isPresent() && !state.is((Holder)this.block.get())) {
            return false;
         } else {
            return !this.state.isPresent() || ((StatePropertiesPredicate)this.state.get()).matches(state);
         }
      }
   }
}
