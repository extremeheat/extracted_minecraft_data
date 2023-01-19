package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityVariantPredicate<V> {
   private static final String VARIANT_KEY = "variant";
   final Codec<V> variantCodec;
   final Function<Entity, Optional<V>> getter;
   final EntitySubPredicate.Type type;

   public static <V> EntityVariantPredicate<V> create(Registry<V> var0, Function<Entity, Optional<V>> var1) {
      return new EntityVariantPredicate<>(var0.byNameCodec(), var1);
   }

   public static <V> EntityVariantPredicate<V> create(Codec<V> var0, Function<Entity, Optional<V>> var1) {
      return new EntityVariantPredicate<>(var0, var1);
   }

   private EntityVariantPredicate(Codec<V> var1, Function<Entity, Optional<V>> var2) {
      super();
      this.variantCodec = var1;
      this.getter = var2;
      this.type = var2x -> {
         JsonElement var3 = var2x.get("variant");
         if (var3 == null) {
            throw new JsonParseException("Missing variant field");
         } else {
            Object var4 = ((Pair)Util.getOrThrow(var1.decode(new Dynamic(JsonOps.INSTANCE, var3)), JsonParseException::new)).getFirst();
            return this.createPredicate((V)var4);
         }
      };
   }

   public EntitySubPredicate.Type type() {
      return this.type;
   }

   public EntitySubPredicate createPredicate(final V var1) {
      return new EntitySubPredicate() {
         @Override
         public boolean matches(Entity var1x, ServerLevel var2, @Nullable Vec3 var3) {
            return EntityVariantPredicate.this.getter.apply(var1x).filter(var1xxx -> var1xxx.equals(var1)).isPresent();
         }

         @Override
         public JsonObject serializeCustomData() {
            JsonObject var1x = new JsonObject();
            var1x.add(
               "variant",
               Util.getOrThrow(
                  EntityVariantPredicate.this.variantCodec.encodeStart(JsonOps.INSTANCE, var1),
                  var1xxx -> new JsonParseException("Can't serialize variant " + var1 + ", message " + var1xxx)
               )
            );
            return var1x;
         }

         @Override
         public EntitySubPredicate.Type type() {
            return EntityVariantPredicate.this.type;
         }
      };
   }
}
