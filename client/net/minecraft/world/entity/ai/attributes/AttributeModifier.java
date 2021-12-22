package net.minecraft.world.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeModifier {
   private static final Logger LOGGER = LogManager.getLogger();
   private final double amount;
   private final AttributeModifier.Operation operation;
   private final Supplier<String> nameGetter;
   // $FF: renamed from: id java.util.UUID
   private final UUID field_251;

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

   public AttributeModifier(UUID var1, Supplier<String> var2, double var3, AttributeModifier.Operation var5) {
      super();
      this.field_251 = var1;
      this.nameGetter = var2;
      this.amount = var3;
      this.operation = var5;
   }

   public UUID getId() {
      return this.field_251;
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

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         AttributeModifier var2 = (AttributeModifier)var1;
         return Objects.equals(this.field_251, var2.field_251);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_251.hashCode();
   }

   public String toString() {
      double var10000 = this.amount;
      return "AttributeModifier{amount=" + var10000 + ", operation=" + this.operation + ", name='" + (String)this.nameGetter.get() + "', id=" + this.field_251 + "}";
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", this.getName());
      var1.putDouble("Amount", this.amount);
      var1.putInt("Operation", this.operation.toValue());
      var1.putUUID("UUID", this.field_251);
      return var1;
   }

   @Nullable
   public static AttributeModifier load(CompoundTag var0) {
      try {
         UUID var1 = var0.getUUID("UUID");
         AttributeModifier.Operation var2 = AttributeModifier.Operation.fromValue(var0.getInt("Operation"));
         return new AttributeModifier(var1, var0.getString("Name"), var0.getDouble("Amount"), var2);
      } catch (Exception var3) {
         LOGGER.warn("Unable to create attribute: {}", var3.getMessage());
         return null;
      }
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

      // $FF: synthetic method
      private static AttributeModifier.Operation[] $values() {
         return new AttributeModifier.Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
      }
   }
}
