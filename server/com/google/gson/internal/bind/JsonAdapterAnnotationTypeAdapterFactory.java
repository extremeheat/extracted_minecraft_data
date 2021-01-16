package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorConstructor constructorConstructor;

   public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor var1) {
      super();
      this.constructorConstructor = var1;
   }

   public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
      Class var3 = var2.getRawType();
      JsonAdapter var4 = (JsonAdapter)var3.getAnnotation(JsonAdapter.class);
      return var4 == null ? null : this.getTypeAdapter(this.constructorConstructor, var1, var2, var4);
   }

   TypeAdapter<?> getTypeAdapter(ConstructorConstructor var1, Gson var2, TypeToken<?> var3, JsonAdapter var4) {
      Object var5 = var1.get(TypeToken.get(var4.value())).construct();
      Object var6;
      if (var5 instanceof TypeAdapter) {
         var6 = (TypeAdapter)var5;
      } else if (var5 instanceof TypeAdapterFactory) {
         var6 = ((TypeAdapterFactory)var5).create(var2, var3);
      } else {
         if (!(var5 instanceof JsonSerializer) && !(var5 instanceof JsonDeserializer)) {
            throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer reference.");
         }

         JsonSerializer var7 = var5 instanceof JsonSerializer ? (JsonSerializer)var5 : null;
         JsonDeserializer var8 = var5 instanceof JsonDeserializer ? (JsonDeserializer)var5 : null;
         var6 = new TreeTypeAdapter(var7, var8, var2, var3, (TypeAdapterFactory)null);
      }

      if (var6 != null && var4.nullSafe()) {
         var6 = ((TypeAdapter)var6).nullSafe();
      }

      return (TypeAdapter)var6;
   }
}
