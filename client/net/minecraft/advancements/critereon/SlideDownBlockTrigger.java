package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SlideDownBlockTrigger extends SimpleCriterionTrigger<SlideDownBlockTrigger.TriggerInstance> {
   public SlideDownBlockTrigger() {
      super();
   }

   @Override
   public Codec<SlideDownBlockTrigger.TriggerInstance> codec() {
      return SlideDownBlockTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, BlockState var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<Holder<Block>> c, Optional<StatePropertiesPredicate> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<Holder<Block>> block;
      private final Optional<StatePropertiesPredicate> state;
      public static final Codec<SlideDownBlockTrigger.TriggerInstance> CODEC = ExtraCodecs.validate(
         RecordCodecBuilder.create(
            var0 -> var0.group(
                     ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(SlideDownBlockTrigger.TriggerInstance::player),
                     ExtraCodecs.strictOptionalField(BuiltInRegistries.BLOCK.holderByNameCodec(), "block")
                        .forGetter(SlideDownBlockTrigger.TriggerInstance::block),
                     ExtraCodecs.strictOptionalField(StatePropertiesPredicate.CODEC, "state").forGetter(SlideDownBlockTrigger.TriggerInstance::state)
                  )
                  .apply(var0, SlideDownBlockTrigger.TriggerInstance::new)
         ),
         SlideDownBlockTrigger.TriggerInstance::validate
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<Holder<Block>> var2, Optional<StatePropertiesPredicate> var3) {
         super();
         this.player = var1;
         this.block = var2;
         this.state = var3;
      }

      private static DataResult<SlideDownBlockTrigger.TriggerInstance> validate(SlideDownBlockTrigger.TriggerInstance var0) {
         return (DataResult<SlideDownBlockTrigger.TriggerInstance>)var0.block
            .flatMap(
               var1 -> var0.state
                     .<String>flatMap(var1x -> var1x.checkState(((Block)var1.value()).getStateDefinition()))
                     .map(var1x -> DataResult.error(() -> "Block" + var1 + " has no property " + var1x))
            )
            .orElseGet(() -> DataResult.success(var0));
      }

      public static Criterion<SlideDownBlockTrigger.TriggerInstance> slidesDownBlock(Block var0) {
         return CriteriaTriggers.HONEY_BLOCK_SLIDE
            .createCriterion(new SlideDownBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.builtInRegistryHolder()), Optional.empty()));
      }

      public boolean matches(BlockState var1) {
         if (this.block.isPresent() && !var1.is(this.block.get())) {
            return false;
         } else {
            return !this.state.isPresent() || ((StatePropertiesPredicate)this.state.get()).matches(var1);
         }
      }
   }
}