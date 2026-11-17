package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ExtraDataFixUtils {
   public ExtraDataFixUtils() {
      super();
   }

   public static Dynamic<?> fixBlockPos(final Dynamic<?> pos) {
      Optional<Number> x = pos.get("X").asNumber().result();
      Optional<Number> y = pos.get("Y").asNumber().result();
      Optional<Number> z = pos.get("Z").asNumber().result();
      return !x.isEmpty() && !y.isEmpty() && !z.isEmpty() ? createBlockPos(pos, ((Number)x.get()).intValue(), ((Number)y.get()).intValue(), ((Number)z.get()).intValue()) : pos;
   }

   public static Dynamic<?> fixInlineBlockPos(final Dynamic<?> input, final String fieldX, final String fieldY, final String fieldZ, final String newField) {
      Optional<Number> x = input.get(fieldX).asNumber().result();
      Optional<Number> y = input.get(fieldY).asNumber().result();
      Optional<Number> z = input.get(fieldZ).asNumber().result();
      return !x.isEmpty() && !y.isEmpty() && !z.isEmpty() ? input.remove(fieldX).remove(fieldY).remove(fieldZ).set(newField, createBlockPos(input, ((Number)x.get()).intValue(), ((Number)y.get()).intValue(), ((Number)z.get()).intValue())) : input;
   }

   public static Dynamic<?> createBlockPos(final Dynamic<?> dynamic, final int x, final int y, final int z) {
      return dynamic.createIntList(IntStream.of(new int[]{x, y, z}));
   }

   public static <T, R> Typed<R> cast(final Type<R> type, final Typed<T> typed) {
      return new Typed(type, typed.getOps(), typed.getValue());
   }

   public static <T> Typed<T> cast(final Type<T> type, final Object value, final DynamicOps<?> ops) {
      return new Typed(type, ops, value);
   }

   public static Type<?> patchSubType(final Type<?> type, final Type<?> find, final Type<?> replace) {
      return type.all(typePatcher(find, replace), true, false).view().newType();
   }

   private static <A, B> TypeRewriteRule typePatcher(final Type<A> inputEntityType, final Type<B> outputEntityType) {
      RewriteResult<A, B> view = RewriteResult.create(View.create("Patcher", inputEntityType, outputEntityType, (var0) -> (var0x) -> {
            throw new UnsupportedOperationException();
         }), new BitSet());
      return TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(inputEntityType, view), PointFreeRule.nop(), true, true);
   }

   @SafeVarargs
   public static Function<Typed<?>, Typed<?>> chainAllFilters(final Function<Typed<?>, Typed<?>>... fixers) {
      return (typed) -> {
         for(Function<Typed<?>, Typed<?>> fixer : fixers) {
            typed = (Typed)fixer.apply(typed);
         }

         return typed;
      };
   }

   public static Dynamic<?> fixStringField(final Dynamic<?> dynamic, final String fieldName, final UnaryOperator<String> fix) {
      return dynamic.update(fieldName, (field) -> {
         DataResult var10000 = field.asString().map(fix);
         Objects.requireNonNull(dynamic);
         return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createString).result(), field);
      });
   }

   public static Optional<String> getPlainTranslationKey(final Dynamic<?> textComponent) {
      return textComponent.getMapValues().result().filter((fields) -> fields.size() == 1).flatMap((var1) -> textComponent.get("translate").asString().result());
   }

   public static String dyeColorIdToName(final int id) {
      String var10000;
      switch (id) {
         case 1 -> var10000 = "orange";
         case 2 -> var10000 = "magenta";
         case 3 -> var10000 = "light_blue";
         case 4 -> var10000 = "yellow";
         case 5 -> var10000 = "lime";
         case 6 -> var10000 = "pink";
         case 7 -> var10000 = "gray";
         case 8 -> var10000 = "light_gray";
         case 9 -> var10000 = "cyan";
         case 10 -> var10000 = "purple";
         case 11 -> var10000 = "blue";
         case 12 -> var10000 = "brown";
         case 13 -> var10000 = "green";
         case 14 -> var10000 = "red";
         case 15 -> var10000 = "black";
         default -> var10000 = "white";
      }

      return var10000;
   }

   public static <T> Typed<?> readAndSet(final Typed<?> target, final OpticFinder<T> optic, final Dynamic<?> value) {
      return target.set(optic, readTypedOrThrow(optic.type(), value, true));
   }

   public static <A, B> Typed<B> writeAndReadTypedOrThrow(final Typed<A> typed, final Type<B> newType, final UnaryOperator<Dynamic<?>> function) {
      Dynamic<?> dynamic = (Dynamic)typed.write().getOrThrow();
      return readTypedOrThrow(newType, (Dynamic)function.apply(dynamic), true);
   }

   public static <T> Typed<T> readTypedOrThrow(final Type<T> type, final Dynamic<?> dynamic) {
      return readTypedOrThrow(type, dynamic, false);
   }

   public static <T> Typed<T> readTypedOrThrow(final Type<T> type, final Dynamic<?> dynamic, final boolean acceptPartial) {
      DataResult<Typed<T>> result = type.readTyped(dynamic).map(Pair::getFirst);

      try {
         return acceptPartial ? (Typed)result.getPartialOrThrow(IllegalStateException::new) : (Typed)result.getOrThrow(IllegalStateException::new);
      } catch (IllegalStateException e) {
         CrashReport report = CrashReport.forThrowable(e, "Reading type");
         CrashReportCategory category = report.addCategory("Info");
         category.setDetail("Data", dynamic);
         category.setDetail("Type", type);
         throw new ReportedException(report);
      }
   }

   public static <T> Dynamic<T> fixComponentsIds(Dynamic<T> remainder, final ComponentIdManipulator manipulator, final boolean renameValuesInRemainder) {
      remainder = remainder.update("minecraft:tooltip_display", (tooltipDisplay) -> fixComponentIdsInTooltipDisplay(tooltipDisplay, manipulator));
      remainder = fixRemovedComponentIds(remainder, manipulator, renameValuesInRemainder);
      return remainder;
   }

   private static <T> Dynamic<T> fixComponentIdsInTooltipDisplay(final Dynamic<T> tooltipDisplay, final ComponentIdManipulator manipulator) {
      return tooltipDisplay.update("hidden_components", (hiddenComponents) -> (Dynamic)DataFixUtils.orElse(hiddenComponents.asStreamOpt().result().map((stream) -> hiddenComponents.createList(stream.flatMap((id) -> {
               Optional<String> maybeId = id.asString().result();
               if (maybeId.isPresent()) {
                  Stream var10000 = manipulator.apply(NamespacedSchema.ensureNamespaced((String)maybeId.get()));
                  Objects.requireNonNull(id);
                  return var10000.map(id::createString);
               } else {
                  return Stream.of(id);
               }
            }))), hiddenComponents));
   }

   private static <T> Dynamic<T> fixRemovedComponentIds(final Dynamic<T> remainder, final ComponentIdManipulator manipulator, final boolean renameValuesInRemainder) {
      Optional<Map<Dynamic<T>, Dynamic<T>>> maybeRemainderMap = remainder.getMapValues().result();
      if (maybeRemainderMap.isPresent()) {
         Map<Dynamic<T>, Dynamic<T>> newRemainderMap = new HashMap();
         ((Map)maybeRemainderMap.get()).forEach((key, value) -> {
            Optional<String> maybeStringKey = key.asString().result();
            if (maybeStringKey.isPresent()) {
               String stringKey = (String)maybeStringKey.get();
               if (stringKey.startsWith("!")) {
                  String componentId = stringKey.substring(1);
                  manipulator.apply(NamespacedSchema.ensureNamespaced(componentId)).forEach((newId) -> newRemainderMap.put(key.createString("!" + newId), value));
               } else if (renameValuesInRemainder) {
                  manipulator.apply(NamespacedSchema.ensureNamespaced(stringKey)).forEach((newId) -> newRemainderMap.put(key.createString(newId), value));
               } else {
                  newRemainderMap.put(key, value);
               }
            } else {
               newRemainderMap.put(key, value);
            }

         });
         return remainder.createMap(newRemainderMap);
      } else {
         return remainder;
      }
   }

   @FunctionalInterface
   public interface ComponentIdManipulator {
      Stream<String> apply(String componentName);

      static ComponentIdManipulator rename(final String from, final String to) {
         return (name) -> name.equals(from) ? Stream.of(to) : Stream.of(name);
      }

      static ComponentIdManipulator remove(final String id) {
         return (name) -> name.equals(id) ? Stream.empty() : Stream.of(name);
      }
   }
}
