package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public class LootPoolEntries {
   private static final Map<ResourceLocation, LootPoolEntryContainer.Serializer<?>> ID_TO_SERIALIZER = Maps.newHashMap();
   private static final Map<Class<?>, LootPoolEntryContainer.Serializer<?>> CLASS_TO_SERIALIZER = Maps.newHashMap();

   private static void register(LootPoolEntryContainer.Serializer<?> var0) {
      ID_TO_SERIALIZER.put(var0.getName(), var0);
      CLASS_TO_SERIALIZER.put(var0.getContainerClass(), var0);
   }

   static {
      register(CompositeEntryBase.createSerializer(new ResourceLocation("alternatives"), AlternativesEntry.class, AlternativesEntry::new));
      register(CompositeEntryBase.createSerializer(new ResourceLocation("sequence"), SequentialEntry.class, SequentialEntry::new));
      register(CompositeEntryBase.createSerializer(new ResourceLocation("group"), EntryGroup.class, EntryGroup::new));
      register(new EmptyLootItem.Serializer());
      register(new LootItem.Serializer());
      register(new LootTableReference.Serializer());
      register(new DynamicLoot.Serializer());
      register(new TagEntry.Serializer());
   }

   public static class Serializer implements JsonDeserializer<LootPoolEntryContainer>, JsonSerializer<LootPoolEntryContainer> {
      public Serializer() {
         super();
      }

      public LootPoolEntryContainer deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
         JsonObject var4 = GsonHelper.convertToJsonObject(var1, "entry");
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, "type"));
         LootPoolEntryContainer.Serializer var6 = (LootPoolEntryContainer.Serializer)LootPoolEntries.ID_TO_SERIALIZER.get(var5);
         if (var6 == null) {
            throw new JsonParseException("Unknown item type: " + var5);
         } else {
            LootItemCondition[] var7 = (LootItemCondition[])GsonHelper.getAsObject(var4, "conditions", new LootItemCondition[0], var3, LootItemCondition[].class);
            return var6.deserialize(var4, var3, var7);
         }
      }

      public JsonElement serialize(LootPoolEntryContainer var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         LootPoolEntryContainer.Serializer var5 = getSerializer(var1.getClass());
         var4.addProperty("type", var5.getName().toString());
         if (!ArrayUtils.isEmpty(var1.conditions)) {
            var4.add("conditions", var3.serialize(var1.conditions));
         }

         var5.serialize(var4, var1, var3);
         return var4;
      }

      private static LootPoolEntryContainer.Serializer<LootPoolEntryContainer> getSerializer(Class<?> var0) {
         LootPoolEntryContainer.Serializer var1 = (LootPoolEntryContainer.Serializer)LootPoolEntries.CLASS_TO_SERIALIZER.get(var0);
         if (var1 == null) {
            throw new JsonParseException("Unknown item type: " + var0);
         } else {
            return var1;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootPoolEntryContainer)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
