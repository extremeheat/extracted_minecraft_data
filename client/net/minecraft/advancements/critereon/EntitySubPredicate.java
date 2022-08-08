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
      public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
         return true;
      }

      public JsonObject serializeCustomData() {
         return new JsonObject();
      }

      public Type type() {
         return EntitySubPredicate.Types.ANY;
      }
   };

   static EntitySubPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "type_specific");
         String var2 = GsonHelper.getAsString(var1, "type", (String)null);
         if (var2 == null) {
            return ANY;
         } else {
            Type var3 = (Type)EntitySubPredicate.Types.TYPES.get(var2);
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

   Type type();

   static EntitySubPredicate variant(CatVariant var0) {
      return EntitySubPredicate.Types.CAT.createPredicate(var0);
   }

   static EntitySubPredicate variant(FrogVariant var0) {
      return EntitySubPredicate.Types.FROG.createPredicate(var0);
   }

   public static final class Types {
      public static final Type ANY = (var0) -> {
         return EntitySubPredicate.ANY;
      };
      public static final Type LIGHTNING = LighthingBoltPredicate::fromJson;
      public static final Type FISHING_HOOK = FishingHookPredicate::fromJson;
      public static final Type PLAYER = PlayerPredicate::fromJson;
      public static final Type SLIME = SlimePredicate::fromJson;
      public static final EntityVariantPredicate<CatVariant> CAT;
      public static final EntityVariantPredicate<FrogVariant> FROG;
      public static final BiMap<String, Type> TYPES;

      public Types() {
         super();
      }

      static {
         CAT = EntityVariantPredicate.create(Registry.CAT_VARIANT, (var0) -> {
            Optional var10000;
            if (var0 instanceof Cat var1) {
               var10000 = Optional.of(var1.getCatVariant());
            } else {
               var10000 = Optional.empty();
            }

            return var10000;
         });
         FROG = EntityVariantPredicate.create(Registry.FROG_VARIANT, (var0) -> {
            Optional var10000;
            if (var0 instanceof Frog var1) {
               var10000 = Optional.of(var1.getVariant());
            } else {
               var10000 = Optional.empty();
            }

            return var10000;
         });
         TYPES = ImmutableBiMap.of("any", ANY, "lightning", LIGHTNING, "fishing_hook", FISHING_HOOK, "player", PLAYER, "slime", SLIME, "cat", CAT.type(), "frog", FROG.type());
      }
   }

   public interface Type {
      EntitySubPredicate deserialize(JsonObject var1);
   }
}
