package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      MapItemSavedData var6 = var1.mapData(this);
      if (var6 == null) {
         var2.accept(Component.translatable("filled_map.unknown").withStyle(ChatFormatting.GRAY));
      } else {
         MapPostProcessing var7 = (MapPostProcessing)var5.get(DataComponents.MAP_POST_PROCESSING);
         if (var5.get(DataComponents.CUSTOM_NAME) == null && var7 == null) {
            var2.accept(Component.translatable("filled_map.id", this.id).withStyle(ChatFormatting.GRAY));
         }

         if (var6.locked || var7 == MapPostProcessing.LOCK) {
            var2.accept(LOCKED_TEXT);
         }

         if (var3.isAdvanced()) {
            int var8 = var7 == MapPostProcessing.SCALE ? 1 : 0;
            int var9 = Math.min(var6.scale + var8, 4);
            var2.accept(Component.translatable("filled_map.scale", 1 << var9).withStyle(ChatFormatting.GRAY));
            var2.accept(Component.translatable("filled_map.level", var9, 4).withStyle(ChatFormatting.GRAY));
         }

      }
   }

   static {
      CODEC = Codec.INT.xmap(MapId::new, MapId::id);
      STREAM_CODEC = ByteBufCodecs.VAR_INT.map(MapId::new, MapId::id);
      LOCKED_TEXT = Component.translatable("filled_map.locked").withStyle(ChatFormatting.GRAY);
   }
}
