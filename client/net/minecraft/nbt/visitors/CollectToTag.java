package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
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
import org.jspecify.annotations.Nullable;

public class CollectToTag implements StreamTagVisitor {
   private final Deque<ContainerBuilder> containerStack = new ArrayDeque();

   public CollectToTag() {
      super();
      this.containerStack.addLast(new RootBuilder());
   }

   public @Nullable Tag getResult() {
      return ((ContainerBuilder)this.containerStack.getFirst()).build();
   }

   protected int depth() {
      return this.containerStack.size() - 1;
   }

   private void appendEntry(final Tag instance) {
      ((ContainerBuilder)this.containerStack.getLast()).acceptValue(instance);
   }

   public StreamTagVisitor.ValueResult visitEnd() {
      this.appendEntry(EndTag.INSTANCE);
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final String value) {
      this.appendEntry(StringTag.valueOf(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final byte value) {
      this.appendEntry(ByteTag.valueOf(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final short value) {
      this.appendEntry(ShortTag.valueOf(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final int value) {
      this.appendEntry(IntTag.valueOf(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final long value) {
      this.appendEntry(LongTag.valueOf(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final float value) {
      this.appendEntry(FloatTag.valueOf(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final double value) {
      this.appendEntry(DoubleTag.valueOf(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final byte[] value) {
      this.appendEntry(new ByteArrayTag(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final int[] value) {
      this.appendEntry(new IntArrayTag(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(final long[] value) {
      this.appendEntry(new LongArrayTag(value));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visitList(final TagType<?> elementType, final int size) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.EntryResult visitElement(final TagType<?> type, final int index) {
      this.enterContainerIfNeeded(type);
      return StreamTagVisitor.EntryResult.ENTER;
   }

   public StreamTagVisitor.EntryResult visitEntry(final TagType<?> type) {
      return StreamTagVisitor.EntryResult.ENTER;
   }

   public StreamTagVisitor.EntryResult visitEntry(final TagType<?> type, final String id) {
      ((ContainerBuilder)this.containerStack.getLast()).acceptKey(id);
      this.enterContainerIfNeeded(type);
      return StreamTagVisitor.EntryResult.ENTER;
   }

   private void enterContainerIfNeeded(final TagType<?> type) {
      if (type == ListTag.TYPE) {
         this.containerStack.addLast(new ListBuilder());
      } else if (type == CompoundTag.TYPE) {
         this.containerStack.addLast(new CompoundBuilder());
      }

   }

   public StreamTagVisitor.ValueResult visitContainerEnd() {
      ContainerBuilder container = (ContainerBuilder)this.containerStack.removeLast();
      Tag tag = container.build();
      if (tag != null) {
         ((ContainerBuilder)this.containerStack.getLast()).acceptValue(tag);
      }

      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visitRootEntry(final TagType<?> type) {
      this.enterContainerIfNeeded(type);
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   private interface ContainerBuilder {
      default void acceptKey(final String id) {
      }

      void acceptValue(Tag tag);

      @Nullable Tag build();
   }

   private static class RootBuilder implements ContainerBuilder {
      private @Nullable Tag result;

      private RootBuilder() {
         super();
      }

      public void acceptValue(final Tag tag) {
         this.result = tag;
      }

      public @Nullable Tag build() {
         return this.result;
      }
   }

   private static class CompoundBuilder implements ContainerBuilder {
      private final CompoundTag compound = new CompoundTag();
      private String lastId = "";

      private CompoundBuilder() {
         super();
      }

      public void acceptKey(final String id) {
         this.lastId = id;
      }

      public void acceptValue(final Tag tag) {
         this.compound.put(this.lastId, tag);
      }

      public Tag build() {
         return this.compound;
      }
   }

   private static class ListBuilder implements ContainerBuilder {
      private final ListTag list = new ListTag();

      private ListBuilder() {
         super();
      }

      public void acceptValue(final Tag tag) {
         this.list.addAndUnwrap(tag);
      }

      public Tag build() {
         return this.list;
      }
   }
}
