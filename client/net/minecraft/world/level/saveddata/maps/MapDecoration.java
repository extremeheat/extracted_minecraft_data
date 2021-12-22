package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class MapDecoration {
   private final MapDecoration.Type type;
   // $FF: renamed from: x byte
   private final byte field_341;
   // $FF: renamed from: y byte
   private final byte field_342;
   private final byte rot;
   @Nullable
   private final Component name;

   public MapDecoration(MapDecoration.Type var1, byte var2, byte var3, byte var4, @Nullable Component var5) {
      super();
      this.type = var1;
      this.field_341 = var2;
      this.field_342 = var3;
      this.rot = var4;
      this.name = var5;
   }

   public byte getImage() {
      return this.type.getIcon();
   }

   public MapDecoration.Type getType() {
      return this.type;
   }

   public byte getX() {
      return this.field_341;
   }

   public byte getY() {
      return this.field_342;
   }

   public byte getRot() {
      return this.rot;
   }

   public boolean renderOnFrame() {
      return this.type.isRenderedOnFrame();
   }

   @Nullable
   public Component getName() {
      return this.name;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MapDecoration)) {
         return false;
      } else {
         MapDecoration var2 = (MapDecoration)var1;
         return this.type == var2.type && this.rot == var2.rot && this.field_341 == var2.field_341 && this.field_342 == var2.field_342 && Objects.equals(this.name, var2.name);
      }
   }

   public int hashCode() {
      byte var1 = this.type.getIcon();
      int var2 = 31 * var1 + this.field_341;
      var2 = 31 * var2 + this.field_342;
      var2 = 31 * var2 + this.rot;
      var2 = 31 * var2 + Objects.hashCode(this.name);
      return var2;
   }

   public static enum Type {
      PLAYER(false, true),
      FRAME(true, true),
      RED_MARKER(false, true),
      BLUE_MARKER(false, true),
      TARGET_X(true, false),
      TARGET_POINT(true, false),
      PLAYER_OFF_MAP(false, true),
      PLAYER_OFF_LIMITS(false, true),
      MANSION(true, 5393476, false),
      MONUMENT(true, 3830373, false),
      BANNER_WHITE(true, true),
      BANNER_ORANGE(true, true),
      BANNER_MAGENTA(true, true),
      BANNER_LIGHT_BLUE(true, true),
      BANNER_YELLOW(true, true),
      BANNER_LIME(true, true),
      BANNER_PINK(true, true),
      BANNER_GRAY(true, true),
      BANNER_LIGHT_GRAY(true, true),
      BANNER_CYAN(true, true),
      BANNER_PURPLE(true, true),
      BANNER_BLUE(true, true),
      BANNER_BROWN(true, true),
      BANNER_GREEN(true, true),
      BANNER_RED(true, true),
      BANNER_BLACK(true, true),
      RED_X(true, false);

      private final byte icon;
      private final boolean renderedOnFrame;
      private final int mapColor;
      private final boolean trackCount;

      private Type(boolean var3, boolean var4) {
         this(var3, -1, var4);
      }

      private Type(boolean var3, int var4, boolean var5) {
         this.trackCount = var5;
         this.icon = (byte)this.ordinal();
         this.renderedOnFrame = var3;
         this.mapColor = var4;
      }

      public byte getIcon() {
         return this.icon;
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

      public static MapDecoration.Type byIcon(byte var0) {
         return values()[Mth.clamp((int)var0, (int)0, (int)(values().length - 1))];
      }

      public boolean shouldTrackCount() {
         return this.trackCount;
      }

      // $FF: synthetic method
      private static MapDecoration.Type[] $values() {
         return new MapDecoration.Type[]{PLAYER, FRAME, RED_MARKER, BLUE_MARKER, TARGET_X, TARGET_POINT, PLAYER_OFF_MAP, PLAYER_OFF_LIMITS, MANSION, MONUMENT, BANNER_WHITE, BANNER_ORANGE, BANNER_MAGENTA, BANNER_LIGHT_BLUE, BANNER_YELLOW, BANNER_LIME, BANNER_PINK, BANNER_GRAY, BANNER_LIGHT_GRAY, BANNER_CYAN, BANNER_PURPLE, BANNER_BLUE, BANNER_BROWN, BANNER_GREEN, BANNER_RED, BANNER_BLACK, RED_X};
      }
   }
}
