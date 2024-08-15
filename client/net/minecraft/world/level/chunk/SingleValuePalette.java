package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.apache.commons.lang3.Validate;

public class SingleValuePalette<T> implements Palette<T> {
   private final IdMap<T> registry;
   @Nullable
   private T value;
   private final PaletteResize<T> resizeHandler;

   public SingleValuePalette(IdMap<T> var1, PaletteResize<T> var2, List<T> var3) {
      super();
      this.registry = var1;
      this.resizeHandler = var2;
      if (var3.size() > 0) {
         Validate.isTrue(var3.size() <= 1, "Can't initialize SingleValuePalette with %d values.", (long)var3.size());
         this.value = (T)var3.get(0);
      }
   }

   public static <A> Palette<A> create(int var0, IdMap<A> var1, PaletteResize<A> var2, List<A> var3) {
      return new SingleValuePalette(var1, var2, var3);
   }

   @Override
   public int idFor(T var1) {
      if (this.value != null && this.value != var1) {
         return this.resizeHandler.onResize(1, (T)var1);
      } else {
         this.value = (T)var1;
         return 0;
      }
   }

   @Override
   public boolean maybeHas(Predicate<T> var1) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return var1.test(this.value);
      }
   }

   @Override
   public T valueFor(int var1) {
      if (this.value != null && var1 == 0) {
         return this.value;
      } else {
         throw new IllegalStateException("Missing Palette entry for id " + var1 + ".");
      }
   }

   @Override
   public void read(FriendlyByteBuf var1) {
      this.value = this.registry.byIdOrThrow(var1.readVarInt());
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         var1.writeVarInt(this.registry.getId(this.value));
      }
   }

   @Override
   public int getSerializedSize() {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return VarInt.getByteSize(this.registry.getId(this.value));
      }
   }

   @Override
   public int getSize() {
      return 1;
   }

   @Override
   public Palette<T> copy(PaletteResize<T> var1) {
      if (this.value == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return this;
      }
   }
}
