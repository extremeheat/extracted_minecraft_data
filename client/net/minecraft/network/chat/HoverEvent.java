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
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

public interface HoverEvent {
   Codec<HoverEvent> CODEC = HoverEvent.Action.CODEC.dispatch("action", HoverEvent::action, (action) -> action.codec);

   Action action();

   public static record ShowText(Component value) implements HoverEvent {
      public static final MapCodec<ShowText> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ComponentSerialization.CODEC.fieldOf("value").forGetter(ShowText::value)).apply(i, ShowText::new));

      public ShowText {
         super();
      }

      public Action action() {
         return HoverEvent.Action.SHOW_TEXT;
      }
   }

   public static record ShowItem(ItemStackTemplate item) implements HoverEvent {
      public static final MapCodec<ShowItem> CODEC;

      public ShowItem {
         super();
      }

      public Action action() {
         return HoverEvent.Action.SHOW_ITEM;
      }

      static {
         CODEC = ItemStackTemplate.MAP_CODEC.xmap(ShowItem::new, ShowItem::item);
      }
   }

   public static record ShowEntity(EntityTooltipInfo entity) implements HoverEvent {
      public static final MapCodec<ShowEntity> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(HoverEvent.EntityTooltipInfo.CODEC.forGetter(ShowEntity::entity)).apply(i, ShowEntity::new));

      public ShowEntity {
         super();
      }

      public Action action() {
         return HoverEvent.Action.SHOW_ENTITY;
      }
   }

   public static class EntityTooltipInfo {
      public static final MapCodec<EntityTooltipInfo> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("id").forGetter((o) -> o.type), UUIDUtil.LENIENT_CODEC.fieldOf("uuid").forGetter((o) -> o.uuid), ComponentSerialization.CODEC.optionalFieldOf("name").forGetter((o) -> o.name)).apply(i, EntityTooltipInfo::new));
      public final EntityType<?> type;
      public final UUID uuid;
      public final Optional<Component> name;
      private @Nullable List<Component> linesCache;

      public EntityTooltipInfo(final EntityType<?> type, final UUID uuid, final @Nullable Component name) {
         this(type, uuid, Optional.ofNullable(name));
      }

      public EntityTooltipInfo(final EntityType<?> type, final UUID uuid, final Optional<Component> name) {
         super();
         this.type = type;
         this.uuid = uuid;
         this.name = name;
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

      public boolean equals(final Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            EntityTooltipInfo that = (EntityTooltipInfo)o;
            return this.type.equals(that.type) && this.uuid.equals(that.uuid) && this.name.equals(that.name);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.type.hashCode();
         result = 31 * result + this.uuid.hashCode();
         result = 31 * result + this.name.hashCode();
         return result;
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
      private final MapCodec<? extends HoverEvent> codec;

      private Action(final String name, final boolean allowFromServer, final MapCodec<? extends HoverEvent> codec) {
         this.name = name;
         this.allowFromServer = allowFromServer;
         this.codec = codec;
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

      private static DataResult<Action> filterForSerialization(final Action action) {
         return !action.isAllowedFromServer() ? DataResult.error(() -> "Action not allowed: " + String.valueOf(action)) : DataResult.success(action, Lifecycle.stable());
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY};
      }
   }
}
