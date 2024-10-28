package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record PotDecorations(Optional<Item> back, Optional<Item> left, Optional<Item> right, Optional<Item> front) {
   public static final PotDecorations EMPTY = new PotDecorations(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
   public static final Codec<PotDecorations> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, PotDecorations> STREAM_CODEC;

   private PotDecorations(List<Item> var1) {
      this(getItem(var1, 0), getItem(var1, 1), getItem(var1, 2), getItem(var1, 3));
   }

   public PotDecorations(Item var1, Item var2, Item var3, Item var4) {
      this(List.of(var1, var2, var3, var4));
   }

   public PotDecorations(Optional<Item> back, Optional<Item> left, Optional<Item> right, Optional<Item> front) {
      super();
      this.back = back;
      this.left = left;
      this.right = right;
      this.front = front;
   }

   private static Optional<Item> getItem(List<Item> var0, int var1) {
      if (var1 >= var0.size()) {
         return Optional.empty();
      } else {
         Item var2 = (Item)var0.get(var1);
         return var2 == Items.BRICK ? Optional.empty() : Optional.of(var2);
      }
   }

   public CompoundTag save(CompoundTag var1) {
      if (this.equals(EMPTY)) {
         return var1;
      } else {
         var1.put("sherds", (Tag)CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
         return var1;
      }
   }

   public List<Item> ordered() {
      return Stream.of(this.back, this.left, this.right, this.front).map((var0) -> {
         return (Item)var0.orElse(Items.BRICK);
      }).toList();
   }

   public static PotDecorations load(@Nullable CompoundTag var0) {
      return var0 != null && var0.contains("sherds") ? (PotDecorations)CODEC.parse(NbtOps.INSTANCE, var0.get("sherds")).result().orElse(EMPTY) : EMPTY;
   }

   public Optional<Item> back() {
      return this.back;
   }

   public Optional<Item> left() {
      return this.left;
   }

   public Optional<Item> right() {
      return this.right;
   }

   public Optional<Item> front() {
      return this.front;
   }

   static {
      CODEC = BuiltInRegistries.ITEM.byNameCodec().sizeLimitedListOf(4).xmap(PotDecorations::new, PotDecorations::ordered);
      STREAM_CODEC = ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list(4)).map(PotDecorations::new, PotDecorations::ordered);
   }
}
