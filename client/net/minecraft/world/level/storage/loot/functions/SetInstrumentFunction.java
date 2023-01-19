package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetInstrumentFunction extends LootItemConditionalFunction {
   final TagKey<Instrument> options;

   SetInstrumentFunction(LootItemCondition[] var1, TagKey<Instrument> var2) {
      super(var1);
      this.options = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_INSTRUMENT;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      InstrumentItem.setRandom(var1, this.options, var2.getRandom());
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setInstrumentOptions(TagKey<Instrument> var0) {
      return simpleBuilder(var1 -> new SetInstrumentFunction(var1, var0));
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetInstrumentFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetInstrumentFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         var1.addProperty("options", "#" + var2.options.location());
      }

      public SetInstrumentFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         String var4 = GsonHelper.getAsString(var1, "options");
         if (!var4.startsWith("#")) {
            throw new JsonSyntaxException("Inline tag value not supported: " + var4);
         } else {
            return new SetInstrumentFunction(var3, TagKey.create(Registries.INSTRUMENT, new ResourceLocation(var4.substring(1))));
         }
      }
   }
}
