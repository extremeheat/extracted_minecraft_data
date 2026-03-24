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

   private CustomData(final CompoundTag tag) {
      super();
      this.tag = tag;
   }

   public static CustomData of(final CompoundTag tag) {
      return new CustomData(tag.copy());
   }

   public boolean matchedBy(final CompoundTag expectedTag) {
      return NbtUtils.compareNbt(expectedTag, this.tag, true);
   }

   public static void update(final DataComponentType<CustomData> component, final ItemStack itemStack, final Consumer<CompoundTag> consumer) {
      CustomData newData = ((CustomData)itemStack.getOrDefault(component, EMPTY)).update(consumer);
      if (newData.tag.isEmpty()) {
         itemStack.remove(component);
      } else {
         itemStack.set(component, newData);
      }

   }

   public static void set(final DataComponentType<CustomData> component, final ItemStack itemStack, final CompoundTag tag) {
      if (!tag.isEmpty()) {
         itemStack.set(component, of(tag));
      } else {
         itemStack.remove(component);
      }

   }

   public CustomData update(final Consumer<CompoundTag> consumer) {
      CompoundTag newTag = this.tag.copy();
      consumer.accept(newTag);
      return new CustomData(newTag);
   }

   public boolean isEmpty() {
      return this.tag.isEmpty();
   }

   public CompoundTag copyTag() {
      return this.tag.copy();
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else if (obj instanceof CustomData) {
         CustomData customData = (CustomData)obj;
         return this.tag.equals(customData.tag);
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
      CODEC = COMPOUND_TAG_CODEC.xmap(CustomData::new, (data) -> data.tag);
      STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(CustomData::new, (data) -> data.tag);
   }
}
