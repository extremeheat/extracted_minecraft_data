package net.minecraft.client.gui.screens.friends;

import java.util.List;
import java.util.Locale;

public class FriendsListFilter {
   private final Runnable updateCallback;
   private String filter = "";

   public FriendsListFilter(final Runnable updateCallback) {
      super();
      this.updateCallback = updateCallback;
   }

   public boolean isEmpty() {
      return this.filter.isEmpty();
   }

   public void updateSearchFilter(final String newFilter) {
      if (!this.filter.equalsIgnoreCase(newFilter)) {
         this.filter = newFilter.toLowerCase(Locale.ROOT);
         this.updateCallback.run();
      }

   }

   public boolean matchesFilter(final String playerName) {
      return playerName.toLowerCase(Locale.ROOT).contains(this.filter);
   }

   public <T extends AbstractFriendsEntryContainerWidget> void filter(final List<T> entries) {
      if (!this.filter.isEmpty()) {
         entries.removeIf((entry) -> !this.matchesFilter(entry.playerName()));
      }

   }
}
