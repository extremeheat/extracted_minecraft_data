package net.minecraft.advancements.critereon;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.phys.Vec3;

public interface EntitySubPredicate {
   EntitySubPredicate ANY = new EntitySubPredicate() {
      @Override
      public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
         return true;
      }

      @Override
      public JsonObject serializeCustomData() {
         return new JsonObject();
      }

      @Override
      public EntitySubPredicate.Type type() {
         return EntitySubPredicate.Types.ANY;
      }
   };

   static EntitySubPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "type_specific");
         String var2 = GsonHelper.getAsString(var1, "type", null);
         if (var2 == null) {
            return ANY;
         } else {
            EntitySubPredicate.Type var3 = (EntitySubPredicate.Type)EntitySubPredicate.Types.TYPES.get(var2);
            if (var3 == null) {
               throw new JsonSyntaxException("Unknown sub-predicate type: " + var2);
            } else {
               return var3.deserialize(var1);
            }
         }
      } else {
         return ANY;
      }
   }

   boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3);

   JsonObject serializeCustomData();

   default JsonElement serialize() {
      if (this.type() == EntitySubPredicate.Types.ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = this.serializeCustomData();
         String var2 = (String)EntitySubPredicate.Types.TYPES.inverse().get(this.type());
         var1.addProperty("type", var2);
         return var1;
      }
   }

   EntitySubPredicate.Type type();

   static EntitySubPredicate variant(CatVariant var0) {
      return EntitySubPredicate.Types.CAT.createPredicate(var0);
   }

   static EntitySubPredicate variant(FrogVariant var0) {
      return EntitySubPredicate.Types.FROG.createPredicate(var0);
   }

   public interface Type {
      EntitySubPredicate deserialize(JsonObject var1);
   }

   public static final class Types {
      public static final EntitySubPredicate.Type ANY = var0 -> EntitySubPredicate.ANY;
      public static final EntitySubPredicate.Type LIGHTNING = LighthingBoltPredicate::fromJson;
      public static final EntitySubPredicate.Type FISHING_HOOK = FishingHookPredicate::fromJson;
      public static final EntitySubPredicate.Type PLAYER = PlayerPredicate::fromJson;
      public static final EntitySubPredicate.Type SLIME = SlimePredicate::fromJson;
      public static final EntityVariantPredicate<CatVariant> CAT = EntityVariantPredicate.create(
         Registry.CAT_VARIANT, var0 -> var0 instanceof Cat var1 ? Optional.of(var1.getCatVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<FrogVariant> FROG = EntityVariantPredicate.create(
         Registry.FROG_VARIANT, var0 -> var0 instanceof Frog var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final BiMap<String, EntitySubPredicate.Type> TYPES = ImmutableBiMap.of(
         "any", ANY, "lightning", LIGHTNING, "fishing_hook", FISHING_HOOK, "player", PLAYER, "slime", SLIME, "cat", CAT.type(), "frog", FROG.type()
      );

      public Types() {
         super();
      }
   }
}
