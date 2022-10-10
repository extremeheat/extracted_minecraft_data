package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public abstract class LootEntry {
   protected final int field_186364_c;
   protected final int field_186365_d;
   protected final LootCondition[] field_186366_e;

   protected LootEntry(int var1, int var2, LootCondition[] var3) {
      super();
      this.field_186364_c = var1;
      this.field_186365_d = var2;
      this.field_186366_e = var3;
   }

   public int func_186361_a(float var1) {
      return Math.max(MathHelper.func_76141_d((float)this.field_186364_c + (float)this.field_186365_d * var1), 0);
   }

   public abstract void func_186363_a(Collection<ItemStack> var1, Random var2, LootContext var3);

   protected abstract void func_186362_a(JsonObject var1, JsonSerializationContext var2);

   public static class Serializer implements JsonDeserializer<LootEntry>, JsonSerializer<LootEntry> {
      public Serializer() {
         super();
      }

      public LootEntry deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = JsonUtils.func_151210_l(var1, "loot item");
         String var5 = JsonUtils.func_151200_h(var4, "type");
         int var6 = JsonUtils.func_151208_a(var4, "weight", 1);
         int var7 = JsonUtils.func_151208_a(var4, "quality", 0);
         LootCondition[] var8;
         if (var4.has("conditions")) {
            var8 = (LootCondition[])JsonUtils.func_188174_a(var4, "conditions", var3, LootCondition[].class);
         } else {
            var8 = new LootCondition[0];
         }

         if ("item".equals(var5)) {
            return LootEntryItem.func_186367_a(var4, var3, var6, var7, var8);
         } else if ("loot_table".equals(var5)) {
            return LootEntryTable.func_186370_a(var4, var3, var6, var7, var8);
         } else if ("empty".equals(var5)) {
            return LootEntryEmpty.func_186372_a(var4, var3, var6, var7, var8);
         } else {
            throw new JsonSyntaxException("Unknown loot entry type '" + var5 + "'");
         }
      }

      public JsonElement serialize(LootEntry var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("weight", var1.field_186364_c);
         var4.addProperty("quality", var1.field_186365_d);
         if (var1.field_186366_e.length > 0) {
            var4.add("conditions", var3.serialize(var1.field_186366_e));
         }

         if (var1 instanceof LootEntryItem) {
            var4.addProperty("type", "item");
         } else if (var1 instanceof LootEntryTable) {
            var4.addProperty("type", "loot_table");
         } else {
            if (!(var1 instanceof LootEntryEmpty)) {
               throw new IllegalArgumentException("Don't know how to serialize " + var1);
            }

            var4.addProperty("type", "empty");
         }

         var1.func_186362_a(var4, var3);
         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootEntry)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
