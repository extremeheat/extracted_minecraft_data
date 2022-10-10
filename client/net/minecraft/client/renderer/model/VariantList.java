package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class VariantList implements IUnbakedModel {
   private final List<Variant> field_188115_a;

   public VariantList(List<Variant> var1) {
      super();
      this.field_188115_a = var1;
   }

   public List<Variant> func_188114_a() {
      return this.field_188115_a;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof VariantList) {
         VariantList var2 = (VariantList)var1;
         return this.field_188115_a.equals(var2.field_188115_a);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_188115_a.hashCode();
   }

   public Collection<ResourceLocation> func_187965_e() {
      return (Collection)this.func_188114_a().stream().map(Variant::func_188046_a).collect(Collectors.toSet());
   }

   public Collection<ResourceLocation> func_209559_a(Function<ResourceLocation, IUnbakedModel> var1, Set<String> var2) {
      return (Collection)this.func_188114_a().stream().map(Variant::func_188046_a).distinct().flatMap((var2x) -> {
         return ((IUnbakedModel)var1.apply(var2x)).func_209559_a(var1, var2).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public IBakedModel func_209558_a(Function<ResourceLocation, IUnbakedModel> var1, Function<ResourceLocation, TextureAtlasSprite> var2, ModelRotation var3, boolean var4) {
      if (this.func_188114_a().isEmpty()) {
         return null;
      } else {
         WeightedBakedModel.Builder var5 = new WeightedBakedModel.Builder();
         Iterator var6 = this.func_188114_a().iterator();

         while(var6.hasNext()) {
            Variant var7 = (Variant)var6.next();
            IBakedModel var8 = ((IUnbakedModel)var1.apply(var7.func_188046_a())).func_209558_a(var1, var2, var7.func_188048_b(), var7.func_188049_c());
            var5.func_177677_a(var8, var7.func_188047_d());
         }

         return var5.func_209614_a();
      }
   }

   public static class Deserializer implements JsonDeserializer<VariantList> {
      public Deserializer() {
         super();
      }

      public VariantList deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         ArrayList var4 = Lists.newArrayList();
         if (var1.isJsonArray()) {
            JsonArray var5 = var1.getAsJsonArray();
            if (var5.size() == 0) {
               throw new JsonParseException("Empty variant array");
            }

            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               JsonElement var7 = (JsonElement)var6.next();
               var4.add(var3.deserialize(var7, Variant.class));
            }
         } else {
            var4.add(var3.deserialize(var1, Variant.class));
         }

         return new VariantList(var4);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
