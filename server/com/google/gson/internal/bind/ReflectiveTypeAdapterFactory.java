package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorConstructor constructorConstructor;
   private final FieldNamingStrategy fieldNamingPolicy;
   private final Excluder excluder;
   private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;

   public ReflectiveTypeAdapterFactory(ConstructorConstructor var1, FieldNamingStrategy var2, Excluder var3, JsonAdapterAnnotationTypeAdapterFactory var4) {
      super();
      this.constructorConstructor = var1;
      this.fieldNamingPolicy = var2;
      this.excluder = var3;
      this.jsonAdapterFactory = var4;
   }

   public boolean excludeField(Field var1, boolean var2) {
      return excludeField(var1, var2, this.excluder);
   }

   static boolean excludeField(Field var0, boolean var1, Excluder var2) {
      return !var2.excludeClass(var0.getType(), var1) && !var2.excludeField(var0, var1);
   }

   private List<String> getFieldNames(Field var1) {
      SerializedName var2 = (SerializedName)var1.getAnnotation(SerializedName.class);
      String var3;
      if (var2 == null) {
         var3 = this.fieldNamingPolicy.translateName(var1);
         return Collections.singletonList(var3);
      } else {
         var3 = var2.value();
         String[] var4 = var2.alternate();
         if (var4.length == 0) {
            return Collections.singletonList(var3);
         } else {
            ArrayList var5 = new ArrayList(var4.length + 1);
            var5.add(var3);
            String[] var6 = var4;
            int var7 = var4.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               var5.add(var9);
            }

            return var5;
         }
      }
   }

   public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
      Class var3 = var2.getRawType();
      if (!Object.class.isAssignableFrom(var3)) {
         return null;
      } else {
         ObjectConstructor var4 = this.constructorConstructor.get(var2);
         return new ReflectiveTypeAdapterFactory.Adapter(var4, this.getBoundFields(var1, var2, var3));
      }
   }

   private ReflectiveTypeAdapterFactory.BoundField createBoundField(final Gson var1, final Field var2, String var3, final TypeToken<?> var4, boolean var5, boolean var6) {
      final boolean var7 = Primitives.isPrimitive(var4.getRawType());
      JsonAdapter var8 = (JsonAdapter)var2.getAnnotation(JsonAdapter.class);
      final TypeAdapter var9 = null;
      if (var8 != null) {
         var9 = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, var1, var4, var8);
      }

      final boolean var10 = var9 != null;
      if (var9 == null) {
         var9 = var1.getAdapter(var4);
      }

      return new ReflectiveTypeAdapterFactory.BoundField(var3, var5, var6) {
         void write(JsonWriter var1x, Object var2x) throws IOException, IllegalAccessException {
            Object var3 = var2.get(var2x);
            Object var4x = var10 ? var9 : new TypeAdapterRuntimeTypeWrapper(var1, var9, var4.getType());
            ((TypeAdapter)var4x).write(var1x, var3);
         }

         void read(JsonReader var1x, Object var2x) throws IOException, IllegalAccessException {
            Object var3 = var9.read(var1x);
            if (var3 != null || !var7) {
               var2.set(var2x, var3);
            }

         }

         public boolean writeField(Object var1x) throws IOException, IllegalAccessException {
            if (!this.serialized) {
               return false;
            } else {
               Object var2x = var2.get(var1x);
               return var2x != var1x;
            }
         }
      };
   }

   private Map<String, ReflectiveTypeAdapterFactory.BoundField> getBoundFields(Gson var1, TypeToken<?> var2, Class<?> var3) {
      LinkedHashMap var4 = new LinkedHashMap();
      if (var3.isInterface()) {
         return var4;
      } else {
         for(Type var5 = var2.getType(); var3 != Object.class; var3 = var2.getRawType()) {
            Field[] var6 = var3.getDeclaredFields();
            Field[] var7 = var6;
            int var8 = var6.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Field var10 = var7[var9];
               boolean var11 = this.excludeField(var10, true);
               boolean var12 = this.excludeField(var10, false);
               if (var11 || var12) {
                  var10.setAccessible(true);
                  Type var13 = $Gson$Types.resolve(var2.getType(), var3, var10.getGenericType());
                  List var14 = this.getFieldNames(var10);
                  ReflectiveTypeAdapterFactory.BoundField var15 = null;

                  for(int var16 = 0; var16 < var14.size(); ++var16) {
                     String var17 = (String)var14.get(var16);
                     if (var16 != 0) {
                        var11 = false;
                     }

                     ReflectiveTypeAdapterFactory.BoundField var18 = this.createBoundField(var1, var10, var17, TypeToken.get(var13), var11, var12);
                     ReflectiveTypeAdapterFactory.BoundField var19 = (ReflectiveTypeAdapterFactory.BoundField)var4.put(var17, var18);
                     if (var15 == null) {
                        var15 = var19;
                     }
                  }

                  if (var15 != null) {
                     throw new IllegalArgumentException(var5 + " declares multiple JSON fields named " + var15.name);
                  }
               }
            }

            var2 = TypeToken.get($Gson$Types.resolve(var2.getType(), var3, var3.getGenericSuperclass()));
         }

         return var4;
      }
   }

   public static final class Adapter<T> extends TypeAdapter<T> {
      private final ObjectConstructor<T> constructor;
      private final Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields;

      Adapter(ObjectConstructor<T> var1, Map<String, ReflectiveTypeAdapterFactory.BoundField> var2) {
         super();
         this.constructor = var1;
         this.boundFields = var2;
      }

      public T read(JsonReader var1) throws IOException {
         if (var1.peek() == JsonToken.NULL) {
            var1.nextNull();
            return null;
         } else {
            Object var2 = this.constructor.construct();

            try {
               var1.beginObject();

               while(var1.hasNext()) {
                  String var3 = var1.nextName();
                  ReflectiveTypeAdapterFactory.BoundField var4 = (ReflectiveTypeAdapterFactory.BoundField)this.boundFields.get(var3);
                  if (var4 != null && var4.deserialized) {
                     var4.read(var1, var2);
                  } else {
                     var1.skipValue();
                  }
               }
            } catch (IllegalStateException var5) {
               throw new JsonSyntaxException(var5);
            } catch (IllegalAccessException var6) {
               throw new AssertionError(var6);
            }

            var1.endObject();
            return var2;
         }
      }

      public void write(JsonWriter var1, T var2) throws IOException {
         if (var2 == null) {
            var1.nullValue();
         } else {
            var1.beginObject();

            try {
               Iterator var3 = this.boundFields.values().iterator();

               while(var3.hasNext()) {
                  ReflectiveTypeAdapterFactory.BoundField var4 = (ReflectiveTypeAdapterFactory.BoundField)var3.next();
                  if (var4.writeField(var2)) {
                     var1.name(var4.name);
                     var4.write(var1, var2);
                  }
               }
            } catch (IllegalAccessException var5) {
               throw new AssertionError(var5);
            }

            var1.endObject();
         }
      }
   }

   abstract static class BoundField {
      final String name;
      final boolean serialized;
      final boolean deserialized;

      protected BoundField(String var1, boolean var2, boolean var3) {
         super();
         this.name = var1;
         this.serialized = var2;
         this.deserialized = var3;
      }

      abstract boolean writeField(Object var1) throws IOException, IllegalAccessException;

      abstract void write(JsonWriter var1, Object var2) throws IOException, IllegalAccessException;

      abstract void read(JsonReader var1, Object var2) throws IOException, IllegalAccessException;
   }
}
