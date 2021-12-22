package net.minecraft.nbt.visitors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public class CollectFields extends CollectToTag {
   private int fieldsToGetCount;
   private final Set<TagType<?>> wantedTypes;
   private final Deque<CollectFields.StackFrame> stack = new ArrayDeque();

   public CollectFields(CollectFields.WantedField... var1) {
      super();
      this.fieldsToGetCount = var1.length;
      Builder var2 = ImmutableSet.builder();
      CollectFields.StackFrame var3 = new CollectFields.StackFrame(1);
      CollectFields.WantedField[] var4 = var1;
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         CollectFields.WantedField var7 = var4[var6];
         var3.addEntry(var7);
         var2.add(var7.type);
      }

      this.stack.push(var3);
      var2.add(CompoundTag.TYPE);
      this.wantedTypes = var2.build();
   }

   public StreamTagVisitor.ValueResult visitRootEntry(TagType<?> var1) {
      return var1 != CompoundTag.TYPE ? StreamTagVisitor.ValueResult.HALT : super.visitRootEntry(var1);
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> var1) {
      CollectFields.StackFrame var2 = (CollectFields.StackFrame)this.stack.element();
      if (this.depth() > var2.depth()) {
         return super.visitEntry(var1);
      } else if (this.fieldsToGetCount <= 0) {
         return StreamTagVisitor.EntryResult.HALT;
      } else {
         return !this.wantedTypes.contains(var1) ? StreamTagVisitor.EntryResult.SKIP : super.visitEntry(var1);
      }
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> var1, String var2) {
      CollectFields.StackFrame var3 = (CollectFields.StackFrame)this.stack.element();
      if (this.depth() > var3.depth()) {
         return super.visitEntry(var1, var2);
      } else if (var3.fieldsToGet.remove(var2, var1)) {
         --this.fieldsToGetCount;
         return super.visitEntry(var1, var2);
      } else {
         if (var1 == CompoundTag.TYPE) {
            CollectFields.StackFrame var4 = (CollectFields.StackFrame)var3.fieldsToRecurse.get(var2);
            if (var4 != null) {
               this.stack.push(var4);
               return super.visitEntry(var1, var2);
            }
         }

         return StreamTagVisitor.EntryResult.SKIP;
      }
   }

   public StreamTagVisitor.ValueResult visitContainerEnd() {
      if (this.depth() == ((CollectFields.StackFrame)this.stack.element()).depth()) {
         this.stack.pop();
      }

      return super.visitContainerEnd();
   }

   public int getMissingFieldCount() {
      return this.fieldsToGetCount;
   }

   static record StackFrame(int a, Map<String, TagType<?>> b, Map<String, CollectFields.StackFrame> c) {
      private final int depth;
      final Map<String, TagType<?>> fieldsToGet;
      final Map<String, CollectFields.StackFrame> fieldsToRecurse;

      public StackFrame(int var1) {
         this(var1, new HashMap(), new HashMap());
      }

      private StackFrame(int var1, Map<String, TagType<?>> var2, Map<String, CollectFields.StackFrame> var3) {
         super();
         this.depth = var1;
         this.fieldsToGet = var2;
         this.fieldsToRecurse = var3;
      }

      public void addEntry(CollectFields.WantedField var1) {
         if (this.depth <= var1.path.size()) {
            ((CollectFields.StackFrame)this.fieldsToRecurse.computeIfAbsent((String)var1.path.get(this.depth - 1), (var1x) -> {
               return new CollectFields.StackFrame(this.depth + 1);
            })).addEntry(var1);
         } else {
            this.fieldsToGet.put(var1.name, var1.type);
         }

      }

      public int depth() {
         return this.depth;
      }

      public Map<String, TagType<?>> fieldsToGet() {
         return this.fieldsToGet;
      }

      public Map<String, CollectFields.StackFrame> fieldsToRecurse() {
         return this.fieldsToRecurse;
      }
   }

   public static record WantedField(List<String> a, TagType<?> b, String c) {
      final List<String> path;
      final TagType<?> type;
      final String name;

      public WantedField(TagType<?> var1, String var2) {
         this(List.of(), var1, var2);
      }

      public WantedField(String var1, TagType<?> var2, String var3) {
         this(List.of(var1), var2, var3);
      }

      public WantedField(String var1, String var2, TagType<?> var3, String var4) {
         this(List.of(var1, var2), var3, var4);
      }

      public WantedField(List<String> var1, TagType<?> var2, String var3) {
         super();
         this.path = var1;
         this.type = var2;
         this.name = var3;
      }

      public List<String> path() {
         return this.path;
      }

      public TagType<?> type() {
         return this.type;
      }

      public String name() {
         return this.name;
      }
   }
}
