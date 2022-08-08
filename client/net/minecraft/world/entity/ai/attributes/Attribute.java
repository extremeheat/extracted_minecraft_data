package net.minecraft.world.entity.ai.attributes;

public class Attribute {
   public static final int MAX_NAME_LENGTH = 64;
   private final double defaultValue;
   private boolean syncable;
   private final String descriptionId;

   protected Attribute(String var1, double var2) {
      super();
      this.defaultValue = var2;
      this.descriptionId = var1;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isClientSyncable() {
      return this.syncable;
   }

   public Attribute setSyncable(boolean var1) {
      this.syncable = var1;
      return this;
   }

   public double sanitizeValue(double var1) {
      return var1;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }
}
