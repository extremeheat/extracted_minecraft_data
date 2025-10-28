package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

public class SingleValuePalette<T> implements Palette<T> {
   private @Nullable T value;

   public SingleValuePalette(List<T> var1) {
      super();
      if (var1.size() > 0) {
         Validate.isTrue(var1.size() <= 1, "Can't initialize SingleValuePalette with %d values.", (long)var1.size());
         this.value = (T)var1.get(0);
      }

   }

   public static <A> Palette<A> create(int var0, List<A> var1) {
      return new SingleValuePalette<A>(var1);
   }

   public int idFor(T var1, PaletteResize<T> var2) {
      if (this.value != null && this.value != var1) {
         return var2.onResize(1, var1);
      } else {
         this.value = var1;
         return 0;
      }
   }

   public boolean maybeHas(Predicate<T> var1) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return var1.test(this.value);
      }
   }

   public T valueFor(int var1) {
      if (this.value != null && var1 == 0) {
         return this.value;
      } else {
         throw new IllegalStateException("Missing Palette entry for id " + var1 + ".");
      }
   }

   public void read(FriendlyByteBuf var1, IdMap<T> var2) {
      this.value = (T)var2.byIdOrThrow(var1.readVarInt());
   }

   public void write(FriendlyByteBuf var1, IdMap<T> var2) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         var1.writeVarInt(var2.getId(this.value));
      }
   }

   public int getSerializedSize(IdMap<T> var1) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return VarInt.getByteSize(var1.getId(this.value));
      }
   }

   public int getSize() {
      return 1;
   }

   public Palette<T> copy() {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return this;
      }
   }
}
