package net.minecraft.world.level.storage;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.util.ProblemReporter;
import org.jspecify.annotations.Nullable;

public class TagValueInput implements ValueInput {
   private final ProblemReporter problemReporter;
   private final ValueInputContextHelper context;
   private final CompoundTag input;

   private TagValueInput(final ProblemReporter problemReporter, final ValueInputContextHelper context, final CompoundTag input) {
      super();
      this.problemReporter = problemReporter;
      this.context = context;
      this.input = input;
   }

   public static ValueInput create(final ProblemReporter problemReporter, final HolderLookup.Provider holders, final CompoundTag tag) {
      return new TagValueInput(problemReporter, new ValueInputContextHelper(holders, NbtOps.INSTANCE), tag);
   }

   public static ValueInput.ValueInputList create(final ProblemReporter problemReporter, final HolderLookup.Provider holders, final List<CompoundTag> tags) {
      return new CompoundListWrapper(problemReporter, new ValueInputContextHelper(holders, NbtOps.INSTANCE), tags);
   }

   public <T> Optional<T> read(final String name, final Codec<T> codec) {
      Tag tag = this.input.get(name);
      if (tag == null) {
         return Optional.empty();
      } else {
         DataResult var10000 = codec.parse(this.context.ops(), tag);
         Objects.requireNonNull(var10000);
         DataResult var4 = var10000;
         byte var5 = 0;
         Optional var8;
         //$FF: var5->value
         //0->com/mojang/serialization/DataResult$Success
         //1->com/mojang/serialization/DataResult$Error
         switch (var4.typeSwitch<invokedynamic>(var4, var5)) {
            case 0:
               DataResult.Success<T> success = (DataResult.Success)var4;
               var8 = Optional.of(success.value());
               break;
            case 1:
               DataResult.Error<T> error = (DataResult.Error)var4;
               this.problemReporter.report(new DecodeFromFieldFailedProblem(name, tag, error));
               var8 = error.partialValue();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var8;
      }
   }

   public <T> Optional<T> read(final MapCodec<T> codec) {
      DynamicOps<Tag> ops = this.context.ops();
      DataResult var10000 = ops.getMap(this.input).flatMap((map) -> codec.decode(ops, map));
      Objects.requireNonNull(var10000);
      DataResult var3 = var10000;
      byte var4 = 0;
      Optional var7;
      //$FF: var4->value
      //0->com/mojang/serialization/DataResult$Success
      //1->com/mojang/serialization/DataResult$Error
      switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
         case 0:
            DataResult.Success<T> success = (DataResult.Success)var3;
            var7 = Optional.of(success.value());
            break;
         case 1:
            DataResult.Error<T> error = (DataResult.Error)var3;
            this.problemReporter.report(new DecodeFromMapFailedProblem(error));
            var7 = error.partialValue();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var7;
   }

   private <T extends Tag> @Nullable T getOptionalTypedTag(final String name, final TagType<T> expectedType) {
      Tag tag = this.input.get(name);
      if (tag == null) {
         return null;
      } else {
         TagType<?> actualType = tag.getType();
         if (actualType != expectedType) {
            this.problemReporter.report(new UnexpectedTypeProblem(name, expectedType, actualType));
            return null;
         } else {
            return (T)tag;
         }
      }
   }

   private @Nullable NumericTag getNumericTag(final String name) {
      Tag tag = this.input.get(name);
      if (tag == null) {
         return null;
      } else if (tag instanceof NumericTag) {
         NumericTag numericTag = (NumericTag)tag;
         return numericTag;
      } else {
         this.problemReporter.report(new UnexpectedNonNumberProblem(name, tag.getType()));
         return null;
      }
   }

   public Optional<ValueInput> child(final String name) {
      CompoundTag compound = (CompoundTag)this.getOptionalTypedTag(name, CompoundTag.TYPE);
      return compound != null ? Optional.of(this.wrapChild(name, compound)) : Optional.empty();
   }

   public ValueInput childOrEmpty(final String name) {
      CompoundTag compound = (CompoundTag)this.getOptionalTypedTag(name, CompoundTag.TYPE);
      return compound != null ? this.wrapChild(name, compound) : this.context.empty();
   }

   public Optional<ValueInput.ValueInputList> childrenList(final String name) {
      ListTag list = (ListTag)this.getOptionalTypedTag(name, ListTag.TYPE);
      return list != null ? Optional.of(this.wrapList(name, this.context, list)) : Optional.empty();
   }

   public ValueInput.ValueInputList childrenListOrEmpty(final String name) {
      ListTag list = (ListTag)this.getOptionalTypedTag(name, ListTag.TYPE);
      return list != null ? this.wrapList(name, this.context, list) : this.context.emptyList();
   }

   public <T> Optional<ValueInput.TypedInputList<T>> list(final String name, final Codec<T> codec) {
      ListTag list = (ListTag)this.getOptionalTypedTag(name, ListTag.TYPE);
      return list != null ? Optional.of(this.wrapTypedList(name, list, codec)) : Optional.empty();
   }

   public <T> ValueInput.TypedInputList<T> listOrEmpty(final String name, final Codec<T> codec) {
      ListTag list = (ListTag)this.getOptionalTypedTag(name, ListTag.TYPE);
      return list != null ? this.wrapTypedList(name, list, codec) : this.context.emptyTypedList();
   }

   public boolean getBooleanOr(final String name, final boolean defaultValue) {
      NumericTag numericTag = this.getNumericTag(name);
      return numericTag != null ? numericTag.byteValue() != 0 : defaultValue;
   }

   public byte getByteOr(final String name, final byte defaultValue) {
      NumericTag numericTag = this.getNumericTag(name);
      return numericTag != null ? numericTag.byteValue() : defaultValue;
   }

   public int getShortOr(final String name, final short defaultValue) {
      NumericTag numericTag = this.getNumericTag(name);
      return numericTag != null ? numericTag.shortValue() : defaultValue;
   }

   public Optional<Integer> getInt(final String name) {
      NumericTag numericTag = this.getNumericTag(name);
      return numericTag != null ? Optional.of(numericTag.intValue()) : Optional.empty();
   }

   public int getIntOr(final String name, final int defaultValue) {
      NumericTag numericTag = this.getNumericTag(name);
      return numericTag != null ? numericTag.intValue() : defaultValue;
   }

   public long getLongOr(final String name, final long defaultValue) {
      NumericTag numericTag = this.getNumericTag(name);
      return numericTag != null ? numericTag.longValue() : defaultValue;
   }

   public Optional<Long> getLong(final String name) {
      NumericTag numericTag = this.getNumericTag(name);
      return numericTag != null ? Optional.of(numericTag.longValue()) : Optional.empty();
   }

   public float getFloatOr(final String name, final float defaultValue) {
      NumericTag numericTag = this.getNumericTag(name);
      return numericTag != null ? numericTag.floatValue() : defaultValue;
   }

   public double getDoubleOr(final String name, final double defaultValue) {
      NumericTag numericTag = this.getNumericTag(name);
      return numericTag != null ? numericTag.doubleValue() : defaultValue;
   }

   public Optional<String> getString(final String name) {
      StringTag tag = (StringTag)this.getOptionalTypedTag(name, StringTag.TYPE);
      return tag != null ? Optional.of(tag.value()) : Optional.empty();
   }

   public String getStringOr(final String name, final String defaultValue) {
      StringTag tag = (StringTag)this.getOptionalTypedTag(name, StringTag.TYPE);
      return tag != null ? tag.value() : defaultValue;
   }

   public Optional<int[]> getIntArray(final String name) {
      IntArrayTag tag = (IntArrayTag)this.getOptionalTypedTag(name, IntArrayTag.TYPE);
      return tag != null ? Optional.of(tag.getAsIntArray()) : Optional.empty();
   }

   public HolderLookup.Provider lookup() {
      return this.context.lookup();
   }

   private ValueInput wrapChild(final String name, final CompoundTag compoundTag) {
      return (ValueInput)(compoundTag.isEmpty() ? this.context.empty() : new TagValueInput(this.problemReporter.forChild(new ProblemReporter.FieldPathElement(name)), this.context, compoundTag));
   }

   private static ValueInput wrapChild(final ProblemReporter problemReporter, final ValueInputContextHelper context, final CompoundTag compoundTag) {
      return (ValueInput)(compoundTag.isEmpty() ? context.empty() : new TagValueInput(problemReporter, context, compoundTag));
   }

   private ValueInput.ValueInputList wrapList(final String name, final ValueInputContextHelper context, final ListTag list) {
      return (ValueInput.ValueInputList)(list.isEmpty() ? context.emptyList() : new ListWrapper(this.problemReporter, name, context, list));
   }

   private <T> ValueInput.TypedInputList<T> wrapTypedList(final String name, final ListTag list, final Codec<T> codec) {
      return (ValueInput.TypedInputList<T>)(list.isEmpty() ? this.context.emptyTypedList() : new TypedListWrapper(this.problemReporter, name, this.context, codec, list));
   }

   private static class ListWrapper implements ValueInput.ValueInputList {
      private final ProblemReporter problemReporter;
      private final String name;
      private final ValueInputContextHelper context;
      private final ListTag list;

      private ListWrapper(final ProblemReporter problemReporter, final String name, final ValueInputContextHelper context, final ListTag list) {
         super();
         this.problemReporter = problemReporter;
         this.name = name;
         this.context = context;
         this.list = list;
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      private ProblemReporter reporterForChild(final int index) {
         return this.problemReporter.forChild(new ProblemReporter.IndexedFieldPathElement(this.name, index));
      }

      private void reportIndexUnwrapProblem(final int index, final Tag value) {
         this.problemReporter.report(new UnexpectedListElementTypeProblem(this.name, index, CompoundTag.TYPE, value.getType()));
      }

      public Stream<ValueInput> stream() {
         return Streams.mapWithIndex(this.list.stream(), (value, index) -> {
            if (value instanceof CompoundTag compoundTag) {
               return TagValueInput.wrapChild(this.reporterForChild((int)index), this.context, compoundTag);
            } else {
               this.reportIndexUnwrapProblem((int)index, value);
               return null;
            }
         }).filter(Objects::nonNull);
      }

      public Iterator<ValueInput> iterator() {
         final Iterator<Tag> iterator = this.list.iterator();
         return new AbstractIterator<ValueInput>() {
            private int index;

            {
               Objects.requireNonNull(ListWrapper.this);
            }

            protected @Nullable ValueInput computeNext() {
               while(iterator.hasNext()) {
                  Tag value = (Tag)iterator.next();
                  int currentIndex = this.index++;
                  if (value instanceof CompoundTag compoundTag) {
                     return TagValueInput.wrapChild(ListWrapper.this.reporterForChild(currentIndex), ListWrapper.this.context, compoundTag);
                  }

                  ListWrapper.this.reportIndexUnwrapProblem(currentIndex, value);
               }

               return (ValueInput)this.endOfData();
            }
         };
      }
   }

   private static class TypedListWrapper<T> implements ValueInput.TypedInputList<T> {
      private final ProblemReporter problemReporter;
      private final String name;
      private final ValueInputContextHelper context;
      private final Codec<T> codec;
      private final ListTag list;

      private TypedListWrapper(final ProblemReporter problemReporter, final String name, final ValueInputContextHelper context, final Codec<T> codec, final ListTag list) {
         super();
         this.problemReporter = problemReporter;
         this.name = name;
         this.context = context;
         this.codec = codec;
         this.list = list;
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      private void reportIndexUnwrapProblem(final int index, final Tag value, final DataResult.Error<?> error) {
         this.problemReporter.report(new DecodeFromListFailedProblem(this.name, index, value, error));
      }

      public Stream<T> stream() {
         return Streams.mapWithIndex(this.list.stream(), (value, index) -> {
            DataResult var10000 = this.codec.parse(this.context.ops(), value);
            Objects.requireNonNull(var10000);
            DataResult selector0$temp = var10000;
            int index$1 = 0;
            Object var8;
            //$FF: index$1->value
            //0->com/mojang/serialization/DataResult$Success
            //1->com/mojang/serialization/DataResult$Error
            switch (selector0$temp.typeSwitch<invokedynamic>(selector0$temp, index$1)) {
               case 0:
                  DataResult.Success<T> success = (DataResult.Success)selector0$temp;
                  var8 = success.value();
                  break;
               case 1:
                  DataResult.Error<T> error = (DataResult.Error)selector0$temp;
                  this.reportIndexUnwrapProblem((int)index, value, error);
                  var8 = error.partialValue().orElse((Object)null);
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return var8;
         }).filter(Objects::nonNull);
      }

      public Iterator<T> iterator() {
         final ListIterator<Tag> iterator = this.list.listIterator();
         return new AbstractIterator<T>() {
            {
               Objects.requireNonNull(TypedListWrapper.this);
            }

            protected @Nullable T computeNext() {
               while(true) {
                  if (iterator.hasNext()) {
                     int index = iterator.nextIndex();
                     Tag value = (Tag)iterator.next();
                     DataResult var10000 = TypedListWrapper.this.codec.parse(TypedListWrapper.this.context.ops(), value);
                     Objects.requireNonNull(var10000);
                     DataResult var3 = var10000;
                     byte var4 = 0;
                     //$FF: var4->value
                     //0->com/mojang/serialization/DataResult$Success
                     //1->com/mojang/serialization/DataResult$Error
                     switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
                        case 0:
                           DataResult.Success<T> success = (DataResult.Success)var3;
                           return (T)success.value();
                        case 1:
                           DataResult.Error<T> error = (DataResult.Error)var3;
                           TypedListWrapper.this.reportIndexUnwrapProblem(index, value, error);
                           if (!error.partialValue().isPresent()) {
                              continue;
                           }

                           return (T)error.partialValue().get();
                        default:
                           throw new MatchException((String)null, (Throwable)null);
                     }
                  }

                  return (T)this.endOfData();
               }
            }
         };
      }
   }

   private static class CompoundListWrapper implements ValueInput.ValueInputList {
      private final ProblemReporter problemReporter;
      private final ValueInputContextHelper context;
      private final List<CompoundTag> list;

      public CompoundListWrapper(final ProblemReporter problemReporter, final ValueInputContextHelper context, final List<CompoundTag> list) {
         super();
         this.problemReporter = problemReporter;
         this.context = context;
         this.list = list;
      }

      private ValueInput wrapChild(final int index, final CompoundTag compoundTag) {
         return TagValueInput.wrapChild(this.problemReporter.forChild(new ProblemReporter.IndexedPathElement(index)), this.context, compoundTag);
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      public Stream<ValueInput> stream() {
         return Streams.mapWithIndex(this.list.stream(), (value, index) -> this.wrapChild((int)index, value));
      }

      public Iterator<ValueInput> iterator() {
         final ListIterator<CompoundTag> iterator = this.list.listIterator();
         return new AbstractIterator<ValueInput>() {
            {
               Objects.requireNonNull(CompoundListWrapper.this);
            }

            protected @Nullable ValueInput computeNext() {
               if (iterator.hasNext()) {
                  int index = iterator.nextIndex();
                  CompoundTag value = (CompoundTag)iterator.next();
                  return CompoundListWrapper.this.wrapChild(index, value);
               } else {
                  return (ValueInput)this.endOfData();
               }
            }
         };
      }
   }

   public static record DecodeFromFieldFailedProblem(String name, Tag tag, DataResult.Error<?> error) implements ProblemReporter.Problem {
      public DecodeFromFieldFailedProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.tag);
         return "Failed to decode value '" + var10000 + "' from field '" + this.name + "': " + this.error.message();
      }
   }

   public static record DecodeFromListFailedProblem(String name, int index, Tag tag, DataResult.Error<?> error) implements ProblemReporter.Problem {
      public DecodeFromListFailedProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.tag);
         return "Failed to decode value '" + var10000 + "' from field '" + this.name + "' at index " + this.index + "': " + this.error.message();
      }
   }

