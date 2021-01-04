package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class DamageSourceCondition implements LootItemCondition {
   private final DamageSourcePredicate predicate;

   private DamageSourceCondition(DamageSourcePredicate var1) {
      super();
      this.predicate = var1;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_POS, LootContextParams.DAMAGE_SOURCE);
   }

   public boolean test(LootContext var1) {
      DamageSource var2 = (DamageSource)var1.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
      BlockPos var3 = (BlockPos)var1.getParamOrNull(LootContextParams.BLOCK_POS);
      return var3 != null && var2 != null && this.predicate.matches(var1.getLevel(), new Vec3(var3), var2);
   }

   public static LootItemCondition.Builder hasDamageSource(DamageSourcePredicate.Builder var0) {
      return () -> {
         return new DamageSourceCondition(var0.build());
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   DamageSourceCondition(DamageSourcePredicate var1, Object var2) {
      this(var1);
   }

   public static class Serializer extends LootItemCondition.Serializer<DamageSourceCondition> {
      protected Serializer() {
         super(new ResourceLocation("damage_source_properties"), DamageSourceCondition.class);
      }

      public void serialize(JsonObject var1, DamageSourceCondition var2, JsonSerializationContext var3) {
         var1.add("predicate", var2.predicate.serializeToJson());
      }

      public DamageSourceCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         DamageSourcePredicate var3 = DamageSourcePredicate.fromJson(var1.get("predicate"));
         return new DamageSourceCondition(var3);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
