package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootItemEntityPropertyCondition implements LootItemCondition {
   private final EntityPredicate predicate;
   private final LootContext.EntityTarget entityTarget;

   private LootItemEntityPropertyCondition(EntityPredicate var1, LootContext.EntityTarget var2) {
      super();
      this.predicate = var1;
      this.entityTarget = var2;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_POS, this.entityTarget.getParam());
   }

   public boolean test(LootContext var1) {
      Entity var2 = (Entity)var1.getParamOrNull(this.entityTarget.getParam());
      BlockPos var3 = (BlockPos)var1.getParamOrNull(LootContextParams.BLOCK_POS);
      return var3 != null && this.predicate.matches(var1.getLevel(), new Vec3(var3), var2);
   }

   public static LootItemCondition.Builder entityPresent(LootContext.EntityTarget var0) {
      return hasProperties(var0, EntityPredicate.Builder.entity());
   }

   public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget var0, EntityPredicate.Builder var1) {
      return () -> {
         return new LootItemEntityPropertyCondition(var1.build(), var0);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   LootItemEntityPropertyCondition(EntityPredicate var1, LootContext.EntityTarget var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemCondition.Serializer<LootItemEntityPropertyCondition> {
      protected Serializer() {
         super(new ResourceLocation("entity_properties"), LootItemEntityPropertyCondition.class);
      }

      public void serialize(JsonObject var1, LootItemEntityPropertyCondition var2, JsonSerializationContext var3) {
         var1.add("predicate", var2.predicate.serializeToJson());
         var1.add("entity", var3.serialize(var2.entityTarget));
      }

      public LootItemEntityPropertyCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         EntityPredicate var3 = EntityPredicate.fromJson(var1.get("predicate"));
         return new LootItemEntityPropertyCondition(var3, (LootContext.EntityTarget)GsonHelper.getAsObject(var1, "entity", var2, LootContext.EntityTarget.class));
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
