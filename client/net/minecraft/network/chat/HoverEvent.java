package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoverEvent {
   static final Logger LOGGER = LogManager.getLogger();
   private final HoverEvent.Action<?> action;
   private final Object value;

   public <T> HoverEvent(HoverEvent.Action<T> var1, T var2) {
      super();
      this.action = var1;
      this.value = var2;
   }

   public HoverEvent.Action<?> getAction() {
      return this.action;
   }

   @Nullable
   public <T> T getValue(HoverEvent.Action<T> var1) {
      return this.action == var1 ? var1.cast(this.value) : null;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         HoverEvent var2 = (HoverEvent)var1;
         return this.action == var2.action && Objects.equals(this.value, var2.value);
      } else {
         return false;
      }
   }

   public String toString() {
      return "HoverEvent{action=" + this.action + ", value='" + this.value + "'}";
   }

   public int hashCode() {
      int var1 = this.action.hashCode();
      var1 = 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
      return var1;
   }

   @Nullable
   public static HoverEvent deserialize(JsonObject var0) {
      String var1 = GsonHelper.getAsString(var0, "action", (String)null);
      if (var1 == null) {
         return null;
      } else {
         HoverEvent.Action var2 = HoverEvent.Action.getByName(var1);
         if (var2 == null) {
            return null;
         } else {
            JsonElement var3 = var0.get("contents");
            if (var3 != null) {
               return var2.deserialize(var3);
            } else {
               MutableComponent var4 = Component.Serializer.fromJson(var0.get("value"));
               return var4 != null ? var2.deserializeFromLegacy(var4) : null;
            }
         }
      }
   }

   public JsonObject serialize() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("action", this.action.getName());
      var1.add("contents", this.action.serializeArg(this.value));
      return var1;
   }

   public static class Action<T> {
      public static final HoverEvent.Action<Component> SHOW_TEXT = new HoverEvent.Action("show_text", true, Component.Serializer::fromJson, Component.Serializer::toJsonTree, Function.identity());
      public static final HoverEvent.Action<HoverEvent.ItemStackInfo> SHOW_ITEM = new HoverEvent.Action("show_item", true, HoverEvent.ItemStackInfo::create, HoverEvent.ItemStackInfo::serialize, HoverEvent.ItemStackInfo::create);
      public static final HoverEvent.Action<HoverEvent.EntityTooltipInfo> SHOW_ENTITY = new HoverEvent.Action("show_entity", true, HoverEvent.EntityTooltipInfo::create, HoverEvent.EntityTooltipInfo::serialize, HoverEvent.EntityTooltipInfo::create);
      private static final Map<String, HoverEvent.Action<?>> LOOKUP;
      private final String name;
      private final boolean allowFromServer;
      private final Function<JsonElement, T> argDeserializer;
      private final Function<T, JsonElement> argSerializer;
      private final Function<Component, T> legacyArgDeserializer;

      public Action(String var1, boolean var2, Function<JsonElement, T> var3, Function<T, JsonElement> var4, Function<Component, T> var5) {
         super();
         this.name = var1;
         this.allowFromServer = var2;
         this.argDeserializer = var3;
         this.argSerializer = var4;
         this.legacyArgDeserializer = var5;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getName() {
         return this.name;
      }

      @Nullable
      public static HoverEvent.Action<?> getByName(String var0) {
         return (HoverEvent.Action)LOOKUP.get(var0);
      }

      T cast(Object var1) {
         return var1;
      }

      @Nullable
      public HoverEvent deserialize(JsonElement var1) {
         Object var2 = this.argDeserializer.apply(var1);
         return var2 == null ? null : new HoverEvent(this, var2);
      }

      @Nullable
      public HoverEvent deserializeFromLegacy(Component var1) {
         Object var2 = this.legacyArgDeserializer.apply(var1);
         return var2 == null ? null : new HoverEvent(this, var2);
      }

      public JsonElement serializeArg(Object var1) {
         return (JsonElement)this.argSerializer.apply(this.cast(var1));
      }

      public String toString() {
         return "<action " + this.name + ">";
      }

      static {
         LOOKUP = (Map)Stream.of(SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY).collect(ImmutableMap.toImmutableMap(HoverEvent.Action::getName, (var0) -> {
            return var0;
         }));
      }
   }

   public static class ItemStackInfo {
      private final Item item;
      private final int count;
      @Nullable
      private final CompoundTag tag;
      @Nullable
      private ItemStack itemStack;

      ItemStackInfo(Item var1, int var2, @Nullable CompoundTag var3) {
         super();
         this.item = var1;
         this.count = var2;
         this.tag = var3;
      }

      public ItemStackInfo(ItemStack var1) {
         this(var1.getItem(), var1.getCount(), var1.getTag() != null ? var1.getTag().copy() : null);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            HoverEvent.ItemStackInfo var2 = (HoverEvent.ItemStackInfo)var1;
            return this.count == var2.count && this.item.equals(var2.item) && Objects.equals(this.tag, var2.tag);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int var1 = this.item.hashCode();
         var1 = 31 * var1 + this.count;
         var1 = 31 * var1 + (this.tag != null ? this.tag.hashCode() : 0);
         return var1;
      }

      public ItemStack getItemStack() {
         if (this.itemStack == null) {
            this.itemStack = new ItemStack(this.item, this.count);
            if (this.tag != null) {
               this.itemStack.setTag(this.tag);
            }
         }

         return this.itemStack;
      }

      private static HoverEvent.ItemStackInfo create(JsonElement var0) {
         if (var0.isJsonPrimitive()) {
            return new HoverEvent.ItemStackInfo((Item)Registry.ITEM.get(new ResourceLocation(var0.getAsString())), 1, (CompoundTag)null);
         } else {
            JsonObject var1 = GsonHelper.convertToJsonObject(var0, "item");
            Item var2 = (Item)Registry.ITEM.get(new ResourceLocation(GsonHelper.getAsString(var1, "id")));
            int var3 = GsonHelper.getAsInt(var1, "count", 1);
            if (var1.has("tag")) {
               String var4 = GsonHelper.getAsString(var1, "tag");

               try {
                  CompoundTag var5 = TagParser.parseTag(var4);
                  return new HoverEvent.ItemStackInfo(var2, var3, var5);
               } catch (CommandSyntaxException var6) {
                  HoverEvent.LOGGER.warn("Failed to parse tag: {}", var4, var6);
               }
            }

            return new HoverEvent.ItemStackInfo(var2, var3, (CompoundTag)null);
         }
      }

      @Nullable
      private static HoverEvent.ItemStackInfo create(Component var0) {
         try {
            CompoundTag var1 = TagParser.parseTag(var0.getString());
            return new HoverEvent.ItemStackInfo(ItemStack.method_85(var1));
         } catch (CommandSyntaxException var2) {
            HoverEvent.LOGGER.warn("Failed to parse item tag: {}", var0, var2);
            return null;
         }
      }

      private JsonElement serialize() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("id", Registry.ITEM.getKey(this.item).toString());
         if (this.count != 1) {
            var1.addProperty("count", this.count);
         }

         if (this.tag != null) {
            var1.addProperty("tag", this.tag.toString());
         }

         return var1;
      }
   }

   public static class EntityTooltipInfo {
      public final EntityType<?> type;
      // $FF: renamed from: id java.util.UUID
      public final UUID field_310;
      @Nullable
      public final Component name;
      @Nullable
      private List<Component> linesCache;

      public EntityTooltipInfo(EntityType<?> var1, UUID var2, @Nullable Component var3) {
         super();
         this.type = var1;
         this.field_310 = var2;
         this.name = var3;
      }

      @Nullable
      public static HoverEvent.EntityTooltipInfo create(JsonElement var0) {
         if (!var0.isJsonObject()) {
            return null;
         } else {
            JsonObject var1 = var0.getAsJsonObject();
            EntityType var2 = (EntityType)Registry.ENTITY_TYPE.get(new ResourceLocation(GsonHelper.getAsString(var1, "type")));
            UUID var3 = UUID.fromString(GsonHelper.getAsString(var1, "id"));
            MutableComponent var4 = Component.Serializer.fromJson(var1.get("name"));
            return new HoverEvent.EntityTooltipInfo(var2, var3, var4);
         }
      }

      @Nullable
      public static HoverEvent.EntityTooltipInfo create(Component var0) {
         try {
            CompoundTag var1 = TagParser.parseTag(var0.getString());
            MutableComponent var2 = Component.Serializer.fromJson(var1.getString("name"));
            EntityType var3 = (EntityType)Registry.ENTITY_TYPE.get(new ResourceLocation(var1.getString("type")));
            UUID var4 = UUID.fromString(var1.getString("id"));
            return new HoverEvent.EntityTooltipInfo(var3, var4, var2);
         } catch (Exception var5) {
            return null;
         }
      }

      public JsonElement serialize() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("type", Registry.ENTITY_TYPE.getKey(this.type).toString());
         var1.addProperty("id", this.field_310.toString());
         if (this.name != null) {
            var1.add("name", Component.Serializer.toJsonTree(this.name));
         }

         return var1;
      }

      public List<Component> getTooltipLines() {
         if (this.linesCache == null) {
            this.linesCache = Lists.newArrayList();
            if (this.name != null) {
               this.linesCache.add(this.name);
            }

            this.linesCache.add(new TranslatableComponent("gui.entity_tooltip.type", new Object[]{this.type.getDescription()}));
            this.linesCache.add(new TextComponent(this.field_310.toString()));
         }

         return this.linesCache;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            HoverEvent.EntityTooltipInfo var2 = (HoverEvent.EntityTooltipInfo)var1;
            return this.type.equals(var2.type) && this.field_310.equals(var2.field_310) && Objects.equals(this.name, var2.name);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int var1 = this.type.hashCode();
         var1 = 31 * var1 + this.field_310.hashCode();
         var1 = 31 * var1 + (this.name != null ? this.name.hashCode() : 0);
         return var1;
      }
   }
}
