package net.minecraft.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ListTag extends CollectionTag<Tag> {
   private static final int SELF_SIZE_IN_BITS = 296;
   public static final TagType<ListTag> TYPE = new TagType.VariableSize<ListTag>() {
      public ListTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(296L);
         if (var2 > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
         } else {
            byte var4 = var1.readByte();
            int var5 = var1.readInt();
            if (var4 == 0 && var5 > 0) {
               throw new RuntimeException("Missing type on ListTag");
            } else {
               var3.accountBits(32L * (long)var5);
               TagType var6 = TagTypes.getType(var4);
               ArrayList var7 = Lists.newArrayListWithCapacity(var5);

               for(int var8 = 0; var8 < var5; ++var8) {
                  var7.add(var6.load(var1, var2 + 1, var3));
               }

               return new ListTag(var7, var4);
            }
         }
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2) throws IOException {
         TagType var3 = TagTypes.getType(var1.readByte());
         int var4 = var1.readInt();
         switch (var2.visitList(var3, var4)) {
            case HALT:
               return StreamTagVisitor.ValueResult.HALT;
            case BREAK:
               var3.skip(var1, var4);
               return var2.visitContainerEnd();
            default:
               int var5 = 0;

               label34:
               for(; var5 < var4; ++var5) {
                  switch (var2.visitElement(var3, var5)) {
                     case HALT:
                        return StreamTagVisitor.ValueResult.HALT;
                     case BREAK:
                        var3.skip(var1);
                        break label34;
                     case SKIP:
                        var3.skip(var1);
                        break;
                     default:
                        switch (var3.parse(var1, var2)) {
                           case HALT:
                              return StreamTagVisitor.ValueResult.HALT;
                           case BREAK:
                              break label34;
                        }
                  }
               }

               int var6 = var4 - 1 - var5;
               if (var6 > 0) {
                  var3.skip(var1, var6);
               }

               return var2.visitContainerEnd();
         }
      }

      public void skip(DataInput var1) throws IOException {
         TagType var2 = TagTypes.getType(var1.readByte());
         int var3 = var1.readInt();
         var2.skip(var1, var3);
      }

      public String getName() {
         return "LIST";
      }

      public String getPrettyName() {
         return "TAG_List";
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
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

   public void write(DataOutput var1) throws IOException {
      if (this.list.isEmpty()) {
         this.type = 0;
      } else {
         this.type = ((Tag)this.list.get(0)).getId();
      }

      var1.writeByte(this.type);
      var1.writeInt(this.list.size());
      Iterator var2 = this.list.iterator();

      while(var2.hasNext()) {
         Tag var3 = (Tag)var2.next();
         var3.write(var1);
      }

   }

   public byte getId() {
      return 9;
   }

   public TagType<ListTag> getType() {
      return TYPE;
   }

   public String toString() {
      return this.getAsString();
   }

   private void updateTypeAfterRemove() {
      if (this.list.isEmpty()) {
         this.type = 0;
      }

   }

   public Tag remove(int var1) {
      Tag var2 = (Tag)this.list.remove(var1);
      this.updateTypeAfterRemove();
      return var2;
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public CompoundTag getCompound(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         if (var2.getId() == 10) {
            return (CompoundTag)var2;
         }
      }

      return new CompoundTag();
   }

   public ListTag getList(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         if (var2.getId() == 9) {
            return (ListTag)var2;
         }
      }

      return new ListTag();
   }

   public short getShort(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         if (var2.getId() == 2) {
            return ((ShortTag)var2).getAsShort();
         }
      }

      return 0;
   }

   public int getInt(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         if (var2.getId() == 3) {
            return ((IntTag)var2).getAsInt();
         }
      }

      return 0;
   }

   public int[] getIntArray(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         if (var2.getId() == 11) {
            return ((IntArrayTag)var2).getAsIntArray();
         }
      }

      return new int[0];
   }

   public long[] getLongArray(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         if (var2.getId() == 11) {
            return ((LongArrayTag)var2).getAsLongArray();
         }
      }

      return new long[0];
   }

   public double getDouble(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         if (var2.getId() == 6) {
            return ((DoubleTag)var2).getAsDouble();
         }
      }

      return 0.0;
   }

   public float getFloat(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         if (var2.getId() == 5) {
            return ((FloatTag)var2).getAsFloat();
         }
      }

      return 0.0F;
   }

   public String getString(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         return var2.getId() == 8 ? var2.getAsString() : var2.toString();
      } else {
         return "";
      }
   }

   public int size() {
      return this.list.size();
   }

   public Tag get(int var1) {
      return (Tag)this.list.get(var1);
   }

   public Tag set(int var1, Tag var2) {
      Tag var3 = this.get(var1);
      if (!this.setTag(var1, var2)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", var2.getId(), this.type));
      } else {
         return var3;
      }
   }

   public void add(int var1, Tag var2) {
      if (!this.addTag(var1, var2)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", var2.getId(), this.type));
      }
   }

   public boolean setTag(int var1, Tag var2) {
      if (this.updateType(var2)) {
         this.list.set(var1, var2);
         return true;
      } else {
         return false;
      }
   }

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

   public void accept(TagVisitor var1) {
      var1.visitList(this);
   }

   public byte getElementType() {
      return this.type;
   }

   public void clear() {
      this.list.clear();
      this.type = 0;
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      switch (var1.visitList(TagTypes.getType(this.type), this.list.size())) {
         case HALT:
            return StreamTagVisitor.ValueResult.HALT;
         case BREAK:
            return var1.visitContainerEnd();
         default:
            int var2 = 0;

            while(var2 < this.list.size()) {
               Tag var3 = (Tag)this.list.get(var2);
               switch (var1.visitElement(var3.getType(), var2)) {
                  case HALT:
                     return StreamTagVisitor.ValueResult.HALT;
                  case BREAK:
                     return var1.visitContainerEnd();
                  default:
                     switch (var3.accept(var1)) {
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

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }

   // $FF: synthetic method
   public Object remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Object var2) {
      this.add(var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object get(int var1) {
      return this.get(var1);
   }
}
