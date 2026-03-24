package net.minecraft.network.syncher;

public record EntityDataAccessor<T>(int id, EntityDataSerializer<T> serializer) {
   public EntityDataAccessor {
      super();
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         EntityDataAccessor<?> that = (EntityDataAccessor)o;
         return this.id == that.id;
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
