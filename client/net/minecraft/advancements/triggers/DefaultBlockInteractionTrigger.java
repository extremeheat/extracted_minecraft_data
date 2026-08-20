package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class DefaultBlockInteractionTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public DefaultBlockInteractionTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return DefaultBlockInteractionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final BlockPos pos) {
      ServerLevel level = player.level();
      BlockState state = level.getBlockState(pos);
      LootParams params = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.THIS_ENTITY, player).withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK_USE);
      LootContext context = (new LootContext.Builder(params)).create(Optional.empty());
      this.trigger(player, (t) -> t.matches(context));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<Holder<LootItemCondition>> location) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), LootItemCondition.CODEC.optionalFieldOf("location").forGetter(TriggerInstance::location)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public boolean matches(final LootContext locationContext) {
         return this.location.isEmpty() || ((LootItemCondition)((Holder)this.location.get()).value()).test(locationContext);
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.context(LootContextParamSets.BLOCK_USE), "location", this.location);
      }
   }
}
