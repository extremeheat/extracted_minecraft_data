package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public interface Tag<T> {
   static <T> Codec<Tag<T>> codec(Supplier<TagCollection<T>> var0) {
      return ResourceLocation.CODEC.flatXmap((var1) -> {
         return (DataResult)Optional.ofNullable(((TagCollection)var0.get()).getTag(var1)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown tag: " + var1);
         });
      }, (var1) -> {
         return (DataResult)Optional.ofNullable(((TagCollection)var0.get()).getId(var1)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown tag: " + var1);
         });
      });
   }

   boolean contains(T var1);

   List<T> getValues();

   default T getRandomElement(Random var1) {
      List var2 = this.getValues();
      return var2.get(var1.nextInt(var2.size()));
   }

   static <T> Tag<T> fromSet(Set<T> var0) {
      return SetTag.create(var0);
   }

   public interface Named<T> extends Tag<T> {
      ResourceLocation getName();
   }

   public static class OptionalTagEntry implements Tag.Entry {
      private final ResourceLocation id;

      public OptionalTagEntry(ResourceLocation var1) {
         super();
         this.id = var1;
      }

      public <T> boolean build(Function<ResourceLocation, Tag<T>> var1, Function<ResourceLocation, T> var2, Consumer<T> var3) {
         Tag var4 = (Tag)var1.apply(this.id);
         if (var4 != null) {
            var4.getValues().forEach(var3);
         }

         return true;
      }

      public void serializeTo(JsonArray var1) {
         JsonObject var2 = new JsonObject();
         var2.addProperty("id", "#" + this.id);
         var2.addProperty("required", false);
         var1.add(var2);
      }

      public String toString() {
         return "#" + this.id + "?";
      }
   }

   public static class TagEntry implements Tag.Entry {
      private final ResourceLocation id;

      public TagEntry(ResourceLocation var1) {
         super();
         this.id = var1;
      }

      public <T> boolean build(Function<ResourceLocation, Tag<T>> var1, Function<ResourceLocation, T> var2, Consumer<T> var3) {
         Tag var4 = (Tag)var1.apply(this.id);
         if (var4 == null) {
            return false;
         } else {
            var4.getValues().forEach(var3);
            return true;
         }
      }

      public void serializeTo(JsonArray var1) {
         var1.add("#" + this.id);
      }

      public String toString() {
         return "#" + this.id;
      }
   }

   public static class OptionalElementEntry implements Tag.Entry {
      private final ResourceLocation id;

      public OptionalElementEntry(ResourceLocation var1) {
         super();
         this.id = var1;
      }

      public <T> boolean build(Function<ResourceLocation, Tag<T>> var1, Function<ResourceLocation, T> var2, Consumer<T> var3) {
         Object var4 = var2.apply(this.id);
         if (var4 != null) {
            var3.accept(var4);
         }

         return true;
      }

      public void serializeTo(JsonArray var1) {
         JsonObject var2 = new JsonObject();
         var2.addProperty("id", this.id.toString());
         var2.addProperty("required", false);
         var1.add(var2);
      }

      public String toString() {
         return this.id.toString() + "?";
      }
   }

   public static class ElementEntry implements Tag.Entry {
      private final ResourceLocation id;

      public ElementEntry(ResourceLocation var1) {
         super();
         this.id = var1;
      }

      public <T> boolean build(Function<ResourceLocation, Tag<T>> var1, Function<ResourceLocation, T> var2, Consumer<T> var3) {
         Object var4 = var2.apply(this.id);
         if (var4 == null) {
            return false;
         } else {
            var3.accept(var4);
            return true;
         }
      }

      public void serializeTo(JsonArray var1) {
         var1.add(this.id.toString());
      }

      public String toString() {
         return this.id.toString();
      }
   }

   public interface Entry {
      <T> boolean build(Function<ResourceLocation, Tag<T>> var1, Function<ResourceLocation, T> var2, Consumer<T> var3);

      void serializeTo(JsonArray var1);
   }

   public static class Builder {
      private final List<Tag.BuilderEntry> entries = Lists.newArrayList();

      public Builder() {
         super();
      }

      public static Tag.Builder tag() {
         return new Tag.Builder();
      }

      public Tag.Builder add(Tag.BuilderEntry var1) {
         this.entries.add(var1);
         return this;
      }

      public Tag.Builder add(Tag.Entry var1, String var2) {
         return this.add(new Tag.BuilderEntry(var1, var2));
      }

      public Tag.Builder addElement(ResourceLocation var1, String var2) {
         return this.add(new Tag.ElementEntry(var1), var2);
      }

      public Tag.Builder addTag(ResourceLocation var1, String var2) {
         return this.add(new Tag.TagEntry(var1), var2);
      }

      public <T> Optional<Tag<T>> build(Function<ResourceLocation, Tag<T>> var1, Function<ResourceLocation, T> var2) {
         com.google.common.collect.ImmutableSet.Builder var3 = ImmutableSet.builder();
         Iterator var4 = this.entries.iterator();

         Tag.Entry var10000;
         do {
            if (!var4.hasNext()) {
               return Optional.of(Tag.fromSet(var3.build()));
            }

            Tag.BuilderEntry var5 = (Tag.BuilderEntry)var4.next();
            var10000 = var5.getEntry();
            var3.getClass();
         } while(var10000.build(var1, var2, var3::add));

         return Optional.empty();
      }

      public Stream<Tag.BuilderEntry> getEntries() {
         return this.entries.stream();
      }

      public <T> Stream<Tag.BuilderEntry> getUnresolvedEntries(Function<ResourceLocation, Tag<T>> var1, Function<ResourceLocation, T> var2) {
         return this.getEntries().filter((var2x) -> {
            return !var2x.getEntry().build(var1, var2, (var0) -> {
            });
         });
      }

      public Tag.Builder addFromJson(JsonObject var1, String var2) {
         JsonArray var3 = GsonHelper.getAsJsonArray(var1, "values");
         ArrayList var4 = Lists.newArrayList();
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            JsonElement var6 = (JsonElement)var5.next();
            var4.add(parseEntry(var6));
         }

         if (GsonHelper.getAsBoolean(var1, "replace", false)) {
            this.entries.clear();
         }

         var4.forEach((var2x) -> {
            this.entries.add(new Tag.BuilderEntry(var2x, var2));
         });
         return this;
      }

      private static Tag.Entry parseEntry(JsonElement var0) {
         String var1;
         boolean var2;
         if (var0.isJsonObject()) {
            JsonObject var3 = var0.getAsJsonObject();
            var1 = GsonHelper.getAsString(var3, "id");
            var2 = GsonHelper.getAsBoolean(var3, "required", true);
         } else {
            var1 = GsonHelper.convertToString(var0, "id");
            var2 = true;
         }

         ResourceLocation var4;
         if (var1.startsWith("#")) {
            var4 = new ResourceLocation(var1.substring(1));
            return (Tag.Entry)(var2 ? new Tag.TagEntry(var4) : new Tag.OptionalTagEntry(var4));
         } else {
            var4 = new ResourceLocation(var1);
            return (Tag.Entry)(var2 ? new Tag.ElementEntry(var4) : new Tag.OptionalElementEntry(var4));
         }
      }

      public JsonObject serializeToJson() {
         JsonObject var1 = new JsonObject();
         JsonArray var2 = new JsonArray();
         Iterator var3 = this.entries.iterator();

         while(var3.hasNext()) {
            Tag.BuilderEntry var4 = (Tag.BuilderEntry)var3.next();
            var4.getEntry().serializeTo(var2);
         }

         var1.addProperty("replace", false);
         var1.add("values", var2);
         return var1;
      }
   }

   public static class BuilderEntry {
      private final Tag.Entry entry;
      private final String source;

      private BuilderEntry(Tag.Entry var1, String var2) {
         super();
         this.entry = var1;
         this.source = var2;
      }

      public Tag.Entry getEntry() {
         return this.entry;
      }

      public String toString() {
         return this.entry.toString() + " (from " + this.source + ")";
      }

      // $FF: synthetic method
      BuilderEntry(Tag.Entry var1, String var2, Object var3) {
         this(var1, var2);
      }
   }
}
