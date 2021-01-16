package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public final class TreeTypeAdapter<T> extends TypeAdapter<T> {
   private final JsonSerializer<T> serializer;
   private final JsonDeserializer<T> deserializer;
   private final Gson gson;
   private final TypeToken<T> typeToken;
   private final TypeAdapterFactory skipPast;
   private final TreeTypeAdapter<T>.GsonContextImpl context = new TreeTypeAdapter.GsonContextImpl();
   private TypeAdapter<T> delegate;

   public TreeTypeAdapter(JsonSerializer<T> var1, JsonDeserializer<T> var2, Gson var3, TypeToken<T> var4, TypeAdapterFactory var5) {
      super();
      this.serializer = var1;
      this.deserializer = var2;
      this.gson = var3;
      this.typeToken = var4;
      this.skipPast = var5;
   }

   public T read(JsonReader var1) throws IOException {
      if (this.deserializer == null) {
         return this.delegate().read(var1);
      } else {
         JsonElement var2 = Streams.parse(var1);
         return var2.isJsonNull() ? null : this.deserializer.deserialize(var2, this.typeToken.getType(), this.context);
      }
   }

   public void write(JsonWriter var1, T var2) throws IOException {
      if (this.serializer == null) {
         this.delegate().write(var1, var2);
      } else if (var2 == null) {
         var1.nullValue();
      } else {
         JsonElement var3 = this.serializer.serialize(var2, this.typeToken.getType(), this.context);
         Streams.write(var3, var1);
      }
   }

   private TypeAdapter<T> delegate() {
      TypeAdapter var1 = this.delegate;
      return var1 != null ? var1 : (this.delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken));
   }

   public static TypeAdapterFactory newFactory(TypeToken<?> var0, Object var1) {
      return new TreeTypeAdapter.SingleTypeFactory(var1, var0, false, (Class)null);
   }

   public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken<?> var0, Object var1) {
      boolean var2 = var0.getType() == var0.getRawType();
      return new TreeTypeAdapter.SingleTypeFactory(var1, var0, var2, (Class)null);
   }

   public static TypeAdapterFactory newTypeHierarchyFactory(Class<?> var0, Object var1) {
      return new TreeTypeAdapter.SingleTypeFactory(var1, (TypeToken)null, false, var0);
   }

   private final class GsonContextImpl implements JsonSerializationContext, JsonDeserializationContext {
      private GsonContextImpl() {
         super();
      }

      public JsonElement serialize(Object var1) {
         return TreeTypeAdapter.this.gson.toJsonTree(var1);
      }

      public JsonElement serialize(Object var1, Type var2) {
         return TreeTypeAdapter.this.gson.toJsonTree(var1, var2);
      }

      public <R> R deserialize(JsonElement var1, Type var2) throws JsonParseException {
         return TreeTypeAdapter.this.gson.fromJson(var1, var2);
      }

      // $FF: synthetic method
      GsonContextImpl(Object var2) {
         this();
      }
   }

   private static final class SingleTypeFactory implements TypeAdapterFactory {
      private final TypeToken<?> exactType;
      private final boolean matchRawType;
      private final Class<?> hierarchyType;
      private final JsonSerializer<?> serializer;
      private final JsonDeserializer<?> deserializer;

      SingleTypeFactory(Object var1, TypeToken<?> var2, boolean var3, Class<?> var4) {
         super();
         this.serializer = var1 instanceof JsonSerializer ? (JsonSerializer)var1 : null;
         this.deserializer = var1 instanceof JsonDeserializer ? (JsonDeserializer)var1 : null;
         $Gson$Preconditions.checkArgument(this.serializer != null || this.deserializer != null);
         this.exactType = var2;
         this.matchRawType = var3;
         this.hierarchyType = var4;
      }

      public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
         boolean var3 = this.exactType != null ? this.exactType.equals(var2) || this.matchRawType && this.exactType.getType() == var2.getRawType() : this.hierarchyType.isAssignableFrom(var2.getRawType());
         return var3 ? new TreeTypeAdapter(this.serializer, this.deserializer, var1, var2, this) : null;
      }
   }
}
