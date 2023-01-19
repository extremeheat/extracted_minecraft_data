package net.minecraft.server.network;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public record FilteredText<T>(T b, @Nullable T c) {
   private final T raw;
   @Nullable
   private final T filtered;
   public static final FilteredText<String> EMPTY_STRING = passThrough("");

   public FilteredText(T var1, @Nullable T var2) {
      super();
      this.raw = (T)var1;
      this.filtered = (T)var2;
   }

   public static <T> FilteredText<T> passThrough(T var0) {
      return new FilteredText<>((T)var0, (T)var0);
   }

   public static <T> FilteredText<T> fullyFiltered(T var0) {
      return new FilteredText<>((T)var0, (T)null);
   }

   public <U> FilteredText<U> map(Function<T, U> var1) {
      return new FilteredText<>((U)var1.apply(this.raw), Util.mapNullable(this.filtered, var1));
   }

   public boolean isFiltered() {
      return !this.raw.equals(this.filtered);
   }

   public boolean isFullyFiltered() {
      return this.filtered == null;
   }

   public T filteredOrElse(T var1) {
      return (T)(this.filtered != null ? this.filtered : var1);
   }

   @Nullable
   public T filter(ServerPlayer var1, ServerPlayer var2) {
      return (T)(var1.shouldFilterMessageTo(var2) ? this.filtered : this.raw);
   }

   @Nullable
   public T filter(CommandSourceStack var1, ServerPlayer var2) {
      ServerPlayer var3 = var1.getPlayer();
      return (T)(var3 != null ? this.filter(var3, var2) : this.raw);
   }
}
