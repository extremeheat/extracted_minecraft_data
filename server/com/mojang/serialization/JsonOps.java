package com.mojang.serialization;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class JsonOps implements DynamicOps<JsonElement> {
   public static final JsonOps INSTANCE = new JsonOps(false);
   public static final JsonOps COMPRESSED = new JsonOps(true);
   private final boolean compressed;

   protected JsonOps(boolean var1) {
      super();
      this.compressed = var1;
   }

   public JsonElement empty() {
      return JsonNull.INSTANCE;
   }

   public <U> U convertTo(DynamicOps<U> var1, JsonElement var2) {
      if (var2 instanceof JsonObject) {
         return this.convertMap(var1, var2);
      } else if (var2 instanceof JsonArray) {
         return this.convertList(var1, var2);
      } else if (var2 instanceof JsonNull) {
         return var1.empty();
      } else {
         JsonPrimitive var3 = var2.getAsJsonPrimitive();
         if (var3.isString()) {
            return var1.createString(var3.getAsString());
         } else if (var3.isBoolean()) {
            return var1.createBoolean(var3.getAsBoolean());
         } else {
            BigDecimal var4 = var3.getAsBigDecimal();

            try {
               long var5 = var4.longValueExact();
               if ((long)((byte)((int)var5)) == var5) {
                  return var1.createByte((byte)((int)var5));
               } else if ((long)((short)((int)var5)) == var5) {
                  return var1.createShort((short)((int)var5));
               } else {
                  return (long)((int)var5) == var5 ? var1.createInt((int)var5) : var1.createLong(var5);
               }
            } catch (ArithmeticException var8) {
               double var6 = var4.doubleValue();
               return (double)((float)var6) == var6 ? var1.createFloat((float)var6) : var1.createDouble(var6);
            }
         }
      }
   }

   public DataResult<Number> getNumberValue(JsonElement var1) {
      if (var1 instanceof JsonPrimitive) {
         if (var1.getAsJsonPrimitive().isNumber()) {
            return DataResult.success(var1.getAsNumber());
         }

         if (var1.getAsJsonPrimitive().isBoolean()) {
            return DataResult.success(var1.getAsBoolean() ? 1 : 0);
         }

         if (this.compressed && var1.getAsJsonPrimitive().isString()) {
            try {
               return DataResult.success(Integer.parseInt(var1.getAsString()));
            } catch (NumberFormatException var3) {
               return DataResult.error("Not a number: " + var3 + " " + var1);
            }
         }
      }

      return var1 instanceof JsonPrimitive && var1.getAsJsonPrimitive().isBoolean() ? DataResult.success(var1.getAsJsonPrimitive().getAsBoolean() ? 1 : 0) : DataResult.error("Not a number: " + var1);
   }

   public JsonElement createNumeric(Number var1) {
      return new JsonPrimitive(var1);
   }

   public DataResult<Boolean> getBooleanValue(JsonElement var1) {
      if (var1 instanceof JsonPrimitive) {
         if (var1.getAsJsonPrimitive().isBoolean()) {
            return DataResult.success(var1.getAsBoolean());
         }

         if (var1.getAsJsonPrimitive().isNumber()) {
            return DataResult.success(var1.getAsNumber().byteValue() != 0);
         }
      }

      return DataResult.error("Not a boolean: " + var1);
   }

   public JsonElement createBoolean(boolean var1) {
      return new JsonPrimitive(var1);
   }

   public DataResult<String> getStringValue(JsonElement var1) {
      return !(var1 instanceof JsonPrimitive) || !var1.getAsJsonPrimitive().isString() && (!var1.getAsJsonPrimitive().isNumber() || !this.compressed) ? DataResult.error("Not a string: " + var1) : DataResult.success(var1.getAsString());
   }

   public JsonElement createString(String var1) {
      return new JsonPrimitive(var1);
   }

   public DataResult<JsonElement> mergeToList(JsonElement var1, JsonElement var2) {
      if (!(var1 instanceof JsonArray) && var1 != this.empty()) {
         return DataResult.error("mergeToList called with not a list: " + var1, (Object)var1);
      } else {
         JsonArray var3 = new JsonArray();
         if (var1 != this.empty()) {
            var3.addAll(var1.getAsJsonArray());
         }

         var3.add(var2);
         return DataResult.success(var3);
      }
   }

   public DataResult<JsonElement> mergeToList(JsonElement var1, List<JsonElement> var2) {
      if (!(var1 instanceof JsonArray) && var1 != this.empty()) {
         return DataResult.error("mergeToList called with not a list: " + var1, (Object)var1);
      } else {
         JsonArray var3 = new JsonArray();
         if (var1 != this.empty()) {
            var3.addAll(var1.getAsJsonArray());
         }

         var2.forEach(var3::add);
         return DataResult.success(var3);
      }
   }

   public DataResult<JsonElement> mergeToMap(JsonElement var1, JsonElement var2, JsonElement var3) {
      if (!(var1 instanceof JsonObject) && var1 != this.empty()) {
         return DataResult.error("mergeToMap called with not a map: " + var1, (Object)var1);
      } else if (var2 instanceof JsonPrimitive && (var2.getAsJsonPrimitive().isString() || this.compressed)) {
         JsonObject var4 = new JsonObject();
         if (var1 != this.empty()) {
            var1.getAsJsonObject().entrySet().forEach((var1x) -> {
               var4.add((String)var1x.getKey(), (JsonElement)var1x.getValue());
            });
         }

         var4.add(var2.getAsString(), var3);
         return DataResult.success(var4);
      } else {
         return DataResult.error("key is not a string: " + var2, (Object)var1);
      }
   }

   public DataResult<JsonElement> mergeToMap(JsonElement var1, MapLike<JsonElement> var2) {
      if (!(var1 instanceof JsonObject) && var1 != this.empty()) {
         return DataResult.error("mergeToMap called with not a map: " + var1, (Object)var1);
      } else {
         JsonObject var3 = new JsonObject();
         if (var1 != this.empty()) {
            var1.getAsJsonObject().entrySet().forEach((var1x) -> {
               var3.add((String)var1x.getKey(), (JsonElement)var1x.getValue());
            });
         }

         ArrayList var4 = Lists.newArrayList();
         var2.entries().forEach((var3x) -> {
            JsonElement var4x = (JsonElement)var3x.getFirst();
            if (var4x instanceof JsonPrimitive && (var4x.getAsJsonPrimitive().isString() || this.compressed)) {
               var3.add(var4x.getAsString(), (JsonElement)var3x.getSecond());
            } else {
               var4.add(var4x);
            }
         });
         return !var4.isEmpty() ? DataResult.error("some keys are not strings: " + var4, (Object)var3) : DataResult.success(var3);
      }
   }

   public DataResult<Stream<Pair<JsonElement, JsonElement>>> getMapValues(JsonElement var1) {
      return !(var1 instanceof JsonObject) ? DataResult.error("Not a JSON object: " + var1) : DataResult.success(var1.getAsJsonObject().entrySet().stream().map((var0) -> {
         return Pair.of(new JsonPrimitive((String)var0.getKey()), var0.getValue() instanceof JsonNull ? null : (JsonElement)var0.getValue());
      }));
   }

   public DataResult<Consumer<BiConsumer<JsonElement, JsonElement>>> getMapEntries(JsonElement var1) {
      return !(var1 instanceof JsonObject) ? DataResult.error("Not a JSON object: " + var1) : DataResult.success((var2) -> {
         Iterator var3 = var1.getAsJsonObject().entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var2.accept(this.createString((String)var4.getKey()), var4.getValue() instanceof JsonNull ? null : (JsonElement)var4.getValue());
         }

      });
   }

   public DataResult<MapLike<JsonElement>> getMap(JsonElement var1) {
      if (!(var1 instanceof JsonObject)) {
         return DataResult.error("Not a JSON object: " + var1);
      } else {
         final JsonObject var2 = var1.getAsJsonObject();
         return DataResult.success(new MapLike<JsonElement>() {
            @Nullable
            public JsonElement get(JsonElement var1) {
               JsonElement var2x = var2.get(var1.getAsString());
               return var2x instanceof JsonNull ? null : var2x;
            }

            @Nullable
            public JsonElement get(String var1) {
               JsonElement var2x = var2.get(var1);
               return var2x instanceof JsonNull ? null : var2x;
            }

            public Stream<Pair<JsonElement, JsonElement>> entries() {
               return var2.entrySet().stream().map((var0) -> {
                  return Pair.of(new JsonPrimitive((String)var0.getKey()), var0.getValue());
               });
            }

            public String toString() {
               return "MapLike[" + var2 + "]";
            }
         });
      }
   }

   public JsonElement createMap(Stream<Pair<JsonElement, JsonElement>> var1) {
      JsonObject var2 = new JsonObject();
      var1.forEach((var1x) -> {
         var2.add(((JsonElement)var1x.getFirst()).getAsString(), (JsonElement)var1x.getSecond());
      });
      return var2;
   }

   public DataResult<Stream<JsonElement>> getStream(JsonElement var1) {
      return var1 instanceof JsonArray ? DataResult.success(StreamSupport.stream(var1.getAsJsonArray().spliterator(), false).map((var0) -> {
         return var0 instanceof JsonNull ? null : var0;
      })) : DataResult.error("Not a json array: " + var1);
   }

   public DataResult<Consumer<Consumer<JsonElement>>> getList(JsonElement var1) {
      return var1 instanceof JsonArray ? DataResult.success((var1x) -> {
         Iterator var2 = var1.getAsJsonArray().iterator();

         while(var2.hasNext()) {
            JsonElement var3 = (JsonElement)var2.next();
            var1x.accept(var3 instanceof JsonNull ? null : var3);
         }

      }) : DataResult.error("Not a json array: " + var1);
   }

   public JsonElement createList(Stream<JsonElement> var1) {
      JsonArray var2 = new JsonArray();
      var1.forEach(var2::add);
      return var2;
   }

   public JsonElement remove(JsonElement var1, String var2) {
      if (var1 instanceof JsonObject) {
         JsonObject var3 = new JsonObject();
         var1.getAsJsonObject().entrySet().stream().filter((var1x) -> {
            return !Objects.equals(var1x.getKey(), var2);
         }).forEach((var1x) -> {
            var3.add((String)var1x.getKey(), (JsonElement)var1x.getValue());
         });
         return var3;
      } else {
         return var1;
      }
   }

   public String toString() {
      return "JSON";
   }

   public ListBuilder<JsonElement> listBuilder() {
      return new JsonOps.ArrayBuilder();
   }

   public boolean compressMaps() {
      return this.compressed;
   }

   public RecordBuilder<JsonElement> mapBuilder() {
      return new JsonOps.JsonRecordBuilder();
   }

   private class JsonRecordBuilder extends RecordBuilder.AbstractStringBuilder<JsonElement, JsonObject> {
      protected JsonRecordBuilder() {
         super(JsonOps.this);
      }

      protected JsonObject initBuilder() {
         return new JsonObject();
      }

      protected JsonObject append(String var1, JsonElement var2, JsonObject var3) {
         var3.add(var1, var2);
         return var3;
      }

      protected DataResult<JsonElement> build(JsonObject var1, JsonElement var2) {
         if (var2 != null && !(var2 instanceof JsonNull)) {
            if (!(var2 instanceof JsonObject)) {
               return DataResult.error("mergeToMap called with not a map: " + var2, (Object)var2);
            } else {
               JsonObject var3 = new JsonObject();
               Iterator var4 = var2.getAsJsonObject().entrySet().iterator();

               Entry var5;
               while(var4.hasNext()) {
                  var5 = (Entry)var4.next();
                  var3.add((String)var5.getKey(), (JsonElement)var5.getValue());
               }

               var4 = var1.entrySet().iterator();

               while(var4.hasNext()) {
                  var5 = (Entry)var4.next();
                  var3.add((String)var5.getKey(), (JsonElement)var5.getValue());
               }

               return DataResult.success(var3);
            }
         } else {
            return DataResult.success(var1);
         }
      }
   }

   private static final class ArrayBuilder implements ListBuilder<JsonElement> {
      private DataResult<JsonArray> builder;

      private ArrayBuilder() {
         super();
         this.builder = DataResult.success(new JsonArray(), Lifecycle.stable());
      }

      public DynamicOps<JsonElement> ops() {
         return JsonOps.INSTANCE;
      }

      public ListBuilder<JsonElement> add(JsonElement var1) {
         this.builder = this.builder.map((var1x) -> {
            var1x.add(var1);
            return var1x;
         });
         return this;
      }

      public ListBuilder<JsonElement> add(DataResult<JsonElement> var1) {
         this.builder = this.builder.apply2stable((var0, var1x) -> {
            var0.add(var1x);
            return var0;
         }, var1);
         return this;
      }

      public ListBuilder<JsonElement> withErrorsFrom(DataResult<?> var1) {
         this.builder = this.builder.flatMap((var1x) -> {
            return var1.map((var1xx) -> {
               return var1x;
            });
         });
         return this;
      }

      public ListBuilder<JsonElement> mapError(UnaryOperator<String> var1) {
         this.builder = this.builder.mapError(var1);
         return this;
      }

      public DataResult<JsonElement> build(JsonElement var1) {
         DataResult var2 = this.builder.flatMap((var2x) -> {
            if (!(var1 instanceof JsonArray) && var1 != this.ops().empty()) {
               return DataResult.error("Cannot append a list to not a list: " + var1, (Object)var1);
            } else {
               JsonArray var3 = new JsonArray();
               if (var1 != this.ops().empty()) {
                  var3.addAll(var1.getAsJsonArray());
               }

               var3.addAll(var2x);
               return DataResult.success(var3, Lifecycle.stable());
            }
         });
         this.builder = DataResult.success(new JsonArray(), Lifecycle.stable());
         return var2;
      }

      // $FF: synthetic method
      ArrayBuilder(Object var1) {
         this();
      }
   }
}
