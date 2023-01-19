package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityVariantPredicate<V> {
   private static final String VARIANT_KEY = "variant";
   final Registry<V> registry;
   final Function<Entity, Optional<V>> getter;
   final EntitySubPredicate.Type type;

   public static <V> EntityVariantPredicate<V> create(Registry<V> var0, Function<Entity, Optional<V>> var1) {
      return new EntityVariantPredicate<>(var0, var1);
   }

   private EntityVariantPredicate(Registry<V> var1, Function<Entity, Optional<V>> var2) {
      super();
      this.registry = var1;
      this.getter = var2;
      this.type = var2x -> {
         String var3 = GsonHelper.getAsString(var2x, "variant");
         Object var4 = var1.get(ResourceLocation.tryParse(var3));
         if (var4 == null) {
            throw new JsonSyntaxException("Unknown variant: " + var3);
         } else {
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
            var1x.addProperty("variant", EntityVariantPredicate.this.registry.getKey((V)var1).toString());
            return var1x;
         }

         @Override
         public EntitySubPredicate.Type type() {
            return EntityVariantPredicate.this.type;
         }
      };
   }
}
