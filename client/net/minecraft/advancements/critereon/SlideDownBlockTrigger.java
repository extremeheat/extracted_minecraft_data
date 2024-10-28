package net.minecraft.advancements.critereon;

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

public class SlideDownBlockTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public SlideDownBlockTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return SlideDownBlockTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, BlockState var2) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var2);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block, Optional<StatePropertiesPredicate> state) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("block").forGetter(TriggerInstance::block), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(TriggerInstance::state)).apply(var0, TriggerInstance::new);
      }).validate(TriggerInstance::validate);

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block, Optional<StatePropertiesPredicate> state) {
         super();
         this.player = player;
         this.block = block;
         this.state = state;
      }

      private static DataResult<TriggerInstance> validate(TriggerInstance var0) {
         return (DataResult)var0.block.flatMap((var1) -> {
            return var0.state.flatMap((var1x) -> {
               return var1x.checkState(((Block)var1.value()).getStateDefinition());
            }).map((var1x) -> {
               return DataResult.error(() -> {
                  String var10000 = String.valueOf(var1);
                  return "Block" + var10000 + " has no property " + var1x;
               });
            });
         }).orElseGet(() -> {
            return DataResult.success(var0);
         });
      }

      public static Criterion<TriggerInstance> slidesDownBlock(Block var0) {
         return CriteriaTriggers.HONEY_BLOCK_SLIDE.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(var0.builtInRegistryHolder()), Optional.empty()));
      }

      public boolean matches(BlockState var1) {
         if (this.block.isPresent() && !var1.is((Holder)this.block.get())) {
            return false;
         } else {
            return !this.state.isPresent() || ((StatePropertiesPredicate)this.state.get()).matches(var1);
         }
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<Holder<Block>> block() {
         return this.block;
      }

      public Optional<StatePropertiesPredicate> state() {
         return this.state;
      }
   }
}
