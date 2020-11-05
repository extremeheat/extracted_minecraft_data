package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class GsonAdapterFactory {
   public static <E, T extends SerializerType<E>> GsonAdapterFactory.Builder<E, T> builder(Registry<T> var0, String var1, String var2, Function<E, T> var3) {
      return new GsonAdapterFactory.Builder(var0, var1, var2, var3);
   }

   public interface DefaultSerializer<T> {
      JsonElement serialize(T var1, JsonSerializationContext var2);

      T deserialize(JsonElement var1, JsonDeserializationContext var2);
   }

   static class JsonAdapter<E, T extends SerializerType<E>> implements JsonDeserializer<E>, JsonSerializer<E> {
      private final Registry<T> registry;
      private final String elementName;
      private final String typeKey;
      private final Function<E, T> typeGetter;
      @Nullable
      private final Pair<T, GsonAdapterFactory.DefaultSerializer<? extends E>> defaultType;

      private JsonAdapter(Registry<T> var1, String var2, String var3, Function<E, T> var4, @Nullable Pair<T, GsonAdapterFactory.DefaultSerializer<? extends E>> var5) {
         super();
         this.registry = var1;
         this.elementName = var2;
         this.typeKey = var3;
         this.typeGetter = var4;
         this.defaultType = var5;
      }

      public E deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonObject()) {
            JsonObject var4 = GsonHelper.convertToJsonObject(var1, this.elementName);
            ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, this.typeKey));
            SerializerType var6 = (SerializerType)this.registry.get(var5);
            if (var6 == null) {
               throw new JsonSyntaxException("Unknown type '" + var5 + "'");
            } else {
               return var6.getSerializer().deserialize(var4, var3);
            }
         } else if (this.defaultType == null) {
            throw new UnsupportedOperationException("Object " + var1 + " can't be deserialized");
         } else {
            return ((GsonAdapterFactory.DefaultSerializer)this.defaultType.getSecond()).deserialize(var1, var3);
         }
      }

      public JsonElement serialize(E var1, Type var2, JsonSerializationContext var3) {
         SerializerType var4 = (SerializerType)this.typeGetter.apply(var1);
         if (this.defaultType != null && this.defaultType.getFirst() == var4) {
            return ((GsonAdapterFactory.DefaultSerializer)this.defaultType.getSecond()).serialize(var1, var3);
         } else if (var4 == null) {
            throw new JsonSyntaxException("Unknown type: " + var1);
         } else {
            JsonObject var5 = new JsonObject();
            var5.addProperty(this.typeKey, this.registry.getKey(var4).toString());
            var4.getSerializer().serialize(var5, var1, var3);
            return var5;
         }
      }

      // $FF: synthetic method
      JsonAdapter(Registry var1, String var2, String var3, Function var4, Pair var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }

   public static class Builder<E, T extends SerializerType<E>> {
      private final Registry<T> registry;
      private final String elementName;
      private final String typeKey;
      private final Function<E, T> typeGetter;
      @Nullable
      private Pair<T, GsonAdapterFactory.DefaultSerializer<? extends E>> defaultType;

      private Builder(Registry<T> var1, String var2, String var3, Function<E, T> var4) {
         super();
         this.registry = var1;
         this.elementName = var2;
         this.typeKey = var3;
         this.typeGetter = var4;
      }

      public Object build() {
         return new GsonAdapterFactory.JsonAdapter(this.registry, this.elementName, this.typeKey, this.typeGetter, this.defaultType);
      }

      // $FF: synthetic method
      Builder(Registry var1, String var2, String var3, Function var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }
}
