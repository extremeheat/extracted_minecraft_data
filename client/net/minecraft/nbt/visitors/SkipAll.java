package net.minecraft.nbt.visitors;

import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public interface SkipAll extends StreamTagVisitor {
   SkipAll INSTANCE = new SkipAll() {
   };

   default StreamTagVisitor.ValueResult visitEnd() {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final String value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final byte value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final short value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final int value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final long value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final float value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final double value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final byte[] value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final int[] value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(final long[] value) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visitList(final TagType<?> elementType, final int size) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.EntryResult visitElement(final TagType<?> type, final int index) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   default StreamTagVisitor.EntryResult visitEntry(final TagType<?> type) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   default StreamTagVisitor.EntryResult visitEntry(final TagType<?> type, final String id) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   default StreamTagVisitor.ValueResult visitContainerEnd() {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visitRootEntry(final TagType<?> type) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }
}
