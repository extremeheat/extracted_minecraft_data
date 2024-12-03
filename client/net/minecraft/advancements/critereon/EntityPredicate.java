package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.HolderGetter;
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
import net.minecraft.world.scores.Team;

public record EntityPredicate(Optional<EntityTypePredicate> entityType, Optional<DistancePredicate> distanceToPlayer, Optional<MovementPredicate> movement, LocationWrapper location, Optional<MobEffectsPredicate> effects, Optional<NbtPredicate> nbt, Optional<EntityFlagsPredicate> flags, Optional<EntityEquipmentPredicate> equipment, Optional<EntitySubPredicate> subPredicate, Optional<Integer> periodicTick, Optional<EntityPredicate> vehicle, Optional<EntityPredicate> passenger, Optional<EntityPredicate> targetedEntity, Optional<String> team, Optional<SlotsPredicate> slots) {
   public static final Codec<EntityPredicate> CODEC = Codec.recursive("EntityPredicate", (var0) -> RecordCodecBuilder.create((var1) -> var1.group(EntityTypePredicate.CODEC.optionalFieldOf("type").forGetter(EntityPredicate::entityType), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(EntityPredicate::distanceToPlayer), MovementPredicate.CODEC.optionalFieldOf("movement").forGetter(EntityPredicate::movement), EntityPredicate.LocationWrapper.CODEC.forGetter(EntityPredicate::location), MobEffectsPredicate.CODEC.optionalFieldOf("effects").forGetter(EntityPredicate::effects), NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(EntityPredicate::nbt), EntityFlagsPredicate.CODEC.optionalFieldOf("flags").forGetter(EntityPredicate::flags), EntityEquipmentPredicate.CODEC.optionalFieldOf("equipment").forGetter(EntityPredicate::equipment), EntitySubPredicate.CODEC.optionalFieldOf("type_specific").forGetter(EntityPredicate::subPredicate), ExtraCodecs.POSITIVE_INT.optionalFieldOf("periodic_tick").forGetter(EntityPredicate::periodicTick), var0.optionalFieldOf("vehicle").forGetter(EntityPredicate::vehicle), var0.optionalFieldOf("passenger").forGetter(EntityPredicate::passenger), var0.optionalFieldOf("targeted_entity").forGetter(EntityPredicate::targetedEntity), Codec.STRING.optionalFieldOf("team").forGetter(EntityPredicate::team), SlotsPredicate.CODEC.optionalFieldOf("slots").forGetter(EntityPredicate::slots)).apply(var1, EntityPredicate::new)));
   public static final Codec<ContextAwarePredicate> ADVANCEMENT_CODEC;

   public EntityPredicate(Optional<EntityTypePredicate> var1, Optional<DistancePredicate> var2, Optional<MovementPredicate> var3, LocationWrapper var4, Optional<MobEffectsPredicate> var5, Optional<NbtPredicate> var6, Optional<EntityFlagsPredicate> var7, Optional<EntityEquipmentPredicate> var8, Optional<EntitySubPredicate> var9, Optional<Integer> var10, Optional<EntityPredicate> var11, Optional<EntityPredicate> var12, Optional<EntityPredicate> var13, Optional<String> var14, Optional<SlotsPredicate> var15) {
      super();
      this.entityType = var1;
      this.distanceToPlayer = var2;
      this.movement = var3;
      this.location = var4;
      this.effects = var5;
      this.nbt = var6;
      this.flags = var7;
      this.equipment = var8;
      this.subPredicate = var9;
      this.periodicTick = var10;
      this.vehicle = var11;
      this.passenger = var12;
      this.targetedEntity = var13;
      this.team = var14;
      this.slots = var15;
   }

   public static ContextAwarePredicate wrap(Builder var0) {
      return wrap(var0.build());
   }

   public static Optional<ContextAwarePredicate> wrap(Optional<EntityPredicate> var0) {
      return var0.map(EntityPredicate::wrap);
   }

