package net.minecraft.network.syncher;

public class EntityDataAccessor {
   private final int id;
   private final EntityDataSerializer serializer;

   public EntityDataAccessor(int var1, EntityDataSerializer var2) {
      this.id = var1;
      this.serializer = var2;
   }

   public int getId() {
      return this.id;
   }

   public EntityDataSerializer getSerializer() {
      return this.serializer;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         EntityDataAccessor var2 = (EntityDataAccessor)var1;
         return this.id == var2.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id;
   }

   public String toString() {
      return "<entity data: " + this.id + ">";
   }
}
