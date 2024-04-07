package net.minecraft.network.syncher;

public record EntityDataAccessor<T>(int id, EntityDataSerializer<T> serializer) {
   public EntityDataAccessor(int id, EntityDataSerializer<T> serializer) {
      super();
      this.id = id;
      this.serializer = serializer;
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
