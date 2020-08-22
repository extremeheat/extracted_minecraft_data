package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class MapDecoration {
   private final MapDecoration.Type type;
   private byte x;
   private byte y;
   private byte rot;
   private final Component name;

   public MapDecoration(MapDecoration.Type var1, byte var2, byte var3, byte var4, @Nullable Component var5) {
      this.type = var1;
      this.x = var2;
      this.y = var3;
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
      return this.x;
   }

   public byte getY() {
      return this.y;
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
         if (this.type != var2.type) {
            return false;
         } else if (this.rot != var2.rot) {
            return false;
         } else if (this.x != var2.x) {
            return false;
         } else if (this.y != var2.y) {
            return false;
         } else {
            return Objects.equals(this.name, var2.name);
         }
      }
   }

   public int hashCode() {
      byte var1 = this.type.getIcon();
      int var2 = 31 * var1 + this.x;
      var2 = 31 * var2 + this.y;
      var2 = 31 * var2 + this.rot;
      var2 = 31 * var2 + Objects.hashCode(this.name);
      return var2;
   }

   public static enum Type {
      PLAYER(false),
      FRAME(true),
      RED_MARKER(false),
      BLUE_MARKER(false),
      TARGET_X(true),
      TARGET_POINT(true),
      PLAYER_OFF_MAP(false),
      PLAYER_OFF_LIMITS(false),
      MANSION(true, 5393476),
      MONUMENT(true, 3830373),
      BANNER_WHITE(true),
      BANNER_ORANGE(true),
      BANNER_MAGENTA(true),
      BANNER_LIGHT_BLUE(true),
      BANNER_YELLOW(true),
      BANNER_LIME(true),
      BANNER_PINK(true),
      BANNER_GRAY(true),
      BANNER_LIGHT_GRAY(true),
      BANNER_CYAN(true),
      BANNER_PURPLE(true),
      BANNER_BLUE(true),
      BANNER_BROWN(true),
      BANNER_GREEN(true),
      BANNER_RED(true),
      BANNER_BLACK(true),
      RED_X(true);

      private final byte icon;
      private final boolean renderedOnFrame;
      private final int mapColor;

      private Type(boolean var3) {
         this(var3, -1);
      }

      private Type(boolean var3, int var4) {
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
         return values()[Mth.clamp(var0, 0, values().length - 1)];
      }
   }
}
