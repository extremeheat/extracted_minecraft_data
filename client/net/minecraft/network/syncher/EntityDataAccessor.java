package net.minecraft.network.syncher;

public class EntityDataAccessor<T> {
   // $FF: renamed from: id int
   private final int field_299;
   private final EntityDataSerializer<T> serializer;

   public EntityDataAccessor(int var1, EntityDataSerializer<T> var2) {
      super();
      this.field_299 = var1;
      this.serializer = var2;
   }

   public int getId() {
      return this.field_299;
   }

   public EntityDataSerializer<T> getSerializer() {
      return this.serializer;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         EntityDataAccessor var2 = (EntityDataAccessor)var1;
         return this.field_299 == var2.field_299;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_299;
   }

   public String toString() {
      return "<entity data: " + this.field_299 + ">";
   }
}
