package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
   final NameSource source;

   CopyNameFunction(LootItemCondition[] var1, NameSource var2) {
      super(var1);
      this.source = var2;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_NAME;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Object var3 = var2.getParamOrNull(this.source.param);
      if (var3 instanceof Nameable var4) {
         if (var4.hasCustomName()) {
            var1.setHoverName(var4.getDisplayName());
         }
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> copyName(NameSource var0) {
      return simpleBuilder((var1) -> {
         return new CopyNameFunction(var1, var0);
      });
   }

   public static enum NameSource {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

      public final String name;
      public final LootContextParam<?> param;

      private NameSource(String var3, LootContextParam var4) {
         this.name = var3;
         this.param = var4;
      }

      public static NameSource getByName(String var0) {
         NameSource[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            NameSource var4 = var1[var3];
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid name source " + var0);
      }

      // $FF: synthetic method
      private static NameSource[] $values() {
         return new NameSource[]{THIS, KILLER, KILLER_PLAYER, BLOCK_ENTITY};
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<CopyNameFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, CopyNameFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.addProperty("source", var2.source.name);
      }

      public CopyNameFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         NameSource var4 = CopyNameFunction.NameSource.getByName(GsonHelper.getAsString(var1, "source"));
         return new CopyNameFunction(var3, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
