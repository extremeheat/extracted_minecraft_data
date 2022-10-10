package net.minecraft.world.storage;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class MapDecoration {
   private final MapDecoration.Type field_191181_a;
   private byte field_176115_b;
   private byte field_176116_c;
   private byte field_176114_d;
   private final ITextComponent field_204310_e;

   public MapDecoration(MapDecoration.Type var1, byte var2, byte var3, byte var4, @Nullable ITextComponent var5) {
      super();
      this.field_191181_a = var1;
      this.field_176115_b = var2;
      this.field_176116_c = var3;
      this.field_176114_d = var4;
      this.field_204310_e = var5;
   }

   public byte func_176110_a() {
      return this.field_191181_a.func_191163_a();
   }

   public MapDecoration.Type func_191179_b() {
      return this.field_191181_a;
   }

   public byte func_176112_b() {
      return this.field_176115_b;
   }

   public byte func_176113_c() {
      return this.field_176116_c;
   }

   public byte func_176111_d() {
      return this.field_176114_d;
   }

   public boolean func_191180_f() {
      return this.field_191181_a.func_191160_b();
   }

   @Nullable
   public ITextComponent func_204309_g() {
      return this.field_204310_e;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MapDecoration)) {
         return false;
      } else {
         MapDecoration var2 = (MapDecoration)var1;
         if (this.field_191181_a != var2.field_191181_a) {
            return false;
         } else if (this.field_176114_d != var2.field_176114_d) {
            return false;
         } else if (this.field_176115_b != var2.field_176115_b) {
            return false;
         } else if (this.field_176116_c != var2.field_176116_c) {
            return false;
         } else {
            return Objects.equals(this.field_204310_e, var2.field_204310_e);
         }
      }
   }

   public int hashCode() {
      byte var1 = this.field_191181_a.func_191163_a();
      int var2 = 31 * var1 + this.field_176115_b;
      var2 = 31 * var2 + this.field_176116_c;
      var2 = 31 * var2 + this.field_176114_d;
      var2 = 31 * var2 + Objects.hashCode(this.field_204310_e);
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

      private final byte field_191175_k;
      private final boolean field_191176_l;
      private final int field_191177_m;

      private Type(boolean var3) {
         this(var3, -1);
      }

      private Type(boolean var3, int var4) {
         this.field_191175_k = (byte)this.ordinal();
         this.field_191176_l = var3;
         this.field_191177_m = var4;
      }

      public byte func_191163_a() {
         return this.field_191175_k;
      }

      public boolean func_191160_b() {
         return this.field_191176_l;
      }

      public boolean func_191162_c() {
         return this.field_191177_m >= 0;
      }

      public int func_191161_d() {
         return this.field_191177_m;
      }

      public static MapDecoration.Type func_191159_a(byte var0) {
         return values()[MathHelper.func_76125_a(var0, 0, values().length - 1)];
      }
   }
}
