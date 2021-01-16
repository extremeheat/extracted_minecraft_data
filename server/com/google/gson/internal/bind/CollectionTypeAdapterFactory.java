package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

public final class CollectionTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorConstructor constructorConstructor;

   public CollectionTypeAdapterFactory(ConstructorConstructor var1) {
      super();
      this.constructorConstructor = var1;
   }

   public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
      Type var3 = var2.getType();
      Class var4 = var2.getRawType();
      if (!Collection.class.isAssignableFrom(var4)) {
         return null;
      } else {
         Type var5 = $Gson$Types.getCollectionElementType(var3, var4);
         TypeAdapter var6 = var1.getAdapter(TypeToken.get(var5));
         ObjectConstructor var7 = this.constructorConstructor.get(var2);
         CollectionTypeAdapterFactory.Adapter var8 = new CollectionTypeAdapterFactory.Adapter(var1, var5, var6, var7);
         return var8;
      }
   }

   private static final class Adapter<E> extends TypeAdapter<Collection<E>> {
      private final TypeAdapter<E> elementTypeAdapter;
      private final ObjectConstructor<? extends Collection<E>> constructor;

      public Adapter(Gson var1, Type var2, TypeAdapter<E> var3, ObjectConstructor<? extends Collection<E>> var4) {
         super();
         this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper(var1, var3, var2);
         this.constructor = var4;
      }

      public Collection<E> read(JsonReader var1) throws IOException {
         if (var1.peek() == JsonToken.NULL) {
            var1.nextNull();
            return null;
         } else {
            Collection var2 = (Collection)this.constructor.construct();
            var1.beginArray();

            while(var1.hasNext()) {
               Object var3 = this.elementTypeAdapter.read(var1);
               var2.add(var3);
            }

            var1.endArray();
            return var2;
         }
      }

      public void write(JsonWriter var1, Collection<E> var2) throws IOException {
         if (var2 == null) {
            var1.nullValue();
         } else {
            var1.beginArray();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               Object var4 = var3.next();
               this.elementTypeAdapter.write(var1, var4);
            }

            var1.endArray();
         }
      }
   }
}
