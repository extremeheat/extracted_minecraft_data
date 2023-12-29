package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HoverEvent {
   public static final Codec<HoverEvent> CODEC = Codec.either(HoverEvent.TypedHoverEvent.CODEC.codec(), HoverEvent.TypedHoverEvent.LEGACY_CODEC.codec())
      .xmap(var0 -> new HoverEvent((HoverEvent.TypedHoverEvent<?>)var0.map(var0x -> var0x, var0x -> var0x)), var0 -> Either.left(var0.event));
   private final HoverEvent.TypedHoverEvent<?> event;

   public <T> HoverEvent(HoverEvent.Action<T> var1, T var2) {
      this(new HoverEvent.TypedHoverEvent<>(var1, var2));
   }

   private HoverEvent(HoverEvent.TypedHoverEvent<?> var1) {
      super();
      this.event = var1;
   }

   public HoverEvent.Action<?> getAction() {
      return this.event.action;
   }

   @Nullable
   public <T> T getValue(HoverEvent.Action<T> var1) {
      return (T)(this.event.action == var1 ? var1.cast(this.event.value) : null);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 != null && this.getClass() == var1.getClass() ? ((HoverEvent)var1).event.equals(this.event) : false;
      }
   }

   @Override
   public String toString() {
      return this.event.toString();
   }

   @Override
   public int hashCode() {
      return this.event.hashCode();
   }

   public static class Action<T> implements StringRepresentable {
      public static final HoverEvent.Action<Component> SHOW_TEXT = new HoverEvent.Action<>(
         "show_text", true, ComponentSerialization.CODEC, DataResult::success
      );
      public static final HoverEvent.Action<HoverEvent.ItemStackInfo> SHOW_ITEM = new HoverEvent.Action<>(
         "show_item", true, HoverEvent.ItemStackInfo.CODEC, HoverEvent.ItemStackInfo::legacyCreate
      );
      public static final HoverEvent.Action<HoverEvent.EntityTooltipInfo> SHOW_ENTITY = new HoverEvent.Action<>(
         "show_entity", true, HoverEvent.EntityTooltipInfo.CODEC, HoverEvent.EntityTooltipInfo::legacyCreate
      );
      public static final Codec<HoverEvent.Action<?>> UNSAFE_CODEC = StringRepresentable.fromValues(
         () -> new HoverEvent.Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY}
      );
      public static final Codec<HoverEvent.Action<?>> CODEC = ExtraCodecs.validate(UNSAFE_CODEC, HoverEvent.Action::filterForSerialization);
      private final String name;
      private final boolean allowFromServer;
      final Codec<HoverEvent.TypedHoverEvent<T>> codec;
      final Codec<HoverEvent.TypedHoverEvent<T>> legacyCodec;

      public Action(String var1, boolean var2, Codec<T> var3, Function<Component, DataResult<T>> var4) {
         super();
         this.name = var1;
         this.allowFromServer = var2;
         this.codec = var3.xmap(var1x -> new HoverEvent.TypedHoverEvent<>(this, (T)var1x), var0 -> var0.value).fieldOf("contents").codec();
         this.legacyCodec = Codec.of(
            Encoder.error("Can't encode in legacy format"),
            ComponentSerialization.CODEC.flatMap(var4).map(var1x -> new HoverEvent.TypedHoverEvent<>(this, (T)var1x))
         );
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      T cast(Object var1) {
         return (T)var1;
      }

      @Override
      public String toString() {
         return "<action " + this.name + ">";
      }

      private static DataResult<HoverEvent.Action<?>> filterForSerialization(@Nullable HoverEvent.Action<?> var0) {
         if (var0 == null) {
            return DataResult.error(() -> "Unknown action");
         } else {
            return !var0.isAllowedFromServer() ? DataResult.error(() -> "Action not allowed: " + var0) : DataResult.success(var0, Lifecycle.stable());
         }
      }
   }

   public static class EntityTooltipInfo {
      public static final Codec<HoverEvent.EntityTooltipInfo> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(var0x -> var0x.type),
                  UUIDUtil.LENIENT_CODEC.fieldOf("id").forGetter(var0x -> var0x.id),
                  ExtraCodecs.strictOptionalField(ComponentSerialization.CODEC, "name").forGetter(var0x -> var0x.name)
               )
               .apply(var0, HoverEvent.EntityTooltipInfo::new)
      );
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

      public static DataResult<HoverEvent.EntityTooltipInfo> legacyCreate(Component var0) {
         try {
            CompoundTag var1 = TagParser.parseTag(var0.getString());
            MutableComponent var2 = Component.Serializer.fromJson(var1.getString("name"));
            EntityType var3 = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(var1.getString("type")));
            UUID var4 = UUID.fromString(var1.getString("id"));
            return DataResult.success(new HoverEvent.EntityTooltipInfo(var3, var4, var2));
         } catch (Exception var5) {
            return DataResult.error(() -> "Failed to parse tooltip: " + var5.getMessage());
         }
      }

      public List<Component> getTooltipLines() {
         if (this.linesCache == null) {
            this.linesCache = new ArrayList<>();
            this.name.ifPresent(this.linesCache::add);
            this.linesCache.add(Component.translatable("gui.entity_tooltip.type", this.type.getDescription()));
            this.linesCache.add(Component.literal(this.id.toString()));
         }

         return this.linesCache;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            HoverEvent.EntityTooltipInfo var2 = (HoverEvent.EntityTooltipInfo)var1;
            return this.type.equals(var2.type) && this.id.equals(var2.id) && this.name.equals(var2.name);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.type.hashCode();
         var1 = 31 * var1 + this.id.hashCode();
         return 31 * var1 + this.name.hashCode();
      }
   }

   public static class ItemStackInfo {
      public static final Codec<HoverEvent.ItemStackInfo> FULL_CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(var0x -> var0x.item),
                  ExtraCodecs.strictOptionalField(Codec.INT, "count", 1).forGetter(var0x -> var0x.count),
                  ExtraCodecs.strictOptionalField(TagParser.AS_CODEC, "tag").forGetter(var0x -> var0x.tag)
               )
               .apply(var0, HoverEvent.ItemStackInfo::new)
      );
      public static final Codec<HoverEvent.ItemStackInfo> CODEC = Codec.either(BuiltInRegistries.ITEM.byNameCodec(), FULL_CODEC)
         .xmap(var0 -> (HoverEvent.ItemStackInfo)var0.map(var0x -> new HoverEvent.ItemStackInfo(var0x, 1, Optional.empty()), var0x -> var0x), Either::right);
      private final Item item;
      private final int count;
      private final Optional<CompoundTag> tag;
      @Nullable
      private ItemStack itemStack;

      ItemStackInfo(Item var1, int var2, @Nullable CompoundTag var3) {
         this(var1, var2, Optional.ofNullable(var3));
      }

      ItemStackInfo(Item var1, int var2, Optional<CompoundTag> var3) {
         super();
         this.item = var1;
         this.count = var2;
         this.tag = var3;
      }

      public ItemStackInfo(ItemStack var1) {
         this(var1.getItem(), var1.getCount(), var1.getTag() != null ? Optional.of(var1.getTag().copy()) : Optional.empty());
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            HoverEvent.ItemStackInfo var2 = (HoverEvent.ItemStackInfo)var1;
            return this.count == var2.count && this.item.equals(var2.item) && this.tag.equals(var2.tag);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.item.hashCode();
         var1 = 31 * var1 + this.count;
         return 31 * var1 + this.tag.hashCode();
      }

      public ItemStack getItemStack() {
         if (this.itemStack == null) {
            this.itemStack = new ItemStack(this.item, this.count);
            this.tag.ifPresent(this.itemStack::setTag);
         }

         return this.itemStack;
      }

      private static DataResult<HoverEvent.ItemStackInfo> legacyCreate(Component var0) {
         try {
            CompoundTag var1 = TagParser.parseTag(var0.getString());
            return DataResult.success(new HoverEvent.ItemStackInfo(ItemStack.of(var1)));
         } catch (CommandSyntaxException var2) {
            return DataResult.error(() -> "Failed to parse item tag: " + var2.getMessage());
         }
      }
   }

   static record TypedHoverEvent<T>(HoverEvent.Action<T> c, T d) {
      final HoverEvent.Action<T> action;
      final T value;
      public static final MapCodec<HoverEvent.TypedHoverEvent<?>> CODEC = HoverEvent.Action.CODEC
         .dispatchMap("action", HoverEvent.TypedHoverEvent::action, var0 -> var0.codec);
      public static final MapCodec<HoverEvent.TypedHoverEvent<?>> LEGACY_CODEC = HoverEvent.Action.CODEC
         .dispatchMap("action", HoverEvent.TypedHoverEvent::action, var0 -> var0.legacyCodec);

      TypedHoverEvent(HoverEvent.Action<T> var1, T var2) {
         super();
         this.action = var1;
         this.value = (T)var2;
      }
   }
}
