package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;

public record DamagePredicate(
   MinMaxBounds.Doubles a, MinMaxBounds.Doubles b, Optional<EntityPredicate> c, Optional<Boolean> d, Optional<DamageSourcePredicate> e
) {
   private final MinMaxBounds.Doubles dealtDamage;
   private final MinMaxBounds.Doubles takenDamage;
   private final Optional<EntityPredicate> sourceEntity;
   private final Optional<Boolean> blocked;
   private final Optional<DamageSourcePredicate> type;

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
      } else if (this.sourceEntity.isPresent() && !this.sourceEntity.get().matches(var1, var2.getEntity())) {
         return false;
      } else if (this.blocked.isPresent() && this.blocked.get() != var5) {
         return false;
      } else {
         return !this.type.isPresent() || this.type.get().matches(var1, var2);
      }
   }

   public static Optional<DamagePredicate> fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "damage");
         MinMaxBounds.Doubles var2 = MinMaxBounds.Doubles.fromJson(var1.get("dealt"));
         MinMaxBounds.Doubles var3 = MinMaxBounds.Doubles.fromJson(var1.get("taken"));
         Optional var4 = var1.has("blocked") ? Optional.of(GsonHelper.getAsBoolean(var1, "blocked")) : Optional.empty();
         Optional var5 = EntityPredicate.fromJson(var1.get("source_entity"));
         Optional var6 = DamageSourcePredicate.fromJson(var1.get("type"));
         return var2.isAny() && var3.isAny() && var5.isEmpty() && var4.isEmpty() && var6.isEmpty()
            ? Optional.empty()
            : Optional.of(new DamagePredicate(var2, var3, var5, var4, var6));
      } else {
         return Optional.empty();
      }
   }

   public JsonElement serializeToJson() {
      JsonObject var1 = new JsonObject();
      var1.add("dealt", this.dealtDamage.serializeToJson());
      var1.add("taken", this.takenDamage.serializeToJson());
      this.sourceEntity.ifPresent(var1x -> var1.add("source_entity", var1x.serializeToJson()));
      this.type.ifPresent(var1x -> var1.add("type", var1x.serializeToJson()));
      this.blocked.ifPresent(var1x -> var1.addProperty("blocked", var1x));
      return var1;
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
