package net.minecraft.client.color;

import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ColorCollection;

public class ColorLerper {
   public static final DyeColor[] MUSIC_NOTE_DYES;
   private static final ColorCollection<Integer> SHEEP_COLORS;
   private static final ColorCollection<Integer> MUSIC_NOTE_COLORS;

   public ColorLerper() {
      super();
   }

   public static int getLerpedColor(final Type type, final float tick) {
      int tickCount = Mth.floor(tick);
      int value = tickCount / type.colorDuration;
      int colorCount = type.dyes.length;
      int c1 = value % colorCount;
      int c2 = (value + 1) % colorCount;
      float subStep = ((float)(tickCount % type.colorDuration) + Mth.frac(tick)) / (float)type.colorDuration;
      int color1 = type.getColor(type.dyes[c1]);
      int color2 = type.getColor(type.dyes[c2]);
      return ARGB.srgbLerp(subStep, color1, color2);
   }

   private static int getModifiedColor(final DyeColor color, final int src, final float brightness) {
      return color == DyeColor.WHITE ? -1644826 : ARGB.color(255, Mth.clamp(Mth.floor((float)ARGB.red(src) * brightness), 0, 255), Mth.clamp(Mth.floor((float)ARGB.green(src) * brightness), 0, 255), Mth.clamp(Mth.floor((float)ARGB.blue(src) * brightness), 0, 255));
   }

   static {
      MUSIC_NOTE_DYES = new DyeColor[]{DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.CYAN, DyeColor.GREEN, DyeColor.LIME, DyeColor.YELLOW, DyeColor.ORANGE, DyeColor.PINK, DyeColor.RED, DyeColor.MAGENTA};
      SHEEP_COLORS = ColorCollection.zipMap(ColorCollection.VALUES, CommonColors.TEXTURE_TINT_COLORS, (dye, color) -> getModifiedColor(dye, color, 0.75F));
      MUSIC_NOTE_COLORS = ColorCollection.zipMap(ColorCollection.VALUES, CommonColors.TEXTURE_TINT_COLORS, (dye, color) -> getModifiedColor(dye, color, 1.25F));
   }

   public static enum Type {
      SHEEP(25, DyeColor.values(), ColorLerper.SHEEP_COLORS),
      MUSIC_NOTE(30, ColorLerper.MUSIC_NOTE_DYES, ColorLerper.MUSIC_NOTE_COLORS);

      private final int colorDuration;
      private final ColorCollection<Integer> colors;
      private final DyeColor[] dyes;

      private Type(final int colorDuration, final DyeColor[] dyes, final ColorCollection<Integer> colors) {
         this.colorDuration = colorDuration;
         this.colors = colors;
         this.dyes = dyes;
      }

      public final int getColor(final DyeColor dyeColor) {
         return (Integer)this.colors.pick(dyeColor);
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{SHEEP, MUSIC_NOTE};
      }
   }
}
