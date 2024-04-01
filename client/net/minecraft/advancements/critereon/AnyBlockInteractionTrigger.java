package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class AnyBlockInteractionTrigger extends SimpleCriterionTrigger<AnyBlockInteractionTrigger.TriggerInstance> {
   public AnyBlockInteractionTrigger() {
      super();
   }

   @Override
   public Codec<AnyBlockInteractionTrigger.TriggerInstance> codec() {
      return AnyBlockInteractionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, BlockPos var2, ItemStack var3) {
      ServerLevel var4 = var1.serverLevel();
      BlockState var5 = var4.getBlockState(var2);
      LootParams var6 = new LootParams.Builder(var4)
         .withParameter(LootContextParams.ORIGIN, var2.getCenter())
         .withParameter(LootContextParams.THIS_ENTITY, var1)
         .withParameter(LootContextParams.BLOCK_STATE, var5)
         .withParameter(LootContextParams.TOOL, var3)
         .create(LootContextParamSets.ADVANCEMENT_LOCATION);
      LootContext var7 = new LootContext.Builder(var6).create(Optional.empty());
      this.trigger(var1, var1x -> var1x.matches(var7));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<ContextAwarePredicate> c) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<ContextAwarePredicate> location;
      public static final Codec<AnyBlockInteractionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(AnyBlockInteractionTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(ContextAwarePredicate.CODEC, "location").forGetter(AnyBlockInteractionTrigger.TriggerInstance::location)
               )
               .apply(var0, AnyBlockInteractionTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2) {
         super();
         this.player = var1;
         this.location = var2;
      }

      public boolean matches(LootContext var1) {
         return this.location.isEmpty() || this.location.get().matches(var1);
      }

      @Override
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         this.location.ifPresent(var1x -> var1.validate(var1x, LootContextParamSets.ADVANCEMENT_LOCATION, ".location"));
      }
   }
}
