package net.minecraft.network.chat;

import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HoverEvent {
   public static final Codec<HoverEvent> CODEC;
   private final TypedHoverEvent<?> event;

   public <T> HoverEvent(Action<T> var1, T var2) {
      this(new TypedHoverEvent(var1, var2));
   }

   private HoverEvent(TypedHoverEvent<?> var1) {
      super();
      this.event = var1;
   }

   public Action<?> getAction() {
      return this.event.action;
   }

   @Nullable
   public <T> T getValue(Action<T> var1) {
      return this.event.action == var1 ? var1.cast(this.event.value) : null;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 != null && this.getClass() == var1.getClass() ? ((HoverEvent)var1).event.equals(this.event) : false;
      }
   }

   public String toString() {
      return this.event.toString();
   }

   public int hashCode() {
      return this.event.hashCode();
   }

   static {
      CODEC = Codec.withAlternative(HoverEvent.TypedHoverEvent.CODEC.codec(), HoverEvent.TypedHoverEvent.LEGACY_CODEC.codec()).xmap(HoverEvent::new, (var0) -> {
         return var0.event;
      });
   }

   private static record TypedHoverEvent<T>(Action<T> action, T value) {
      final Action<T> action;
      final T value;
      public static final MapCodec<TypedHoverEvent<?>> CODEC;
      public static final MapCodec<TypedHoverEvent<?>> LEGACY_CODEC;

      TypedHoverEvent(Action<T> var1, T var2) {
         super();
         this.action = var1;
         this.value = var2;
      }

      public Action<T> action() {
         return this.action;
      }

      public T value() {
         return this.value;
      }

      static {
         CODEC = HoverEvent.Action.CODEC.dispatchMap("action", TypedHoverEvent::action, (var0) -> {
            return var0.codec;
         });
         LEGACY_CODEC = HoverEvent.Action.CODEC.dispatchMap("action", TypedHoverEvent::action, (var0) -> {
            return var0.legacyCodec;
         });
      }
   }

   public static class Action<T> implements StringRepresentable {
      public static final Action<Component> SHOW_TEXT;
      public static final Action<ItemStackInfo> SHOW_ITEM;
      public static final Action<EntityTooltipInfo> SHOW_ENTITY;
      public static final Codec<Action<?>> UNSAFE_CODEC;
      public static final Codec<Action<?>> CODEC;
      private final String name;
      private final boolean allowFromServer;
      final MapCodec<TypedHoverEvent<T>> codec;
      final MapCodec<TypedHoverEvent<T>> legacyCodec;

      public Action(String var1, boolean var2, Codec<T> var3, final LegacyConverter<T> var4) {
         super();
         this.name = var1;
         this.allowFromServer = var2;
         this.codec = var3.xmap((var1x) -> {
            return new TypedHoverEvent(this, var1x);
         }, (var0) -> {
            return var0.value;
         }).fieldOf("contents");
         this.legacyCodec = (new Codec<TypedHoverEvent<T>>() {
            public <D> DataResult<Pair<TypedHoverEvent<T>, D>> decode(DynamicOps<D> var1, D var2) {
               return ComponentSerialization.CODEC.decode(var1, var2).flatMap((var3) -> {
                  DataResult var4x;
                  if (var1 instanceof RegistryOps var5) {
                     var4x = var4.parse((Component)var3.getFirst(), var5);
                  } else {
                     var4x = var4.parse((Component)var3.getFirst(), (RegistryOps)null);
                  }

                  return var4x.map((var2) -> {
                     return Pair.of(new TypedHoverEvent(Action.this, var2), var3.getSecond());
                  });
               });
            }

            public <D> DataResult<D> encode(TypedHoverEvent<T> var1, DynamicOps<D> var2, D var3) {
               return DataResult.error(() -> {
                  return "Can't encode in legacy format";
               });
            }

            // $FF: synthetic method
            public DataResult encode(final Object var1, final DynamicOps var2, final Object var3) {
               return this.encode((TypedHoverEvent)var1, var2, var3);
            }
         }).fieldOf("value");
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getSerializedName() {
         return this.name;
      }

      T cast(Object var1) {
         return var1;
      }

      public String toString() {
         return "<action " + this.name + ">";
      }

      private static DataResult<Action<?>> filterForSerialization(@Nullable Action<?> var0) {
         if (var0 == null) {
            return DataResult.error(() -> {
               return "Unknown action";
            });
         } else {
            return !var0.isAllowedFromServer() ? DataResult.error(() -> {
               return "Action not allowed: " + String.valueOf(var0);
            }) : DataResult.success(var0, Lifecycle.stable());
         }
      }

      static {
         SHOW_TEXT = new Action("show_text", true, ComponentSerialization.CODEC, (var0, var1) -> {
            return DataResult.success(var0);
         });
         SHOW_ITEM = new Action("show_item", true, HoverEvent.ItemStackInfo.CODEC, ItemStackInfo::legacyCreate);
         SHOW_ENTITY = new Action("show_entity", true, HoverEvent.EntityTooltipInfo.CODEC, EntityTooltipInfo::legacyCreate);
         UNSAFE_CODEC = StringRepresentable.fromValues(() -> {
            return new Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY};
         });
         CODEC = UNSAFE_CODEC.validate(Action::filterForSerialization);
      }
   }

   public interface LegacyConverter<T> {
      DataResult<T> parse(Component var1, @Nullable RegistryOps<?> var2);
   }

   public static class ItemStackInfo {
      public static final Codec<ItemStackInfo> FULL_CODEC;
      private static final Codec<ItemStackInfo> SIMPLE_CODEC;
      public static final Codec<ItemStackInfo> CODEC;
      private final Holder<Item> item;
      private final int count;
      private final DataComponentPatch components;
      @Nullable
      private ItemStack itemStack;

      ItemStackInfo(Holder<Item> var1, int var2, DataComponentPatch var3) {
         super();
         this.item = var1;
         this.count = var2;
         this.components = var3;
      }

      public ItemStackInfo(ItemStack var1) {
         this(var1.getItemHolder(), var1.getCount(), var1.getComponentsPatch());
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ItemStackInfo var2 = (ItemStackInfo)var1;
            return this.count == var2.count && this.item.equals(var2.item) && this.components.equals(var2.components);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int var1 = this.item.hashCode();
         var1 = 31 * var1 + this.count;
         var1 = 31 * var1 + this.components.hashCode();
         return var1;
      }

      public ItemStack getItemStack() {
         if (this.itemStack == null) {
            this.itemStack = new ItemStack(this.item, this.count, this.components);
         }

         return this.itemStack;
      }

      private static DataResult<ItemStackInfo> legacyCreate(Component var0, @Nullable RegistryOps<?> var1) {
         try {
            CompoundTag var2 = TagParser.parseTag(var0.getString());
            Object var3 = var1 != null ? var1.withParent(NbtOps.INSTANCE) : NbtOps.INSTANCE;
            return ItemStack.CODEC.parse((DynamicOps)var3, var2).map(ItemStackInfo::new);
         } catch (CommandSyntaxException var4) {
            return DataResult.error(() -> {
               return "Failed to parse item tag: " + var4.getMessage();
            });
         }
      }

      static {
         FULL_CODEC = ItemStack.CODEC.xmap(ItemStackInfo::new, ItemStackInfo::getItemStack);
         SIMPLE_CODEC = ItemStack.SIMPLE_ITEM_CODEC.xmap(ItemStackInfo::new, ItemStackInfo::getItemStack);
         CODEC = Codec.withAlternative(FULL_CODEC, SIMPLE_CODEC);
      }
   }

   public static class EntityTooltipInfo {
      public static final Codec<EntityTooltipInfo> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter((var0x) -> {
            return var0x.type;
         }), UUIDUtil.LENIENT_CODEC.fieldOf("id").forGetter((var0x) -> {
            return var0x.id;
         }), ComponentSerialization.CODEC.lenientOptionalFieldOf("name").forGetter((var0x) -> {
            return var0x.name;
         })).apply(var0, EntityTooltipInfo::new);
      });
      public final EntityType<?> type;
      public final UUID id;
      public final Optional<Component> name;
      @Nullable
      private List<Component> linesCache;

      public EntityTooltipInfo(EntityType<?> var1, UUID var2, @Nullable Component var3) {
         this(var1, var2, Optional.ofNullable(var3));
      }

      public EntityTooltipInfo(EntityType<?> var1, UUID var2, Optional<Component> var3) {
         super();
         this.type = var1;
         this.id = var2;
         this.name = var3;
      }

      public static DataResult<EntityTooltipInfo> legacyCreate(Component var0, @Nullable RegistryOps<?> var1) {
         try {
            CompoundTag var2 = TagParser.parseTag(var0.getString());
            Object var3 = var1 != null ? var1.withParent(JsonOps.INSTANCE) : JsonOps.INSTANCE;
            DataResult var4 = ComponentSerialization.CODEC.parse((DynamicOps)var3, JsonParser.parseString(var2.getString("name")));
            EntityType var5 = (EntityType)BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.parse(var2.getString("type")));
            UUID var6 = UUID.fromString(var2.getString("id"));
            return var4.map((var2x) -> {
               return new EntityTooltipInfo(var5, var6, var2x);
            });
         } catch (Exception var7) {
            return DataResult.error(() -> {
               return "Failed to parse tooltip: " + var7.getMessage();
            });
         }
      }

      public List<Component> getTooltipLines() {
         if (this.linesCache == null) {
            this.linesCache = new ArrayList();
            Optional var10000 = this.name;
            List var10001 = this.linesCache;
            Objects.requireNonNull(var10001);
            var10000.ifPresent(var10001::add);
            this.linesCache.add(Component.translatable("gui.entity_tooltip.type", this.type.getDescription()));
            this.linesCache.add(Component.literal(this.id.toString()));
         }

         return this.linesCache;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            EntityTooltipInfo var2 = (EntityTooltipInfo)var1;
            return this.type.equals(var2.type) && this.id.equals(var2.id) && this.name.equals(var2.name);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int var1 = this.type.hashCode();
         var1 = 31 * var1 + this.id.hashCode();
         var1 = 31 * var1 + this.name.hashCode();
         return var1;
      }
   }
}
