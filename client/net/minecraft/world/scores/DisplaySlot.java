package net.minecraft.world.scores;

import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum DisplaySlot implements StringRepresentable {
   LIST(0, "list"),
   SIDEBAR(1, "sidebar"),
   BELOW_NAME(2, "below_name"),
   TEAM_BLACK(3, "sidebar.team.black"),
   TEAM_DARK_BLUE(4, "sidebar.team.dark_blue"),
   TEAM_DARK_GREEN(5, "sidebar.team.dark_green"),
   TEAM_DARK_AQUA(6, "sidebar.team.dark_aqua"),
   TEAM_DARK_RED(7, "sidebar.team.dark_red"),
   TEAM_DARK_PURPLE(8, "sidebar.team.dark_purple"),
   TEAM_GOLD(9, "sidebar.team.gold"),
   TEAM_GRAY(10, "sidebar.team.gray"),
   TEAM_DARK_GRAY(11, "sidebar.team.dark_gray"),
   TEAM_BLUE(12, "sidebar.team.blue"),
   TEAM_GREEN(13, "sidebar.team.green"),
   TEAM_AQUA(14, "sidebar.team.aqua"),
   TEAM_RED(15, "sidebar.team.red"),
   TEAM_LIGHT_PURPLE(16, "sidebar.team.light_purple"),
   TEAM_YELLOW(17, "sidebar.team.yellow"),
   TEAM_WHITE(18, "sidebar.team.white");

   public static final StringRepresentable.EnumCodec<DisplaySlot> CODEC = StringRepresentable.fromEnum(DisplaySlot::values);
   public static final IntFunction<DisplaySlot> BY_ID = ByIdMap.continuous(DisplaySlot::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   private final int id;
   private final String name;

   private DisplaySlot(final int var3, final String var4) {
      this.id = var3;
      this.name = var4;
   }

   public int id() {
      return this.id;
   }

   public String getSerializedName() {
      return this.name;
   }

   @Nullable
   public static DisplaySlot teamColorToSlot(ChatFormatting var0) {
      DisplaySlot var10000;
      switch (var0) {
         case BLACK:
            var10000 = TEAM_BLACK;
            break;
         case DARK_BLUE:
            var10000 = TEAM_DARK_BLUE;
            break;
         case DARK_GREEN:
            var10000 = TEAM_DARK_GREEN;
            break;
         case DARK_AQUA:
            var10000 = TEAM_DARK_AQUA;
            break;
         case DARK_RED:
            var10000 = TEAM_DARK_RED;
            break;
         case DARK_PURPLE:
            var10000 = TEAM_DARK_PURPLE;
            break;
         case GOLD:
            var10000 = TEAM_GOLD;
            break;
         case GRAY:
            var10000 = TEAM_GRAY;
            break;
         case DARK_GRAY:
            var10000 = TEAM_DARK_GRAY;
            break;
         case BLUE:
            var10000 = TEAM_BLUE;
            break;
         case GREEN:
            var10000 = TEAM_GREEN;
            break;
         case AQUA:
            var10000 = TEAM_AQUA;
            break;
         case RED:
            var10000 = TEAM_RED;
            break;
         case LIGHT_PURPLE:
            var10000 = TEAM_LIGHT_PURPLE;
            break;
         case YELLOW:
            var10000 = TEAM_YELLOW;
            break;
         case WHITE:
            var10000 = TEAM_WHITE;
            break;
         case BOLD:
         case ITALIC:
         case UNDERLINE:
         case RESET:
         case OBFUSCATED:
         case STRIKETHROUGH:
            var10000 = null;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static DisplaySlot[] $values() {
      return new DisplaySlot[]{LIST, SIDEBAR, BELOW_NAME, TEAM_BLACK, TEAM_DARK_BLUE, TEAM_DARK_GREEN, TEAM_DARK_AQUA, TEAM_DARK_RED, TEAM_DARK_PURPLE, TEAM_GOLD, TEAM_GRAY, TEAM_DARK_GRAY, TEAM_BLUE, TEAM_GREEN, TEAM_AQUA, TEAM_RED, TEAM_LIGHT_PURPLE, TEAM_YELLOW, TEAM_WHITE};
   }
}
