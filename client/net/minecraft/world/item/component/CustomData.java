package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public final class CustomData {
   public static final CustomData EMPTY = new CustomData(new CompoundTag());
   public static final Codec<CompoundTag> COMPOUND_TAG_CODEC;
   public static final Codec<CustomData> CODEC;
   /** @deprecated */
   @Deprecated
   public static final StreamCodec<ByteBuf, CustomData> STREAM_CODEC;
   private final CompoundTag tag;

   private CustomData(CompoundTag var1) {
      super();
      this.tag = var1;
   }

   public static CustomData of(CompoundTag var0) {
      return new CustomData(var0.copy());
   }

   public boolean matchedBy(CompoundTag var1) {
      return NbtUtils.compareNbt(var1, this.tag, true);
   }

   public static void update(DataComponentType<CustomData> var0, ItemStack var1, Consumer<CompoundTag> var2) {
      CustomData var3 = ((CustomData)var1.getOrDefault(var0, EMPTY)).update(var2);
      if (var3.tag.isEmpty()) {
         var1.remove(var0);
      } else {
         var1.set(var0, var3);
      }

   }

   public static void set(DataComponentType<CustomData> var0, ItemStack var1, CompoundTag var2) {
      if (!var2.isEmpty()) {
         var1.set(var0, of(var2));
      } else {
         var1.remove(var0);
      }

   }

   public CustomData update(Consumer<CompoundTag> var1) {
      CompoundTag var2 = this.tag.copy();
      var1.accept(var2);
      return new CustomData(var2);
   }

   public boolean isEmpty() {
      return this.tag.isEmpty();
   }

   public CompoundTag copyTag() {
      return this.tag.copy();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof CustomData) {
         CustomData var2 = (CustomData)var1;
         return this.tag.equals(var2.tag);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.tag.hashCode();
   }

   public String toString() {
      return this.tag.toString();
   }

   static {
      COMPOUND_TAG_CODEC = Codec.withAlternative(CompoundTag.CODEC, TagParser.FLATTENED_CODEC);
      CODEC = COMPOUND_TAG_CODEC.xmap(CustomData::new, (var0) -> var0.tag);
      STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(CustomData::new, (var0) -> var0.tag);
   }
}
