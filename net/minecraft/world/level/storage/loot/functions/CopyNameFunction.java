package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
   private final CopyNameFunction.NameSource source;

   private CopyNameFunction(LootItemCondition[] var1, CopyNameFunction.NameSource var2) {
      super(var1);
      this.source = var2;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Object var3 = var2.getParamOrNull(this.source.param);
      if (var3 instanceof Nameable) {
         Nameable var4 = (Nameable)var3;
         if (var4.hasCustomName()) {
            var1.setHoverName(var4.getDisplayName());
         }
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder copyName(CopyNameFunction.NameSource var0) {
      return simpleBuilder((var1) -> {
         return new CopyNameFunction(var1, var0);
      });
   }

   // $FF: synthetic method
   CopyNameFunction(LootItemCondition[] var1, CopyNameFunction.NameSource var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("copy_name"), CopyNameFunction.class);
      }

      public void serialize(JsonObject var1, CopyNameFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.addProperty("source", var2.source.name);
      }

      public CopyNameFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         CopyNameFunction.NameSource var4 = CopyNameFunction.NameSource.getByName(GsonHelper.getAsString(var1, "source"));
         return new CopyNameFunction(var3, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static enum NameSource {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

      public final String name;
      public final LootContextParam param;

      private NameSource(String var3, LootContextParam var4) {
         this.name = var3;
         this.param = var4;
      }

      public static CopyNameFunction.NameSource getByName(String var0) {
         CopyNameFunction.NameSource[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CopyNameFunction.NameSource var4 = var1[var3];
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid name source " + var0);
      }
   }
}
