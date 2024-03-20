package net.minecraft.network.syncher;

public record EntityDataAccessor<T>(int a, EntityDataSerializer<T> b) {
   private final int id;
   private final EntityDataSerializer<T> serializer;

   public EntityDataAccessor(int var1, EntityDataSerializer<T> var2) {
      super();
      this.id = var1;
      this.serializer = var2;
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