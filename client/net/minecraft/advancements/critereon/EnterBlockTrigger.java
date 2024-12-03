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

public class EnterBlockTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public EnterBlockTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return EnterBlockTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, BlockState var2) {
      this.trigger(var1, (var1x) -> var1x.matches(var2));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block, Optional<StatePropertiesPredicate> state) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("block").forGetter(TriggerInstance::block), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(TriggerInstance::state)).apply(var0, TriggerInstance::new)).validate(TriggerInstance::validate);

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<Holder<Block>> var2, Optional<StatePropertiesPredicate> var3) {
         super();
         this.player = var1;
         this.block = var2;
         this.state = var3;
      }

      private static DataResult<TriggerInstance> validate(TriggerInstance var0) {
         return (DataResult)var0.block.flatMap((var1) -> var0.state.flatMap((var1x) -> var1x.checkState(((Block)var1.value()).getStateDefinition())).map((var1x) -> DataResult.error(() -> {
                  String var10000 = String.valueOf(var1);
                  return "Block" + var10000 + " has no property " + var1x;
               }))).orElseGet(() -> DataResult.success(var0));
      }

      public static Criterion<TriggerInstance> entersBlock(Block var0) {
         return CriteriaTriggers.ENTER_BLOCK.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(var0.builtInRegistryHolder()), Optional.empty()));
      }

      public boolean matches(BlockState var1) {
         if (this.block.isPresent() && !var1.is((Holder)this.block.get())) {
            return false;
         } else {
            return !this.state.isPresent() || ((StatePropertiesPredicate)this.state.get()).matches(var1);
         }
      }
   }
}
