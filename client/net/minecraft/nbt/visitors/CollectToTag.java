package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
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
   private String lastId = "";
   @Nullable
   private Tag rootTag;
   private final Deque<Consumer<Tag>> consumerStack = new ArrayDeque();

   public CollectToTag() {
      super();
   }

   @Nullable
   public Tag getResult() {
      return this.rootTag;
   }

   protected int depth() {
      return this.consumerStack.size();
   }

   private void appendEntry(Tag var1) {
      ((Consumer)this.consumerStack.getLast()).accept(var1);
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
      this.lastId = var2;
      this.enterContainerIfNeeded(var1);
      return StreamTagVisitor.EntryResult.ENTER;
   }

   private void enterContainerIfNeeded(TagType<?> var1) {
      if (var1 == ListTag.TYPE) {
         ListTag var2 = new ListTag();
         this.appendEntry(var2);
         Deque var10000 = this.consumerStack;
         Objects.requireNonNull(var2);
         var10000.addLast(var2::add);
      } else if (var1 == CompoundTag.TYPE) {
         CompoundTag var3 = new CompoundTag();
         this.appendEntry(var3);
         this.consumerStack.addLast((var2x) -> {
            var3.put(this.lastId, var2x);
         });
      }

   }

   public StreamTagVisitor.ValueResult visitContainerEnd() {
      this.consumerStack.removeLast();
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visitRootEntry(TagType<?> var1) {
      if (var1 == ListTag.TYPE) {
         ListTag var2 = new ListTag();
         this.rootTag = var2;
         Deque var10000 = this.consumerStack;
         Objects.requireNonNull(var2);
         var10000.addLast(var2::add);
      } else if (var1 == CompoundTag.TYPE) {
         CompoundTag var3 = new CompoundTag();
         this.rootTag = var3;
         this.consumerStack.addLast((var2x) -> {
            var3.put(this.lastId, var2x);
         });
      } else {
         this.consumerStack.addLast((var1x) -> {
            this.rootTag = var1x;
         });
      }

      return StreamTagVisitor.ValueResult.CONTINUE;
   }
}
