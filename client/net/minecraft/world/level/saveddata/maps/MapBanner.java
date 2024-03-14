package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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

   public MapDecoration.Type getDecoration() {
      return switch(this.color) {
         case WHITE -> MapDecoration.Type.BANNER_WHITE;
         case ORANGE -> MapDecoration.Type.BANNER_ORANGE;
         case MAGENTA -> MapDecoration.Type.BANNER_MAGENTA;
         case LIGHT_BLUE -> MapDecoration.Type.BANNER_LIGHT_BLUE;
         case YELLOW -> MapDecoration.Type.BANNER_YELLOW;
         case LIME -> MapDecoration.Type.BANNER_LIME;
         case PINK -> MapDecoration.Type.BANNER_PINK;
         case GRAY -> MapDecoration.Type.BANNER_GRAY;
         case LIGHT_GRAY -> MapDecoration.Type.BANNER_LIGHT_GRAY;
         case CYAN -> MapDecoration.Type.BANNER_CYAN;
         case PURPLE -> MapDecoration.Type.BANNER_PURPLE;
         case BLUE -> MapDecoration.Type.BANNER_BLUE;
         case BROWN -> MapDecoration.Type.BANNER_BROWN;
         case GREEN -> MapDecoration.Type.BANNER_GREEN;
         case RED -> MapDecoration.Type.BANNER_RED;
         default -> MapDecoration.Type.BANNER_BLACK;
      };
   }

   public String getId() {
      return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
   }
}
