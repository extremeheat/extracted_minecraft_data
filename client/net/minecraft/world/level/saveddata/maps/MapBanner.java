package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public record MapBanner(BlockPos c, DyeColor d, Optional<Component> e) {
   private final BlockPos pos;
   private final DyeColor color;
   private final Optional<Component> name;
   public static final Codec<MapBanner> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               BlockPos.CODEC.fieldOf("pos").forGetter(MapBanner::pos),
               DyeColor.CODEC.optionalFieldOf("color", DyeColor.WHITE).forGetter(MapBanner::color),
               ComponentSerialization.FLAT_CODEC.optionalFieldOf("name").forGetter(MapBanner::name)
            )
            .apply(var0, MapBanner::new)
   );
   public static final Codec<List<MapBanner>> LIST_CODEC = CODEC.listOf();

   public MapBanner(BlockPos var1, DyeColor var2, Optional<Component> var3) {
      super();
      this.pos = var1;
      this.color = var2;
      this.name = var3;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   public static MapBanner fromWorld(BlockGetter var0, BlockPos var1) {
      BlockEntity var2 = var0.getBlockEntity(var1);
      if (var2 instanceof BannerBlockEntity var3) {
         DyeColor var4 = var3.getBaseColor();
         Optional var5 = Optional.ofNullable(var3.getCustomName());
         return new MapBanner(var1, var4, var5);
      } else {
         return null;
      }
   }

   public Holder<MapDecorationType> getDecoration() {
      return switch(this.color) {
         case WHITE -> MapDecorationTypes.WHITE_BANNER;
         case ORANGE -> MapDecorationTypes.ORANGE_BANNER;
         case MAGENTA -> MapDecorationTypes.MAGENTA_BANNER;
         case LIGHT_BLUE -> MapDecorationTypes.LIGHT_BLUE_BANNER;
         case YELLOW -> MapDecorationTypes.YELLOW_BANNER;
         case LIME -> MapDecorationTypes.LIME_BANNER;
         case PINK -> MapDecorationTypes.PINK_BANNER;
         case GRAY -> MapDecorationTypes.GRAY_BANNER;
         case LIGHT_GRAY -> MapDecorationTypes.LIGHT_GRAY_BANNER;
         case CYAN -> MapDecorationTypes.CYAN_BANNER;
         case PURPLE -> MapDecorationTypes.PURPLE_BANNER;
         case BLUE -> MapDecorationTypes.BLUE_BANNER;
         case BROWN -> MapDecorationTypes.BROWN_BANNER;
         case GREEN -> MapDecorationTypes.GREEN_BANNER;
         case RED -> MapDecorationTypes.RED_BANNER;
         case BLACK -> MapDecorationTypes.BLACK_BANNER;
      };
   }

   public String getId() {
      return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
   }
}
