package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ProblemReporter;
import org.jspecify.annotations.Nullable;

public class TagValueOutput implements ValueOutput {
   private final ProblemReporter problemReporter;
   private final DynamicOps<Tag> ops;
   private final CompoundTag output;

   private TagValueOutput(final ProblemReporter problemReporter, final DynamicOps<Tag> ops, final CompoundTag output) {
      super();
      this.problemReporter = problemReporter;
      this.ops = ops;
      this.output = output;
   }

   public static TagValueOutput createWithContext(final ProblemReporter problemReporter, final HolderLookup.Provider provider) {
      return new TagValueOutput(problemReporter, provider.createSerializationContext(NbtOps.INSTANCE), new CompoundTag());
   }

   public static TagValueOutput createWithoutContext(final ProblemReporter problemReporter) {
      return new TagValueOutput(problemReporter, NbtOps.INSTANCE, new CompoundTag());
   }

   public <T> void store(final String name, final Codec<T> codec, final T value) {
      DataResult var10000 = codec.encodeStart(this.ops, value);
      Objects.requireNonNull(var10000);
      DataResult var4 = var10000;
      byte var5 = 0;
      //$FF: var5->value
      //0->com/mojang/serialization/DataResult$Success
      //1->com/mojang/serialization/DataResult$Error
      switch (var4.typeSwitch<invokedynamic>(var4, var5)) {
         case 0:
            DataResult.Success<Tag> success = (DataResult.Success)var4;
            this.output.put(name, (Tag)success.value());
            break;
         case 1:
            DataResult.Error<Tag> error = (DataResult.Error)var4;
            this.problemReporter.report(new EncodeToFieldFailedProblem(name, value, error));
            error.partialValue().ifPresent((partial) -> this.output.put(name, partial));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

   }

   public <T> void storeNullable(final String name, final Codec<T> codec, final @Nullable T value) {
      if (value != null) {
         this.store(name, codec, value);
      }

   }

   public <T> void store(final MapCodec<T> codec, final T value) {
      DataResult var10000 = codec.encoder().encodeStart(this.ops, value);
      Objects.requireNonNull(var10000);
      DataResult var3 = var10000;
      byte var4 = 0;
      //$FF: var4->value
      //0->com/mojang/serialization/DataResult$Success
      //1->com/mojang/serialization/DataResult$Error
      switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
         case 0:
            DataResult.Success<Tag> success = (DataResult.Success)var3;
            this.output.merge((CompoundTag)success.value());
            break;
         case 1:
            DataResult.Error<Tag> error = (DataResult.Error)var3;
            this.problemReporter.report(new EncodeToMapFailedProblem(value, error));
            error.partialValue().ifPresent((partial) -> this.output.merge((CompoundTag)partial));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

   }

   public void putBoolean(final String name, final boolean value) {
      this.output.putBoolean(name, value);
   }

   public void putByte(final String name, final byte value) {
      this.output.putByte(name, value);
   }

   public void putShort(final String name, final short value) {
      this.output.putShort(name, value);
   }

   public void putInt(final String name, final int value) {
      this.output.putInt(name, value);
   }

   public void putLong(final String name, final long value) {
      this.output.putLong(name, value);
   }

   public void putFloat(final String name, final float value) {
      this.output.putFloat(name, value);
   }

   public void putDouble(final String name, final double value) {
      this.output.putDouble(name, value);
   }

   public void putString(final String name, final String value) {
      this.output.putString(name, value);
   }

   public void putIntArray(final String name, final int[] value) {
      this.output.putIntArray(name, value);
   }

   private ProblemReporter reporterForChild(final String name) {
      return this.problemReporter.forChild(new ProblemReporter.FieldPathElement(name));
   }

   public ValueOutput child(final String name) {
      CompoundTag childTag = new CompoundTag();
      this.output.put(name, childTag);
      return new TagValueOutput(this.reporterForChild(name), this.ops, childTag);
   }

   public ValueOutput.ValueOutputList childrenList(final String name) {
      ListTag childList = new ListTag();
      this.output.put(name, childList);
      return new ListWrapper(name, this.problemReporter, this.ops, childList);
   }

   public <T> ValueOutput.TypedOutputList<T> list(final String name, final Codec<T> codec) {
      ListTag childList = new ListTag();
      this.output.put(name, childList);
      return new TypedListWrapper<T>(this.problemReporter, name, this.ops, codec, childList);
   }

   public void discard(final String name) {
      this.output.remove(name);
   }

   public boolean isEmpty() {
      return this.output.isEmpty();
   }

   public CompoundTag buildResult() {
      return this.output;
   }

   private static class ListWrapper implements ValueOutput.ValueOutputList {
      private final String fieldName;
      private final ProblemReporter problemReporter;
      private final DynamicOps<Tag> ops;
      private final ListTag output;

      private ListWrapper(final String fieldName, final ProblemReporter problemReporter, final DynamicOps<Tag> ops, final ListTag output) {
         super();
         this.fieldName = fieldName;
         this.problemReporter = problemReporter;
         this.ops = ops;
         this.output = output;
      }

      public ValueOutput addChild() {
         int newChildIndex = this.output.size();
         CompoundTag child = new CompoundTag();
         this.output.add(child);
         return new TagValueOutput(this.problemReporter.forChild(new ProblemReporter.IndexedFieldPathElement(this.fieldName, newChildIndex)), this.ops, child);
      }

      public void discardLast() {
         this.output.removeLast();
      }

      public boolean isEmpty() {
         return this.output.isEmpty();
      }
   }

   private static class TypedListWrapper<T> implements ValueOutput.TypedOutputList<T> {
      private final ProblemReporter problemReporter;
      private final String name;
      private final DynamicOps<Tag> ops;
      private final Codec<T> codec;
      private final ListTag output;

      private TypedListWrapper(final ProblemReporter problemReporter, final String name, final DynamicOps<Tag> ops, final Codec<T> codec, final ListTag output) {
         super();
         this.problemReporter = problemReporter;
         this.name = name;
         this.ops = ops;
         this.codec = codec;
         this.output = output;
      }

      public void add(final T value) {
         DataResult var10000 = this.codec.encodeStart(this.ops, value);
         Objects.requireNonNull(var10000);
         DataResult var2 = var10000;
         byte var3 = 0;
         //$FF: var3->value
         //0->com/mojang/serialization/DataResult$Success
         //1->com/mojang/serialization/DataResult$Error
         switch (var2.typeSwitch<invokedynamic>(var2, var3)) {
            case 0:
               DataResult.Success<Tag> success = (DataResult.Success)var2;
               this.output.add((Tag)success.value());
               break;
            case 1:
               DataResult.Error<Tag> error = (DataResult.Error)var2;
               this.problemReporter.report(new EncodeToListFailedProblem(this.name, value, error));
               Optional var6 = error.partialValue();
               ListTag var10001 = this.output;
               Objects.requireNonNull(var10001);
               var6.ifPresent(var10001::add);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

      }

      public boolean isEmpty() {
         return this.output.isEmpty();
      }
   }

   public static record EncodeToFieldFailedProblem(String name, Object value, DataResult.Error<?> error) implements ProblemReporter.Problem {
      public EncodeToFieldFailedProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.value);
         return "Failed to encode value '" + var10000 + "' to field '" + this.name + "': " + this.error.message();
      }
   }

   public static record EncodeToListFailedProblem(String name, Object value, DataResult.Error<?> error) implements ProblemReporter.Problem {
      public EncodeToListFailedProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.value);
         return "Failed to append value '" + var10000 + "' to list '" + this.name + "': " + this.error.message();
      }
   }

   public static record EncodeToMapFailedProblem(Object value, DataResult.Error<?> error) implements ProblemReporter.Problem {
      public EncodeToMapFailedProblem {
         super();
      }

      public String description() {
         String var10000 = String.valueOf(this.value);
         return "Failed to merge value '" + var10000 + "' to an object: " + this.error.message();
      }
   }
}
