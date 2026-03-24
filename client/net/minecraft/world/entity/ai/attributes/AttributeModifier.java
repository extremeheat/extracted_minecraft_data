package net.minecraft.world.entity.ai.attributes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public record AttributeModifier(Identifier id, double amount, Operation operation) {
   public static final MapCodec<AttributeModifier> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("id").forGetter(AttributeModifier::id), Codec.DOUBLE.fieldOf("amount").forGetter(AttributeModifier::amount), AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeModifier::operation)).apply(i, AttributeModifier::new));
   public static final Codec<AttributeModifier> CODEC;
   public static final StreamCodec<ByteBuf, AttributeModifier> STREAM_CODEC;

   public AttributeModifier {
      super();
   }

   public boolean is(final Identifier id) {
      return id.equals(this.id);
   }

   static {
      CODEC = MAP_CODEC.codec();
      STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, AttributeModifier::id, ByteBufCodecs.DOUBLE, AttributeModifier::amount, AttributeModifier.Operation.STREAM_CODEC, AttributeModifier::operation, AttributeModifier::new);
   }

   public static enum Operation implements StringRepresentable {
      ADD_VALUE("add_value", 0),
      ADD_MULTIPLIED_BASE("add_multiplied_base", 1),
      ADD_MULTIPLIED_TOTAL("add_multiplied_total", 2);

      public static final IntFunction<Operation> BY_ID = ByIdMap.<Operation>continuous(Operation::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Operation> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Operation::id);
      public static final Codec<Operation> CODEC = StringRepresentable.<Operation>fromEnum(Operation::values);
      private final String name;
      private final int id;

      private Operation(final String name, final int id) {
         this.name = name;
         this.id = id;
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
