package net.minecraft.world.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.util.Mth;

public class AttributeModifier {
   private final double amount;
   private final AttributeModifier.Operation operation;
   private final Supplier nameGetter;
   private final UUID id;
   private boolean serialize;

   public AttributeModifier(String var1, double var2, AttributeModifier.Operation var4) {
      this(Mth.createInsecureUUID(ThreadLocalRandom.current()), () -> {
         return var1;
      }, var2, var4);
   }

   public AttributeModifier(UUID var1, String var2, double var3, AttributeModifier.Operation var5) {
      this(var1, () -> {
         return var2;
      }, var3, var5);
   }

   public AttributeModifier(UUID var1, Supplier var2, double var3, AttributeModifier.Operation var5) {
      this.serialize = true;
      this.id = var1;
      this.nameGetter = var2;
      this.amount = var3;
      this.operation = var5;
   }

   public UUID getId() {
      return this.id;
   }

   public String getName() {
      return (String)this.nameGetter.get();
   }

   public AttributeModifier.Operation getOperation() {
      return this.operation;
   }

   public double getAmount() {
      return this.amount;
   }

   public boolean isSerializable() {
      return this.serialize;
   }

   public AttributeModifier setSerialize(boolean var1) {
      this.serialize = var1;
      return this;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         AttributeModifier var2 = (AttributeModifier)var1;
         return Objects.equals(this.id, var2.id);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id != null ? this.id.hashCode() : 0;
   }

   public String toString() {
      return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + (String)this.nameGetter.get() + '\'' + ", id=" + this.id + ", serialize=" + this.serialize + '}';
   }

   public static enum Operation {
      ADDITION(0),
      MULTIPLY_BASE(1),
      MULTIPLY_TOTAL(2);

      private static final AttributeModifier.Operation[] OPERATIONS = new AttributeModifier.Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
      private final int value;

      private Operation(int var3) {
         this.value = var3;
      }

      public int toValue() {
         return this.value;
      }

      public static AttributeModifier.Operation fromValue(int var0) {
         if (var0 >= 0 && var0 < OPERATIONS.length) {
            return OPERATIONS[var0];
         } else {
            throw new IllegalArgumentException("No operation with value " + var0);
         }
      }
   }
}
