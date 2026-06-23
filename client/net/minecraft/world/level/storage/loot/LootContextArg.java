package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface LootContextArg<R> {
   Codec<LootContextArg<Object>> ENTITY_OR_BLOCK = createArgCodec((builder) -> builder.anyOf(LootContext.EntityTarget.values()).anyOf(LootContext.BlockEntityTarget.values()));

   @Nullable R get(LootContext context);

   ContextKey<?> contextParam();

   static <U> LootContextArg<U> cast(final LootContextArg<? extends U> original) {
      return original;
   }

   static <R> LootContextArg<R> of(final ContextKey<? extends R> contextParam) {
      return () -> contextParam;
   }

   static <R> Codec<LootContextArg<R>> createArgCodec(final UnaryOperator<ArgCodecBuilder<R>> consumer) {
      return ((ArgCodecBuilder)consumer.apply(new ArgCodecBuilder())).build();
   }

   public interface Getter<T, R> extends LootContextArg<R> {
      @Nullable R get(T value);

      ContextKey<? extends T> contextParam();

      default @Nullable R get(final LootContext context) {
         T value = (T)context.getOptionalParameter(this.contextParam());
         return (R)(value != null ? this.get(value) : null);
      }
   }

   public interface SimpleGetter<T> extends LootContextArg<T> {
      ContextKey<? extends T> contextParam();

      default @Nullable T get(final LootContext context) {
         return (T)context.getOptionalParameter(this.contextParam());
      }
   }

   public static final class ArgCodecBuilder<R> {
      private final ExtraCodecs.LateBoundIdMapper<String, LootContextArg<R>> sources = new ExtraCodecs.LateBoundIdMapper<String, LootContextArg<R>>();

      private ArgCodecBuilder() {
         super();
      }

      public <T> ArgCodecBuilder<R> anyOf(final T[] targets, final Function<T, String> nameGetter, final Function<T, ? extends LootContextArg<R>> argFactory) {
         for(T target : targets) {
            this.sources.put((String)nameGetter.apply(target), (LootContextArg)argFactory.apply(target));
         }

         return this;
      }

      public <T extends StringRepresentable> ArgCodecBuilder<R> anyOf(final T[] targets, final Function<T, ? extends LootContextArg<R>> argFactory) {
         return this.anyOf(targets, StringRepresentable::getSerializedName, argFactory);
      }

      public <T extends StringRepresentable & LootContextArg<? extends R>> ArgCodecBuilder<R> anyOf(final T[] targets) {
         return this.anyOf(targets, (x$0) -> LootContextArg.cast((LootContextArg)x$0));
      }

      public ArgCodecBuilder<R> anyEntity(final Function<? super ContextKey<? extends Entity>, ? extends LootContextArg<R>> function) {
         return this.anyOf(LootContext.EntityTarget.values(), (target) -> (LootContextArg)function.apply(target.contextParam()));
      }

      public ArgCodecBuilder<R> anyBlockEntity(final Function<? super ContextKey<? extends BlockEntity>, ? extends LootContextArg<R>> function) {
         return this.anyOf(LootContext.BlockEntityTarget.values(), (target) -> (LootContextArg)function.apply(target.contextParam()));
      }

      public ArgCodecBuilder<R> anyItemStack(final Function<? super ContextKey<? extends ItemInstance>, ? extends LootContextArg<R>> function) {
         return this.anyOf(LootContext.ItemStackTarget.values(), (target) -> (LootContextArg)function.apply(target.contextParam()));
      }

      public ArgCodecBuilder<R> or(final String name, final LootContextArg<R> arg) {
         this.sources.put(name, arg);
         return this;
      }

      private Codec<LootContextArg<R>> build() {
         return this.sources.codec(Codec.STRING);
      }
   }
}
