package net.minecraft.world.waypoints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface Waypoint {
   int MAX_RANGE = 60000000;
   AttributeModifier WAYPOINT_TRANSMIT_RANGE_HIDE_MODIFIER = new AttributeModifier(ResourceLocation.withDefaultNamespace("waypoint_transmit_range_hide"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

   static Item.Properties addHideAttribute(Item.Properties var0) {
      return var0.component(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder().add(Attributes.WAYPOINT_TRANSMIT_RANGE, WAYPOINT_TRANSMIT_RANGE_HIDE_MODIFIER, EquipmentSlotGroup.HEAD, ItemAttributeModifiers.Display.hidden()).build());
   }

   public static class Icon {
      public static final Codec<Icon> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Waypoint.Icon.Fade.CODEC.fieldOf("alpha_fade").forGetter((var0x) -> var0x.alphaFade), ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color").forGetter((var0x) -> var0x.color)).apply(var0, Icon::new));
      public static final StreamCodec<ByteBuf, Icon> STREAM_CODEC;
      public static final Icon NULL;
      public Fade alphaFade;
      public Optional<Integer> color;

      public Icon() {
         super();
         this.alphaFade = Waypoint.Icon.Fade.DEFAULT;
         this.color = Optional.empty();
      }

      private Icon(Fade var1, Optional<Integer> var2) {
         super();
         this.alphaFade = Waypoint.Icon.Fade.DEFAULT;
         this.color = Optional.empty();
         this.alphaFade = var1;
         this.color = var2;
      }

      public Icon cloneAndAssignColor(LivingEntity var1) {
         return this.color.isPresent() ? this : new Icon(this.alphaFade, Optional.ofNullable(var1.getTeam()).map((var0) -> var0.getColor().getColor()).map((var0) -> var0 == 0 ? -13619152 : var0));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Waypoint.Icon.Fade.STREAM_CODEC, (var0) -> var0.alphaFade, ByteBufCodecs.optional(ByteBufCodecs.RGB_COLOR), (var0) -> var0.color, Icon::new);
         NULL = new Icon();
      }

      public static record Fade(int nearDist, int farDist, float nearAlpha, float farAlpha) {
         private static final Function<Byte, Float> byteToFloat = (var0) -> (float)(var0 & 255) / 255.0F;
         private static final Function<Float, Byte> floatToByte = (var0) -> (byte)((int)(var0 * 255.0F));
         public static final Codec<Fade> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.INT.fieldOf("near_dist").forGetter(Fade::nearDist), Codec.INT.fieldOf("far_dist").forGetter(Fade::farDist), Codec.BYTE.xmap(byteToFloat, floatToByte).fieldOf("near_alpha").forGetter(Fade::nearAlpha), Codec.BYTE.xmap(byteToFloat, floatToByte).fieldOf("far_alpha").forGetter(Fade::farAlpha)).apply(var0, Fade::new));
         public static final StreamCodec<ByteBuf, Fade> STREAM_CODEC;
         public static final Fade DEFAULT;

         public Fade(int var1, int var2, float var3, float var4) {
            super();
            this.nearDist = var1;
            this.farDist = var2;
            this.nearAlpha = var3;
            this.farAlpha = var4;
         }

         public float lerpAlpha(float var1) {
            return Mth.lerp((var1 - (float)this.nearDist) / (float)(this.farDist - this.nearDist), this.nearAlpha(), this.farAlpha());
         }

         static {
            STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, Fade::nearDist, ByteBufCodecs.INT, Fade::farDist, ByteBufCodecs.BYTE.map(byteToFloat, floatToByte), Fade::nearAlpha, ByteBufCodecs.BYTE.map(byteToFloat, floatToByte), Fade::farAlpha, Fade::new);
            DEFAULT = new Fade(128, 332, 1.0F, 0.2F);
         }
      }
   }
}
