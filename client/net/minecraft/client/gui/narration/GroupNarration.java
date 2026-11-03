package net.minecraft.client.gui.narration;

import java.util.List;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public interface GroupNarration {
   String LIST_POSITION_KEY = "narrator.position.list";
   GroupNarration LIST = list("narrator.position.list");

   static GroupNarration list(final String positionKey) {
      return new AsList(positionKey, (String)null, (ScreenAxis)null);
   }

   static GroupNarration list(final String positionKey, final String usageKey, final @Nullable ScreenAxis axis) {
      return new AsList(positionKey, usageKey, axis);
   }

   static GroupNarration grid(final int columns, final String usageKey) {
      return new AsGrid(columns, usageKey);
   }

   @Nullable Component position(int index, int count);

   @Nullable Component usage(int count);

   default <T> void narratePosition(final NarrationElementOutput output, final List<T> children, final T child) {
      Component position = this.createPositionNarration(children, child);
      if (position != null) {
         output.add(NarratedElementType.POSITION, position);
      }

   }

   default <T> @Nullable Component createPositionNarration(final List<T> children, final T child) {
      int index = children.indexOf(child);
      return index != -1 ? this.position(index, children.size()) : null;
   }

   default void narrateUsage(final NarrationElementOutput output, final List<?> children) {
      Component usage = this.usage(children.size());
      if (usage != null) {
         output.add(NarratedElementType.USAGE, usage);
      }

   }

   private static Component createUsageNarration(final String usageKey, final @Nullable ScreenAxis axis) {
      byte var4 = 0;
      String var10000;
      //$FF: var4->value
      //0->HORIZONTAL
      //1->VERTICAL
      switch (axis.enumSwitch<invokedynamic>(axis, var4)) {
         case -1 -> var10000 = "narration.group_navigation.usage.direction.any";
         case 0 -> var10000 = "narration.group_navigation.usage.direction.horizontal";
         case 1 -> var10000 = "narration.group_navigation.usage.direction.vertical";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      String directionKey = var10000;
      return Component.translatable(usageKey, Component.translatable(directionKey));
   }

   public static record AsList(String positionKey, @Nullable String usageKey, @Nullable ScreenAxis axis) implements GroupNarration {
      public AsList {
         super();
      }

      public @Nullable Component position(final int index, final int count) {
         return count > 1 ? Component.translatable(this.positionKey, index + 1, count) : null;
      }

      public @Nullable Component usage(final int count) {
         return count > 1 && this.usageKey != null ? GroupNarration.createUsageNarration(this.usageKey, this.axis) : null;
      }
   }

   public static record AsGrid(int columns, String usageKey) implements GroupNarration {
      public AsGrid {
         super();
      }

      public @Nullable Component position(final int index, final int count) {
         return count > 1 ? Component.translatable("narrator.position.grid", index % this.columns + 1, Math.min(this.columns, count), index / this.columns + 1, Mth.positiveCeilDiv(count, this.columns)) : null;
      }

      public @Nullable Component usage(final int count) {
         return count > 1 ? GroupNarration.createUsageNarration(this.usageKey, (ScreenAxis)null) : null;
      }
   }
}
