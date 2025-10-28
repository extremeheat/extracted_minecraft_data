package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface LootContextArg<R> {
   Codec<LootContextArg<Object>> ENTITY_OR_BLOCK = createArgCodec((var0) -> var0.anyOf(LootContext.EntityTarget.values()).anyOf(LootContext.BlockEntityTarget.values()));

   @Nullable R get(LootContext var1);

   ContextKey<?> contextParam();

   static <U> LootContextArg<U> cast(LootContextArg<? extends U> var0) {
      return var0;
   }

   static <R> Codec<LootContextArg<R>> createArgCodec(UnaryOperator<ArgCodecBuilder<R>> var0) {
      return ((ArgCodecBuilder)var0.apply(new ArgCodecBuilder())).build();
   }

   public interface Getter<T, R> extends LootContextArg<R> {
      @Nullable R get(T var1);

      ContextKey<? extends T> contextParam();

      default @Nullable R get(LootContext var1) {
         Object var2 = var1.getOptionalParameter(this.contextParam());
         return (R)(var2 != null ? this.get(var2) : null);
      }
   }

   public interface SimpleGetter<T> extends LootContextArg<T> {
      ContextKey<? extends T> contextParam();

      default @Nullable T get(LootContext var1) {
         return (T)var1.getOptionalParameter(this.contextParam());
      }
   }

   public static final class ArgCodecBuilder<R> {
      private final ExtraCodecs.LateBoundIdMapper<String, LootContextArg<R>> sources = new ExtraCodecs.LateBoundIdMapper<String, LootContextArg<R>>();

      ArgCodecBuilder() {
         super();
      }

      public <T> ArgCodecBuilder<R> anyOf(T[] var1, Function<T, String> var2, Function<T, ? extends LootContextArg<R>> var3) {
         for(Object var7 : var1) {
            this.sources.put((String)var2.apply(var7), (LootContextArg)var3.apply(var7));
         }

         return this;
      }

      public <T extends StringRepresentable> ArgCodecBuilder<R> anyOf(T[] var1, Function<T, ? extends LootContextArg<R>> var2) {
         return this.anyOf(var1, StringRepresentable::getSerializedName, var2);
      }

      public <T extends StringRepresentable & LootContextArg<? extends R>> ArgCodecBuilder<R> anyOf(T[] var1) {
         return this.anyOf(var1, (var0) -> LootContextArg.cast((LootContextArg)var0));
      }

      public ArgCodecBuilder<R> anyEntity(Function<? super ContextKey<? extends Entity>, ? extends LootContextArg<R>> var1) {
         return this.anyOf(LootContext.EntityTarget.values(), (var1x) -> (LootContextArg)var1.apply(var1x.contextParam()));
      }

      public ArgCodecBuilder<R> anyBlockEntity(Function<? super ContextKey<? extends BlockEntity>, ? extends LootContextArg<R>> var1) {
         return this.anyOf(LootContext.BlockEntityTarget.values(), (var1x) -> (LootContextArg)var1.apply(var1x.contextParam()));
      }

      public ArgCodecBuilder<R> anyItemStack(Function<? super ContextKey<? extends ItemStack>, ? extends LootContextArg<R>> var1) {
         return this.anyOf(LootContext.ItemStackTarget.values(), (var1x) -> (LootContextArg)var1.apply(var1x.contextParam()));
      }

      Codec<LootContextArg<R>> build() {
         return this.sources.codec(Codec.STRING);
      }
   }
}
