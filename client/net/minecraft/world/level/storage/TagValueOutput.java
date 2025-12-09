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

   TagValueOutput(ProblemReporter var1, DynamicOps<Tag> var2, CompoundTag var3) {
      super();
      this.problemReporter = var1;
      this.ops = var2;
      this.output = var3;
   }

   public static TagValueOutput createWithContext(ProblemReporter var0, HolderLookup.Provider var1) {
      return new TagValueOutput(var0, var1.createSerializationContext(NbtOps.INSTANCE), new CompoundTag());
   }

   public static TagValueOutput createWithoutContext(ProblemReporter var0) {
      return new TagValueOutput(var0, NbtOps.INSTANCE, new CompoundTag());
   }

   public <T> void store(String var1, Codec<T> var2, T var3) {
      DataResult var10000 = var2.encodeStart(this.ops, var3);
      Objects.requireNonNull(var10000);
      DataResult var4 = var10000;
      byte var5 = 0;
      //$FF: var5->value
      //0->com/mojang/serialization/DataResult$Success
      //1->com/mojang/serialization/DataResult$Error
      switch (var4.typeSwitch<invokedynamic>(var4, var5)) {
         case 0:
            DataResult.Success var6 = (DataResult.Success)var4;
            this.output.put(var1, (Tag)var6.value());
            break;
         case 1:
            DataResult.Error var7 = (DataResult.Error)var4;
            this.problemReporter.report(new EncodeToFieldFailedProblem(var1, var3, var7));
            var7.partialValue().ifPresent((var2x) -> this.output.put(var1, var2x));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

   }

   public <T> void storeNullable(String var1, Codec<T> var2, @Nullable T var3) {
      if (var3 != null) {
         this.store(var1, var2, var3);
      }

   }

   public <T> void store(MapCodec<T> var1, T var2) {
      DataResult var10000 = var1.encoder().encodeStart(this.ops, var2);
      Objects.requireNonNull(var10000);
      DataResult var3 = var10000;
      byte var4 = 0;
      //$FF: var4->value
      //0->com/mojang/serialization/DataResult$Success
      //1->com/mojang/serialization/DataResult$Error
      switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
         case 0:
            DataResult.Success var5 = (DataResult.Success)var3;
            this.output.merge((CompoundTag)var5.value());
            break;
         case 1:
            DataResult.Error var6 = (DataResult.Error)var3;
            this.problemReporter.report(new EncodeToMapFailedProblem(var2, var6));
            var6.partialValue().ifPresent((var1x) -> this.output.merge((CompoundTag)var1x));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

   }

   public void putBoolean(String var1, boolean var2) {
      this.output.putBoolean(var1, var2);
   }

   public void putByte(String var1, byte var2) {
      this.output.putByte(var1, var2);
   }

   public void putShort(String var1, short var2) {
      this.output.putShort(var1, var2);
   }

   public void putInt(String var1, int var2) {
      this.output.putInt(var1, var2);
   }

   public void putLong(String var1, long var2) {
      this.output.putLong(var1, var2);
   }

   public void putFloat(String var1, float var2) {
      this.output.putFloat(var1, var2);
   }

   public void putDouble(String var1, double var2) {
      this.output.putDouble(var1, var2);
   }

   public void putString(String var1, String var2) {
      this.output.putString(var1, var2);
   }

   public void putIntArray(String var1, int[] var2) {
      this.output.putIntArray(var1, var2);
   }

   private ProblemReporter reporterForChild(String var1) {
      return this.problemReporter.forChild(new ProblemReporter.FieldPathElement(var1));
   }

   public ValueOutput child(String var1) {
      CompoundTag var2 = new CompoundTag();
      this.output.put(var1, var2);
      return new TagValueOutput(this.reporterForChild(var1), this.ops, var2);
   }

   public ValueOutput.ValueOutputList childrenList(String var1) {
      ListTag var2 = new ListTag();
      this.output.put(var1, var2);
      return new ListWrapper(var1, this.problemReporter, this.ops, var2);
   }

   public <T> ValueOutput.TypedOutputList<T> list(String var1, Codec<T> var2) {
      ListTag var3 = new ListTag();
      this.output.put(var1, var3);
      return new TypedListWrapper<T>(this.problemReporter, var1, this.ops, var2, var3);
   }

   public void discard(String var1) {
      this.output.remove(var1);
   }

   public boolean isEmpty() {
      return this.output.isEmpty();
   }

   public CompoundTag buildResult() {
      return this.output;
   }

   static class ListWrapper implements ValueOutput.ValueOutputList {
      private final String fieldName;
      private final ProblemReporter problemReporter;
      private final DynamicOps<Tag> ops;
      private final ListTag output;

      ListWrapper(String var1, ProblemReporter var2, DynamicOps<Tag> var3, ListTag var4) {
         super();
         this.fieldName = var1;
         this.problemReporter = var2;
         this.ops = var3;
         this.output = var4;
      }

      public ValueOutput addChild() {
         int var1 = this.output.size();
         CompoundTag var2 = new CompoundTag();
         this.output.add(var2);
         return new TagValueOutput(this.problemReporter.forChild(new ProblemReporter.IndexedFieldPathElement(this.fieldName, var1)), this.ops, var2);
      }

      public void discardLast() {
         this.output.removeLast();
      }

      public boolean isEmpty() {
         return this.output.isEmpty();
      }
   }

   static class TypedListWrapper<T> implements ValueOutput.TypedOutputList<T> {
      private final ProblemReporter problemReporter;
      private final String name;
      private final DynamicOps<Tag> ops;
      private final Codec<T> codec;
      private final ListTag output;

      TypedListWrapper(ProblemReporter var1, String var2, DynamicOps<Tag> var3, Codec<T> var4, ListTag var5) {
         super();
         this.problemReporter = var1;
         this.name = var2;
         this.ops = var3;
         this.codec = var4;
         this.output = var5;
      }

      public void add(T var1) {
         DataResult var10000 = this.codec.encodeStart(this.ops, var1);
         Objects.requireNonNull(var10000);
         DataResult var2 = var10000;
         byte var3 = 0;
         //$FF: var3->value
         //0->com/mojang/serialization/DataResult$Success
         //1->com/mojang/serialization/DataResult$Error
         switch (var2.typeSwitch<invokedynamic>(var2, var3)) {
            case 0:
               DataResult.Success var4 = (DataResult.Success)var2;
               this.output.add((Tag)var4.value());
               break;
            case 1:
               DataResult.Error var5 = (DataResult.Error)var2;
               this.problemReporter.report(new EncodeToListFailedProblem(this.name, var1, var5));
               Optional var6 = var5.partialValue();
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
      public EncodeToFieldFailedProblem(String var1, Object var2, DataResult.Error<?> var3) {
         super();
         this.name = var1;
         this.value = var2;
         this.error = var3;
      }

      public String description() {
         String var10000 = String.valueOf(this.value);
         return "Failed to encode value '" + var10000 + "' to field '" + this.name + "': " + this.error.message();
      }
   }

   public static record EncodeToListFailedProblem(String name, Object value, DataResult.Error<?> error) implements ProblemReporter.Problem {
      public EncodeToListFailedProblem(String var1, Object var2, DataResult.Error<?> var3) {
         super();
         this.name = var1;
         this.value = var2;
         this.error = var3;
      }

      public String description() {
         String var10000 = String.valueOf(this.value);
         return "Failed to append value '" + var10000 + "' to list '" + this.name + "': " + this.error.message();
      }
   }

   public static record EncodeToMapFailedProblem(Object value, DataResult.Error<?> error) implements ProblemReporter.Problem {
      public EncodeToMapFailedProblem(Object var1, DataResult.Error<?> var2) {
         super();
         this.value = var1;
         this.error = var2;
      }

      public String description() {
         String var10000 = String.valueOf(this.value);
         return "Failed to merge value '" + var10000 + "' to an object: " + this.error.message();
      }
   }
}
