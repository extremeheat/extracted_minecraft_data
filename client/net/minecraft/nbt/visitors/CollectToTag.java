package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;

public class CollectToTag implements StreamTagVisitor {
   private final Deque<ContainerBuilder> containerStack = new ArrayDeque();

   public CollectToTag() {
      super();
      this.containerStack.addLast(new RootBuilder());
   }

   @Nullable
   public Tag getResult() {
      return ((ContainerBuilder)this.containerStack.getFirst()).build();
   }

   protected int depth() {
      return this.containerStack.size() - 1;
   }

   private void appendEntry(Tag var1) {
      ((ContainerBuilder)this.containerStack.getLast()).acceptValue(var1);
   }

   public StreamTagVisitor.ValueResult visitEnd() {
      this.appendEntry(EndTag.INSTANCE);
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(String var1) {
      this.appendEntry(StringTag.valueOf(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(byte var1) {
      this.appendEntry(ByteTag.valueOf(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(short var1) {
      this.appendEntry(ShortTag.valueOf(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(int var1) {
      this.appendEntry(IntTag.valueOf(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(long var1) {
      this.appendEntry(LongTag.valueOf(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(float var1) {
      this.appendEntry(FloatTag.valueOf(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(double var1) {
      this.appendEntry(DoubleTag.valueOf(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(byte[] var1) {
      this.appendEntry(new ByteArrayTag(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(int[] var1) {
      this.appendEntry(new IntArrayTag(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(long[] var1) {
      this.appendEntry(new LongArrayTag(var1));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visitList(TagType<?> var1, int var2) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.EntryResult visitElement(TagType<?> var1, int var2) {
      this.enterContainerIfNeeded(var1);
      return StreamTagVisitor.EntryResult.ENTER;
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> var1) {
      return StreamTagVisitor.EntryResult.ENTER;
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> var1, String var2) {
      ((ContainerBuilder)this.containerStack.getLast()).acceptKey(var2);
      this.enterContainerIfNeeded(var1);
      return StreamTagVisitor.EntryResult.ENTER;
   }

   private void enterContainerIfNeeded(TagType<?> var1) {
      if (var1 == ListTag.TYPE) {
         this.containerStack.addLast(new ListBuilder());
      } else if (var1 == CompoundTag.TYPE) {
         this.containerStack.addLast(new CompoundBuilder());
      }

   }

   public StreamTagVisitor.ValueResult visitContainerEnd() {
      ContainerBuilder var1 = (ContainerBuilder)this.containerStack.removeLast();
      Tag var2 = var1.build();
      if (var2 != null) {
         ((ContainerBuilder)this.containerStack.getLast()).acceptValue(var2);
      }

      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visitRootEntry(TagType<?> var1) {
      this.enterContainerIfNeeded(var1);
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   interface ContainerBuilder {
      default void acceptKey(String var1) {
      }

      void acceptValue(Tag var1);

      @Nullable
      Tag build();
   }

   static class RootBuilder implements ContainerBuilder {
      @Nullable
      private Tag result;

      RootBuilder() {
         super();
      }

      public void acceptValue(Tag var1) {
         this.result = var1;
      }

      @Nullable
      public Tag build() {
         return this.result;
      }
   }

   static class CompoundBuilder implements ContainerBuilder {
      private final CompoundTag compound = new CompoundTag();
      private String lastId = "";

      CompoundBuilder() {
         super();
      }

      public void acceptKey(String var1) {
         this.lastId = var1;
      }

      public void acceptValue(Tag var1) {
         this.compound.put(this.lastId, var1);
      }

      public Tag build() {
         return this.compound;
      }
   }

   static class ListBuilder implements ContainerBuilder {
      private final ListTag list = new ListTag();

      ListBuilder() {
         super();
      }

      public void acceptValue(Tag var1) {
         this.list.addAndUnwrap(var1);
      }

      public Tag build() {
         return this.list;
      }
   }
}
