package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

public interface AttributeModifier<Subject, Argument> {
   Map<OperationId, AttributeModifier<Boolean, ?>> BOOLEAN_LIBRARY = Map.of(AttributeModifier.OperationId.AND, BooleanModifier.AND, AttributeModifier.OperationId.NAND, BooleanModifier.NAND, AttributeModifier.OperationId.OR, BooleanModifier.OR, AttributeModifier.OperationId.NOR, BooleanModifier.NOR, AttributeModifier.OperationId.XOR, BooleanModifier.XOR, AttributeModifier.OperationId.XNOR, BooleanModifier.XNOR);
   Map<OperationId, AttributeModifier<Float, ?>> FLOAT_LIBRARY = Map.of(AttributeModifier.OperationId.ALPHA_BLEND, FloatModifier.ALPHA_BLEND, AttributeModifier.OperationId.ADD, FloatModifier.ADD, AttributeModifier.OperationId.SUBTRACT, FloatModifier.SUBTRACT, AttributeModifier.OperationId.MULTIPLY, FloatModifier.MULTIPLY, AttributeModifier.OperationId.MINIMUM, FloatModifier.MINIMUM, AttributeModifier.OperationId.MAXIMUM, FloatModifier.MAXIMUM);
   Map<OperationId, AttributeModifier<Vector3fc, ?>> RGB_COLOR_LIBRARY = Map.of(AttributeModifier.OperationId.ALPHA_BLEND, ColorModifier.ALPHA_BLEND_RGB, AttributeModifier.OperationId.ADD, ColorModifier.ADD_RGB, AttributeModifier.OperationId.SUBTRACT, ColorModifier.SUBTRACT_RGB, AttributeModifier.OperationId.MULTIPLY, ColorModifier.MULTIPLY_RGB, AttributeModifier.OperationId.BLEND_TO_GRAY, ColorModifier.BLEND_TO_GRAY_RGB);
   Map<OperationId, AttributeModifier<Vector4fc, ?>> ARGB_COLOR_LIBRARY = Map.of(AttributeModifier.OperationId.ALPHA_BLEND, ColorModifier.ALPHA_BLEND_ARGB, AttributeModifier.OperationId.ADD, ColorModifier.ADD_ARGB, AttributeModifier.OperationId.SUBTRACT, ColorModifier.SUBTRACT_ARGB, AttributeModifier.OperationId.MULTIPLY, ColorModifier.MULTIPLY_ARGB, AttributeModifier.OperationId.BLEND_TO_GRAY, ColorModifier.BLEND_TO_GRAY_ARGB);
   Map<OperationId, AttributeModifier<Integer, ?>> INTEGER_LIBRARY = Map.of(AttributeModifier.OperationId.ADD, IntegerModifier.ADD, AttributeModifier.OperationId.SUBTRACT, IntegerModifier.SUBTRACT, AttributeModifier.OperationId.MULTIPLY, IntegerModifier.MULTIPLY, AttributeModifier.OperationId.MINIMUM, IntegerModifier.MINIMUM, AttributeModifier.OperationId.MAXIMUM, IntegerModifier.MAXIMUM);
   Map<OperationId, AttributeModifier<MobSpawnSettings, ?>> MOB_SPAWN_SETTINGS_LIBRARY = Map.of(AttributeModifier.OperationId.OVERLAY, MobSpawnSettingsModifier.overlay());

   static <Value> AttributeModifier<Value, Value> override() {
      return AttributeModifier.OverrideModifier.INSTANCE;
   }

   static <Element> Map<OperationId, AttributeModifier<List<Element>, ?>> listLibrary() {
      return Map.of(AttributeModifier.OperationId.APPEND, ListModifier.append());
   }

   Subject apply(Subject subject, Argument argument);

   Codec<Argument> argumentCodec(EnvironmentAttribute<Subject> attribute);

   LerpFunction<Argument> argumentKeyframeLerp(EnvironmentAttribute<Subject> attribute);

   public static record OverrideModifier<Value>() implements AttributeModifier<Value, Value> {
      private static final OverrideModifier<?> INSTANCE = new OverrideModifier();

      public OverrideModifier() {
         super();
      }

      public Value apply(final Value subject, final Value argument) {
         return argument;
      }

      public Codec<Value> argumentCodec(final EnvironmentAttribute<Value> attribute) {
         return attribute.valueCodec();
      }

      public LerpFunction<Value> argumentKeyframeLerp(final EnvironmentAttribute<Value> attribute) {
         return attribute.type().keyframeLerp();
      }
   }

   public static enum OperationId implements StringRepresentable {
      OVERRIDE("override"),
      ALPHA_BLEND("alpha_blend"),
      ADD("add"),
      SUBTRACT("subtract"),
      MULTIPLY("multiply"),
      BLEND_TO_GRAY("blend_to_gray"),
      MINIMUM("minimum"),
      MAXIMUM("maximum"),
      AND("and"),
      NAND("nand"),
      OR("or"),
      NOR("nor"),
      XOR("xor"),
      XNOR("xnor"),
      APPEND("append"),
      OVERLAY("overlay");

      public static final Codec<OperationId> CODEC = StringRepresentable.<OperationId>fromEnum(OperationId::values);
      private final String name;

      private OperationId(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static OperationId[] $values() {
         return new OperationId[]{OVERRIDE, ALPHA_BLEND, ADD, SUBTRACT, MULTIPLY, BLEND_TO_GRAY, MINIMUM, MAXIMUM, AND, NAND, OR, NOR, XOR, XNOR, APPEND, OVERLAY};
      }
   }
}
