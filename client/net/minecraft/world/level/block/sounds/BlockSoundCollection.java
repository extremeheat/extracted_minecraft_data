package net.minecraft.world.level.block.sounds;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record BlockSoundCollection<T>(T breakSound, T stepSound, T placeSound, T hitSound, T fallSound) {
   public static final BlockSoundCollection<Boolean> SELECT_ALL = create(true);
   public static final BlockSoundCollection<Boolean> SELECT_NONE = create(false);

   public BlockSoundCollection {
      super();
   }

   public static <T> BlockSoundCollection<T> create(final T value) {
      return new BlockSoundCollection<T>(value, value, value, value, value);
   }

   public static BlockSoundCollection<Identifier> createIds(final Identifier base) {
      return new BlockSoundCollection<Identifier>(base.withPath((UnaryOperator)((name) -> "block." + name + ".break")), base.withPath((UnaryOperator)((name) -> "block." + name + ".step")), base.withPath((UnaryOperator)((name) -> "block." + name + ".place")), base.withPath((UnaryOperator)((name) -> "block." + name + ".hit")), base.withPath((UnaryOperator)((name) -> "block." + name + ".fall")));
   }

   public static BlockSoundCollection<Identifier> createIdsForBlockSoundSet(final ResourceKey<BlockSoundSet> blockSoundSet) {
      return createIds(blockSoundSet.identifier());
   }

   public BlockSoundCollection<T> withBreak(final T breakSound) {
      return new BlockSoundCollection<T>(breakSound, this.stepSound, this.placeSound, this.hitSound, this.fallSound);
   }

   public BlockSoundCollection<T> withStep(final T stepSound) {
      return new BlockSoundCollection<T>(this.breakSound, stepSound, this.placeSound, this.hitSound, this.fallSound);
   }

   public BlockSoundCollection<T> withPlace(final T placeSound) {
      return new BlockSoundCollection<T>(this.breakSound, this.stepSound, placeSound, this.hitSound, this.fallSound);
   }

   public BlockSoundCollection<T> withHit(final T hitSound) {
      return new BlockSoundCollection<T>(this.breakSound, this.stepSound, this.placeSound, hitSound, this.fallSound);
   }

   public BlockSoundCollection<T> withFall(final T fallSound) {
      return new BlockSoundCollection<T>(this.breakSound, this.stepSound, this.placeSound, this.hitSound, fallSound);
   }

   public <S> BlockSoundCollection<S> map(final Function<T, S> mapper) {
      return new BlockSoundCollection<S>(mapper.apply(this.breakSound), mapper.apply(this.stepSound), mapper.apply(this.placeSound), mapper.apply(this.hitSound), mapper.apply(this.fallSound));
   }

   public static <T, U, R> BlockSoundCollection<R> zipMap(final BlockSoundCollection<T> first, final BlockSoundCollection<U> second, final BiFunction<T, U, R> operation) {
      return new BlockSoundCollection<R>(operation.apply(first.breakSound, second.breakSound), operation.apply(first.stepSound, second.stepSound), operation.apply(first.placeSound, second.placeSound), operation.apply(first.hitSound, second.hitSound), operation.apply(first.fallSound, second.fallSound));
   }

   public static <T> BlockSoundCollection<Optional<T>> mask(final BlockSoundCollection<T> inputs, final BlockSoundCollection<Boolean> mask) {
      return zipMap(inputs, mask, (value, shouldInclude) -> shouldInclude ? Optional.of(value) : Optional.empty());
   }
}
