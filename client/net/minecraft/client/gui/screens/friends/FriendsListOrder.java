package net.minecraft.client.gui.screens.friends;

import java.util.Comparator;
import net.minecraft.client.gui.components.ScaledWidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class FriendsListOrder implements Comparator<FriendEntry> {
   public static final SortBy DEFAULT_SORTING;
   private final Runnable updateCallback;
   private SortBy sorting;

   public FriendsListOrder(final Runnable updateCallback) {
      super();
      this.sorting = DEFAULT_SORTING;
      this.updateCallback = updateCallback;
   }

   public void updateSorting(final SortBy newSorting) {
      if (this.sorting != newSorting) {
         this.sorting = newSorting;
         this.updateCallback.run();
      }

   }

   public int compare(final FriendEntry o1, final FriendEntry o2) {
      return this.sorting.getComparator().compare(o1, o2);
   }

   static {
      DEFAULT_SORTING = FriendsListOrder.SortBy.PRESENCE;
   }

   public static enum SortBy {
      PRESENCE("presence", FriendEntry.PRESENCE_COMPARATOR.thenComparing(FriendEntry.ALPHABETICAL_COMPARATOR)),
      ALPHABETICAL("alphabetical", FriendEntry.ALPHABETICAL_COMPARATOR);

      private final Component translatable;
      private final ScaledWidgetSprites sprites;
      private final Comparator<FriendEntry> comparator;

      private SortBy(final String name, final Comparator<FriendEntry> comparator) {
         this.comparator = comparator;
         this.translatable = Component.translatable("gui.friends.sort." + name);
         this.sprites = new ScaledWidgetSprites(Identifier.withDefaultNamespace("friends/sort_" + name), 16);
      }

      public Component getTranslation() {
         return this.translatable;
      }

      public ScaledWidgetSprites getSprites() {
         return this.sprites;
      }

      public Comparator<FriendEntry> getComparator() {
         return this.comparator;
      }

      // $FF: synthetic method
      private static SortBy[] $values() {
         return new SortBy[]{PRESENCE, ALPHABETICAL};
      }
   }
}
