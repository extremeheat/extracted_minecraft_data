package net.minecraft.advancements.criterion;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class EntityTypePredicate {
   public static final EntityTypePredicate field_209371_a = new EntityTypePredicate();
   private static final Joiner field_209372_b = Joiner.on(", ");
   @Nullable
   private final EntityType<?> field_209373_c;

   public EntityTypePredicate(EntityType<?> var1) {
      super();
      this.field_209373_c = var1;
   }

   private EntityTypePredicate() {
      super();
      this.field_209373_c = null;
   }

   public boolean func_209368_a(EntityType<?> var1) {
      return this.field_209373_c == null || this.field_209373_c == var1;
   }

   public static EntityTypePredicate func_209370_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         String var1 = JsonUtils.func_151206_a(var0, "type");
         ResourceLocation var2 = new ResourceLocation(var1);
         EntityType var3 = (EntityType)IRegistry.field_212629_r.func_212608_b(var2);
         if (var3 == null) {
            throw new JsonSyntaxException("Unknown entity type '" + var2 + "', valid types are: " + field_209372_b.join(IRegistry.field_212629_r.func_148742_b()));
         } else {
            return new EntityTypePredicate(var3);
         }
      } else {
         return field_209371_a;
      }
   }

   public JsonElement func_209369_a() {
      return (JsonElement)(this.field_209373_c == null ? JsonNull.INSTANCE : new JsonPrimitive(IRegistry.field_212629_r.func_177774_c(this.field_209373_c).toString()));
   }
}
