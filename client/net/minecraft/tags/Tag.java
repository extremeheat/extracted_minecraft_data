package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class Tag<T> {
   private final ResourceLocation field_199888_a;
   private final Set<T> field_199889_b;
   private final Collection<Tag.ITagEntry<T>> field_200150_c;

   public Tag(ResourceLocation var1) {
      super();
      this.field_199888_a = var1;
      this.field_199889_b = Collections.emptySet();
      this.field_200150_c = Collections.emptyList();
   }

   public Tag(ResourceLocation var1, Collection<Tag.ITagEntry<T>> var2, boolean var3) {
      super();
      this.field_199888_a = var1;
      this.field_199889_b = (Set)(var3 ? Sets.newLinkedHashSet() : Sets.newHashSet());
      this.field_200150_c = var2;
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Tag.ITagEntry var5 = (Tag.ITagEntry)var4.next();
         var5.func_200162_a(this.field_199889_b);
      }

   }

   public JsonObject func_200571_a(Function<T, ResourceLocation> var1) {
      JsonObject var2 = new JsonObject();
      JsonArray var3 = new JsonArray();
      Iterator var4 = this.field_200150_c.iterator();

      while(var4.hasNext()) {
         Tag.ITagEntry var5 = (Tag.ITagEntry)var4.next();
         var5.func_200576_a(var3, var1);
      }

      var2.addProperty("replace", false);
      var2.add("values", var3);
      return var2;
   }

   public boolean func_199685_a_(T var1) {
      return this.field_199889_b.contains(var1);
   }

   public Collection<T> func_199885_a() {
      return this.field_199889_b;
   }

   public Collection<Tag.ITagEntry<T>> func_200570_b() {
      return this.field_200150_c;
   }

   public T func_205596_a(Random var1) {
      ArrayList var2 = Lists.newArrayList(this.func_199885_a());
      return var2.get(var1.nextInt(var2.size()));
   }

   public ResourceLocation func_199886_b() {
      return this.field_199888_a;
   }

   public static class TagEntry<T> implements Tag.ITagEntry<T> {
      @Nullable
      private final ResourceLocation field_200163_a;
      @Nullable
      private Tag<T> field_200164_b;

      public TagEntry(ResourceLocation var1) {
         super();
         this.field_200163_a = var1;
      }

      public TagEntry(Tag<T> var1) {
         super();
         this.field_200163_a = var1.func_199886_b();
         this.field_200164_b = var1;
      }

      public boolean func_200161_a(Function<ResourceLocation, Tag<T>> var1) {
         if (this.field_200164_b == null) {
            this.field_200164_b = (Tag)var1.apply(this.field_200163_a);
         }

         return this.field_200164_b != null;
      }

      public void func_200162_a(Collection<T> var1) {
         if (this.field_200164_b == null) {
            throw new IllegalStateException("Cannot build unresolved tag entry");
         } else {
            var1.addAll(this.field_200164_b.func_199885_a());
         }
      }

      public ResourceLocation func_200577_a() {
         if (this.field_200164_b != null) {
            return this.field_200164_b.func_199886_b();
         } else if (this.field_200163_a != null) {
            return this.field_200163_a;
         } else {
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
         }
      }

      public void func_200576_a(JsonArray var1, Function<T, ResourceLocation> var2) {
         var1.add("#" + this.func_200577_a());
      }
   }

   public static class ListEntry<T> implements Tag.ITagEntry<T> {
      private final Collection<T> field_200165_a;

      public ListEntry(Collection<T> var1) {
         super();
         this.field_200165_a = var1;
      }

      public void func_200162_a(Collection<T> var1) {
         var1.addAll(this.field_200165_a);
      }

      public void func_200576_a(JsonArray var1, Function<T, ResourceLocation> var2) {
         Iterator var3 = this.field_200165_a.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            ResourceLocation var5 = (ResourceLocation)var2.apply(var4);
            if (var5 == null) {
               throw new IllegalStateException("Unable to serialize an anonymous value to json!");
            }

            var1.add(var5.toString());
         }

      }

      public Collection<T> func_200578_a() {
         return this.field_200165_a;
      }
   }

   public interface ITagEntry<T> {
      default boolean func_200161_a(Function<ResourceLocation, Tag<T>> var1) {
         return true;
      }

      void func_200162_a(Collection<T> var1);

      void func_200576_a(JsonArray var1, Function<T, ResourceLocation> var2);
   }

   public static class Builder<T> {
      private final Set<Tag.ITagEntry<T>> field_200052_a = Sets.newLinkedHashSet();
      private boolean field_200053_b;

      public Builder() {
         super();
      }

      public static <T> Tag.Builder<T> func_200047_a() {
         return new Tag.Builder();
      }

      public Tag.Builder<T> func_200575_a(Tag.ITagEntry<T> var1) {
         this.field_200052_a.add(var1);
         return this;
      }

      public Tag.Builder<T> func_200048_a(T var1) {
         this.field_200052_a.add(new Tag.ListEntry(Collections.singleton(var1)));
         return this;
      }

      @SafeVarargs
      public final Tag.Builder<T> func_200573_a(T... var1) {
         this.field_200052_a.add(new Tag.ListEntry(Lists.newArrayList(var1)));
         return this;
      }

      public Tag.Builder<T> func_200046_a(Collection<T> var1) {
         this.field_200052_a.add(new Tag.ListEntry(var1));
         return this;
      }

      public Tag.Builder<T> func_200159_a(ResourceLocation var1) {
         this.field_200052_a.add(new Tag.TagEntry(var1));
         return this;
      }

      public Tag.Builder<T> func_200574_a(Tag<T> var1) {
         this.field_200052_a.add(new Tag.TagEntry(var1));
         return this;
      }

      public Tag.Builder<T> func_200045_a(boolean var1) {
         this.field_200053_b = var1;
         return this;
      }

      public boolean func_200160_a(Function<ResourceLocation, Tag<T>> var1) {
         Iterator var2 = this.field_200052_a.iterator();

         Tag.ITagEntry var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Tag.ITagEntry)var2.next();
         } while(var3.func_200161_a(var1));

         return false;
      }

      public Tag<T> func_200051_a(ResourceLocation var1) {
         return new Tag(var1, this.field_200052_a, this.field_200053_b);
      }

      public Tag.Builder<T> func_200158_a(Predicate<ResourceLocation> var1, Function<ResourceLocation, T> var2, JsonObject var3) {
         JsonArray var4 = JsonUtils.func_151214_t(var3, "values");
         if (JsonUtils.func_151209_a(var3, "replace", false)) {
            this.field_200052_a.clear();
         }

         Iterator var5 = var4.iterator();

         while(true) {
            while(var5.hasNext()) {
               JsonElement var6 = (JsonElement)var5.next();
               String var7 = JsonUtils.func_151206_a(var6, "value");
               if (!var7.startsWith("#")) {
                  ResourceLocation var8 = new ResourceLocation(var7);
                  Object var9 = var2.apply(var8);
                  if (var9 == null || !var1.test(var8)) {
                     throw new JsonParseException("Unknown value '" + var8 + "'");
                  }

                  this.func_200048_a(var9);
               } else {
                  this.func_200159_a(new ResourceLocation(var7.substring(1)));
               }
            }

            return this;
         }
      }
   }
}
