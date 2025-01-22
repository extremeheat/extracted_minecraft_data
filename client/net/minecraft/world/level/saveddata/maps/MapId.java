package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.item.component.TooltipProvider;

public record MapId(int id) implements TooltipProvider {
   public static final Codec<MapId> CODEC;
   public static final StreamCodec<ByteBuf, MapId> STREAM_CODEC;
   private static final Component LOCKED_TEXT;

   public MapId(int var1) {
      super();
      this.id = var1;
   }

   public String key() {
      return "map_" + this.id;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, DataComponentGetter var4) {
      MapItemSavedData var5 = var1.mapData(this);
      if (var5 == null) {
         var2.accept(Component.translatable("filled_map.unknown").withStyle(ChatFormatting.GRAY));
      } else {
         MapPostProcessing var6 = (MapPostProcessing)var4.get(DataComponents.MAP_POST_PROCESSING);
         if (var4.get(DataComponents.CUSTOM_NAME) == null && var6 == null) {
            var2.accept(Component.translatable("filled_map.id", this.id).withStyle(ChatFormatting.GRAY));
         }

         if (var5.locked || var6 == MapPostProcessing.LOCK) {
            var2.accept(LOCKED_TEXT);
         }

         if (var3.isAdvanced()) {
            int var7 = var6 == MapPostProcessing.SCALE ? 1 : 0;
            int var8 = Math.min(var5.scale + var7, 4);
            var2.accept(Component.translatable("filled_map.scale", 1 << var8).withStyle(ChatFormatting.GRAY));
            var2.accept(Component.translatable("filled_map.level", var8, 4).withStyle(ChatFormatting.GRAY));
         }

      }
   }

   static {
      CODEC = Codec.INT.xmap(MapId::new, MapId::id);
      STREAM_CODEC = ByteBufCodecs.VAR_INT.map(MapId::new, MapId::id);
      LOCKED_TEXT = Component.translatable("filled_map.locked").withStyle(ChatFormatting.GRAY);
   }
}
