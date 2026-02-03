package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public final class ListTag extends AbstractList<Tag> implements CollectionTag {
   private static final String WRAPPER_MARKER = "";
   private static final int SELF_SIZE_IN_BYTES = 36;
   public static final TagType<ListTag> TYPE = new TagType.VariableSize<ListTag>() {
      public ListTag load(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.pushDepth();

         ListTag var3;
         try {
            var3 = loadList(input, accounter);
         } finally {
            accounter.popDepth();
         }

         return var3;
      }

      private static ListTag loadList(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(36L);
         byte typeId = input.readByte();
         int count = readListCount(input);
         if (typeId == 0 && count > 0) {
            throw new NbtFormatException("Missing type on ListTag");
         } else {
            accounter.accountBytes(4L, (long)count);
            TagType<?> type = TagTypes.getType(typeId);
            ListTag list = new ListTag(new ArrayList(count));

            for(int i = 0; i < count; ++i) {
               list.addAndUnwrap(type.load(input, accounter));
            }

            return list;
         }
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         accounter.pushDepth();

         StreamTagVisitor.ValueResult var4;
         try {
            var4 = parseList(input, output, accounter);
         } finally {
            accounter.popDepth();
         }

         return var4;
      }

      private static StreamTagVisitor.ValueResult parseList(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(36L);
         TagType<?> elementType = TagTypes.getType(input.readByte());
         int count = readListCount(input);
         switch (output.visitList(elementType, count)) {
            case HALT:
               return StreamTagVisitor.ValueResult.HALT;
            case BREAK:
               elementType.skip(input, count, accounter);
               return output.visitContainerEnd();
            default:
               accounter.accountBytes(4L, (long)count);
               int i = 0;

               while(true) {
                  label45: {
                     if (i < count) {
                        switch (output.visitElement(elementType, i)) {
                           case HALT:
                              return StreamTagVisitor.ValueResult.HALT;
                           case BREAK:
                              elementType.skip(input, accounter);
                              break;
                           case SKIP:
                              elementType.skip(input, accounter);
                              break label45;
                           default:
                              switch (elementType.parse(input, output, accounter)) {
                                 case HALT -> {
                                    return StreamTagVisitor.ValueResult.HALT;
                                 }
                                 case BREAK -> { }
                                 default -> { }
                              }
                        }
                     }

                     int amountToSkip = count - 1 - i;
                     if (amountToSkip > 0) {
                        elementType.skip(input, amountToSkip, accounter);
                     }

                     return output.visitContainerEnd();
                  }

                  ++i;
               }
         }
      }

      private static int readListCount(final DataInput input) throws IOException {
         int count = input.readInt();
         if (count < 0) {
            throw new NbtFormatException("ListTag length cannot be negative: " + count);
         } else {
            return count;
         }
      }

      public void skip(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.pushDepth();

         try {
            TagType<?> type = TagTypes.getType(input.readByte());
            int count = input.readInt();
            type.skip(input, count, accounter);
         } finally {
            accounter.popDepth();
         }

      }

      public String getName() {
         return "LIST";
      }

      public String getPrettyName() {
         return "TAG_List";
      }
   };
   private final List<Tag> list;

   public ListTag() {
      this(new ArrayList());
   }

   ListTag(final List<Tag> list) {
      super();
      this.list = list;
   }

   private static Tag tryUnwrap(final CompoundTag tag) {
      if (tag.size() == 1) {
         Tag value = tag.get("");
         if (value != null) {
            return value;
         }
      }

      return tag;
   }

   private static boolean isWrapper(final CompoundTag tag) {
      return tag.size() == 1 && tag.contains("");
   }

   private static Tag wrapIfNeeded(final byte elementType, final Tag tag) {
      if (elementType != 10) {
         return tag;
      } else {
         if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            if (!isWrapper(compoundTag)) {
               return compoundTag;
            }
         }

         return wrapElement(tag);
      }
   }

   private static CompoundTag wrapElement(final Tag tag) {
      return new CompoundTag(Map.of("", tag));
   }

   public void write(final DataOutput output) throws IOException {
      byte elementType = this.identifyRawElementType();
      output.writeByte(elementType);
      output.writeInt(this.list.size());

      for(Tag element : this.list) {
         wrapIfNeeded(elementType, element).write(output);
      }

   }

   @VisibleForTesting
   byte identifyRawElementType() {
      byte homogenousType = 0;

      for(Tag element : this.list) {
         byte elementType = element.getId();
         if (homogenousType == 0) {
            homogenousType = elementType;
         } else if (homogenousType != elementType) {
            return 10;
         }
      }

      return homogenousType;
   }

   public void addAndUnwrap(final Tag tag) {
      if (tag instanceof CompoundTag compound) {
         this.add(tryUnwrap(compound));
      } else {
         this.add(tag);
      }

   }

   public int sizeInBytes() {
      int size = 36;
      size += 4 * this.list.size();

      for(Tag child : this.list) {
         size += child.sizeInBytes();
      }

      return size;
   }

   public byte getId() {
      return 9;
   }

   public TagType<ListTag> getType() {
      return TYPE;
   }

   public String toString() {
      StringTagVisitor visitor = new StringTagVisitor();
      visitor.visitList(this);
      return visitor.build();
   }

   public Tag remove(final int index) {
      return (Tag)this.list.remove(index);
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public Optional<CompoundTag> getCompound(final int index) {
      Tag var3 = this.getNullable(index);
      if (var3 instanceof CompoundTag tag) {
         return Optional.of(tag);
      } else {
         return Optional.empty();
      }
   }

   public CompoundTag getCompoundOrEmpty(final int index) {
      return (CompoundTag)this.getCompound(index).orElseGet(CompoundTag::new);
   }

   public Optional<ListTag> getList(final int index) {
      Tag var3 = this.getNullable(index);
      if (var3 instanceof ListTag tag) {
         return Optional.of(tag);
      } else {
         return Optional.empty();
      }
   }

   public ListTag getListOrEmpty(final int index) {
      return (ListTag)this.getList(index).orElseGet(ListTag::new);
   }

   public Optional<Short> getShort(final int index) {
      return this.getOptional(index).flatMap(Tag::asShort);
   }

   public short getShortOr(final int index, final short defaultValue) {
      Tag var4 = this.getNullable(index);
      if (var4 instanceof NumericTag tag) {
         return tag.shortValue();
      } else {
         return defaultValue;
      }
   }

   public Optional<Integer> getInt(final int index) {
      return this.getOptional(index).flatMap(Tag::asInt);
   }

   public int getIntOr(final int index, final int defaultValue) {
      Tag var4 = this.getNullable(index);
      if (var4 instanceof NumericTag tag) {
         return tag.intValue();
      } else {
         return defaultValue;
      }
   }

   public Optional<int[]> getIntArray(final int index) {
      Tag var3 = this.getNullable(index);
      if (var3 instanceof IntArrayTag tag) {
         return Optional.of(tag.getAsIntArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional<long[]> getLongArray(final int index) {
      Tag var3 = this.getNullable(index);
      if (var3 instanceof LongArrayTag tag) {
         return Optional.of(tag.getAsLongArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional<Double> getDouble(final int index) {
      return this.getOptional(index).flatMap(Tag::asDouble);
   }

   public double getDoubleOr(final int index, final double defaultValue) {
      Tag var5 = this.getNullable(index);
      if (var5 instanceof NumericTag tag) {
         return tag.doubleValue();
      } else {
         return defaultValue;
      }
   }

   public Optional<Float> getFloat(final int index) {
      return this.getOptional(index).flatMap(Tag::asFloat);
   }

   public float getFloatOr(final int index, final float defaultValue) {
      Tag var4 = this.getNullable(index);
      if (var4 instanceof NumericTag tag) {
         return tag.floatValue();
      } else {
         return defaultValue;
      }
   }

   public Optional<String> getString(final int index) {
      return this.getOptional(index).flatMap(Tag::asString);
   }

   public String getStringOr(final int index, final String defaultValue) {
      Tag tag = this.getNullable(index);
      if (tag instanceof StringTag var4) {
         StringTag var10000 = var4;

         try {
            var8 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         String value = var8;
         return value;
      } else {
         return defaultValue;
      }
   }

   private @Nullable Tag getNullable(final int index) {
      return index >= 0 && index < this.list.size() ? (Tag)this.list.get(index) : null;
   }

   private Optional<Tag> getOptional(final int index) {
      return Optional.ofNullable(this.getNullable(index));
   }

   public int size() {
      return this.list.size();
   }

   public Tag get(final int index) {
      return (Tag)this.list.get(index);
   }

   public Tag set(final int index, final Tag tag) {
      return (Tag)this.list.set(index, tag);
   }

   public void add(final int index, final Tag tag) {
      this.list.add(index, tag);
   }

   public boolean setTag(final int index, final Tag tag) {
      this.list.set(index, tag);
      return true;
   }

   public boolean addTag(final int index, final Tag tag) {
      this.list.add(index, tag);
      return true;
   }

   public ListTag copy() {
      List<Tag> copy = new ArrayList(this.list.size());

      for(Tag tag : this.list) {
         copy.add(tag.copy());
      }

      return new ListTag(copy);
   }

   public Optional<ListTag> asList() {
      return Optional.of(this);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof ListTag && Objects.equals(this.list, ((ListTag)obj).list);
      }
   }

   public int hashCode() {
      return this.list.hashCode();
   }

   public Stream<Tag> stream() {
      return super.stream();
   }

   public Stream<CompoundTag> compoundStream() {
      return this.stream().mapMulti((tag, output) -> {
         if (tag instanceof CompoundTag compound) {
            output.accept(compound);
         }

      });
   }

   public void accept(final TagVisitor visitor) {
      visitor.visitList(this);
   }

   public void clear() {
      this.list.clear();
   }

   public StreamTagVisitor.ValueResult accept(final StreamTagVisitor visitor) {
      byte elementType = this.identifyRawElementType();
      switch (visitor.visitList(TagTypes.getType(elementType), this.list.size())) {
         case HALT:
            return StreamTagVisitor.ValueResult.HALT;
         case BREAK:
            return visitor.visitContainerEnd();
         default:
            int i = 0;

            while(i < this.list.size()) {
               Tag tag = wrapIfNeeded(elementType, (Tag)this.list.get(i));
               switch (visitor.visitElement(tag.getType(), i)) {
                  case HALT:
                     return StreamTagVisitor.ValueResult.HALT;
                  case BREAK:
                     return visitor.visitContainerEnd();
                  default:
                     switch (tag.accept(visitor)) {
                        case HALT -> {
                           return StreamTagVisitor.ValueResult.HALT;
                        }
                        case BREAK -> {
                           return visitor.visitContainerEnd();
                        }
                     }
                  case SKIP:
                     ++i;
               }
            }

            return visitor.visitContainerEnd();
      }
   }
}
