package net.minecraft.world.item.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;

public final class TypedEntityData<IdType> implements TooltipProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TYPE_TAG = "id";
   final IdType type;
   final CompoundTag tag;

   public static <T> Codec<TypedEntityData<T>> codec(final Codec<T> var0) {
      return new Codec<TypedEntityData<T>>() {
         public <V> DataResult<Pair<TypedEntityData<T>, V>> decode(DynamicOps<V> var1, V var2) {
            return CustomData.COMPOUND_TAG_CODEC.decode(var1, var2).flatMap((var3) -> {
               CompoundTag var4 = ((CompoundTag)var3.getFirst()).copy();
               Tag var5 = var4.remove("id");
               return var5 == null ? DataResult.error(() -> "Expected 'id' field in " + String.valueOf(var2)) : var0.parse(asNbtOps(var1), var5).map((var2x) -> Pair.of(new TypedEntityData(var2x, var4), var3.getSecond()));
            });
         }

         public <V> DataResult<V> encode(TypedEntityData<T> var1, DynamicOps<V> var2, V var3) {
            return var0.encodeStart(asNbtOps(var2), var1.type).flatMap((var3x) -> {
               CompoundTag var4 = var1.tag.copy();
               var4.put("id", var3x);
               return CustomData.COMPOUND_TAG_CODEC.encode(var4, var2, var3);
            });
         }

         private static <T> DynamicOps<Tag> asNbtOps(DynamicOps<T> var0x) {
            if (var0x instanceof RegistryOps var1) {
               return var1.<Tag>withParent(NbtOps.INSTANCE);
            } else {
               return NbtOps.INSTANCE;
            }
         }

         // $FF: synthetic method
         public DataResult encode(final Object var1, final DynamicOps var2, final Object var3) {
            return this.encode((TypedEntityData)var1, var2, var3);
         }
      };
   }

   public static <B extends ByteBuf, T> StreamCodec<B, TypedEntityData<T>> streamCodec(StreamCodec<B, T> var0) {
      return StreamCodec.composite(var0, TypedEntityData::type, ByteBufCodecs.COMPOUND_TAG, TypedEntityData::tag, TypedEntityData::new);
   }

   TypedEntityData(IdType var1, CompoundTag var2) {
      super();
      this.type = var1;
      this.tag = stripId(var2);
   }

   public static <T> TypedEntityData<T> of(T var0, CompoundTag var1) {
      return new TypedEntityData<T>(var0, var1);
   }

   private static CompoundTag stripId(CompoundTag var0) {
      if (var0.contains("id")) {
         CompoundTag var1 = var0.copy();
         var1.remove("id");
         return var1;
      } else {
         return var0;
      }
   }

   public IdType type() {
      return this.type;
   }

   public boolean contains(String var1) {
      return this.tag.contains(var1);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TypedEntityData)) {
         return false;
      } else {
         TypedEntityData var2 = (TypedEntityData)var1;
         return this.type == var2.type && this.tag.equals(var2.tag);
      }
   }

   public int hashCode() {
      return 31 * this.type.hashCode() + this.tag.hashCode();
   }

   public String toString() {
      String var10000 = String.valueOf(this.type);
      return var10000 + " " + String.valueOf(this.tag);
   }

   public void loadInto(Entity var1) {
      try (ProblemReporter.ScopedCollector var2 = new ProblemReporter.ScopedCollector(var1.problemPath(), LOGGER)) {
         TagValueOutput var3 = TagValueOutput.createWithContext(var2, var1.registryAccess());
         var1.saveWithoutId(var3);
         CompoundTag var4 = var3.buildResult();
         UUID var5 = var1.getUUID();
         var4.merge(this.getUnsafe());
         var1.load(TagValueInput.create(var2, var1.registryAccess(), var4));
         var1.setUUID(var5);
      }

   }

   public boolean loadInto(BlockEntity var1, HolderLookup.Provider var2) {
      try (ProblemReporter.ScopedCollector var3 = new ProblemReporter.ScopedCollector(var1.problemPath(), LOGGER)) {
         TagValueOutput var4 = TagValueOutput.createWithContext(var3, var2);
         var1.saveCustomOnly((ValueOutput)var4);
         CompoundTag var5 = var4.buildResult();
         CompoundTag var6 = var5.copy();
         var5.merge(this.getUnsafe());
         if (!var5.equals(var6)) {
            try {
               var1.loadCustomOnly(TagValueInput.create(var3, var2, var5));
               var1.setChanged();
               return true;
            } catch (Exception var11) {
               LOGGER.warn("Failed to apply custom data to block entity at {}", var1.getBlockPos(), var11);

               try {
                  var1.loadCustomOnly(TagValueInput.create(var3.forChild(() -> "(rollback)"), var2, var6));
               } catch (Exception var10) {
                  LOGGER.warn("Failed to rollback block entity at {} after failure", var1.getBlockPos(), var10);
               }
            }
         }

         return false;
      }
   }

   private CompoundTag tag() {
      return this.tag;
   }

   /** @deprecated */
   @Deprecated
   public CompoundTag getUnsafe() {
      return this.tag;
   }

   public CompoundTag copyTagWithoutId() {
      return this.tag.copy();
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, DataComponentGetter var4) {
      if (this.type.getClass() == EntityType.class) {
         EntityType var5 = (EntityType)this.type;
         if (var1.isPeaceful() && !var5.isAllowedInPeaceful()) {
            var2.accept(Component.translatable("item.spawn_egg.peaceful").withStyle(ChatFormatting.RED));
         }
      }

   }
}
