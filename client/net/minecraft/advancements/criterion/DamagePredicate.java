package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public record DamagePredicate(MinMaxBounds.Doubles dealtDamage, MinMaxBounds.Doubles takenDamage, Optional<EntityPredicate> sourceEntity, Optional<Boolean> blocked, Optional<DamageSourcePredicate> type) {
   public static final Codec<DamagePredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Doubles.CODEC.optionalFieldOf("dealt", MinMaxBounds.Doubles.ANY).forGetter(DamagePredicate::dealtDamage), MinMaxBounds.Doubles.CODEC.optionalFieldOf("taken", MinMaxBounds.Doubles.ANY).forGetter(DamagePredicate::takenDamage), EntityPredicate.CODEC.optionalFieldOf("source_entity").forGetter(DamagePredicate::sourceEntity), Codec.BOOL.optionalFieldOf("blocked").forGetter(DamagePredicate::blocked), DamageSourcePredicate.CODEC.optionalFieldOf("type").forGetter(DamagePredicate::type)).apply(i, DamagePredicate::new));

   public DamagePredicate {
      super();
   }

   public boolean matches(final ServerPlayer player, final DamageSource source, final float originalDamage, final float actualDamage, final boolean blocked) {
      if (!this.dealtDamage.matches((double)originalDamage)) {
         return false;
      } else if (!this.takenDamage.matches((double)actualDamage)) {
         return false;
      } else if (this.sourceEntity.isPresent() && !((EntityPredicate)this.sourceEntity.get()).matches(player, source.getEntity())) {
         return false;
      } else if (this.blocked.isPresent() && (Boolean)this.blocked.get() != blocked) {
         return false;
      } else {
         return !this.type.isPresent() || ((DamageSourcePredicate)this.type.get()).matches(player, source);
      }
   }

   public static class Builder {
      private MinMaxBounds.Doubles dealtDamage;
      private MinMaxBounds.Doubles takenDamage;
      private Optional<EntityPredicate> sourceEntity;
      private Optional<Boolean> blocked;
      private Optional<DamageSourcePredicate> type;

      public Builder() {
         super();
         this.dealtDamage = MinMaxBounds.Doubles.ANY;
         this.takenDamage = MinMaxBounds.Doubles.ANY;
         this.sourceEntity = Optional.empty();
         this.blocked = Optional.empty();
         this.type = Optional.empty();
      }

      public static Builder damageInstance() {
         return new Builder();
      }

      public Builder dealtDamage(final MinMaxBounds.Doubles dealtDamage) {
         this.dealtDamage = dealtDamage;
         return this;
      }

      public Builder takenDamage(final MinMaxBounds.Doubles takenDamage) {
         this.takenDamage = takenDamage;
         return this;
      }

      public Builder sourceEntity(final EntityPredicate sourceEntity) {
         this.sourceEntity = Optional.of(sourceEntity);
         return this;
      }

      public Builder blocked(final Boolean blocked) {
         this.blocked = Optional.of(blocked);
         return this;
      }

      public Builder type(final DamageSourcePredicate type) {
         this.type = Optional.of(type);
         return this;
      }

      public Builder type(final DamageSourcePredicate.Builder type) {
         this.type = Optional.of(type.build());
         return this;
      }

      public DamagePredicate build() {
         return new DamagePredicate(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
      }
   }
}
