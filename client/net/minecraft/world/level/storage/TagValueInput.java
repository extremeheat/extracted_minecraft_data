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

   private TagValueInput(ProblemReporter var1, ValueInputContextHelper var2, CompoundTag var3) {
      super();
      this.problemReporter = var1;
      this.context = var2;
      this.input = var3;
   }

   public static ValueInput create(ProblemReporter var0, HolderLookup.Provider var1, CompoundTag var2) {
      return new TagValueInput(var0, new ValueInputContextHelper(var1, NbtOps.INSTANCE), var2);
   }

   public static ValueInput.ValueInputList create(ProblemReporter var0, HolderLookup.Provider var1, List<CompoundTag> var2) {
      return new CompoundListWrapper(var0, new ValueInputContextHelper(var1, NbtOps.INSTANCE), var2);
   }

   public <T> Optional<T> read(String var1, Codec<T> var2) {
      Tag var3 = this.input.get(var1);
      if (var3 == null) {
         return Optional.empty();
      } else {
         DataResult var10000 = var2.parse(this.context.ops(), var3);
         Objects.requireNonNull(var10000);
         DataResult var4 = var10000;
         byte var5 = 0;
         Optional var8;
         //$FF: var5->value
         //0->com/mojang/serialization/DataResult$Success
         //1->com/mojang/serialization/DataResult$Error
         switch (var4.typeSwitch<invokedynamic>(var4, var5)) {
            case 0:
               DataResult.Success var6 = (DataResult.Success)var4;
               var8 = Optional.of(var6.value());
               break;
            case 1:
               DataResult.Error var7 = (DataResult.Error)var4;
               this.problemReporter.report(new DecodeFromFieldFailedProblem(var1, var3, var7));
               var8 = var7.partialValue();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var8;
      }
   }

   public <T> Optional<T> read(MapCodec<T> var1) {
      DynamicOps var2 = this.context.ops();
      DataResult var10000 = var2.getMap(this.input).flatMap((var2x) -> var1.decode(var2, var2x));
      Objects.requireNonNull(var10000);
      DataResult var3 = var10000;
      byte var4 = 0;
      Optional var7;
      //$FF: var4->value
      //0->com/mojang/serialization/DataResult$Success
      //1->com/mojang/serialization/DataResult$Error
      switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
         case 0:
            DataResult.Success var5 = (DataResult.Success)var3;
            var7 = Optional.of(var5.value());
            break;
         case 1:
            DataResult.Error var6 = (DataResult.Error)var3;
            this.problemReporter.report(new DecodeFromMapFailedProblem(var6));
            var7 = var6.partialValue();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var7;
   }

   private <T extends Tag> @Nullable T getOptionalTypedTag(String var1, TagType<T> var2) {
      Tag var3 = this.input.get(var1);
      if (var3 == null) {
         return null;
      } else {
         TagType var4 = var3.getType();
         if (var4 != var2) {
            this.problemReporter.report(new UnexpectedTypeProblem(var1, var2, var4));
            return null;
         } else {
            return (T)var3;
         }
      }
   }

   private @Nullable NumericTag getNumericTag(String var1) {
      Tag var2 = this.input.get(var1);
      if (var2 == null) {
         return null;
      } else if (var2 instanceof NumericTag) {
         NumericTag var3 = (NumericTag)var2;
         return var3;
      } else {
         this.problemReporter.report(new UnexpectedNonNumberProblem(var1, var2.getType()));
         return null;
      }
   }

   public Optional<ValueInput> child(String var1) {
      CompoundTag var2 = (CompoundTag)this.getOptionalTypedTag(var1, CompoundTag.TYPE);
      return var2 != null ? Optional.of(this.wrapChild(var1, var2)) : Optional.empty();
   }

   public ValueInput childOrEmpty(String var1) {
      CompoundTag var2 = (CompoundTag)this.getOptionalTypedTag(var1, CompoundTag.TYPE);
      return var2 != null ? this.wrapChild(var1, var2) : this.context.empty();
   }

   public Optional<ValueInput.ValueInputList> childrenList(String var1) {
      ListTag var2 = (ListTag)this.getOptionalTypedTag(var1, ListTag.TYPE);
      return var2 != null ? Optional.of(this.wrapList(var1, this.context, var2)) : Optional.empty();
   }

   public ValueInput.ValueInputList childrenListOrEmpty(String var1) {
      ListTag var2 = (ListTag)this.getOptionalTypedTag(var1, ListTag.TYPE);
      return var2 != null ? this.wrapList(var1, this.context, var2) : this.context.emptyList();
   }

   public <T> Optional<ValueInput.TypedInputList<T>> list(String var1, Codec<T> var2) {
      ListTag var3 = (ListTag)this.getOptionalTypedTag(var1, ListTag.TYPE);
      return var3 != null ? Optional.of(this.wrapTypedList(var1, var3, var2)) : Optional.empty();
   }

   public <T> ValueInput.TypedInputList<T> listOrEmpty(String var1, Codec<T> var2) {
      ListTag var3 = (ListTag)this.getOptionalTypedTag(var1, ListTag.TYPE);
      return var3 != null ? this.wrapTypedList(var1, var3, var2) : this.context.emptyTypedList();
   }

   public boolean getBooleanOr(String var1, boolean var2) {
      NumericTag var3 = this.getNumericTag(var1);
      return var3 != null ? var3.byteValue() != 0 : var2;
   }

   public byte getByteOr(String var1, byte var2) {
      NumericTag var3 = this.getNumericTag(var1);
      return var3 != null ? var3.byteValue() : var2;
   }

   public int getShortOr(String var1, short var2) {
      NumericTag var3 = this.getNumericTag(var1);
      return var3 != null ? var3.shortValue() : var2;
   }

   public Optional<Integer> getInt(String var1) {
      NumericTag var2 = this.getNumericTag(var1);
      return var2 != null ? Optional.of(var2.intValue()) : Optional.empty();
   }

   public int getIntOr(String var1, int var2) {
      NumericTag var3 = this.getNumericTag(var1);
      return var3 != null ? var3.intValue() : var2;
   }

   public long getLongOr(String var1, long var2) {
      NumericTag var4 = this.getNumericTag(var1);
      return var4 != null ? var4.longValue() : var2;
   }

   public Optional<Long> getLong(String var1) {
      NumericTag var2 = this.getNumericTag(var1);
      return var2 != null ? Optional.of(var2.longValue()) : Optional.empty();
   }

   public float getFloatOr(String var1, float var2) {
      NumericTag var3 = this.getNumericTag(var1);
      return var3 != null ? var3.floatValue() : var2;
   }

   public double getDoubleOr(String var1, double var2) {
      NumericTag var4 = this.getNumericTag(var1);
      return var4 != null ? var4.doubleValue() : var2;
   }

   public Optional<String> getString(String var1) {
      StringTag var2 = (StringTag)this.getOptionalTypedTag(var1, StringTag.TYPE);
      return var2 != null ? Optional.of(var2.value()) : Optional.empty();
   }

   public String getStringOr(String var1, String var2) {
      StringTag var3 = (StringTag)this.getOptionalTypedTag(var1, StringTag.TYPE);
      return var3 != null ? var3.value() : var2;
   }

   public Optional<int[]> getIntArray(String var1) {
      IntArrayTag var2 = (IntArrayTag)this.getOptionalTypedTag(var1, IntArrayTag.TYPE);
      return var2 != null ? Optional.of(var2.getAsIntArray()) : Optional.empty();
   }

   public HolderLookup.Provider lookup() {
      return this.context.lookup();
   }

   private ValueInput wrapChild(String var1, CompoundTag var2) {
      return (ValueInput)(var2.isEmpty() ? this.context.empty() : new TagValueInput(this.problemReporter.forChild(new ProblemReporter.FieldPathElement(var1)), this.context, var2));
   }

   static ValueInput wrapChild(ProblemReporter var0, ValueInputContextHelper var1, CompoundTag var2) {
      return (ValueInput)(var2.isEmpty() ? var1.empty() : new TagValueInput(var0, var1, var2));
   }

   private ValueInput.ValueInputList wrapList(String var1, ValueInputContextHelper var2, ListTag var3) {
      return (ValueInput.ValueInputList)(var3.isEmpty() ? var2.emptyList() : new ListWrapper(this.problemReporter, var1, var2, var3));
   }

   private <T> ValueInput.TypedInputList<T> wrapTypedList(String var1, ListTag var2, Codec<T> var3) {
      return (ValueInput.TypedInputList<T>)(var2.isEmpty() ? this.context.emptyTypedList() : new TypedListWrapper(this.problemReporter, var1, this.context, var3, var2));
   }

   static class ListWrapper implements ValueInput.ValueInputList {
      private final ProblemReporter problemReporter;
      private final String name;
      final ValueInputContextHelper context;
      private final ListTag list;

      ListWrapper(ProblemReporter var1, String var2, ValueInputContextHelper var3, ListTag var4) {
         super();
         this.problemReporter = var1;
         this.name = var2;
         this.context = var3;
         this.list = var4;
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      ProblemReporter reporterForChild(int var1) {
         return this.problemReporter.forChild(new ProblemReporter.IndexedFieldPathElement(this.name, var1));
      }

      void reportIndexUnwrapProblem(int var1, Tag var2) {
         this.problemReporter.report(new UnexpectedListElementTypeProblem(this.name, var1, CompoundTag.TYPE, var2.getType()));
      }

      public Stream<ValueInput> stream() {
         return Streams.mapWithIndex(this.list.stream(), (var1, var2) -> {
            if (var1 instanceof CompoundTag var4) {
               return TagValueInput.wrapChild(this.reporterForChild((int)var2), this.context, var4);
            } else {
               this.reportIndexUnwrapProblem((int)var2, var1);
               return null;
            }
         }).filter(Objects::nonNull);
      }

      public Iterator<ValueInput> iterator() {
         final Iterator var1 = this.list.iterator();
         return new AbstractIterator<ValueInput>() {
            private int index;

            protected @Nullable ValueInput computeNext() {
               while(var1.hasNext()) {
                  Tag var1x = (Tag)var1.next();
                  int var2 = this.index++;
                  if (var1x instanceof CompoundTag var3) {
                     return TagValueInput.wrapChild(ListWrapper.this.reporterForChild(var2), ListWrapper.this.context, var3);
                  }

                  ListWrapper.this.reportIndexUnwrapProblem(var2, var1x);
               }

               return (ValueInput)this.endOfData();
            }

            // $FF: synthetic method
            protected @Nullable Object computeNext() {
               return this.computeNext();
            }
         };
      }
   }

   static class TypedListWrapper<T> implements ValueInput.TypedInputList<T> {
      private final ProblemReporter problemReporter;
      private final String name;
      final ValueInputContextHelper context;
      final Codec<T> codec;
      private final ListTag list;

      TypedListWrapper(ProblemReporter var1, String var2, ValueInputContextHelper var3, Codec<T> var4, ListTag var5) {
         super();
         this.problemReporter = var1;
         this.name = var2;
         this.context = var3;
         this.codec = var4;
         this.list = var5;
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      void reportIndexUnwrapProblem(int var1, Tag var2, DataResult.Error<?> var3) {
         this.problemReporter.report(new DecodeFromListFailedProblem(this.name, var1, var2, var3));
      }

      public Stream<T> stream() {
         return Streams.mapWithIndex(this.list.stream(), (var1, var2) -> {
            DataResult var10000 = this.codec.parse(this.context.ops(), var1);
            Objects.requireNonNull(var10000);
            DataResult var4 = var10000;
            byte var5 = 0;
            Object var8;
            //$FF: var5->value
            //0->com/mojang/serialization/DataResult$Success
            //1->com/mojang/serialization/DataResult$Error
            switch (var4.typeSwitch<invokedynamic>(var4, var5)) {
               case 0:
                  DataResult.Success var6 = (DataResult.Success)var4;
                  var8 = var6.value();
                  break;
               case 1:
                  DataResult.Error var7 = (DataResult.Error)var4;
                  this.reportIndexUnwrapProblem((int)var2, var1, var7);
                  var8 = var7.partialValue().orElse((Object)null);
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return var8;
         }).filter(Objects::nonNull);
      }

      public Iterator<T> iterator() {
         final ListIterator var1 = this.list.listIterator();
         return new AbstractIterator<T>() {
            protected @Nullable T computeNext() {
               while(true) {
                  if (var1.hasNext()) {
                     int var1x = var1.nextIndex();
                     Tag var2 = (Tag)var1.next();
                     DataResult var10000 = TypedListWrapper.this.codec.parse(TypedListWrapper.this.context.ops(), var2);
                     Objects.requireNonNull(var10000);
                     DataResult var3 = var10000;
                     byte var4 = 0;
                     //$FF: var4->value
                     //0->com/mojang/serialization/DataResult$Success
                     //1->com/mojang/serialization/DataResult$Error
                     switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
                        case 0:
                           DataResult.Success var5 = (DataResult.Success)var3;
                           return (T)var5.value();
                        case 1:
                           DataResult.Error var6 = (DataResult.Error)var3;
                           TypedListWrapper.this.reportIndexUnwrapProblem(var1x, var2, var6);
                           if (!var6.partialValue().isPresent()) {
                              continue;
                           }

                           return (T)var6.partialValue().get();
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

   static class CompoundListWrapper implements ValueInput.ValueInputList {
      private final ProblemReporter problemReporter;
      private final ValueInputContextHelper context;
      private final List<CompoundTag> list;

      public CompoundListWrapper(ProblemReporter var1, ValueInputContextHelper var2, List<CompoundTag> var3) {
         super();
         this.problemReporter = var1;
         this.context = var2;
         this.list = var3;
      }

      ValueInput wrapChild(int var1, CompoundTag var2) {
         return TagValueInput.wrapChild(this.problemReporter.forChild(new ProblemReporter.IndexedPathElement(var1)), this.context, var2);
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      public Stream<ValueInput> stream() {
         return Streams.mapWithIndex(this.list.stream(), (var1, var2) -> this.wrapChild((int)var2, var1));
      }

      public Iterator<ValueInput> iterator() {
         final ListIterator var1 = this.list.listIterator();
         return new AbstractIterator<ValueInput>() {
            protected @Nullable ValueInput computeNext() {
               if (var1.hasNext()) {
                  int var1x = var1.nextIndex();
                  CompoundTag var2 = (CompoundTag)var1.next();
                  return CompoundListWrapper.this.wrapChild(var1x, var2);
               } else {
                  return (ValueInput)this.endOfData();
               }
            }

            // $FF: synthetic method
            protected @Nullable Object computeNext() {
               return this.computeNext();
            }
         };
      }
   }

   public static record DecodeFromFieldFailedProblem(String name, Tag tag, DataResult.Error<?> error) implements ProblemReporter.Problem {
      public DecodeFromFieldFailedProblem(String var1, Tag var2, DataResult.Error<?> var3) {
         super();
         this.name = var1;
         this.tag = var2;
         this.error = var3;
      }

      public String description() {
         String var10000 = String.valueOf(this.tag);
         return "Failed to decode value '" + var10000 + "' from field '" + this.name + "': " + this.error.message();
      }
   }

   public static record DecodeFromListFailedProblem(String name, int index, Tag tag, DataResult.Error<?> error) implements ProblemReporter.Problem {
      public DecodeFromListFailedProblem(String var1, int var2, Tag var3, DataResult.Error<?> var4) {
         super();
         this.name = var1;
         this.index = var2;
         this.tag = var3;
         this.error = var4;
      }

      public String description() {
         String var10000 = String.valueOf(this.tag);
         return "Failed to decode value '" + var10000 + "' from field '" + this.name + "' at index " + this.index + "': " + this.error.message();
      }
   }

   public static record DecodeFromMapFailedProblem(DataResult.Error<?> error) implements ProblemReporter.Problem {
      public DecodeFromMapFailedProblem(DataResult.Error<?> var1) {
         super();
         this.error = var1;
      }

      public String description() {
         return "Failed to decode from map: " + this.error.message();
      }
   }

   public static record UnexpectedTypeProblem(String name, TagType<?> expected, TagType<?> actual) implements ProblemReporter.Problem {
      public UnexpectedTypeProblem(String var1, TagType<?> var2, TagType<?> var3) {
         super();
         this.name = var1;
         this.expected = var2;
         this.actual = var3;
      }

      public String description() {
         String var10000 = this.name;
         return "Expected field '" + var10000 + "' to contain value of type " + this.expected.getName() + ", but got " + this.actual.getName();
      }
   }

   public static record UnexpectedNonNumberProblem(String name, TagType<?> actual) implements ProblemReporter.Problem {
      public UnexpectedNonNumberProblem(String var1, TagType<?> var2) {
         super();
         this.name = var1;
         this.actual = var2;
      }

      public String description() {
         String var10000 = this.name;
         return "Expected field '" + var10000 + "' to contain number, but got " + this.actual.getName();
      }
   }

   public static record UnexpectedListElementTypeProblem(String name, int index, TagType<?> expected, TagType<?> actual) implements ProblemReporter.Problem {
      public UnexpectedListElementTypeProblem(String var1, int var2, TagType<?> var3, TagType<?> var4) {
         super();
         this.name = var1;
         this.index = var2;
         this.expected = var3;
         this.actual = var4;
      }

      public String description() {
         String var10000 = this.name;
         return "Expected list '" + var10000 + "' to contain at index " + this.index + " value of type " + this.expected.getName() + ", but got " + this.actual.getName();
      }
   }
}
