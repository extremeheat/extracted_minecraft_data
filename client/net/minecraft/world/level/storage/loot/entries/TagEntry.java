package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class TagEntry extends LootPoolSingletonContainer {
   final TagKey<Item> tag;
   final boolean expand;

   TagEntry(TagKey<Item> var1, boolean var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
      super(var3, var4, var5, var6);
      this.tag = var1;
      this.expand = var2;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.TAG;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      Registry.ITEM.getTagOrEmpty(this.tag).forEach((var1x) -> {
         var1.accept(new ItemStack(var1x));
      });
   }

   private boolean expandTag(LootContext var1, Consumer<LootPoolEntry> var2) {
      if (!this.canRun(var1)) {
         return false;
      } else {
         Iterator var3 = Registry.ITEM.getTagOrEmpty(this.tag).iterator();

         while(var3.hasNext()) {
            final Holder var4 = (Holder)var3.next();
            var2.accept(new LootPoolSingletonContainer.EntryBase() {
               public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
                  var1.accept(new ItemStack(var4));
               }
            });
         }

         return true;
      }
   }

   public boolean expand(LootContext var1, Consumer<LootPoolEntry> var2) {
      return this.expand ? this.expandTag(var1, var2) : super.expand(var1, var2);
   }

   public static LootPoolSingletonContainer.Builder<?> tagContents(TagKey<Item> var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new TagEntry(var0, false, var1, var2, var3, var4);
      });
   }

   public static LootPoolSingletonContainer.Builder<?> expandTag(TagKey<Item> var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new TagEntry(var0, true, var1, var2, var3, var4);
      });
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer<TagEntry> {
      public Serializer() {
         super();
      }

      public void serializeCustom(JsonObject var1, TagEntry var2, JsonSerializationContext var3) {
         super.serializeCustom(var1, (LootPoolSingletonContainer)var2, var3);
         var1.addProperty("name", var2.tag.location().toString());
         var1.addProperty("expand", var2.expand);
      }

      protected TagEntry deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         ResourceLocation var7 = new ResourceLocation(GsonHelper.getAsString(var1, "name"));
         TagKey var8 = TagKey.create(Registry.ITEM_REGISTRY, var7);
         boolean var9 = GsonHelper.getAsBoolean(var1, "expand");
         return new TagEntry(var8, var9, var3, var4, var5, var6);
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
