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
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Tag {
   private final ResourceLocation id;
   private final Set values;
   private final Collection source;

   public Tag(ResourceLocation var1) {
      this.id = var1;
      this.values = Collections.emptySet();
      this.source = Collections.emptyList();
   }

   public Tag(ResourceLocation var1, Collection var2, boolean var3) {
      this.id = var1;
      this.values = (Set)(var3 ? Sets.newLinkedHashSet() : Sets.newHashSet());
      this.source = var2;
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Tag.Entry var5 = (Tag.Entry)var4.next();
         var5.build(this.values);
      }

   }

   public JsonObject serializeToJson(Function var1) {
      JsonObject var2 = new JsonObject();
      JsonArray var3 = new JsonArray();
      Iterator var4 = this.source.iterator();

      while(var4.hasNext()) {
         Tag.Entry var5 = (Tag.Entry)var4.next();
         var5.serializeTo(var3, var1);
      }

      var2.addProperty("replace", false);
      var2.add("values", var3);
      return var2;
   }

   public boolean contains(Object var1) {
      return this.values.contains(var1);
   }

   public Collection getValues() {
      return this.values;
   }

   public Collection getSource() {
      return this.source;
   }

   public Object getRandomElement(Random var1) {
      ArrayList var2 = Lists.newArrayList(this.getValues());
      return var2.get(var1.nextInt(var2.size()));
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public static class TagEntry implements Tag.Entry {
      @Nullable
      private final ResourceLocation id;
      @Nullable
      private Tag tag;

      public TagEntry(ResourceLocation var1) {
         this.id = var1;
      }

      public TagEntry(Tag var1) {
         this.id = var1.getId();
         this.tag = var1;
      }

      public boolean canBuild(Function var1) {
         if (this.tag == null) {
            this.tag = (Tag)var1.apply(this.id);
         }

         return this.tag != null;
      }

      public void build(Collection var1) {
         if (this.tag == null) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Cannot build unresolved tag entry"));
         } else {
            var1.addAll(this.tag.getValues());
         }
      }

      public ResourceLocation getId() {
         if (this.tag != null) {
            return this.tag.getId();
         } else if (this.id != null) {
            return this.id;
         } else {
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
         }
      }

      public void serializeTo(JsonArray var1, Function var2) {
         var1.add("#" + this.getId());
      }
   }

   public static class ValuesEntry implements Tag.Entry {
      private final Collection values;

      public ValuesEntry(Collection var1) {
         this.values = var1;
      }

      public void build(Collection var1) {
         var1.addAll(this.values);
      }

      public void serializeTo(JsonArray var1, Function var2) {
         Iterator var3 = this.values.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            ResourceLocation var5 = (ResourceLocation)var2.apply(var4);
            if (var5 == null) {
               throw new IllegalStateException("Unable to serialize an anonymous value to json!");
            }

            var1.add(var5.toString());
         }

      }

      public Collection getValues() {
         return this.values;
      }
   }

   public interface Entry {
      default boolean canBuild(Function var1) {
         return true;
      }

      void build(Collection var1);

      void serializeTo(JsonArray var1, Function var2);
   }

   public static class Builder {
      private final Set values = Sets.newLinkedHashSet();
      private boolean ordered;

      public static Tag.Builder tag() {
         return new Tag.Builder();
      }

      public Tag.Builder add(Tag.Entry var1) {
         this.values.add(var1);
         return this;
      }

      public Tag.Builder add(Object var1) {
         this.values.add(new Tag.ValuesEntry(Collections.singleton(var1)));
         return this;
      }

      @SafeVarargs
      public final Tag.Builder add(Object... var1) {
         this.values.add(new Tag.ValuesEntry(Lists.newArrayList(var1)));
         return this;
      }

      public Tag.Builder addTag(Tag var1) {
         this.values.add(new Tag.TagEntry(var1));
         return this;
      }

      public Tag.Builder keepOrder(boolean var1) {
         this.ordered = var1;
         return this;
      }

      public boolean canBuild(Function var1) {
         Iterator var2 = this.values.iterator();

         Tag.Entry var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Tag.Entry)var2.next();
         } while(var3.canBuild(var1));

         return false;
      }

      public Tag build(ResourceLocation var1) {
         return new Tag(var1, this.values, this.ordered);
      }

      public Tag.Builder addFromJson(Function var1, JsonObject var2) {
         JsonArray var3 = GsonHelper.getAsJsonArray(var2, "values");
         ArrayList var4 = Lists.newArrayList();
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            JsonElement var6 = (JsonElement)var5.next();
            String var7 = GsonHelper.convertToString(var6, "value");
            if (var7.startsWith("#")) {
               var4.add(new Tag.TagEntry(new ResourceLocation(var7.substring(1))));
            } else {
               ResourceLocation var8 = new ResourceLocation(var7);
               var4.add(new Tag.ValuesEntry(Collections.singleton(((Optional)var1.apply(var8)).orElseThrow(() -> {
                  return new JsonParseException("Unknown value '" + var8 + "'");
               }))));
            }
         }

         if (GsonHelper.getAsBoolean(var2, "replace", false)) {
            this.values.clear();
         }

         this.values.addAll(var4);
         return this;
      }
   }
}
