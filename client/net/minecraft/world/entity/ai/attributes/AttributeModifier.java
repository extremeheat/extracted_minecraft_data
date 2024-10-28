package net.minecraft.world.entity.ai.attributes;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public record AttributeModifier(ResourceLocation id, double amount, Operation operation) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<AttributeModifier> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ResourceLocation.CODEC.fieldOf("id").forGetter(AttributeModifier::id), Codec.DOUBLE.fieldOf("amount").forGetter(AttributeModifier::amount), AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeModifier::operation)).apply(var0, AttributeModifier::new);
   });
   public static final Codec<AttributeModifier> CODEC;
   public static final StreamCodec<ByteBuf, AttributeModifier> STREAM_CODEC;

   public AttributeModifier(ResourceLocation var1, double var2, Operation var4) {
      super();
      this.id = var1;
      this.amount = var2;
      this.operation = var4;
   }

   public CompoundTag save() {
      DataResult var1 = CODEC.encode(this, NbtOps.INSTANCE, new CompoundTag());
      return (CompoundTag)var1.getOrThrow();
   }

   @Nullable
   public static AttributeModifier load(CompoundTag var0) {
      DataResult var1 = CODEC.parse(NbtOps.INSTANCE, var0);
      if (var1.isSuccess()) {
         return (AttributeModifier)var1.getOrThrow();
      } else {
         LOGGER.warn("Unable to create attribute: {}", ((DataResult.Error)var1.error().get()).message());
         return null;
      }
   }

   public boolean is(ResourceLocation var1) {
      return var1.equals(this.id);
   }

   public ResourceLocation id() {
      return this.id;
   }

   public double amount() {
      return this.amount;
   }

   public Operation operation() {
      return this.operation;
   }

   static {
      CODEC = MAP_CODEC.codec();
      STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, AttributeModifier::id, ByteBufCodecs.DOUBLE, AttributeModifier::amount, AttributeModifier.Operation.STREAM_CODEC, AttributeModifier::operation, AttributeModifier::new);
   }

   public static enum Operation implements StringRepresentable {
      ADD_VALUE("add_value", 0),
      ADD_MULTIPLIED_BASE("add_multiplied_base", 1),
      ADD_MULTIPLIED_TOTAL("add_multiplied_total", 2);

      public static final IntFunction<Operation> BY_ID = ByIdMap.continuous(Operation::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Operation> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Operation::id);
      public static final Codec<Operation> CODEC = StringRepresentable.fromEnum(Operation::values);
      private final String name;
      private final int id;

      private Operation(final String var3, final int var4) {
         this.name = var3;
         this.id = var4;
      }

      public int id() {
         return this.id;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Operation[] $values() {
         return new Operation[]{ADD_VALUE, ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL};
      }
   }
}
