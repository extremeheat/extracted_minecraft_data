package net.minecraft.world.entity.ai.attributes;

import javax.annotation.Nullable;

public abstract class BaseAttribute implements Attribute {
   private final Attribute parent;
   private final String name;
   private final double defaultValue;
   private boolean syncable;

   protected BaseAttribute(@Nullable Attribute var1, String var2, double var3) {
      this.parent = var1;
      this.name = var2;
      this.defaultValue = var3;
      if (var2 == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   public String getName() {
      return this.name;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isClientSyncable() {
      return this.syncable;
   }

   public BaseAttribute setSyncable(boolean var1) {
      this.syncable = var1;
      return this;
   }

   @Nullable
   public Attribute getParentAttribute() {
      return this.parent;
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public boolean equals(Object var1) {
      return var1 instanceof Attribute && this.name.equals(((Attribute)var1).getName());
   }
}
