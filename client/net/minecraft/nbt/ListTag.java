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
import javax.annotation.Nullable;

public final class ListTag extends AbstractList<Tag> implements CollectionTag {
   private static final String WRAPPER_MARKER = "";
   private static final int SELF_SIZE_IN_BYTES = 36;
   public static final TagType<ListTag> TYPE = new TagType.VariableSize<ListTag>() {
      public ListTag load(DataInput var1, NbtAccounter var2) throws IOException {
         var2.pushDepth();

         ListTag var3;
         try {
            var3 = loadList(var1, var2);
         } finally {
            var2.popDepth();
         }

         return var3;
      }

      private static ListTag loadList(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(36L);
         byte var2 = var0.readByte();
         int var3 = var0.readInt();
         if (var2 == 0 && var3 > 0) {
            throw new NbtFormatException("Missing type on ListTag");
         } else {
            var1.accountBytes(4L, (long)var3);
            TagType var4 = TagTypes.getType(var2);
            ListTag var5 = new ListTag(new ArrayList(var3));

            for(int var6 = 0; var6 < var3; ++var6) {
               var5.addAndUnwrap(var4.load(var0, var1));
            }

            return var5;
         }
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         var3.pushDepth();

         StreamTagVisitor.ValueResult var4;
         try {
            var4 = parseList(var1, var2, var3);
         } finally {
            var3.popDepth();
         }

         return var4;
      }

      private static StreamTagVisitor.ValueResult parseList(DataInput var0, StreamTagVisitor var1, NbtAccounter var2) throws IOException {
         var2.accountBytes(36L);
         TagType var3 = TagTypes.getType(var0.readByte());
         int var4 = var0.readInt();
         switch (var1.visitList(var3, var4)) {
            case HALT:
               return StreamTagVisitor.ValueResult.HALT;
            case BREAK:
               var3.skip(var0, var4, var2);
               return var1.visitContainerEnd();
            default:
               var2.accountBytes(4L, (long)var4);
               int var5 = 0;

               while(true) {
                  label45: {
                     if (var5 < var4) {
                        switch (var1.visitElement(var3, var5)) {
                           case HALT:
                              return StreamTagVisitor.ValueResult.HALT;
                           case BREAK:
                              var3.skip(var0, var2);
                              break;
                           case SKIP:
                              var3.skip(var0, var2);
                              break label45;
                           default:
                              switch (var3.parse(var0, var1, var2)) {
                                 case HALT -> {
                                    return StreamTagVisitor.ValueResult.HALT;
                                 }
                                 case BREAK -> { }
                                 default -> { }
                              }
                        }
                     }

                     int var6 = var4 - 1 - var5;
                     if (var6 > 0) {
                        var3.skip(var0, var6, var2);
                     }

                     return var1.visitContainerEnd();
                  }

                  ++var5;
               }
         }
      }

      public void skip(DataInput var1, NbtAccounter var2) throws IOException {
         var2.pushDepth();

         try {
            TagType var3 = TagTypes.getType(var1.readByte());
            int var4 = var1.readInt();
            var3.skip(var1, var4, var2);
         } finally {
            var2.popDepth();
         }

      }

      public String getName() {
         return "LIST";
      }

      public String getPrettyName() {
         return "TAG_List";
      }

      // $FF: synthetic method
      public Tag load(final DataInput var1, final NbtAccounter var2) throws IOException {
         return this.load(var1, var2);
      }
   };
   private final List<Tag> list;

   public ListTag() {
      this(new ArrayList());
   }

   ListTag(List<Tag> var1) {
      super();
      this.list = var1;
   }

   private static Tag tryUnwrap(CompoundTag var0) {
      if (var0.size() == 1) {
         Tag var1 = var0.get("");
         if (var1 != null) {
            return var1;
         }
      }

      return var0;
   }

   private static boolean isWrapper(CompoundTag var0) {
      return var0.size() == 1 && var0.contains("");
   }

   private static Tag wrapIfNeeded(byte var0, Tag var1) {
      if (var0 != 10) {
         return var1;
      } else {
         if (var1 instanceof CompoundTag) {
            CompoundTag var2 = (CompoundTag)var1;
            if (!isWrapper(var2)) {
               return var2;
            }
         }

         return wrapElement(var1);
      }
   }

   private static CompoundTag wrapElement(Tag var0) {
      return new CompoundTag(Map.of("", var0));
   }

   public void write(DataOutput var1) throws IOException {
      byte var2 = this.identifyRawElementType();
      var1.writeByte(var2);
      var1.writeInt(this.list.size());

      for(Tag var4 : this.list) {
         wrapIfNeeded(var2, var4).write(var1);
      }

   }

   @VisibleForTesting
   byte identifyRawElementType() {
      byte var1 = 0;

      for(Tag var3 : this.list) {
         byte var4 = var3.getId();
         if (var1 == 0) {
            var1 = var4;
         } else if (var1 != var4) {
            return 10;
         }
      }

      return var1;
   }

   public void addAndUnwrap(Tag var1) {
      if (var1 instanceof CompoundTag var2) {
         this.add(tryUnwrap(var2));
      } else {
         this.add(var1);
      }

   }

   public int sizeInBytes() {
      int var1 = 36;
      var1 += 4 * this.list.size();

      for(Tag var3 : this.list) {
         var1 += var3.sizeInBytes();
      }

      return var1;
   }

   public byte getId() {
      return 9;
   }

   public TagType<ListTag> getType() {
      return TYPE;
   }

   public String toString() {
      StringTagVisitor var1 = new StringTagVisitor();
      var1.visitList(this);
      return var1.build();
   }

