package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public record MapBanner(BlockPos pos, DyeColor color, Optional<Component> name) {
   public static final Codec<MapBanner> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockPos.CODEC.fieldOf("pos").forGetter(MapBanner::pos), DyeColor.CODEC.lenientOptionalFieldOf("color", DyeColor.WHITE).forGetter(MapBanner::color), ComponentSerialization.FLAT_CODEC.lenientOptionalFieldOf("name").forGetter(MapBanner::name)).apply(var0, MapBanner::new);
   });
   public static final Codec<List<MapBanner>> LIST_CODEC;

   public MapBanner(BlockPos pos, DyeColor color, Optional<Component> name) {
      super();
      this.pos = pos;
      this.color = color;
      this.name = name;
   }

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

   public BlockPos pos() {
      return this.pos;
   }

   public DyeColor color() {
      return this.color;
   }

   public Optional<Component> name() {
      return this.name;
   }

   static {
      LIST_CODEC = CODEC.listOf();
   }
}
