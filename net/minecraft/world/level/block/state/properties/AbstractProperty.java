package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;

public abstract class AbstractProperty implements Property {
   private final Class clazz;
   private final String name;
   private Integer hashCode;

   protected AbstractProperty(String var1, Class var2) {
      this.clazz = var2;
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public Class getValueClass() {
      return this.clazz;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AbstractProperty)) {
         return false;
      } else {
         AbstractProperty var2 = (AbstractProperty)var1;
         return this.clazz.equals(var2.clazz) && this.name.equals(var2.name);
      }
   }

   public final int hashCode() {
      if (this.hashCode == null) {
         this.hashCode = this.generateHashCode();
      }

      return this.hashCode;
   }

   public int generateHashCode() {
      return 31 * this.clazz.hashCode() + this.name.hashCode();
   }
}
