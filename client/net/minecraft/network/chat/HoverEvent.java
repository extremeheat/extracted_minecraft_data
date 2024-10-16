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
   public static final Codec<HoverEvent> CODEC = Codec.withAlternative(
         HoverEvent.TypedHoverEvent.CODEC.codec(), HoverEvent.TypedHoverEvent.LEGACY_CODEC.codec()
      )
      .xmap(HoverEvent::new, var0 -> var0.event);
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
         "show_text", true, ComponentSerialization.CODEC, (var0, var1) -> DataResult.success(var0)
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
      public static final Codec<HoverEvent.Action<?>> CODEC = UNSAFE_CODEC.validate(HoverEvent.Action::filterForSerialization);
      private final String name;
      private final boolean allowFromServer;
      final MapCodec<HoverEvent.TypedHoverEvent<T>> codec;
      final MapCodec<HoverEvent.TypedHoverEvent<T>> legacyCodec;

      public Action(String var1, boolean var2, Codec<T> var3, final HoverEvent.LegacyConverter<T> var4) {
         super();
         this.name = var1;
         this.allowFromServer = var2;
         this.codec = var3.xmap(var1x -> new HoverEvent.TypedHoverEvent<>(this, (T)var1x), var0 -> var0.value).fieldOf("contents");
         this.legacyCodec = (new Codec<HoverEvent.TypedHoverEvent<T>>() {
            public <D> DataResult<Pair<HoverEvent.TypedHoverEvent<T>, D>> decode(DynamicOps<D> var1, D var2) {
               return ComponentSerialization.CODEC.decode(var1, var2).flatMap(var3 -> {
                  DataResult var4x;
                  if (var1 instanceof RegistryOps var5) {
                     var4x = var4.parse((Component)var3.getFirst(), var5);
                  } else {
                     var4x = var4.parse((Component)var3.getFirst(), null);
                  }

                  return var4x.map(var2xx -> Pair.of(new HoverEvent.TypedHoverEvent<>(Action.this, var2xx), var3.getSecond()));
               });
            }

            public <D> DataResult<D> encode(HoverEvent.TypedHoverEvent<T> var1, DynamicOps<D> var2, D var3) {
               return DataResult.error(() -> "Can't encode in legacy format");
            }
         }).fieldOf("value");
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
                  ComponentSerialization.CODEC.lenientOptionalFieldOf("name").forGetter(var0x -> var0x.name)
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

      public static DataResult<HoverEvent.EntityTooltipInfo> legacyCreate(Component var0, @Nullable RegistryOps<?> var1) {
         try {
            CompoundTag var2 = TagParser.parseTag(var0.getString());
            Object var3 = var1 != null ? var1.withParent(JsonOps.INSTANCE) : JsonOps.INSTANCE;
            DataResult var4 = ComponentSerialization.CODEC.parse((DynamicOps)var3, JsonParser.parseString(var2.getString("name")));
            EntityType var5 = BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.parse(var2.getString("type")));
            UUID var6 = UUID.fromString(var2.getString("id"));
            return var4.map(var2x -> new HoverEvent.EntityTooltipInfo(var5, var6, var2x));
         } catch (Exception var7) {
            return DataResult.error(() -> "Failed to parse tooltip: " + var7.getMessage());
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
      public static final Codec<HoverEvent.ItemStackInfo> FULL_CODEC = ItemStack.CODEC
         .xmap(HoverEvent.ItemStackInfo::new, HoverEvent.ItemStackInfo::getItemStack);
      private static final Codec<HoverEvent.ItemStackInfo> SIMPLE_CODEC = ItemStack.SIMPLE_ITEM_CODEC
         .xmap(HoverEvent.ItemStackInfo::new, HoverEvent.ItemStackInfo::getItemStack);
      public static final Codec<HoverEvent.ItemStackInfo> CODEC = Codec.withAlternative(FULL_CODEC, SIMPLE_CODEC);
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

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            HoverEvent.ItemStackInfo var2 = (HoverEvent.ItemStackInfo)var1;
            return this.count == var2.count && this.item.equals(var2.item) && this.components.equals(var2.components);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.item.hashCode();
         var1 = 31 * var1 + this.count;
         return 31 * var1 + this.components.hashCode();
      }

      public ItemStack getItemStack() {
         if (this.itemStack == null) {
            this.itemStack = new ItemStack(this.item, this.count, this.components);
         }

         return this.itemStack;
      }

      private static DataResult<HoverEvent.ItemStackInfo> legacyCreate(Component var0, @Nullable RegistryOps<?> var1) {
         try {
            CompoundTag var2 = TagParser.parseTag(var0.getString());
            Object var3 = var1 != null ? var1.withParent(NbtOps.INSTANCE) : NbtOps.INSTANCE;
            return ItemStack.CODEC.parse((DynamicOps)var3, var2).map(HoverEvent.ItemStackInfo::new);
         } catch (CommandSyntaxException var4) {
            return DataResult.error(() -> "Failed to parse item tag: " + var4.getMessage());
         }
      }
   }

   public interface LegacyConverter<T> {
      DataResult<T> parse(Component var1, @Nullable RegistryOps<?> var2);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
