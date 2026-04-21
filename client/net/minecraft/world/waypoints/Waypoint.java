package net.minecraft.world.waypoints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface Waypoint {
   AttributeModifier WAYPOINT_TRANSMIT_RANGE_HIDE_MODIFIER = new AttributeModifier(Identifier.withDefaultNamespace("waypoint_transmit_range_hide"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

   static Item.Properties addHideAttribute(final Item.Properties properties) {
      return properties.component(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder().add(Attributes.WAYPOINT_TRANSMIT_RANGE, WAYPOINT_TRANSMIT_RANGE_HIDE_MODIFIER, EquipmentSlotGroup.HEAD, ItemAttributeModifiers.Display.hidden()).build());
   }

   public static class Icon {
      public static final Codec<Icon> CODEC = RecordCodecBuilder.create((i) -> i.group(ResourceKey.codec(WaypointStyleAssets.ROOT_ID).fieldOf("style").forGetter((icon) -> icon.style), ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color").forGetter((icon) -> icon.color)).apply(i, Icon::new));
      public static final StreamCodec<ByteBuf, Icon> STREAM_CODEC;
      public static final Icon NULL;
      public ResourceKey<WaypointStyleAsset> style;
      public Optional<Integer> color;

      public Icon() {
         super();
         this.style = WaypointStyleAssets.DEFAULT;
         this.color = Optional.empty();
      }

      private Icon(final ResourceKey<WaypointStyleAsset> style, final Optional<Integer> color) {
         super();
         this.style = WaypointStyleAssets.DEFAULT;
         this.color = Optional.empty();
         this.style = style;
         this.color = color;
      }

      public boolean hasData() {
         return this.style != WaypointStyleAssets.DEFAULT || this.color.isPresent();
      }

      public Icon cloneAndAssignStyle(final LivingEntity livingEntity) {
         ResourceKey<WaypointStyleAsset> overrideStyle = this.getOverrideStyle();
         Optional<Integer> colorOverride = this.color.or(() -> Optional.ofNullable(livingEntity.getTeam()).map((t) -> t.getColor().getColor()).map((teamColor) -> teamColor == 0 ? -13619152 : teamColor));
         return overrideStyle == this.style && colorOverride.isEmpty() ? this : new Icon(overrideStyle, colorOverride);
      }

      public void copyFrom(final Icon other) {
         this.color = other.color;
         this.style = other.style;
      }

      private ResourceKey<WaypointStyleAsset> getOverrideStyle() {
         return this.style != WaypointStyleAssets.DEFAULT ? this.style : WaypointStyleAssets.DEFAULT;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ResourceKey.streamCodec(WaypointStyleAssets.ROOT_ID), (icon) -> icon.style, ByteBufCodecs.optional(ByteBufCodecs.RGB_COLOR), (icon) -> icon.color, Icon::new);
         NULL = new Icon();
      }
   }
}