   public static List<ContextAwarePredicate> wrap(Builder... var0) {
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
      } else if (this.entityType.isPresent() && !((EntityTypePredicate)this.entityType.get()).matches(var3.getType())) {
         return false;
      } else {
         if (var2 == null) {
            if (this.distanceToPlayer.isPresent()) {
               return false;
            }
         } else if (this.distanceToPlayer.isPresent() && !((DistancePredicate)this.distanceToPlayer.get()).matches(var2.x, var2.y, var2.z, var3.getX(), var3.getY(), var3.getZ())) {
            return false;
         }

         if (this.movement.isPresent()) {
            Vec3 var4 = var3.getKnownMovement();
            Vec3 var5 = var4.scale(20.0);
            if (!((MovementPredicate)this.movement.get()).matches(var5.x, var5.y, var5.z, (double)var3.fallDistance)) {
               return false;
            }
         }

         if (this.location.located.isPresent() && !((LocationPredicate)this.location.located.get()).matches(var1, var3.getX(), var3.getY(), var3.getZ())) {
            return false;
         } else {
            if (this.location.steppingOn.isPresent()) {
               Vec3 var6 = Vec3.atCenterOf(var3.getOnPos());
               if (!((LocationPredicate)this.location.steppingOn.get()).matches(var1, var6.x(), var6.y(), var6.z())) {
                  return false;
               }
            }

            if (this.location.affectsMovement.isPresent()) {
               Vec3 var7 = Vec3.atCenterOf(var3.getBlockPosBelowThatAffectsMyMovement());
               if (!((LocationPredicate)this.location.affectsMovement.get()).matches(var1, var7.x(), var7.y(), var7.z())) {
                  return false;
               }
            }

            if (this.effects.isPresent() && !((MobEffectsPredicate)this.effects.get()).matches(var3)) {
               return false;
            } else if (this.flags.isPresent() && !((EntityFlagsPredicate)this.flags.get()).matches(var3)) {
               return false;
            } else if (this.equipment.isPresent() && !((EntityEquipmentPredicate)this.equipment.get()).matches(var3)) {
               return false;
            } else if (this.subPredicate.isPresent() && !((EntitySubPredicate)this.subPredicate.get()).matches(var3, var1, var2)) {
               return false;
            } else if (this.vehicle.isPresent() && !((EntityPredicate)this.vehicle.get()).matches(var1, var2, var3.getVehicle())) {
               return false;
            } else if (this.passenger.isPresent() && var3.getPassengers().stream().noneMatch((var3x) -> ((EntityPredicate)this.passenger.get()).matches(var1, var2, var3x))) {
               return false;
            } else if (this.targetedEntity.isPresent() && !((EntityPredicate)this.targetedEntity.get()).matches(var1, var2, var3 instanceof Mob ? ((Mob)var3).getTarget() : null)) {
               return false;
            } else if (this.periodicTick.isPresent() && var3.tickCount % (Integer)this.periodicTick.get() != 0) {
               return false;
            } else {
               if (this.team.isPresent()) {
                  PlayerTeam var8 = var3.getTeam();
                  if (var8 == null || !((String)this.team.get()).equals(((Team)var8).getName())) {
                     return false;
                  }
               }

               if (this.slots.isPresent() && !((SlotsPredicate)this.slots.get()).matches(var3)) {
                  return false;
               } else if (this.nbt.isPresent() && !((NbtPredicate)this.nbt.get()).matches(var3)) {
                  return false;
               } else {
                  return true;
               }
            }
         }
      }
   }

   public static LootContext createContext(ServerPlayer var0, Entity var1) {
      LootParams var2 = (new LootParams.Builder(var0.serverLevel())).withParameter(LootContextParams.THIS_ENTITY, var1).withParameter(LootContextParams.ORIGIN, var0.position()).create(LootContextParamSets.ADVANCEMENT_ENTITY);
      return (new LootContext.Builder(var2)).create(Optional.empty());
   }

   static {
      ADVANCEMENT_CODEC = Codec.withAlternative(ContextAwarePredicate.CODEC, CODEC, EntityPredicate::wrap);
   }

   public static record LocationWrapper(Optional<LocationPredicate> located, Optional<LocationPredicate> steppingOn, Optional<LocationPredicate> affectsMovement) {
      final Optional<LocationPredicate> located;
      final Optional<LocationPredicate> steppingOn;
      final Optional<LocationPredicate> affectsMovement;
      public static final MapCodec<LocationWrapper> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(LocationPredicate.CODEC.optionalFieldOf("location").forGetter(LocationWrapper::located), LocationPredicate.CODEC.optionalFieldOf("stepping_on").forGetter(LocationWrapper::steppingOn), LocationPredicate.CODEC.optionalFieldOf("movement_affected_by").forGetter(LocationWrapper::affectsMovement)).apply(var0, LocationWrapper::new));

      public LocationWrapper(Optional<LocationPredicate> var1, Optional<LocationPredicate> var2, Optional<LocationPredicate> var3) {
         super();
         this.located = var1;
         this.steppingOn = var2;
         this.affectsMovement = var3;
      }
   }

   public static class Builder {
      private Optional<EntityTypePredicate> entityType = Optional.empty();
      private Optional<DistancePredicate> distanceToPlayer = Optional.empty();
      private Optional<MovementPredicate> movement = Optional.empty();
      private Optional<LocationPredicate> located = Optional.empty();
      private Optional<LocationPredicate> steppingOnLocation = Optional.empty();
      private Optional<LocationPredicate> movementAffectedBy = Optional.empty();
      private Optional<MobEffectsPredicate> effects = Optional.empty();
      private Optional<NbtPredicate> nbt = Optional.empty();
      private Optional<EntityFlagsPredicate> flags = Optional.empty();
      private Optional<EntityEquipmentPredicate> equipment = Optional.empty();
      private Optional<EntitySubPredicate> subPredicate = Optional.empty();
      private Optional<Integer> periodicTick = Optional.empty();
      private Optional<EntityPredicate> vehicle = Optional.empty();
      private Optional<EntityPredicate> passenger = Optional.empty();
      private Optional<EntityPredicate> targetedEntity = Optional.empty();
      private Optional<String> team = Optional.empty();
      private Optional<SlotsPredicate> slots = Optional.empty();

      public Builder() {
         super();
      }

      public static Builder entity() {
         return new Builder();
      }

      public Builder of(HolderGetter<EntityType<?>> var1, EntityType<?> var2) {
         this.entityType = Optional.of(EntityTypePredicate.of(var1, var2));
         return this;
      }

      public Builder of(HolderGetter<EntityType<?>> var1, TagKey<EntityType<?>> var2) {
         this.entityType = Optional.of(EntityTypePredicate.of(var1, var2));
         return this;
      }

      public Builder entityType(EntityTypePredicate var1) {
         this.entityType = Optional.of(var1);
         return this;
      }

      public Builder distance(DistancePredicate var1) {
         this.distanceToPlayer = Optional.of(var1);
         return this;
      }

      public Builder moving(MovementPredicate var1) {
         this.movement = Optional.of(var1);
         return this;
      }

      public Builder located(LocationPredicate.Builder var1) {
         this.located = Optional.of(var1.build());
         return this;
      }

      public Builder steppingOn(LocationPredicate.Builder var1) {
         this.steppingOnLocation = Optional.of(var1.build());
         return this;
      }

      public Builder movementAffectedBy(LocationPredicate.Builder var1) {
         this.movementAffectedBy = Optional.of(var1.build());
         return this;
      }

      public Builder effects(MobEffectsPredicate.Builder var1) {
         this.effects = var1.build();
         return this;
      }

      public Builder nbt(NbtPredicate var1) {
         this.nbt = Optional.of(var1);
         return this;
      }

      public Builder flags(EntityFlagsPredicate.Builder var1) {
         this.flags = Optional.of(var1.build());
         return this;
      }

      public Builder equipment(EntityEquipmentPredicate.Builder var1) {
         this.equipment = Optional.of(var1.build());
         return this;
      }

      public Builder equipment(EntityEquipmentPredicate var1) {
         this.equipment = Optional.of(var1);
         return this;
      }

      public Builder subPredicate(EntitySubPredicate var1) {
         this.subPredicate = Optional.of(var1);
         return this;
      }

      public Builder periodicTick(int var1) {
         this.periodicTick = Optional.of(var1);
         return this;
      }

      public Builder vehicle(Builder var1) {
         this.vehicle = Optional.of(var1.build());
         return this;
      }

      public Builder passenger(Builder var1) {
         this.passenger = Optional.of(var1.build());
         return this;
      }

      public Builder targetedEntity(Builder var1) {
         this.targetedEntity = Optional.of(var1.build());
         return this;
      }

      public Builder team(String var1) {
         this.team = Optional.of(var1);
         return this;
      }

      public Builder slots(SlotsPredicate var1) {
         this.slots = Optional.of(var1);
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(this.entityType, this.distanceToPlayer, this.movement, new LocationWrapper(this.located, this.steppingOnLocation, this.movementAffectedBy), this.effects, this.nbt, this.flags, this.equipment, this.subPredicate, this.periodicTick, this.vehicle, this.passenger, this.targetedEntity, this.team, this.slots);
      }
   }
}
