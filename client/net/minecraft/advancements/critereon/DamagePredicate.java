package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public record DamagePredicate(MinMaxBounds.Doubles dealtDamage, MinMaxBounds.Doubles takenDamage, Optional<EntityPredicate> sourceEntity, Optional<Boolean> blocked, Optional<DamageSourcePredicate> type) {
   public static final Codec<DamagePredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(MinMaxBounds.Doubles.CODEC.optionalFieldOf("dealt", MinMaxBounds.Doubles.ANY).forGetter(DamagePredicate::dealtDamage), MinMaxBounds.Doubles.CODEC.optionalFieldOf("taken", MinMaxBounds.Doubles.ANY).forGetter(DamagePredicate::takenDamage), EntityPredicate.CODEC.optionalFieldOf("source_entity").forGetter(DamagePredicate::sourceEntity), Codec.BOOL.optionalFieldOf("blocked").forGetter(DamagePredicate::blocked), DamageSourcePredicate.CODEC.optionalFieldOf("type").forGetter(DamagePredicate::type)).apply(var0, DamagePredicate::new);
   });

   public DamagePredicate(MinMaxBounds.Doubles dealtDamage, MinMaxBounds.Doubles takenDamage, Optional<EntityPredicate> sourceEntity, Optional<Boolean> blocked, Optional<DamageSourcePredicate> type) {
      super();
      this.dealtDamage = dealtDamage;
      this.takenDamage = takenDamage;
      this.sourceEntity = sourceEntity;
      this.blocked = blocked;
      this.type = type;
   }

   public boolean matches(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
      if (!this.dealtDamage.matches((double)var3)) {
         return false;
      } else if (!this.takenDamage.matches((double)var4)) {
         return false;
      } else if (this.sourceEntity.isPresent() && !((EntityPredicate)this.sourceEntity.get()).matches(var1, var2.getEntity())) {
         return false;
      } else if (this.blocked.isPresent() && (Boolean)this.blocked.get() != var5) {
         return false;
      } else {
         return !this.type.isPresent() || ((DamageSourcePredicate)this.type.get()).matches(var1, var2);
      }
   }

   public MinMaxBounds.Doubles dealtDamage() {
      return this.dealtDamage;
   }

   public MinMaxBounds.Doubles takenDamage() {
      return this.takenDamage;
   }

   public Optional<EntityPredicate> sourceEntity() {
      return this.sourceEntity;
   }

   public Optional<Boolean> blocked() {
      return this.blocked;
   }

   public Optional<DamageSourcePredicate> type() {
      return this.type;
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

      public Builder dealtDamage(MinMaxBounds.Doubles var1) {
         this.dealtDamage = var1;
         return this;
      }

      public Builder takenDamage(MinMaxBounds.Doubles var1) {
         this.takenDamage = var1;
         return this;
      }

      public Builder sourceEntity(EntityPredicate var1) {
         this.sourceEntity = Optional.of(var1);
         return this;
      }

      public Builder blocked(Boolean var1) {
         this.blocked = Optional.of(var1);
         return this;
      }

      public Builder type(DamageSourcePredicate var1) {
         this.type = Optional.of(var1);
         return this;
      }

      public Builder type(DamageSourcePredicate.Builder var1) {
         this.type = Optional.of(var1.build());
         return this;
      }

      public DamagePredicate build() {
         return new DamagePredicate(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
      }
   }
}
