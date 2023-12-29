package net.minecraft.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ListTag extends CollectionTag<Tag> {
   private static final int SELF_SIZE_IN_BYTES = 37;
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
         var1.accountBytes(37L);
         byte var2 = var0.readByte();
         int var3 = var0.readInt();
         if (var2 == 0 && var3 > 0) {
            throw new NbtFormatException("Missing type on ListTag");
         } else {
            var1.accountBytes(4L, (long)var3);
            TagType var4 = TagTypes.getType(var2);
            ArrayList var5 = Lists.newArrayListWithCapacity(var3);

            for(int var6 = 0; var6 < var3; ++var6) {
               var5.add(var4.load(var0, var1));
            }

            return new ListTag(var5, var2);
         }
      }

      @Override
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
         var2.accountBytes(37L);
         TagType var3 = TagTypes.getType(var0.readByte());
         int var4 = var0.readInt();
         switch(var1.visitList(var3, var4)) {
            case HALT:
               return StreamTagVisitor.ValueResult.HALT;
            case BREAK:
               var3.skip(var0, var4, var2);
               return var1.visitContainerEnd();
            default:
               var2.accountBytes(4L, (long)var4);
               int var5 = 0;

               while(true) {
                  label41: {
                     if (var5 < var4) {
                        switch(var1.visitElement(var3, var5)) {
                           case HALT:
                              return StreamTagVisitor.ValueResult.HALT;
                           case BREAK:
                              var3.skip(var0, var2);
                              break;
                           case SKIP:
                              var3.skip(var0, var2);
                              break label41;
                           default:
                              switch(var3.parse(var0, var1, var2)) {
                                 case HALT:
                                    return StreamTagVisitor.ValueResult.HALT;
                                 case BREAK:
                                    break;
                                 default:
                                    break label41;
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

      @Override
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

      @Override
      public String getName() {
         return "LIST";
      }

      @Override
      public String getPrettyName() {
         return "TAG_List";
      }
   };
   private final List<Tag> list;
   private byte type;

   ListTag(List<Tag> var1, byte var2) {
      super();
      this.list = var1;
      this.type = var2;
   }

   public ListTag() {
      this(Lists.newArrayList(), (byte)0);
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      if (this.list.isEmpty()) {
         this.type = 0;
      } else {
         this.type = this.list.get(0).getId();
      }

      var1.writeByte(this.type);
      var1.writeInt(this.list.size());

      for(Tag var3 : this.list) {
         var3.write(var1);
      }
   }

   @Override
   public int sizeInBytes() {
      int var1 = 37;
      var1 += 4 * this.list.size();

      for(Tag var3 : this.list) {
         var1 += var3.sizeInBytes();
      }

      return var1;
   }

   @Override
   public byte getId() {
      return 9;
   }

   @Override
   public TagType<ListTag> getType() {
      return TYPE;
   }

   @Override
   public String toString() {
      return this.getAsString();
   }

   private void updateTypeAfterRemove() {
      if (this.list.isEmpty()) {
         this.type = 0;
      }
   }

   @Override
   public Tag remove(int var1) {
      Tag var2 = this.list.remove(var1);
      this.updateTypeAfterRemove();
      return var2;
   }

   @Override
   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public CompoundTag getCompound(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = this.list.get(var1);
         if (var2.getId() == 10) {
            return (CompoundTag)var2;
         }
      }

      return new CompoundTag();
   }

   public ListTag getList(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = this.list.get(var1);
         if (var2.getId() == 9) {
            return (ListTag)var2;
         }
      }

      return new ListTag();
   }

   public short getShort(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = this.list.get(var1);
         if (var2.getId() == 2) {
            return ((ShortTag)var2).getAsShort();
         }
      }

      return 0;
   }

   public int getInt(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = this.list.get(var1);
         if (var2.getId() == 3) {
            return ((IntTag)var2).getAsInt();
         }
      }

      return 0;
   }

   public int[] getIntArray(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = this.list.get(var1);
         if (var2.getId() == 11) {
            return ((IntArrayTag)var2).getAsIntArray();
         }
      }

      return new int[0];
   }

   public long[] getLongArray(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = this.list.get(var1);
         if (var2.getId() == 12) {
            return ((LongArrayTag)var2).getAsLongArray();
         }
      }

      return new long[0];
   }

   public double getDouble(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = this.list.get(var1);
         if (var2.getId() == 6) {
            return ((DoubleTag)var2).getAsDouble();
         }
      }

      return 0.0;
   }

   public float getFloat(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = this.list.get(var1);
         if (var2.getId() == 5) {
            return ((FloatTag)var2).getAsFloat();
         }
      }

      return 0.0F;
   }

   public String getString(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = this.list.get(var1);
         return var2.getId() == 8 ? var2.getAsString() : var2.toString();
      } else {
         return "";
      }
   }

   @Override
   public int size() {
      return this.list.size();
   }

   public Tag get(int var1) {
      return this.list.get(var1);
   }

   @Override
   public Tag set(int var1, Tag var2) {
      Tag var3 = this.get(var1);
      if (!this.setTag(var1, var2)) {
         throw new UnsupportedOperationException(String.format(Locale.ROOT, "Trying to add tag of type %d to list of %d", var2.getId(), this.type));
      } else {
         return var3;
      }
   }

   @Override
   public void add(int var1, Tag var2) {
      if (!this.addTag(var1, var2)) {
         throw new UnsupportedOperationException(String.format(Locale.ROOT, "Trying to add tag of type %d to list of %d", var2.getId(), this.type));
      }
   }

   @Override
   public boolean setTag(int var1, Tag var2) {
      if (this.updateType(var2)) {
         this.list.set(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean addTag(int var1, Tag var2) {
      if (this.updateType(var2)) {
         this.list.add(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   private boolean updateType(Tag var1) {
      if (var1.getId() == 0) {
         return false;
      } else if (this.type == 0) {
         this.type = var1.getId();
         return true;
      } else {
         return this.type == var1.getId();
      }
   }

   public ListTag copy() {
      Object var1 = TagTypes.getType(this.type).isValue() ? this.list : Iterables.transform(this.list, Tag::copy);
      ArrayList var2 = Lists.newArrayList((Iterable)var1);
      return new ListTag(var2, this.type);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ListTag && Objects.equals(this.list, ((ListTag)var1).list);
      }
   }

   @Override
   public int hashCode() {
      return this.list.hashCode();
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitList(this);
   }

   @Override
   public byte getElementType() {
      return this.type;
   }

   @Override
   public void clear() {
      this.list.clear();
      this.type = 0;
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      switch(var1.visitList(TagTypes.getType(this.type), this.list.size())) {
         case HALT:
            return StreamTagVisitor.ValueResult.HALT;
         case BREAK:
            return var1.visitContainerEnd();
         default:
            int var2 = 0;

            while(var2 < this.list.size()) {
               Tag var3 = this.list.get(var2);
               switch(var1.visitElement(var3.getType(), var2)) {
                  case HALT:
                     return StreamTagVisitor.ValueResult.HALT;
                  case BREAK:
                     return var1.visitContainerEnd();
                  default:
                     switch(var3.accept(var1)) {
                        case HALT:
                           return StreamTagVisitor.ValueResult.HALT;
                        case BREAK:
                           return var1.visitContainerEnd();
                     }
                  case SKIP:
                     ++var2;
               }
            }

            return var1.visitContainerEnd();
      }
   }
}
