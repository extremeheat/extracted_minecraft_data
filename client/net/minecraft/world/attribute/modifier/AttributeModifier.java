package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.attribute.EnvironmentAttribute;

public interface AttributeModifier<Subject, Argument> {
   Map<OperationId, AttributeModifier<Boolean, ?>> BOOLEAN_LIBRARY = Map.of(AttributeModifier.OperationId.AND, BooleanModifier.AND, AttributeModifier.OperationId.NAND, BooleanModifier.NAND, AttributeModifier.OperationId.OR, BooleanModifier.OR, AttributeModifier.OperationId.NOR, BooleanModifier.NOR, AttributeModifier.OperationId.XOR, BooleanModifier.XOR, AttributeModifier.OperationId.XNOR, BooleanModifier.XNOR);
   Map<OperationId, AttributeModifier<Float, ?>> FLOAT_LIBRARY = Map.of(AttributeModifier.OperationId.ALPHA_BLEND, FloatModifier.ALPHA_BLEND, AttributeModifier.OperationId.ADD, FloatModifier.ADD, AttributeModifier.OperationId.SUBTRACT, FloatModifier.SUBTRACT, AttributeModifier.OperationId.MULTIPLY, FloatModifier.MULTIPLY, AttributeModifier.OperationId.MINIMUM, FloatModifier.MINIMUM, AttributeModifier.OperationId.MAXIMUM, FloatModifier.MAXIMUM);
   Map<OperationId, AttributeModifier<Integer, ?>> COLOR_LIBRARY = Map.of(AttributeModifier.OperationId.ALPHA_BLEND, ColorModifier.ALPHA_BLEND, AttributeModifier.OperationId.ADD, ColorModifier.ADD, AttributeModifier.OperationId.SUBTRACT, ColorModifier.SUBTRACT, AttributeModifier.OperationId.MULTIPLY, ColorModifier.MULTIPLY);

   static <Value> AttributeModifier<Value, Value> override() {
      return AttributeModifier.OverrideModifier.INSTANCE;
   }

   Subject apply(Subject var1, Argument var2);

   Codec<Argument> argumentCodec(EnvironmentAttribute<Subject> var1);

   public static record OverrideModifier<Value>() implements AttributeModifier<Value, Value> {
      static final OverrideModifier<?> INSTANCE = new OverrideModifier();

      public OverrideModifier() {
         super();
      }

      public Value apply(Value var1, Value var2) {
         return var2;
      }

      public Codec<Value> argumentCodec(EnvironmentAttribute<Value> var1) {
         return var1.valueCodec();
      }
   }

   public static enum OperationId implements StringRepresentable {
      OVERRIDE("override"),
      ALPHA_BLEND("alpha_blend"),
      ADD("add"),
      SUBTRACT("subtract"),
      MULTIPLY("multiply"),
      MINIMUM("minimum"),
      MAXIMUM("maximum"),
      AND("and"),
      NAND("nand"),
      OR("or"),
      NOR("nor"),
      XOR("xor"),
      XNOR("xnor");

      public static final Codec<OperationId> CODEC = StringRepresentable.<OperationId>fromEnum(OperationId::values);
      private final String name;

      private OperationId(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static OperationId[] $values() {
         return new OperationId[]{OVERRIDE, ALPHA_BLEND, ADD, SUBTRACT, MULTIPLY, MINIMUM, MAXIMUM, AND, NAND, OR, NOR, XOR, XNOR};
      }
   }
}
