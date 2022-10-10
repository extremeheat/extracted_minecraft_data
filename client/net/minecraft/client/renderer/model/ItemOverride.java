package net.minecraft.client.renderer.model;

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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemOverride {
   private final ResourceLocation field_188028_a;
   private final Map<ResourceLocation, Float> field_188029_b;

   public ItemOverride(ResourceLocation var1, Map<ResourceLocation, Float> var2) {
      super();
      this.field_188028_a = var1;
      this.field_188029_b = var2;
   }

   public ResourceLocation func_188026_a() {
      return this.field_188028_a;
   }

   boolean func_188027_a(ItemStack var1, @Nullable World var2, @Nullable EntityLivingBase var3) {
      Item var4 = var1.func_77973_b();
      Iterator var5 = this.field_188029_b.entrySet().iterator();

      Entry var6;
      IItemPropertyGetter var7;
      do {
         if (!var5.hasNext()) {
            return true;
         }

         var6 = (Entry)var5.next();
         var7 = var4.func_185045_a((ResourceLocation)var6.getKey());
      } while(var7 != null && var7.call(var1, var2, var3) >= (Float)var6.getValue());

      return false;
   }

   static class Deserializer implements JsonDeserializer<ItemOverride> {
      Deserializer() {
         super();
      }

      public ItemOverride deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ResourceLocation var5 = new ResourceLocation(JsonUtils.func_151200_h(var4, "model"));
         Map var6 = this.func_188025_a(var4);
         return new ItemOverride(var5, var6);
      }

      protected Map<ResourceLocation, Float> func_188025_a(JsonObject var1) {
         LinkedHashMap var2 = Maps.newLinkedHashMap();
         JsonObject var3 = JsonUtils.func_152754_s(var1, "predicate");
         Iterator var4 = var3.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            var2.put(new ResourceLocation((String)var5.getKey()), JsonUtils.func_151220_d((JsonElement)var5.getValue(), (String)var5.getKey()));
         }

         return var2;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
