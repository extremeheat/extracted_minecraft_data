package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ListTag extends CollectionTag<Tag> {
   private List<Tag> list = Lists.newArrayList();
   private byte type = 0;

   public ListTag() {
      super();
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

   public void load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
      var3.accountBits(296L);
      if (var2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.type = var1.readByte();
         int var4 = var1.readInt();
         if (this.type == 0 && var4 > 0) {
            throw new RuntimeException("Missing type on ListTag");
         } else {
            var3.accountBits(32L * (long)var4);
            this.list = Lists.newArrayListWithCapacity(var4);

            for(int var5 = 0; var5 < var4; ++var5) {
               Tag var6 = Tag.newTag(this.type);
               var6.load(var1, var2 + 1, var3);
               this.list.add(var6);
            }

         }
      }
   }

   public byte getId() {
      return 9;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[");

      for(int var2 = 0; var2 < this.list.size(); ++var2) {
         if (var2 != 0) {
            var1.append(',');
         }

         var1.append(this.list.get(var2));
      }

      return var1.append(']').toString();
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

   public double getDouble(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         Tag var2 = (Tag)this.list.get(var1);
         if (var2.getId() == 6) {
            return ((DoubleTag)var2).getAsDouble();
         }
      }

      return 0.0D;
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
      ListTag var1 = new ListTag();
      var1.type = this.type;
      Iterator var2 = this.list.iterator();

      while(var2.hasNext()) {
         Tag var3 = (Tag)var2.next();
         Tag var4 = var3.copy();
         var1.list.add(var4);
      }

      return var1;
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

   public Component getPrettyDisplay(String var1, int var2) {
      if (this.isEmpty()) {
         return new TextComponent("[]");
      } else {
         TextComponent var3 = new TextComponent("[");
         if (!var1.isEmpty()) {
            var3.append("\n");
         }

         for(int var4 = 0; var4 < this.list.size(); ++var4) {
            TextComponent var5 = new TextComponent(Strings.repeat(var1, var2 + 1));
            var5.append(((Tag)this.list.get(var4)).getPrettyDisplay(var1, var2 + 1));
            if (var4 != this.list.size() - 1) {
               var5.append(String.valueOf(',')).append(var1.isEmpty() ? " " : "\n");
            }

            var3.append((Component)var5);
         }

         if (!var1.isEmpty()) {
            var3.append("\n").append(Strings.repeat(var1, var2));
         }

         var3.append("]");
         return var3;
      }
   }

   public int getElementType() {
      return this.type;
   }

   public void clear() {
      this.list.clear();
      this.type = 0;
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
