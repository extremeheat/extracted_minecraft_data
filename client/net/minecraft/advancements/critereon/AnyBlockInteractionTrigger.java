package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class AnyBlockInteractionTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public AnyBlockInteractionTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return AnyBlockInteractionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, BlockPos var2, ItemStack var3) {
      ServerLevel var4 = var1.serverLevel();
      BlockState var5 = var4.getBlockState(var2);
      LootParams var6 = (new LootParams.Builder(var4)).withParameter(LootContextParams.ORIGIN, var2.getCenter()).withParameter(LootContextParams.THIS_ENTITY, var1).withParameter(LootContextParams.BLOCK_STATE, var5).withParameter(LootContextParams.TOOL, var3).create(LootContextParamSets.ADVANCEMENT_LOCATION);
      LootContext var7 = (new LootContext.Builder(var6)).create(Optional.empty());
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var7);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> location) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ContextAwarePredicate.CODEC.optionalFieldOf("location").forGetter(TriggerInstance::location)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> location) {
         super();
         this.player = player;
         this.location = location;
      }

      public boolean matches(LootContext var1) {
         return this.location.isEmpty() || ((ContextAwarePredicate)this.location.get()).matches(var1);
      }

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         this.location.ifPresent((var1x) -> {
            var1.validate(var1x, LootContextParamSets.ADVANCEMENT_LOCATION, ".location");
         });
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<ContextAwarePredicate> location() {
         return this.location;
      }
   }
}
