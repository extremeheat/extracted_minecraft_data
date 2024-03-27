package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public record DamagePredicate(
   MinMaxBounds.Doubles b, MinMaxBounds.Doubles c, Optional<EntityPredicate> d, Optional<Boolean> e, Optional<DamageSourcePredicate> f
) {
   private final MinMaxBounds.Doubles dealtDamage;
   private final MinMaxBounds.Doubles takenDamage;
   private final Optional<EntityPredicate> sourceEntity;
   private final Optional<Boolean> blocked;
   private final Optional<DamageSourcePredicate> type;
   public static final Codec<DamagePredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               MinMaxBounds.Doubles.CODEC.optionalFieldOf("dealt", MinMaxBounds.Doubles.ANY).forGetter(DamagePredicate::dealtDamage),
               MinMaxBounds.Doubles.CODEC.optionalFieldOf("taken", MinMaxBounds.Doubles.ANY).forGetter(DamagePredicate::takenDamage),
               EntityPredicate.CODEC.optionalFieldOf("source_entity").forGetter(DamagePredicate::sourceEntity),
               Codec.BOOL.optionalFieldOf("blocked").forGetter(DamagePredicate::blocked),
               DamageSourcePredicate.CODEC.optionalFieldOf("type").forGetter(DamagePredicate::type)
            )
            .apply(var0, DamagePredicate::new)
   );

   public DamagePredicate(
      MinMaxBounds.Doubles var1, MinMaxBounds.Doubles var2, Optional<EntityPredicate> var3, Optional<Boolean> var4, Optional<DamageSourcePredicate> var5
   ) {
      super();
      this.dealtDamage = var1;
      this.takenDamage = var2;
      this.sourceEntity = var3;
      this.blocked = var4;
      this.type = var5;
   }

   public boolean matches(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
      if (!this.dealtDamage.matches((double)var3)) {
         return false;
      } else if (!this.takenDamage.matches((double)var4)) {
         return false;
      } else if (this.sourceEntity.isPresent() && !((EntityPredicate)this.sourceEntity.get()).matches(var1, var2.getEntity())) {
         return false;
      } else if (this.blocked.isPresent() && this.blocked.get() != var5) {
         return false;
      } else {
         return !this.type.isPresent() || ((DamageSourcePredicate)this.type.get()).matches(var1, var2);
      }
   }

   public static class Builder {
      private MinMaxBounds.Doubles dealtDamage = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles takenDamage = MinMaxBounds.Doubles.ANY;
      private Optional<EntityPredicate> sourceEntity = Optional.empty();
      private Optional<Boolean> blocked = Optional.empty();
      private Optional<DamageSourcePredicate> type = Optional.empty();

      public Builder() {
         super();
      }

      public static DamagePredicate.Builder damageInstance() {
         return new DamagePredicate.Builder();
      }

      public DamagePredicate.Builder dealtDamage(MinMaxBounds.Doubles var1) {
         this.dealtDamage = var1;
         return this;
      }

      public DamagePredicate.Builder takenDamage(MinMaxBounds.Doubles var1) {
         this.takenDamage = var1;
         return this;
      }

      public DamagePredicate.Builder sourceEntity(EntityPredicate var1) {
         this.sourceEntity = Optional.of(var1);
         return this;
      }

      public DamagePredicate.Builder blocked(Boolean var1) {
         this.blocked = Optional.of(var1);
         return this;
      }

      public DamagePredicate.Builder type(DamageSourcePredicate var1) {
         this.type = Optional.of(var1);
         return this;
      }

      public DamagePredicate.Builder type(DamageSourcePredicate.Builder var1) {
         this.type = Optional.of(var1.build());
         return this;
      }

      public DamagePredicate build() {
         return new DamagePredicate(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
      }
   }
}
