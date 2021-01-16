package com.google.gson;

import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public abstract class TypeAdapter<T> {
   public TypeAdapter() {
      super();
   }

   public abstract void write(JsonWriter var1, T var2) throws IOException;

   public final void toJson(Writer var1, T var2) throws IOException {
      JsonWriter var3 = new JsonWriter(var1);
      this.write(var3, var2);
   }

   public final TypeAdapter<T> nullSafe() {
      return new TypeAdapter<T>() {
         public void write(JsonWriter var1, T var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               TypeAdapter.this.write(var1, var2);
            }

         }

         public T read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return TypeAdapter.this.read(var1);
            }
         }
      };
   }

   public final String toJson(T var1) {
      StringWriter var2 = new StringWriter();

      try {
         this.toJson(var2, var1);
      } catch (IOException var4) {
         throw new AssertionError(var4);
      }

      return var2.toString();
   }

   public final JsonElement toJsonTree(T var1) {
      try {
         JsonTreeWriter var2 = new JsonTreeWriter();
         this.write(var2, var1);
         return var2.get();
      } catch (IOException var3) {
         throw new JsonIOException(var3);
      }
   }

   public abstract T read(JsonReader var1) throws IOException;

   public final T fromJson(Reader var1) throws IOException {
      JsonReader var2 = new JsonReader(var1);
      return this.read(var2);
   }

   public final T fromJson(String var1) throws IOException {
      return this.fromJson((Reader)(new StringReader(var1)));
   }

   public final T fromJsonTree(JsonElement var1) {
      try {
         JsonTreeReader var2 = new JsonTreeReader(var1);
         return this.read(var2);
      } catch (IOException var3) {
         throw new JsonIOException(var3);
      }
   }
}
