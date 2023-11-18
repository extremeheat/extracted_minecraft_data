package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.damageType().build();
   private final List<TagPredicate<DamageType>> tags;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(List<TagPredicate<DamageType>> var1, EntityPredicate var2, EntityPredicate var3) {
      super();
      this.tags = var1;
      this.directEntity = var2;
      this.sourceEntity = var3;
   }

   public boolean matches(ServerPlayer var1, DamageSource var2) {
      return this.matches(var1.serverLevel(), var1.position(), var2);
   }

   public boolean matches(ServerLevel var1, Vec3 var2, DamageSource var3) {
      if (this == ANY) {
         return true;
      } else {
         for(TagPredicate var5 : this.tags) {
            if (!var5.matches(var3.typeHolder())) {
               return false;
            }
         }

         if (!this.directEntity.matches(var1, var2, var3.getDirectEntity())) {
            return false;
         } else {
            return this.sourceEntity.matches(var1, var2, var3.getEntity());
         }
      }
   }

   public static DamageSourcePredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "damage type");
         JsonArray var2 = GsonHelper.getAsJsonArray(var1, "tags", null);
         Object var3;
         if (var2 != null) {
            var3 = new ArrayList(var2.size());

            for(JsonElement var5 : var2) {
               var3.add(TagPredicate.fromJson(var5, Registries.DAMAGE_TYPE));
            }
         } else {
            var3 = List.of();
         }

         EntityPredicate var6 = EntityPredicate.fromJson(var1.get("direct_entity"));
         EntityPredicate var7 = EntityPredicate.fromJson(var1.get("source_entity"));
         return new DamageSourcePredicate((List<TagPredicate<DamageType>>)var3, var6, var7);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (!this.tags.isEmpty()) {
            JsonArray var2 = new JsonArray(this.tags.size());

            for(int var3 = 0; var3 < this.tags.size(); ++var3) {
               var2.add(this.tags.get(var3).serializeToJson());
            }

            var1.add("tags", var2);
         }

         var1.add("direct_entity", this.directEntity.serializeToJson());
         var1.add("source_entity", this.sourceEntity.serializeToJson());
         return var1;
      }
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableList.Builder<TagPredicate<DamageType>> tags = ImmutableList.builder();
      private EntityPredicate directEntity = EntityPredicate.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;

      public Builder() {
         super();
      }

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder tag(TagPredicate<DamageType> var1) {
         this.tags.add(var1);
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate var1) {
         this.directEntity = var1;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder var1) {
         this.directEntity = var1.build();
         return this;
      }

      public DamageSourcePredicate.Builder source(EntityPredicate var1) {
         this.sourceEntity = var1;
         return this;
      }

      public DamageSourcePredicate.Builder source(EntityPredicate.Builder var1) {
         this.sourceEntity = var1.build();
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.tags.build(), this.directEntity, this.sourceEntity);
      }
   }
}
