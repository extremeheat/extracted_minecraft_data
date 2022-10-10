package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import org.apache.commons.lang3.ArrayUtils;

public class LootPool {
   private final LootEntry[] field_186453_a;
   private final LootCondition[] field_186454_b;
   private final RandomValueRange field_186455_c;
   private final RandomValueRange field_186456_d;

   public LootPool(LootEntry[] var1, LootCondition[] var2, RandomValueRange var3, RandomValueRange var4) {
      super();
      this.field_186453_a = var1;
      this.field_186454_b = var2;
      this.field_186455_c = var3;
      this.field_186456_d = var4;
   }

   protected void func_186452_a(Collection<ItemStack> var1, Random var2, LootContext var3) {
      ArrayList var4 = Lists.newArrayList();
      int var5 = 0;
      LootEntry[] var6 = this.field_186453_a;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         LootEntry var9 = var6[var8];
         if (LootConditionManager.func_186638_a(var9.field_186366_e, var2, var3)) {
            int var10 = var9.func_186361_a(var3.func_186491_f());
            if (var10 > 0) {
               var4.add(var9);
               var5 += var10;
            }
         }
      }

      if (var5 != 0 && !var4.isEmpty()) {
         int var11 = var2.nextInt(var5);
         Iterator var12 = var4.iterator();

         LootEntry var13;
         do {
            if (!var12.hasNext()) {
               return;
            }

            var13 = (LootEntry)var12.next();
            var11 -= var13.func_186361_a(var3.func_186491_f());
         } while(var11 >= 0);

         var13.func_186363_a(var1, var2, var3);
      }
   }

   public void func_186449_b(Collection<ItemStack> var1, Random var2, LootContext var3) {
      if (LootConditionManager.func_186638_a(this.field_186454_b, var2, var3)) {
         int var4 = this.field_186455_c.func_186511_a(var2) + MathHelper.func_76141_d(this.field_186456_d.func_186507_b(var2) * var3.func_186491_f());

         for(int var5 = 0; var5 < var4; ++var5) {
            this.func_186452_a(var1, var2, var3);
         }

      }
   }

   public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {
      public Serializer() {
         super();
      }

      public LootPool deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = JsonUtils.func_151210_l(var1, "loot pool");
         LootEntry[] var5 = (LootEntry[])JsonUtils.func_188174_a(var4, "entries", var3, LootEntry[].class);
         LootCondition[] var6 = (LootCondition[])JsonUtils.func_188177_a(var4, "conditions", new LootCondition[0], var3, LootCondition[].class);
         RandomValueRange var7 = (RandomValueRange)JsonUtils.func_188174_a(var4, "rolls", var3, RandomValueRange.class);
         RandomValueRange var8 = (RandomValueRange)JsonUtils.func_188177_a(var4, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), var3, RandomValueRange.class);
         return new LootPool(var5, var6, var7, var8);
      }

      public JsonElement serialize(LootPool var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var4.add("entries", var3.serialize(var1.field_186453_a));
         var4.add("rolls", var3.serialize(var1.field_186455_c));
         if (var1.field_186456_d.func_186509_a() != 0.0F && var1.field_186456_d.func_186512_b() != 0.0F) {
            var4.add("bonus_rolls", var3.serialize(var1.field_186456_d));
         }

         if (!ArrayUtils.isEmpty(var1.field_186454_b)) {
            var4.add("conditions", var3.serialize(var1.field_186454_b));
         }

         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootPool)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
