package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

public class EntityPredicate {
   public static final EntityPredicate ANY = new EntityPredicate(
      EntityTypePredicate.ANY,
      DistancePredicate.ANY,
      LocationPredicate.ANY,
      LocationPredicate.ANY,
      MobEffectsPredicate.ANY,
      NbtPredicate.ANY,
      EntityFlagsPredicate.ANY,
      EntityEquipmentPredicate.ANY,
      EntitySubPredicate.ANY,
      null
   );
   private final EntityTypePredicate entityType;
   private final DistancePredicate distanceToPlayer;
   private final LocationPredicate location;
   private final LocationPredicate steppingOnLocation;
   private final MobEffectsPredicate effects;
   private final NbtPredicate nbt;
   private final EntityFlagsPredicate flags;
   private final EntityEquipmentPredicate equipment;
   private final EntitySubPredicate subPredicate;
   private final EntityPredicate vehicle;
   private final EntityPredicate passenger;
   private final EntityPredicate targetedEntity;
   @Nullable
   private final String team;

   private EntityPredicate(
      EntityTypePredicate var1,
      DistancePredicate var2,
      LocationPredicate var3,
      LocationPredicate var4,
      MobEffectsPredicate var5,
      NbtPredicate var6,
      EntityFlagsPredicate var7,
      EntityEquipmentPredicate var8,
      EntitySubPredicate var9,
      @Nullable String var10
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
      this.passenger = this;
      this.vehicle = this;
      this.targetedEntity = this;
      this.team = var10;
   }

