package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public record MapBanner(BlockPos pos, DyeColor color, Optional<Component> name) {
   public static final Codec<MapBanner> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockPos.CODEC.fieldOf("pos").forGetter(MapBanner::pos), DyeColor.CODEC.lenientOptionalFieldOf("color", DyeColor.WHITE).forGetter(MapBanner::color), ComponentSerialization.CODEC.lenientOptionalFieldOf("name").forGetter(MapBanner::name)).apply(i, MapBanner::new));

   public MapBanner {
      super();
   }

   public static @Nullable MapBanner fromWorld(final BlockGetter level, final BlockPos pos) {
      BlockEntity entity = level.getBlockEntity(pos);
      if (entity instanceof BannerBlockEntity banner) {
         DyeColor color = banner.getBaseColor();
         Optional<Component> name = Optional.ofNullable(banner.getCustomName());
         return new MapBanner(pos, color, name);
      } else {
         return null;
      }
   }

   public Holder<MapDecorationType> getDecoration() {
      Holder var10000;
      switch (this.color) {
         case WHITE -> var10000 = MapDecorationTypes.WHITE_BANNER;
         case ORANGE -> var10000 = MapDecorationTypes.ORANGE_BANNER;
         case MAGENTA -> var10000 = MapDecorationTypes.MAGENTA_BANNER;
         case LIGHT_BLUE -> var10000 = MapDecorationTypes.LIGHT_BLUE_BANNER;
         case YELLOW -> var10000 = MapDecorationTypes.YELLOW_BANNER;
         case LIME -> var10000 = MapDecorationTypes.LIME_BANNER;
         case PINK -> var10000 = MapDecorationTypes.PINK_BANNER;
         case GRAY -> var10000 = MapDecorationTypes.GRAY_BANNER;
         case LIGHT_GRAY -> var10000 = MapDecorationTypes.LIGHT_GRAY_BANNER;
         case CYAN -> var10000 = MapDecorationTypes.CYAN_BANNER;
         case PURPLE -> var10000 = MapDecorationTypes.PURPLE_BANNER;
         case BLUE -> var10000 = MapDecorationTypes.BLUE_BANNER;
         case BROWN -> var10000 = MapDecorationTypes.BROWN_BANNER;
         case GREEN -> var10000 = MapDecorationTypes.GREEN_BANNER;
         case RED -> var10000 = MapDecorationTypes.RED_BANNER;
         case BLACK -> var10000 = MapDecorationTypes.BLACK_BANNER;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public String getId() {
      int var10000 = this.pos.getX();
      return "banner-" + var10000 + "," + this.pos.getY() + "," + this.pos.getZ();
   }
}
