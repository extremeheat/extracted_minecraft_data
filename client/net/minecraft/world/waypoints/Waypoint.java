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
   int MAX_RANGE = 60000000;
   AttributeModifier WAYPOINT_TRANSMIT_RANGE_HIDE_MODIFIER = new AttributeModifier(Identifier.withDefaultNamespace("waypoint_transmit_range_hide"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

   static Item.Properties addHideAttribute(Item.Properties var0) {
      return var0.component(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder().add(Attributes.WAYPOINT_TRANSMIT_RANGE, WAYPOINT_TRANSMIT_RANGE_HIDE_MODIFIER, EquipmentSlotGroup.HEAD, ItemAttributeModifiers.Display.hidden()).build());
   }

   public static class Icon {
      public static final Codec<Icon> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceKey.codec(WaypointStyleAssets.ROOT_ID).fieldOf("style").forGetter((var0x) -> var0x.style), ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color").forGetter((var0x) -> var0x.color)).apply(var0, Icon::new));
      public static final StreamCodec<ByteBuf, Icon> STREAM_CODEC;
      public static final Icon NULL;
      public ResourceKey<WaypointStyleAsset> style;
      public Optional<Integer> color;

      public Icon() {
         super();
         this.style = WaypointStyleAssets.DEFAULT;
         this.color = Optional.empty();
      }

      private Icon(ResourceKey<WaypointStyleAsset> var1, Optional<Integer> var2) {
         super();
         this.style = WaypointStyleAssets.DEFAULT;
         this.color = Optional.empty();
         this.style = var1;
         this.color = var2;
      }

      public boolean hasData() {
         return this.style != WaypointStyleAssets.DEFAULT || this.color.isPresent();
      }

      public Icon cloneAndAssignStyle(LivingEntity var1) {
         ResourceKey var2 = this.getOverrideStyle();
         Optional var3 = this.color.or(() -> Optional.ofNullable(var1.getTeam()).map((var0) -> var0.getColor().getColor()).map((var0) -> var0 == 0 ? -13619152 : var0));
         return var2 == this.style && var3.isEmpty() ? this : new Icon(var2, var3);
      }

      public void copyFrom(Icon var1) {
         this.color = var1.color;
         this.style = var1.style;
      }

      private ResourceKey<WaypointStyleAsset> getOverrideStyle() {
         return this.style != WaypointStyleAssets.DEFAULT ? this.style : WaypointStyleAssets.DEFAULT;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ResourceKey.streamCodec(WaypointStyleAssets.ROOT_ID), (var0) -> var0.style, ByteBufCodecs.optional(ByteBufCodecs.RGB_COLOR), (var0) -> var0.color, Icon::new);
         NULL = new Icon();
      }
   }
}