   EntityPredicate(
      EntityTypePredicate var1,
      DistancePredicate var2,
      LocationPredicate var3,
      LocationPredicate var4,
      MobEffectsPredicate var5,
      NbtPredicate var6,
      EntityFlagsPredicate var7,
      EntityEquipmentPredicate var8,
      EntitySubPredicate var9,
      EntityPredicate var10,
      EntityPredicate var11,
      EntityPredicate var12,
      @Nullable String var13
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

   public boolean matches(ServerPlayer var1, @Nullable Entity var2) {
      return this.matches(var1.getLevel(), var1.position(), var2);
   }

   public boolean matches(ServerLevel var1, @Nullable Vec3 var2, @Nullable Entity var3) {
      if (this == ANY) {
         return true;
      } else if (var3 == null) {
         return false;
      } else if (!this.entityType.matches(var3.getType())) {
         return false;
      } else {
         if (var2 == null) {
            if (this.distanceToPlayer != DistancePredicate.ANY) {
               return false;
            }
         } else if (!this.distanceToPlayer.matches(var2.x, var2.y, var2.z, var3.getX(), var3.getY(), var3.getZ())) {
            return false;
         }

         if (!this.location.matches(var1, var3.getX(), var3.getY(), var3.getZ())) {
            return false;
         } else {
            if (this.steppingOnLocation != LocationPredicate.ANY) {
               Vec3 var4 = Vec3.atCenterOf(var3.getOnPosLegacy());
               if (!this.steppingOnLocation.matches(var1, var4.x(), var4.y(), var4.z())) {
                  return false;
               }
            }

            if (!this.effects.matches(var3)) {
               return false;
            } else if (!this.nbt.matches(var3)) {
               return false;
            } else if (!this.flags.matches(var3)) {
               return false;
            } else if (!this.equipment.matches(var3)) {
               return false;
            } else if (!this.subPredicate.matches(var3, var1, var2)) {
               return false;
            } else if (!this.vehicle.matches(var1, var2, var3.getVehicle())) {
               return false;
            } else if (this.passenger != ANY && var3.getPassengers().stream().noneMatch(var3x -> this.passenger.matches(var1, var2, var3x))) {
               return false;
            } else if (!this.targetedEntity.matches(var1, var2, var3 instanceof Mob ? ((Mob)var3).getTarget() : null)) {
               return false;
            } else {
               if (this.team != null) {
                  Team var5 = var3.getTeam();
                  if (var5 == null || !this.team.equals(var5.getName())) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   public static EntityPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "entity");
         EntityTypePredicate var2 = EntityTypePredicate.fromJson(var1.get("type"));
         DistancePredicate var3 = DistancePredicate.fromJson(var1.get("distance"));
         LocationPredicate var4 = LocationPredicate.fromJson(var1.get("location"));
         LocationPredicate var5 = LocationPredicate.fromJson(var1.get("stepping_on"));
         MobEffectsPredicate var6 = MobEffectsPredicate.fromJson(var1.get("effects"));
         NbtPredicate var7 = NbtPredicate.fromJson(var1.get("nbt"));
         EntityFlagsPredicate var8 = EntityFlagsPredicate.fromJson(var1.get("flags"));
         EntityEquipmentPredicate var9 = EntityEquipmentPredicate.fromJson(var1.get("equipment"));
         EntitySubPredicate var10 = EntitySubPredicate.fromJson(var1.get("type_specific"));
         EntityPredicate var11 = fromJson(var1.get("vehicle"));
         EntityPredicate var12 = fromJson(var1.get("passenger"));
         EntityPredicate var13 = fromJson(var1.get("targeted_entity"));
         String var14 = GsonHelper.getAsString(var1, "team", null);
         return new EntityPredicate.Builder()
            .entityType(var2)
            .distance(var3)
            .located(var4)
            .steppingOn(var5)
            .effects(var6)
            .nbt(var7)
            .flags(var8)
            .equipment(var9)
            .subPredicate(var10)
            .team(var14)
            .vehicle(var11)
            .passenger(var12)
            .targetedEntity(var13)
            .build();
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("type", this.entityType.serializeToJson());
         var1.add("distance", this.distanceToPlayer.serializeToJson());
         var1.add("location", this.location.serializeToJson());
         var1.add("stepping_on", this.steppingOnLocation.serializeToJson());
         var1.add("effects", this.effects.serializeToJson());
         var1.add("nbt", this.nbt.serializeToJson());
         var1.add("flags", this.flags.serializeToJson());
         var1.add("equipment", this.equipment.serializeToJson());
         var1.add("type_specific", this.subPredicate.serialize());
         var1.add("vehicle", this.vehicle.serializeToJson());
         var1.add("passenger", this.passenger.serializeToJson());
         var1.add("targeted_entity", this.targetedEntity.serializeToJson());
         var1.addProperty("team", this.team);
         return var1;
      }
   }

   public static LootContext createContext(ServerPlayer var0, Entity var1) {
      return new LootContext.Builder(var0.getLevel())
         .withParameter(LootContextParams.THIS_ENTITY, var1)
         .withParameter(LootContextParams.ORIGIN, var0.position())
         .withRandom(var0.getRandom())
         .create(LootContextParamSets.ADVANCEMENT_ENTITY);
   }

   public static class Builder {
      private EntityTypePredicate entityType = EntityTypePredicate.ANY;
      private DistancePredicate distanceToPlayer = DistancePredicate.ANY;
      private LocationPredicate location = LocationPredicate.ANY;
      private LocationPredicate steppingOnLocation = LocationPredicate.ANY;
      private MobEffectsPredicate effects = MobEffectsPredicate.ANY;
      private NbtPredicate nbt = NbtPredicate.ANY;
      private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
      private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
      private EntitySubPredicate subPredicate = EntitySubPredicate.ANY;
      private EntityPredicate vehicle = EntityPredicate.ANY;
      private EntityPredicate passenger = EntityPredicate.ANY;
      private EntityPredicate targetedEntity = EntityPredicate.ANY;
      @Nullable
      private String team;

      public Builder() {
         super();
      }

      public static EntityPredicate.Builder entity() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder of(EntityType<?> var1) {
         this.entityType = EntityTypePredicate.of(var1);
         return this;
      }

      public EntityPredicate.Builder of(TagKey<EntityType<?>> var1) {
         this.entityType = EntityTypePredicate.of(var1);
         return this;
      }

      public EntityPredicate.Builder entityType(EntityTypePredicate var1) {
         this.entityType = var1;
         return this;
      }

      public EntityPredicate.Builder distance(DistancePredicate var1) {
         this.distanceToPlayer = var1;
         return this;
      }

      public EntityPredicate.Builder located(LocationPredicate var1) {
         this.location = var1;
         return this;
      }

      public EntityPredicate.Builder steppingOn(LocationPredicate var1) {
         this.steppingOnLocation = var1;
         return this;
      }

      public EntityPredicate.Builder effects(MobEffectsPredicate var1) {
         this.effects = var1;
         return this;
      }

      public EntityPredicate.Builder nbt(NbtPredicate var1) {
         this.nbt = var1;
         return this;
      }

      public EntityPredicate.Builder flags(EntityFlagsPredicate var1) {
         this.flags = var1;
         return this;
      }

      public EntityPredicate.Builder equipment(EntityEquipmentPredicate var1) {
         this.equipment = var1;
         return this;
      }

      public EntityPredicate.Builder subPredicate(EntitySubPredicate var1) {
         this.subPredicate = var1;
         return this;
      }

      public EntityPredicate.Builder vehicle(EntityPredicate var1) {
         this.vehicle = var1;
         return this;
      }

      public EntityPredicate.Builder passenger(EntityPredicate var1) {
         this.passenger = var1;
         return this;
      }

      public EntityPredicate.Builder targetedEntity(EntityPredicate var1) {
         this.targetedEntity = var1;
         return this;
      }

      public EntityPredicate.Builder team(@Nullable String var1) {
         this.team = var1;
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

   public static class Composite {
      public static final EntityPredicate.Composite ANY = new EntityPredicate.Composite(new LootItemCondition[0]);
      private final LootItemCondition[] conditions;
      private final Predicate<LootContext> compositePredicates;

      private Composite(LootItemCondition[] var1) {
         super();
         this.conditions = var1;
         this.compositePredicates = LootItemConditions.andConditions(var1);
      }

      public static EntityPredicate.Composite create(LootItemCondition... var0) {
         return new EntityPredicate.Composite(var0);
      }

      public static EntityPredicate.Composite fromJson(JsonObject var0, String var1, DeserializationContext var2) {
         JsonElement var3 = var0.get(var1);
         return fromElement(var1, var2, var3);
      }

      public static EntityPredicate.Composite[] fromJsonArray(JsonObject var0, String var1, DeserializationContext var2) {
         JsonElement var3 = var0.get(var1);
         if (var3 != null && !var3.isJsonNull()) {
            JsonArray var4 = GsonHelper.convertToJsonArray(var3, var1);
            EntityPredicate.Composite[] var5 = new EntityPredicate.Composite[var4.size()];

            for(int var6 = 0; var6 < var4.size(); ++var6) {
               var5[var6] = fromElement(var1 + "[" + var6 + "]", var2, var4.get(var6));
            }

            return var5;
         } else {
            return new EntityPredicate.Composite[0];
         }
      }

      private static EntityPredicate.Composite fromElement(String var0, DeserializationContext var1, @Nullable JsonElement var2) {
         if (var2 != null && var2.isJsonArray()) {
            LootItemCondition[] var4 = var1.deserializeConditions(
               var2.getAsJsonArray(), var1.getAdvancementId() + "/" + var0, LootContextParamSets.ADVANCEMENT_ENTITY
            );
            return new EntityPredicate.Composite(var4);
         } else {
            EntityPredicate var3 = EntityPredicate.fromJson(var2);
            return wrap(var3);
         }
      }

      public static EntityPredicate.Composite wrap(EntityPredicate var0) {
         if (var0 == EntityPredicate.ANY) {
            return ANY;
         } else {
            LootItemCondition var1 = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, var0).build();
            return new EntityPredicate.Composite(new LootItemCondition[]{var1});
         }
      }

      public boolean matches(LootContext var1) {
         return this.compositePredicates.test(var1);
      }

      public JsonElement toJson(SerializationContext var1) {
         return (JsonElement)(this.conditions.length == 0 ? JsonNull.INSTANCE : var1.serializeConditions(this.conditions));
      }

      public static JsonElement toJson(EntityPredicate.Composite[] var0, SerializationContext var1) {
         if (var0.length == 0) {
            return JsonNull.INSTANCE;
         } else {
            JsonArray var2 = new JsonArray();

            for(EntityPredicate.Composite var6 : var0) {
               var2.add(var6.toJson(var1));
            }

            return var2;
         }
      }
   }
}
