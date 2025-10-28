package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface HoverEvent {
   Codec<HoverEvent> CODEC = HoverEvent.Action.CODEC.dispatch("action", HoverEvent::action, (var0) -> var0.codec);

   Action action();

   public static record ShowText(Component value) implements HoverEvent {
      public static final MapCodec<ShowText> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ComponentSerialization.CODEC.fieldOf("value").forGetter(ShowText::value)).apply(var0, ShowText::new));

      public ShowText(Component var1) {
         super();
         this.value = var1;
      }

      public Action action() {
         return HoverEvent.Action.SHOW_TEXT;
      }
   }

   public static record ShowItem(ItemStack item) implements HoverEvent {
      public static final MapCodec<ShowItem> CODEC;

      public ShowItem(ItemStack var1) {
         super();
         var1 = var1.copy();
         this.item = var1;
      }

      public Action action() {
         return HoverEvent.Action.SHOW_ITEM;
      }

      public boolean equals(Object var1) {
         boolean var10000;
         if (var1 instanceof ShowItem var2) {
            if (ItemStack.matches(this.item, var2.item)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }

      public int hashCode() {
         return ItemStack.hashItemAndComponents(this.item);
      }

      static {
         CODEC = ItemStack.MAP_CODEC.xmap(ShowItem::new, ShowItem::item);
      }
   }

   public static record ShowEntity(EntityTooltipInfo entity) implements HoverEvent {
      public static final MapCodec<ShowEntity> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(HoverEvent.EntityTooltipInfo.CODEC.forGetter(ShowEntity::entity)).apply(var0, ShowEntity::new));

      public ShowEntity(EntityTooltipInfo var1) {
         super();
         this.entity = var1;
      }

      public Action action() {
         return HoverEvent.Action.SHOW_ENTITY;
      }
   }

   public static class EntityTooltipInfo {
      public static final MapCodec<EntityTooltipInfo> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("id").forGetter((var0x) -> var0x.type), UUIDUtil.LENIENT_CODEC.fieldOf("uuid").forGetter((var0x) -> var0x.uuid), ComponentSerialization.CODEC.optionalFieldOf("name").forGetter((var0x) -> var0x.name)).apply(var0, EntityTooltipInfo::new));
      public final EntityType<?> type;
      public final UUID uuid;
      public final Optional<Component> name;
      private @Nullable List<Component> linesCache;

      public EntityTooltipInfo(EntityType<?> var1, UUID var2, @Nullable Component var3) {
         this(var1, var2, Optional.ofNullable(var3));
      }

      public EntityTooltipInfo(EntityType<?> var1, UUID var2, Optional<Component> var3) {
         super();
         this.type = var1;
         this.uuid = var2;
         this.name = var3;
      }

      public List<Component> getTooltipLines() {
         if (this.linesCache == null) {
            this.linesCache = new ArrayList();
            Optional var10000 = this.name;
            List var10001 = this.linesCache;
            Objects.requireNonNull(var10001);
            var10000.ifPresent(var10001::add);
            this.linesCache.add(Component.translatable("gui.entity_tooltip.type", this.type.getDescription()));
            this.linesCache.add(Component.literal(this.uuid.toString()));
         }

         return this.linesCache;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            EntityTooltipInfo var2 = (EntityTooltipInfo)var1;
            return this.type.equals(var2.type) && this.uuid.equals(var2.uuid) && this.name.equals(var2.name);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int var1 = this.type.hashCode();
         var1 = 31 * var1 + this.uuid.hashCode();
         var1 = 31 * var1 + this.name.hashCode();
         return var1;
      }
   }

   public static enum Action implements StringRepresentable {
      SHOW_TEXT("show_text", true, HoverEvent.ShowText.CODEC),
      SHOW_ITEM("show_item", true, HoverEvent.ShowItem.CODEC),
      SHOW_ENTITY("show_entity", true, HoverEvent.ShowEntity.CODEC);

      public static final Codec<Action> UNSAFE_CODEC = StringRepresentable.<Action>fromValues(Action::values);
      public static final Codec<Action> CODEC = UNSAFE_CODEC.validate(Action::filterForSerialization);
      private final String name;
      private final boolean allowFromServer;
      final MapCodec<? extends HoverEvent> codec;

      private Action(final String var3, final boolean var4, final MapCodec<? extends HoverEvent> var5) {
         this.name = var3;
         this.allowFromServer = var4;
         this.codec = var5;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getSerializedName() {
         return this.name;
      }

      public String toString() {
         return "<action " + this.name + ">";
      }

      private static DataResult<Action> filterForSerialization(Action var0) {
         return !var0.isAllowedFromServer() ? DataResult.error(() -> "Action not allowed: " + String.valueOf(var0)) : DataResult.success(var0, Lifecycle.stable());
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY};
      }
   }
}
