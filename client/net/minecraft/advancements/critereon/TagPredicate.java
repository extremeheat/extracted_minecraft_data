package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;

public class TagPredicate<T> {
   private final TagKey<T> tag;
   private final boolean expected;

   public TagPredicate(TagKey<T> var1, boolean var2) {
      super();
      this.tag = var1;
      this.expected = var2;
   }

   public static <T> TagPredicate<T> is(TagKey<T> var0) {
      return new TagPredicate<>(var0, true);
   }

   public static <T> TagPredicate<T> isNot(TagKey<T> var0) {
      return new TagPredicate<>(var0, false);
   }

   public boolean matches(Holder<T> var1) {
      return var1.is(this.tag) == this.expected;
   }

   public JsonElement serializeToJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("id", this.tag.location().toString());
      var1.addProperty("expected", this.expected);
      return var1;
   }

   public static <T> TagPredicate<T> fromJson(@Nullable JsonElement var0, ResourceKey<? extends Registry<T>> var1) {
      if (var0 == null) {
         throw new JsonParseException("Expected a tag predicate");
      } else {
         JsonObject var2 = GsonHelper.convertToJsonObject(var0, "Tag Predicate");
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var2, "id"));
         boolean var4 = GsonHelper.getAsBoolean(var2, "expected");
         return new TagPredicate<>(TagKey.create(var1, var3), var4);
      }
   }
}
