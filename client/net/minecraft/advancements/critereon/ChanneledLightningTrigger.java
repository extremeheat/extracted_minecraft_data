package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger<ChanneledLightningTrigger.TriggerInstance> {
   public ChanneledLightningTrigger() {
      super();
   }

   @Override
   public Codec<ChanneledLightningTrigger.TriggerInstance> codec() {
      return ChanneledLightningTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Collection<? extends Entity> var2) {
      List var3 = var2.stream().map(var1x -> EntityPredicate.createContext(var1, var1x)).collect(Collectors.toList());
      this.trigger(var1, var1x -> var1x.matches(var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, List<ContextAwarePredicate> c) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final List<ContextAwarePredicate> victims;
      public static final Codec<ChanneledLightningTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ChanneledLightningTrigger.TriggerInstance::player),
                  EntityPredicate.ADVANCEMENT_CODEC
                     .listOf()
                     .optionalFieldOf("victims", List.of())
                     .forGetter(ChanneledLightningTrigger.TriggerInstance::victims)
               )
               .apply(var0, ChanneledLightningTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, List<ContextAwarePredicate> var2) {
         super();
         this.player = var1;
         this.victims = var2;
      }

      public static Criterion<ChanneledLightningTrigger.TriggerInstance> channeledLightning(EntityPredicate.Builder... var0) {
         return CriteriaTriggers.CHANNELED_LIGHTNING
            .createCriterion(new ChanneledLightningTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0)));
      }

      public boolean matches(Collection<? extends LootContext> var1) {
         for(ContextAwarePredicate var3 : this.victims) {
            boolean var4 = false;

            for(LootContext var6 : var1) {
               if (var3.matches(var6)) {
                  var4 = true;
                  break;
               }
            }

            if (!var4) {
               return false;
            }
         }

         return true;
      }

      @Override
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntities(this.victims, ".victims");
      }
   }
}
