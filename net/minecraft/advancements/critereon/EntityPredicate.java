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
import net.minecraft.world.scores.Team;

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
   private final PlayerPredicate player;
   @Nullable
   private final String team;
   @Nullable
   private final ResourceLocation catType;

   private EntityPredicate(EntityTypePredicate var1, DistancePredicate var2, LocationPredicate var3, MobEffectsPredicate var4, NbtPredicate var5, EntityFlagsPredicate var6, EntityEquipmentPredicate var7, PlayerPredicate var8, @Nullable String var9, @Nullable ResourceLocation var10) {
      this.entityType = var1;
      this.distanceToPlayer = var2;
      this.location = var3;
      this.effects = var4;
      this.nbt = var5;
      this.flags = var6;
      this.equipment = var7;
      this.player = var8;
      this.team = var9;
      this.catType = var10;
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
         } else if (!this.effects.matches(var3)) {
            return false;
         } else if (!this.nbt.matches(var3)) {
            return false;
         } else if (!this.flags.matches(var3)) {
            return false;
         } else if (!this.equipment.matches(var3)) {
            return false;
         } else if (!this.player.matches(var3)) {
            return false;
         } else {
            if (this.team != null) {
               Team var4 = var3.getTeam();
               if (var4 == null || !this.team.equals(var4.getName())) {
                  return false;
               }
            }

            return this.catType == null || var3 instanceof Cat && ((Cat)var3).getResourceLocation().equals(this.catType);
         }
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
         PlayerPredicate var9 = PlayerPredicate.fromJson(var1.get("player"));
         String var10 = GsonHelper.getAsString(var1, "team", (String)null);
         ResourceLocation var11 = var1.has("catType") ? new ResourceLocation(GsonHelper.getAsString(var1, "catType")) : null;
         return (new EntityPredicate.Builder()).entityType(var2).distance(var3).located(var4).effects(var5).nbt(var6).flags(var7).equipment(var8).player(var9).team(var10).catType(var11).build();
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
         var1.add("player", this.player.serializeToJson());
         var1.addProperty("team", this.team);
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
   EntityPredicate(EntityTypePredicate var1, DistancePredicate var2, LocationPredicate var3, MobEffectsPredicate var4, NbtPredicate var5, EntityFlagsPredicate var6, EntityEquipmentPredicate var7, PlayerPredicate var8, String var9, ResourceLocation var10, Object var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   static {
      ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, PlayerPredicate.ANY, (String)null, (ResourceLocation)null);
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
      private PlayerPredicate player;
      private String team;
      private ResourceLocation catType;

      public Builder() {
         this.entityType = EntityTypePredicate.ANY;
         this.distanceToPlayer = DistancePredicate.ANY;
         this.location = LocationPredicate.ANY;
         this.effects = MobEffectsPredicate.ANY;
         this.nbt = NbtPredicate.ANY;
         this.flags = EntityFlagsPredicate.ANY;
         this.equipment = EntityEquipmentPredicate.ANY;
         this.player = PlayerPredicate.ANY;
      }

      public static EntityPredicate.Builder entity() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder of(EntityType var1) {
         this.entityType = EntityTypePredicate.of(var1);
         return this;
      }

      public EntityPredicate.Builder of(Tag var1) {
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

      public EntityPredicate.Builder player(PlayerPredicate var1) {
         this.player = var1;
         return this;
      }

      public EntityPredicate.Builder team(@Nullable String var1) {
         this.team = var1;
         return this;
      }

      public EntityPredicate.Builder catType(@Nullable ResourceLocation var1) {
         this.catType = var1;
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(this.entityType, this.distanceToPlayer, this.location, this.effects, this.nbt, this.flags, this.equipment, this.player, this.team, this.catType);
      }
   }
}
