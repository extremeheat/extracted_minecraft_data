package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;

public class EntityHasProperty implements LootCondition {
   private final EntityProperty[] field_186623_a;
   private final LootContext.EntityTarget field_186624_b;

   public EntityHasProperty(EntityProperty[] var1, LootContext.EntityTarget var2) {
      super();
      this.field_186623_a = var1;
      this.field_186624_b = var2;
   }

   public boolean func_186618_a(Random var1, LootContext var2) {
      Entity var3 = var2.func_186494_a(this.field_186624_b);
      if (var3 == null) {
         return false;
      } else {
         EntityProperty[] var4 = this.field_186623_a;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EntityProperty var7 = var4[var6];
            if (!var7.func_186657_a(var1, var3)) {
               return false;
            }
         }

         return true;
      }
   }

   public static class Serializer extends LootCondition.Serializer<EntityHasProperty> {
      protected Serializer() {
         super(new ResourceLocation("entity_properties"), EntityHasProperty.class);
      }

      public void func_186605_a(JsonObject var1, EntityHasProperty var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         EntityProperty[] var5 = var2.field_186623_a;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EntityProperty var8 = var5[var7];
            EntityProperty.Serializer var9 = EntityPropertyManager.func_186645_a(var8);
            var4.add(var9.func_186649_a().toString(), var9.func_186650_a(var8, var3));
         }

         var1.add("properties", var4);
         var1.add("entity", var3.serialize(var2.field_186624_b));
      }

      public EntityHasProperty func_186603_b(JsonObject var1, JsonDeserializationContext var2) {
         Set var3 = JsonUtils.func_152754_s(var1, "properties").entrySet();
         EntityProperty[] var4 = new EntityProperty[var3.size()];
         int var5 = 0;

         Entry var7;
         for(Iterator var6 = var3.iterator(); var6.hasNext(); var4[var5++] = EntityPropertyManager.func_186646_a(new ResourceLocation((String)var7.getKey())).func_186652_a((JsonElement)var7.getValue(), var2)) {
            var7 = (Entry)var6.next();
         }

         return new EntityHasProperty(var4, (LootContext.EntityTarget)JsonUtils.func_188174_a(var1, "entity", var2, LootContext.EntityTarget.class));
      }

      // $FF: synthetic method
      public LootCondition func_186603_b(JsonObject var1, JsonDeserializationContext var2) {
         return this.func_186603_b(var1, var2);
      }
   }
}
