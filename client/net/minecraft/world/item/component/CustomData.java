package net.minecraft.world.item.component;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

public final class CustomData {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final CustomData EMPTY = new CustomData(new CompoundTag());
   public static final Codec<CustomData> CODEC;
   public static final Codec<CustomData> CODEC_WITH_ID;
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

   public static Predicate<ItemStack> itemMatcher(DataComponentType<CustomData> var0, CompoundTag var1) {
      return (var2) -> {
         CustomData var3 = (CustomData)var2.getOrDefault(var0, EMPTY);
         return var3.matchedBy(var1);
      };
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

   public void loadInto(Entity var1) {
      CompoundTag var2 = var1.saveWithoutId(new CompoundTag());
      UUID var3 = var1.getUUID();
      var2.merge(this.tag);
      var1.load(var2);
      var1.setUUID(var3);
   }

   public boolean loadInto(BlockEntity var1, HolderLookup.Provider var2) {
      CompoundTag var3 = var1.saveCustomOnly(var2);
      CompoundTag var4 = var3.copy();
      var3.merge(this.tag);
      if (!var3.equals(var4)) {
         try {
            var1.loadCustomOnly(var3, var2);
            var1.setChanged();
            return true;
         } catch (Exception var8) {
            LOGGER.warn("Failed to apply custom data to block entity at {}", var1.getBlockPos(), var8);

            try {
               var1.loadCustomOnly(var4, var2);
            } catch (Exception var7) {
               LOGGER.warn("Failed to rollback block entity at {} after failure", var1.getBlockPos(), var7);
            }
         }
      }

      return false;
   }

   public <T> DataResult<CustomData> update(DynamicOps<Tag> var1, MapEncoder<T> var2, T var3) {
      return var2.encode(var3, var1, var1.mapBuilder()).build(this.tag).map((var0) -> {
         return new CustomData((CompoundTag)var0);
      });
   }

   public <T> DataResult<T> read(MapDecoder<T> var1) {
      return this.read(NbtOps.INSTANCE, var1);
   }

   public <T> DataResult<T> read(DynamicOps<Tag> var1, MapDecoder<T> var2) {
      MapLike var3 = (MapLike)var1.getMap(this.tag).getOrThrow();
      return var2.decode(var1, var3);
   }

   public int size() {
      return this.tag.size();
   }

   public boolean isEmpty() {
      return this.tag.isEmpty();
   }

   public CompoundTag copyTag() {
      return this.tag.copy();
   }

   public boolean contains(String var1) {
      return this.tag.contains(var1);
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

   /** @deprecated */
   @Deprecated
   public CompoundTag getUnsafe() {
      return this.tag;
   }

   static {
      CODEC = Codec.withAlternative(CompoundTag.CODEC, TagParser.AS_CODEC).xmap(CustomData::new, (var0) -> {
         return var0.tag;
      });
      CODEC_WITH_ID = CODEC.validate((var0) -> {
         return var0.getUnsafe().contains("id", 8) ? DataResult.success(var0) : DataResult.error(() -> {
            return "Missing id for entity in: " + String.valueOf(var0);
         });
      });
      STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(CustomData::new, (var0) -> {
         return var0.tag;
      });
   }
}
