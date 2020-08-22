package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemOverride {
   private final ResourceLocation model;
   private final Map predicates;

   public ItemOverride(ResourceLocation var1, Map var2) {
      this.model = var1;
      this.predicates = var2;
   }

   public ResourceLocation getModel() {
      return this.model;
   }

   boolean test(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3) {
      Item var4 = var1.getItem();
      Iterator var5 = this.predicates.entrySet().iterator();

      Entry var6;
      ItemPropertyFunction var7;
      do {
         if (!var5.hasNext()) {
            return true;
         }

         var6 = (Entry)var5.next();
         var7 = var4.getProperty((ResourceLocation)var6.getKey());
      } while(var7 != null && var7.call(var1, var2, var3) >= (Float)var6.getValue());

      return false;
   }

   public static class Deserializer implements JsonDeserializer {
      protected Deserializer() {
      }

      public ItemOverride deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, "model"));
         Map var6 = this.getPredicates(var4);
         return new ItemOverride(var5, var6);
      }

      protected Map getPredicates(JsonObject var1) {
         LinkedHashMap var2 = Maps.newLinkedHashMap();
         JsonObject var3 = GsonHelper.getAsJsonObject(var1, "predicate");
         Iterator var4 = var3.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            var2.put(new ResourceLocation((String)var5.getKey()), GsonHelper.convertToFloat((JsonElement)var5.getValue(), (String)var5.getKey()));
         }

         return var2;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
