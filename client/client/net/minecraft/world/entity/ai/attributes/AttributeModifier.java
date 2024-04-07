package net.minecraft.world.entity.ai.attributes;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public record AttributeModifier(UUID id, String name, double amount, AttributeModifier.Operation operation) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<AttributeModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               UUIDUtil.CODEC.fieldOf("uuid").forGetter(AttributeModifier::id),
               Codec.STRING.fieldOf("name").forGetter(var0x -> var0x.name),
               Codec.DOUBLE.fieldOf("amount").forGetter(AttributeModifier::amount),
               AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeModifier::operation)
            )
            .apply(var0, AttributeModifier::new)
   );
   public static final Codec<AttributeModifier> CODEC = MAP_CODEC.codec();
   public static final StreamCodec<ByteBuf, AttributeModifier> STREAM_CODEC = StreamCodec.composite(
      UUIDUtil.STREAM_CODEC,
      AttributeModifier::id,
      ByteBufCodecs.STRING_UTF8,
      var0 -> var0.name,
      ByteBufCodecs.DOUBLE,
      AttributeModifier::amount,
      AttributeModifier.Operation.STREAM_CODEC,
      AttributeModifier::operation,
      AttributeModifier::new
   );

   public AttributeModifier(String var1, double var2, AttributeModifier.Operation var4) {
      this(Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()), var1, var2, var4);
   }

   public AttributeModifier(UUID id, String name, double amount, AttributeModifier.Operation operation) {
      super();
      this.id = id;
      this.name = name;
      this.amount = amount;
      this.operation = operation;
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", this.name);
      var1.putDouble("Amount", this.amount);
      var1.putInt("Operation", this.operation.id());
      var1.putUUID("UUID", this.id);
      return var1;
   }

   @Nullable
   public static AttributeModifier load(CompoundTag var0) {
      try {
         UUID var1 = var0.getUUID("UUID");
         AttributeModifier.Operation var2 = AttributeModifier.Operation.BY_ID.apply(var0.getInt("Operation"));
         return new AttributeModifier(var1, var0.getString("Name"), var0.getDouble("Amount"), var2);
      } catch (Exception var3) {
         LOGGER.warn("Unable to create attribute: {}", var3.getMessage());
         return null;
      }
   }

   public static enum Operation implements StringRepresentable {
      ADD_VALUE("add_value", 0),
      ADD_MULTIPLIED_BASE("add_multiplied_base", 1),
      ADD_MULTIPLIED_TOTAL("add_multiplied_total", 2);

      public static final IntFunction<AttributeModifier.Operation> BY_ID = ByIdMap.continuous(
         AttributeModifier.Operation::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO
      );
      public static final StreamCodec<ByteBuf, AttributeModifier.Operation> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, AttributeModifier.Operation::id);
      public static final Codec<AttributeModifier.Operation> CODEC = StringRepresentable.fromEnum(AttributeModifier.Operation::values);
      private final String name;
      private final int id;

      private Operation(String var3, int var4) {
         this.name = var3;
         this.id = var4;
      }

      public int id() {
         return this.id;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