   public Tag remove(int var1) {
      return (Tag)this.list.remove(var1);
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public Optional<CompoundTag> getCompound(int var1) {
      Tag var3 = this.getNullable(var1);
      if (var3 instanceof CompoundTag var2) {
         return Optional.of(var2);
      } else {
         return Optional.empty();
      }
   }

   public CompoundTag getCompoundOrEmpty(int var1) {
      return (CompoundTag)this.getCompound(var1).orElseGet(CompoundTag::new);
   }

   public Optional<ListTag> getList(int var1) {
      Tag var3 = this.getNullable(var1);
      if (var3 instanceof ListTag var2) {
         return Optional.of(var2);
      } else {
         return Optional.empty();
      }
   }

   public ListTag getListOrEmpty(int var1) {
      return (ListTag)this.getList(var1).orElseGet(ListTag::new);
   }

   public Optional<Short> getShort(int var1) {
      return this.getOptional(var1).flatMap(Tag::asShort);
   }

   public short getShortOr(int var1, short var2) {
      Tag var4 = this.getNullable(var1);
      if (var4 instanceof NumericTag var3) {
         return var3.shortValue();
      } else {
         return var2;
      }
   }

   public Optional<Integer> getInt(int var1) {
      return this.getOptional(var1).flatMap(Tag::asInt);
   }

   public int getIntOr(int var1, int var2) {
      Tag var4 = this.getNullable(var1);
      if (var4 instanceof NumericTag var3) {
         return var3.intValue();
      } else {
         return var2;
      }
   }

   public Optional<int[]> getIntArray(int var1) {
      Tag var3 = this.getNullable(var1);
      if (var3 instanceof IntArrayTag var2) {
         return Optional.of(var2.getAsIntArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional<long[]> getLongArray(int var1) {
      Tag var3 = this.getNullable(var1);
      if (var3 instanceof LongArrayTag var2) {
         return Optional.of(var2.getAsLongArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional<Double> getDouble(int var1) {
      return this.getOptional(var1).flatMap(Tag::asDouble);
   }

   public double getDoubleOr(int var1, double var2) {
      Tag var5 = this.getNullable(var1);
      if (var5 instanceof NumericTag var4) {
         return var4.doubleValue();
      } else {
         return var2;
      }
   }

   public Optional<Float> getFloat(int var1) {
      return this.getOptional(var1).flatMap(Tag::asFloat);
   }

   public float getFloatOr(int var1, float var2) {
      Tag var4 = this.getNullable(var1);
      if (var4 instanceof NumericTag var3) {
         return var3.floatValue();
      } else {
         return var2;
      }
   }

   public Optional<String> getString(int var1) {
      return this.getOptional(var1).flatMap(Tag::asString);
   }

   public String getStringOr(int var1, String var2) {
      Tag var3 = this.getNullable(var1);
      if (var3 instanceof StringTag var4) {
         StringTag var10000 = var4;

         try {
            var8 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         String var6 = var8;
         return var6;
      } else {
         return var2;
      }
   }

   @Nullable
   private Tag getNullable(int var1) {
      return var1 >= 0 && var1 < this.list.size() ? (Tag)this.list.get(var1) : null;
   }

   private Optional<Tag> getOptional(int var1) {
      return Optional.ofNullable(this.getNullable(var1));
   }

   public int size() {
      return this.list.size();
   }

   public Tag get(int var1) {
      return (Tag)this.list.get(var1);
   }

   public Tag set(int var1, Tag var2) {
      return (Tag)this.list.set(var1, var2);
   }

   public void add(int var1, Tag var2) {
      this.list.add(var1, var2);
   }

   public boolean setTag(int var1, Tag var2) {
      this.list.set(var1, var2);
      return true;
   }

   public boolean addTag(int var1, Tag var2) {
      this.list.add(var1, var2);
      return true;
   }

   public ListTag copy() {
      ArrayList var1 = new ArrayList(this.list.size());

      for(Tag var3 : this.list) {
         var1.add(var3.copy());
      }

      return new ListTag(var1);
   }

   public Optional<ListTag> asList() {
      return Optional.of(this);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ListTag && Objects.equals(this.list, ((ListTag)var1).list);
      }
   }

   public int hashCode() {
      return this.list.hashCode();
   }

   public Stream<Tag> stream() {
      return super.stream();
   }

   public Stream<CompoundTag> compoundStream() {
      return this.stream().mapMulti((var0, var1) -> {
         if (var0 instanceof CompoundTag var2) {
            var1.accept(var2);
         }

      });
   }

   public void accept(TagVisitor var1) {
      var1.visitList(this);
   }

   public void clear() {
      this.list.clear();
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      byte var2 = this.identifyRawElementType();
      switch (var1.visitList(TagTypes.getType(var2), this.list.size())) {
         case HALT:
            return StreamTagVisitor.ValueResult.HALT;
         case BREAK:
            return var1.visitContainerEnd();
         default:
            int var3 = 0;

            while(var3 < this.list.size()) {
               Tag var4 = wrapIfNeeded(var2, (Tag)this.list.get(var3));
               switch (var1.visitElement(var4.getType(), var3)) {
                  case HALT:
                     return StreamTagVisitor.ValueResult.HALT;
                  case BREAK:
                     return var1.visitContainerEnd();
                  default:
                     switch (var4.accept(var1)) {
                        case HALT -> {
                           return StreamTagVisitor.ValueResult.HALT;
                        }
                        case BREAK -> {
                           return var1.visitContainerEnd();
                        }
                     }
                  case SKIP:
                     ++var3;
               }
            }

            return var1.visitContainerEnd();
      }
   }

   // $FF: synthetic method
   public Object remove(final int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(final int var1, final Object var2) {
      this.add(var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object set(final int var1, final Object var2) {
      return this.set(var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object get(final int var1) {
      return this.get(var1);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
