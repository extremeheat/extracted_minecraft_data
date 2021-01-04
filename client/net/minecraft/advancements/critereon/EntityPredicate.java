package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.phys.Vec3;

public class EntityPredicate {
   public static final EntityPredicate ANY;
   public static final EntityPredicate[] ANY_ARRAY;
   private final EntityTypePredicate entityType;
   private final DistancePredicate distanceToPlayer;
   private final LocationPredicate location;
   private final MobEffectsPredicate effects;
   private final NbtPredicate nbt;
   private final EntityFlagsPredicate flags;
   private final EntityEquipmentPredicate equipment;
   private final ResourceLocation catType;

   private EntityPredicate(EntityTypePredicate var1, DistancePredicate var2, LocationPredicate var3, MobEffectsPredicate var4, NbtPredicate var5, EntityFlagsPredicate var6, EntityEquipmentPredicate var7, @Nullable ResourceLocation var8) {
      super();
      this.entityType = var1;
      this.distanceToPlayer = var2;
      this.location = var3;
      this.effects = var4;
      this.nbt = var5;
      this.flags = var6;
      this.equipment = var7;
      this.catType = var8;
   }

   public boolean matches(ServerPlayer var1, @Nullable Entity var2) {
      return this.matches(var1.getLevel(), new Vec3(var1.x, var1.y, var1.z), var2);
   }

   public boolean matches(ServerLevel var1, Vec3 var2, @Nullable Entity var3) {
      if (this == ANY) {
         return true;
      } else if (var3 == null) {
         return false;
      } else if (!this.entityType.matches(var3.getType())) {
         return false;
      } else if (!this.distanceToPlayer.matches(var2.x, var2.y, var2.z, var3.x, var3.y, var3.z)) {
         return false;
      } else if (!this.location.matches(var1, var3.x, var3.y, var3.z)) {
         return false;
      } else if (!this.effects.matches(var3)) {
         return false;
      } else if (!this.nbt.matches(var3)) {
         return false;
      } else if (!this.flags.matches(var3)) {
         return false;
      } else if (!this.equipment.matches(var3)) {
         return false;
      } else {
         return this.catType == null || var3 instanceof Cat && ((Cat)var3).getResourceLocation().equals(this.catType);
      }
   }

   public static EntityPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "entity");
         EntityTypePredicate var2 = EntityTypePredicate.fromJson(var1.get("type"));
         DistancePredicate var3 = DistancePredicate.fromJson(var1.get("distance"));
         LocationPredicate var4 = LocationPredicate.fromJson(var1.get("location"));
         MobEffectsPredicate var5 = MobEffectsPredicate.fromJson(var1.get("effects"));
         NbtPredicate var6 = NbtPredicate.fromJson(var1.get("nbt"));
         EntityFlagsPredicate var7 = EntityFlagsPredicate.fromJson(var1.get("flags"));
         EntityEquipmentPredicate var8 = EntityEquipmentPredicate.fromJson(var1.get("equipment"));
         ResourceLocation var9 = var1.has("catType") ? new ResourceLocation(GsonHelper.getAsString(var1, "catType")) : null;
         return (new EntityPredicate.Builder()).entityType(var2).distance(var3).located(var4).effects(var5).nbt(var6).flags(var7).equipment(var8).catType(var9).build();
      } else {
         return ANY;
      }
   }

   public static EntityPredicate[] fromJsonArray(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonArray var1 = GsonHelper.convertToJsonArray(var0, "entities");
         EntityPredicate[] var2 = new EntityPredicate[var1.size()];

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            var2[var3] = fromJson(var1.get(var3));
         }

         return var2;
      } else {
         return ANY_ARRAY;
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
         var1.add("effects", this.effects.serializeToJson());
         var1.add("nbt", this.nbt.serializeToJson());
         var1.add("flags", this.flags.serializeToJson());
         var1.add("equipment", this.equipment.serializeToJson());
         if (this.catType != null) {
            var1.addProperty("catType", this.catType.toString());
         }

         return var1;
      }
   }

   public static JsonElement serializeArrayToJson(EntityPredicate[] var0) {
      if (var0 == ANY_ARRAY) {
         return JsonNull.INSTANCE;
      } else {
         JsonArray var1 = new JsonArray();
         EntityPredicate[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EntityPredicate var5 = var2[var4];
            JsonElement var6 = var5.serializeToJson();
            if (!var6.isJsonNull()) {
               var1.add(var6);
            }
         }

         return var1;
      }
   }

   // $FF: synthetic method
   EntityPredicate(EntityTypePredicate var1, DistancePredicate var2, LocationPredicate var3, MobEffectsPredicate var4, NbtPredicate var5, EntityFlagsPredicate var6, EntityEquipmentPredicate var7, ResourceLocation var8, Object var9) {
      this(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   static {
      ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, (ResourceLocation)null);
      ANY_ARRAY = new EntityPredicate[0];
   }

   public static class Builder {
      private EntityTypePredicate entityType;
      private DistancePredicate distanceToPlayer;
      private LocationPredicate location;
      private MobEffectsPredicate effects;
      private NbtPredicate nbt;
      private EntityFlagsPredicate flags;
      private EntityEquipmentPredicate equipment;
      @Nullable
      private ResourceLocation catType;

      public Builder() {
         super();
         this.entityType = EntityTypePredicate.ANY;
         this.distanceToPlayer = DistancePredicate.ANY;
         this.location = LocationPredicate.ANY;
         this.effects = MobEffectsPredicate.ANY;
         this.nbt = NbtPredicate.ANY;
         this.flags = EntityFlagsPredicate.ANY;
         this.equipment = EntityEquipmentPredicate.ANY;
      }

      public static EntityPredicate.Builder entity() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder of(EntityType<?> var1) {
         this.entityType = EntityTypePredicate.of(var1);
         return this;
      }

      public EntityPredicate.Builder of(Tag<EntityType<?>> var1) {
         this.entityType = EntityTypePredicate.of(var1);
         return this;
      }

      public EntityPredicate.Builder of(ResourceLocation var1) {
         this.catType = var1;
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

      public EntityPredicate.Builder catType(@Nullable ResourceLocation var1) {
         this.catType = var1;
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(this.entityType, this.distanceToPlayer, this.location, this.effects, this.nbt, this.flags, this.equipment, this.catType);
      }
   }
}
