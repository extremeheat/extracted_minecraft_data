package net.minecraft.nbt.visitors;

import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public interface SkipAll extends StreamTagVisitor {
   SkipAll INSTANCE = new SkipAll() {
   };

   default StreamTagVisitor.ValueResult visitEnd() {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(String var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(byte var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(short var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(int var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(long var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(float var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(double var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(byte[] var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(int[] var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(long[] var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visitList(TagType<?> var1, int var2) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.EntryResult visitElement(TagType<?> var1, int var2) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   default StreamTagVisitor.EntryResult visitEntry(TagType<?> var1) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   default StreamTagVisitor.EntryResult visitEntry(TagType<?> var1, String var2) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   default StreamTagVisitor.ValueResult visitContainerEnd() {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visitRootEntry(TagType<?> var1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }
}
