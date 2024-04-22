package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
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
   Optional<EntityTypePredicate> entityType,
   Optional<DistancePredicate> distanceToPlayer,
   Optional<LocationPredicate> location,
   Optional<LocationPredicate> steppingOnLocation,
   Optional<MobEffectsPredicate> effects,
   Optional<NbtPredicate> nbt,
   Optional<EntityFlagsPredicate> flags,
   Optional<EntityEquipmentPredicate> equipment,
   Optional<EntitySubPredicate> subPredicate,
   Optional<EntityPredicate> vehicle,
   Optional<EntityPredicate> passenger,
   Optional<EntityPredicate> targetedEntity,
   Optional<String> team,
   Optional<SlotsPredicate> slots
) {
   public static final Codec<EntityPredicate> CODEC = Codec.recursive(
      "EntityPredicate",
      var0 -> RecordCodecBuilder.create(
            var1 -> var1.group(
                     EntityTypePredicate.CODEC.optionalFieldOf("type").forGetter(EntityPredicate::entityType),
                     DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(EntityPredicate::distanceToPlayer),
                     LocationPredicate.CODEC.optionalFieldOf("location").forGetter(EntityPredicate::location),
                     LocationPredicate.CODEC.optionalFieldOf("stepping_on").forGetter(EntityPredicate::steppingOnLocation),
                     MobEffectsPredicate.CODEC.optionalFieldOf("effects").forGetter(EntityPredicate::effects),
                     NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(EntityPredicate::nbt),
                     EntityFlagsPredicate.CODEC.optionalFieldOf("flags").forGetter(EntityPredicate::flags),
                     EntityEquipmentPredicate.CODEC.optionalFieldOf("equipment").forGetter(EntityPredicate::equipment),
                     EntitySubPredicate.CODEC.optionalFieldOf("type_specific").forGetter(EntityPredicate::subPredicate),
                     var0.optionalFieldOf("vehicle").forGetter(EntityPredicate::vehicle),
                     var0.optionalFieldOf("passenger").forGetter(EntityPredicate::passenger),
                     var0.optionalFieldOf("targeted_entity").forGetter(EntityPredicate::targetedEntity),
                     Codec.STRING.optionalFieldOf("team").forGetter(EntityPredicate::team),
                     SlotsPredicate.CODEC.optionalFieldOf("slots").forGetter(EntityPredicate::slots)
                  )
                  .apply(var1, EntityPredicate::new)
         )
   );
   public static final Codec<ContextAwarePredicate> ADVANCEMENT_CODEC = Codec.withAlternative(ContextAwarePredicate.CODEC, CODEC, EntityPredicate::wrap);

   public EntityPredicate(
      Optional<EntityTypePredicate> entityType,
      Optional<DistancePredicate> distanceToPlayer,
      Optional<LocationPredicate> location,
      Optional<LocationPredicate> steppingOnLocation,
      Optional<MobEffectsPredicate> effects,
      Optional<NbtPredicate> nbt,
      Optional<EntityFlagsPredicate> flags,
      Optional<EntityEquipmentPredicate> equipment,
      Optional<EntitySubPredicate> subPredicate,
      Optional<EntityPredicate> vehicle,
      Optional<EntityPredicate> passenger,
      Optional<EntityPredicate> targetedEntity,
      Optional<String> team,
      Optional<SlotsPredicate> slots
   ) {
      super();
      this.entityType = entityType;
      this.distanceToPlayer = distanceToPlayer;
      this.location = location;
      this.steppingOnLocation = steppingOnLocation;
      this.effects = effects;
      this.nbt = nbt;
      this.flags = flags;
      this.equipment = equipment;
      this.subPredicate = subPredicate;
      this.vehicle = vehicle;
      this.passenger = passenger;
      this.targetedEntity = targetedEntity;
      this.team = team;
      this.slots = slots;
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
            } else if (this.targetedEntity.isPresent() && !this.targetedEntity.get().matches(var1, var2, var3 instanceof Mob ? ((Mob)var3).getTarget() : null)) {
               return false;
            } else {
               if (this.team.isPresent()) {
                  PlayerTeam var5 = var3.getTeam();
                  if (var5 == null || !this.team.get().equals(var5.getName())) {
                     return false;
                  }
               }

               return this.slots.isPresent() && !this.slots.get().matches(var3) ? false : !this.nbt.isPresent() || this.nbt.get().matches(var3);
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
      private Optional<SlotsPredicate> slots = Optional.empty();

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

      public EntityPredicate.Builder slots(SlotsPredicate var1) {
         this.slots = Optional.of(var1);
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
            this.team,
            this.slots
         );
      }
   }
}