package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;

public record EntityPredicate(
   Optional<EntityTypePredicate> c,
   Optional<DistancePredicate> d,
   Optional<LocationPredicate> e,
   Optional<LocationPredicate> f,
   Optional<MobEffectsPredicate> g,
   Optional<NbtPredicate> h,
   Optional<EntityFlagsPredicate> i,
   Optional<EntityEquipmentPredicate> j,
   Optional<EntitySubPredicate> k,
   Optional<EntityPredicate> l,
   Optional<EntityPredicate> m,
   Optional<EntityPredicate> n,
   Optional<String> o
) {
   private final Optional<EntityTypePredicate> entityType;
   private final Optional<DistancePredicate> distanceToPlayer;
   private final Optional<LocationPredicate> location;
   private final Optional<LocationPredicate> steppingOnLocation;
   private final Optional<MobEffectsPredicate> effects;
   private final Optional<NbtPredicate> nbt;
   private final Optional<EntityFlagsPredicate> flags;
   private final Optional<EntityEquipmentPredicate> equipment;
   private final Optional<EntitySubPredicate> subPredicate;
   private final Optional<EntityPredicate> vehicle;
   private final Optional<EntityPredicate> passenger;
   private final Optional<EntityPredicate> targetedEntity;
   private final Optional<String> team;
   public static final Codec<EntityPredicate> CODEC = ExtraCodecs.recursive(
      "EntityPredicate",
      var0 -> RecordCodecBuilder.create(
            var1 -> var1.group(
                     ExtraCodecs.strictOptionalField(EntityTypePredicate.CODEC, "type").forGetter(EntityPredicate::entityType),
                     ExtraCodecs.strictOptionalField(DistancePredicate.CODEC, "distance").forGetter(EntityPredicate::distanceToPlayer),
                     ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "location").forGetter(EntityPredicate::location),
                     ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "stepping_on").forGetter(EntityPredicate::steppingOnLocation),
                     ExtraCodecs.strictOptionalField(MobEffectsPredicate.CODEC, "effects").forGetter(EntityPredicate::effects),
                     ExtraCodecs.strictOptionalField(NbtPredicate.CODEC, "nbt").forGetter(EntityPredicate::nbt),
                     ExtraCodecs.strictOptionalField(EntityFlagsPredicate.CODEC, "flags").forGetter(EntityPredicate::flags),
                     ExtraCodecs.strictOptionalField(EntityEquipmentPredicate.CODEC, "equipment").forGetter(EntityPredicate::equipment),
                     ExtraCodecs.strictOptionalField(EntitySubPredicate.CODEC, "type_specific").forGetter(EntityPredicate::subPredicate),
                     ExtraCodecs.strictOptionalField(var0, "vehicle").forGetter(EntityPredicate::vehicle),
                     ExtraCodecs.strictOptionalField(var0, "passenger").forGetter(EntityPredicate::passenger),
                     ExtraCodecs.strictOptionalField(var0, "targeted_entity").forGetter(EntityPredicate::targetedEntity),
                     ExtraCodecs.strictOptionalField(Codec.STRING, "team").forGetter(EntityPredicate::team)
                  )
                  .apply(var1, EntityPredicate::new)
         )
   );
   public static final Codec<ContextAwarePredicate> ADVANCEMENT_CODEC = ExtraCodecs.withAlternative(ContextAwarePredicate.CODEC, CODEC, EntityPredicate::wrap);

   public EntityPredicate(
      Optional<EntityTypePredicate> var1,
      Optional<DistancePredicate> var2,
      Optional<LocationPredicate> var3,
      Optional<LocationPredicate> var4,
      Optional<MobEffectsPredicate> var5,
      Optional<NbtPredicate> var6,
      Optional<EntityFlagsPredicate> var7,
      Optional<EntityEquipmentPredicate> var8,
      Optional<EntitySubPredicate> var9,
      Optional<EntityPredicate> var10,
      Optional<EntityPredicate> var11,
      Optional<EntityPredicate> var12,
      Optional<String> var13
   ) {
      super();
      this.entityType = var1;
      this.distanceToPlayer = var2;
      this.location = var3;
      this.steppingOnLocation = var4;
      this.effects = var5;
      this.nbt = var6;
      this.flags = var7;
      this.equipment = var8;
      this.subPredicate = var9;
      this.vehicle = var10;
      this.passenger = var11;
      this.targetedEntity = var12;
      this.team = var13;
   }

   public static ContextAwarePredicate wrap(EntityPredicate.Builder var0) {
      return wrap(var0.build());
   }

   public static Optional<ContextAwarePredicate> wrap(Optional<EntityPredicate> var0) {
      return var0.map(EntityPredicate::wrap);
   }

   public static List<ContextAwarePredicate> wrap(EntityPredicate.Builder... var0) {
      return Stream.of(var0).map(EntityPredicate::wrap).toList();
   }

   public static ContextAwarePredicate wrap(EntityPredicate var0) {
      LootItemCondition var1 = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, var0).build();
      return new ContextAwarePredicate(List.of(var1));
   }

   public boolean matches(ServerPlayer var1, @Nullable Entity var2) {
      return this.matches(var1.serverLevel(), var1.position(), var2);
   }

   public boolean matches(ServerLevel var1, @Nullable Vec3 var2, @Nullable Entity var3) {
      if (var3 == null) {
         return false;
      } else if (this.entityType.isPresent() && !this.entityType.get().matches(var3.getType())) {
         return false;
      } else {
         if (var2 == null) {
            if (this.distanceToPlayer.isPresent()) {
               return false;
            }
         } else if (this.distanceToPlayer.isPresent() && !this.distanceToPlayer.get().matches(var2.x, var2.y, var2.z, var3.getX(), var3.getY(), var3.getZ())) {
            return false;
         }

         if (this.location.isPresent() && !this.location.get().matches(var1, var3.getX(), var3.getY(), var3.getZ())) {
            return false;
         } else {
            if (this.steppingOnLocation.isPresent()) {
               Vec3 var4 = Vec3.atCenterOf(var3.getOnPos());
               if (!this.steppingOnLocation.get().matches(var1, var4.x(), var4.y(), var4.z())) {
                  return false;
               }
            }

            if (this.effects.isPresent() && !this.effects.get().matches(var3)) {
               return false;
            } else if (this.nbt.isPresent() && !this.nbt.get().matches(var3)) {
               return false;
            } else if (this.flags.isPresent() && !this.flags.get().matches(var3)) {
               return false;
            } else if (this.equipment.isPresent() && !this.equipment.get().matches(var3)) {
               return false;
            } else if (this.subPredicate.isPresent() && !this.subPredicate.get().matches(var3, var1, var2)) {
               return false;
            } else if (this.vehicle.isPresent() && !this.vehicle.get().matches(var1, var2, var3.getVehicle())) {
               return false;
            } else if (this.passenger.isPresent() && var3.getPassengers().stream().noneMatch(var3x -> this.passenger.get().matches(var1, var2, var3x))) {
               return false;
            } else if (this.targetedEntity.isPresent() && !this.targetedEntity.get().matches(var1, var2, var3 instanceof Mob ? ((Mob)var3).getTarget() : null)
               )
             {
               return false;
            } else {
               if (this.team.isPresent()) {
                  PlayerTeam var5 = var3.getTeam();
                  if (var5 == null || !this.team.get().equals(var5.getName())) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   public static LootContext createContext(ServerPlayer var0, Entity var1) {
      LootParams var2 = new LootParams.Builder(var0.serverLevel())
         .withParameter(LootContextParams.THIS_ENTITY, var1)
         .withParameter(LootContextParams.ORIGIN, var0.position())
         .create(LootContextParamSets.ADVANCEMENT_ENTITY);
      return new LootContext.Builder(var2).create(Optional.empty());
   }

   public static class Builder {
      private Optional<EntityTypePredicate> entityType = Optional.empty();
      private Optional<DistancePredicate> distanceToPlayer = Optional.empty();
      private Optional<LocationPredicate> location = Optional.empty();
      private Optional<LocationPredicate> steppingOnLocation = Optional.empty();
      private Optional<MobEffectsPredicate> effects = Optional.empty();
      private Optional<NbtPredicate> nbt = Optional.empty();
      private Optional<EntityFlagsPredicate> flags = Optional.empty();
      private Optional<EntityEquipmentPredicate> equipment = Optional.empty();
      private Optional<EntitySubPredicate> subPredicate = Optional.empty();
      private Optional<EntityPredicate> vehicle = Optional.empty();
      private Optional<EntityPredicate> passenger = Optional.empty();
      private Optional<EntityPredicate> targetedEntity = Optional.empty();
      private Optional<String> team = Optional.empty();

      public Builder() {
         super();
      }

      public static EntityPredicate.Builder entity() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder of(EntityType<?> var1) {
         this.entityType = Optional.of(EntityTypePredicate.of(var1));
         return this;
      }

      public EntityPredicate.Builder of(TagKey<EntityType<?>> var1) {
         this.entityType = Optional.of(EntityTypePredicate.of(var1));
         return this;
      }

      public EntityPredicate.Builder entityType(EntityTypePredicate var1) {
         this.entityType = Optional.of(var1);
         return this;
      }

      public EntityPredicate.Builder distance(DistancePredicate var1) {
         this.distanceToPlayer = Optional.of(var1);
         return this;
      }

      public EntityPredicate.Builder located(LocationPredicate.Builder var1) {
         this.location = Optional.of(var1.build());
         return this;
      }

      public EntityPredicate.Builder steppingOn(LocationPredicate.Builder var1) {
         this.steppingOnLocation = Optional.of(var1.build());
         return this;
      }

      public EntityPredicate.Builder effects(MobEffectsPredicate.Builder var1) {
         this.effects = var1.build();
         return this;
      }

      public EntityPredicate.Builder nbt(NbtPredicate var1) {
         this.nbt = Optional.of(var1);
         return this;
      }

      public EntityPredicate.Builder flags(EntityFlagsPredicate.Builder var1) {
         this.flags = Optional.of(var1.build());
         return this;
      }

      public EntityPredicate.Builder equipment(EntityEquipmentPredicate.Builder var1) {
         this.equipment = Optional.of(var1.build());
         return this;
      }

      public EntityPredicate.Builder equipment(EntityEquipmentPredicate var1) {
         this.equipment = Optional.of(var1);
         return this;
      }

      public EntityPredicate.Builder subPredicate(EntitySubPredicate var1) {
         this.subPredicate = Optional.of(var1);
         return this;
      }

      public EntityPredicate.Builder vehicle(EntityPredicate.Builder var1) {
         this.vehicle = Optional.of(var1.build());
         return this;
      }

      public EntityPredicate.Builder passenger(EntityPredicate.Builder var1) {
         this.passenger = Optional.of(var1.build());
         return this;
      }

      public EntityPredicate.Builder targetedEntity(EntityPredicate.Builder var1) {
         this.targetedEntity = Optional.of(var1.build());
         return this;
      }

      public EntityPredicate.Builder team(String var1) {
         this.team = Optional.of(var1);
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(
            this.entityType,
            this.distanceToPlayer,
            this.location,
            this.steppingOnLocation,
            this.effects,
            this.nbt,
            this.flags,
            this.equipment,
            this.subPredicate,
            this.vehicle,
            this.passenger,
            this.targetedEntity,
            this.team
         );
      }
   }
}
