package net.minecraft.server.network;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FilterMask;

public record FilteredText(String b, FilterMask c) {
   private final String raw;
   private final FilterMask mask;
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

   @Nullable
   public String filtered() {
      return this.mask.apply(this.raw);
   }

   public String filteredOrEmpty() {
      return (String)Objects.requireNonNullElse(this.filtered(), "");
   }

   public boolean isFiltered() {
      return !this.mask.isEmpty();
   }

   public String raw() {
      return this.raw;
   }

   public FilterMask mask() {
      return this.mask;
   }
}
