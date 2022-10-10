package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;

public class EntityHasScore implements LootCondition {
   private final Map<String, RandomValueRange> field_186634_a;
   private final LootContext.EntityTarget field_186635_b;

   public EntityHasScore(Map<String, RandomValueRange> var1, LootContext.EntityTarget var2) {
      super();
      this.field_186634_a = var1;
      this.field_186635_b = var2;
   }

   public boolean func_186618_a(Random var1, LootContext var2) {
      Entity var3 = var2.func_186494_a(this.field_186635_b);
      if (var3 == null) {
         return false;
      } else {
         Scoreboard var4 = var3.field_70170_p.func_96441_U();
         Iterator var5 = this.field_186634_a.entrySet().iterator();

         Entry var6;
         do {
            if (!var5.hasNext()) {
               return true;
            }

            var6 = (Entry)var5.next();
         } while(this.func_186631_a(var3, var4, (String)var6.getKey(), (RandomValueRange)var6.getValue()));

         return false;
      }
   }

   protected boolean func_186631_a(Entity var1, Scoreboard var2, String var3, RandomValueRange var4) {
      ScoreObjective var5 = var2.func_96518_b(var3);
      if (var5 == null) {
         return false;
      } else {
         String var6 = var1.func_195047_I_();
         return !var2.func_178819_b(var6, var5) ? false : var4.func_186510_a(var2.func_96529_a(var6, var5).func_96652_c());
      }
   }

   public static class Serializer extends LootCondition.Serializer<EntityHasScore> {
      protected Serializer() {
         super(new ResourceLocation("entity_scores"), EntityHasScore.class);
      }

      public void func_186605_a(JsonObject var1, EntityHasScore var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         Iterator var5 = var2.field_186634_a.entrySet().iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            var4.add((String)var6.getKey(), var3.serialize(var6.getValue()));
         }

         var1.add("scores", var4);
         var1.add("entity", var3.serialize(var2.field_186635_b));
      }

      public EntityHasScore func_186603_b(JsonObject var1, JsonDeserializationContext var2) {
         Set var3 = JsonUtils.func_152754_s(var1, "scores").entrySet();
         LinkedHashMap var4 = Maps.newLinkedHashMap();
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            var4.put(var6.getKey(), JsonUtils.func_188179_a((JsonElement)var6.getValue(), "score", var2, RandomValueRange.class));
         }

         return new EntityHasScore(var4, (LootContext.EntityTarget)JsonUtils.func_188174_a(var1, "entity", var2, LootContext.EntityTarget.class));
      }

      // $FF: synthetic method
      public LootCondition func_186603_b(JsonObject var1, JsonDeserializationContext var2) {
         return this.func_186603_b(var1, var2);
      }
   }
}
