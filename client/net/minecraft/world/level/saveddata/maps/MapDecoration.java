package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.MapColor;

public record MapDecoration(MapDecoration.Type b, byte c, byte d, byte e, Optional<Component> f) {
   private final MapDecoration.Type type;
   private final byte x;
   private final byte y;
   private final byte rot;
   private final Optional<Component> name;
   public static final StreamCodec<RegistryFriendlyByteBuf, MapDecoration> STREAM_CODEC = StreamCodec.composite(
      MapDecoration.Type.STREAM_CODEC,
      MapDecoration::type,
      ByteBufCodecs.BYTE,
      MapDecoration::x,
      ByteBufCodecs.BYTE,
      MapDecoration::y,
      ByteBufCodecs.BYTE,
      MapDecoration::rot,
      ComponentSerialization.OPTIONAL_STREAM_CODEC,
      MapDecoration::name,
      MapDecoration::new
   );

   public MapDecoration(MapDecoration.Type var1, byte var2, byte var3, byte var4, Optional<Component> var5) {
      super();
      var4 = (byte)(var4 & 15);
      this.type = var1;
      this.x = var2;
      this.y = var3;
      this.rot = var4;
      this.name = var5;
   }

   public byte getImage() {
      return this.type.getIcon();
   }

   public boolean renderOnFrame() {
      return this.type.isRenderedOnFrame();
   }

   public static enum Type implements StringRepresentable {
      PLAYER(0, "player", false, true),
      FRAME(1, "frame", true, true),
      RED_MARKER(2, "red_marker", false, true),
      BLUE_MARKER(3, "blue_marker", false, true),
      TARGET_X(4, "target_x", true, false),
      TARGET_POINT(5, "target_point", true, false),
      PLAYER_OFF_MAP(6, "player_off_map", false, true),
      PLAYER_OFF_LIMITS(7, "player_off_limits", false, true),
      MANSION(8, "mansion", true, 5393476, false, true),
      MONUMENT(9, "monument", true, 3830373, false, true),
      BANNER_WHITE(10, "banner_white", true, true),
      BANNER_ORANGE(11, "banner_orange", true, true),
      BANNER_MAGENTA(12, "banner_magenta", true, true),
      BANNER_LIGHT_BLUE(13, "banner_light_blue", true, true),
      BANNER_YELLOW(14, "banner_yellow", true, true),
      BANNER_LIME(15, "banner_lime", true, true),
      BANNER_PINK(16, "banner_pink", true, true),
      BANNER_GRAY(17, "banner_gray", true, true),
      BANNER_LIGHT_GRAY(18, "banner_light_gray", true, true),
      BANNER_CYAN(19, "banner_cyan", true, true),
      BANNER_PURPLE(20, "banner_purple", true, true),
      BANNER_BLUE(21, "banner_blue", true, true),
      BANNER_BROWN(22, "banner_brown", true, true),
      BANNER_GREEN(23, "banner_green", true, true),
      BANNER_RED(24, "banner_red", true, true),
      BANNER_BLACK(25, "banner_black", true, true),
      RED_X(26, "red_x", true, false),
      DESERT_VILLAGE(27, "village_desert", true, MapColor.COLOR_LIGHT_GRAY.col, false, true),
      PLAINS_VILLAGE(28, "village_plains", true, MapColor.COLOR_LIGHT_GRAY.col, false, true),
      SAVANNA_VILLAGE(29, "village_savanna", true, MapColor.COLOR_LIGHT_GRAY.col, false, true),
      SNOWY_VILLAGE(30, "village_snowy", true, MapColor.COLOR_LIGHT_GRAY.col, false, true),
      TAIGA_VILLAGE(31, "village_taiga", true, MapColor.COLOR_LIGHT_GRAY.col, false, true),
      JUNGLE_TEMPLE(32, "jungle_temple", true, MapColor.COLOR_LIGHT_GRAY.col, false, true),
      SWAMP_HUT(33, "swamp_hut", true, MapColor.COLOR_LIGHT_GRAY.col, false, true);

      public static final IntFunction<MapDecoration.Type> BY_ID = ByIdMap.continuous(MapDecoration.Type::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<MapDecoration.Type> CODEC = StringRepresentable.fromEnum(MapDecoration.Type::values);
      public static final StreamCodec<ByteBuf, MapDecoration.Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, MapDecoration.Type::id);
      private final int id;
      private final String name;
      private final byte icon;
      private final boolean renderedOnFrame;
      private final int mapColor;
      private final boolean isExplorationMapElement;
      private final boolean trackCount;

      private Type(int var3, String var4, boolean var5, boolean var6) {
         this(var3, var4, var5, -1, var6, false);
      }

      private Type(int var3, String var4, boolean var5, int var6, boolean var7, boolean var8) {
         this.id = var3;
         this.name = var4;
         this.trackCount = var7;
         this.icon = (byte)this.ordinal();
         this.renderedOnFrame = var5;
         this.mapColor = var6;
         this.isExplorationMapElement = var8;
      }

      public int id() {
         return this.id;
      }

      public byte getIcon() {
         return this.icon;
      }

      public boolean isExplorationMapElement() {
         return this.isExplorationMapElement;
      }

      public boolean isRenderedOnFrame() {
         return this.renderedOnFrame;
      }

      public boolean hasMapColor() {
         return this.mapColor >= 0;
      }

      public int getMapColor() {
         return this.mapColor;
      }

      public boolean shouldTrackCount() {
         return this.trackCount;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