   public static record DecodeFromMapFailedProblem(DataResult.Error<?> error) implements ProblemReporter.Problem {
      public DecodeFromMapFailedProblem {
         super();
      }

      public String description() {
         return "Failed to decode from map: " + this.error.message();
      }
   }

   public static record UnexpectedTypeProblem(String name, TagType<?> expected, TagType<?> actual) implements ProblemReporter.Problem {
      public UnexpectedTypeProblem {
         super();
      }

      public String description() {
         String var10000 = this.name;
         return "Expected field '" + var10000 + "' to contain value of type " + this.expected.getName() + ", but got " + this.actual.getName();
      }
   }

   public static record UnexpectedNonNumberProblem(String name, TagType<?> actual) implements ProblemReporter.Problem {
      public UnexpectedNonNumberProblem {
         super();
      }

      public String description() {
         String var10000 = this.name;
         return "Expected field '" + var10000 + "' to contain number, but got " + this.actual.getName();
      }
   }

   public static record UnexpectedListElementTypeProblem(String name, int index, TagType<?> expected, TagType<?> actual) implements ProblemReporter.Problem {
      public UnexpectedListElementTypeProblem {
         super();
      }

      public String description() {
         String var10000 = this.name;
         return "Expected list '" + var10000 + "' to contain at index " + this.index + " value of type " + this.expected.getName() + ", but got " + this.actual.getName();
      }
   }
}
