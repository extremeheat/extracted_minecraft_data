package net.minecraft.server.network;

import java.util.Objects;
import net.minecraft.network.chat.FilterMask;
import org.jspecify.annotations.Nullable;

public record FilteredText(String raw, FilterMask mask) {
   public static final FilteredText EMPTY = passThrough("");

   public FilteredText(String var1, FilterMask var2) {
      super();
      this.raw = var1;
      this.mask = var2;
   }

   public static FilteredText passThrough(String var0) {
      return new FilteredText(var0, FilterMask.PASS_THROUGH);
   }

   public static FilteredText fullyFiltered(String var0) {
      return new FilteredText(var0, FilterMask.FULLY_FILTERED);
   }

   public @Nullable String filtered() {
      return this.mask.apply(this.raw);
   }

   public String filteredOrEmpty() {
      return (String)Objects.requireNonNullElse(this.filtered(), "");
   }

   public boolean isFiltered() {
      return !this.mask.isEmpty();
   }
}
