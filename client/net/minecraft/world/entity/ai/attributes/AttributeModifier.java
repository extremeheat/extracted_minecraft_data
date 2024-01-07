package net.minecraft.world.entity.ai.attributes;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public class AttributeModifier {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<AttributeModifier> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               UUIDUtil.CODEC.fieldOf("UUID").forGetter(AttributeModifier::getId),
               Codec.STRING.fieldOf("Name").forGetter(var0x -> var0x.name),
               Codec.DOUBLE.fieldOf("Amount").forGetter(AttributeModifier::getAmount),
               AttributeModifier.Operation.CODEC.fieldOf("Operation").forGetter(AttributeModifier::getOperation)
            )
            .apply(var0, AttributeModifier::new)
   );
   private final double amount;
   private final AttributeModifier.Operation operation;
   private final String name;
   private final UUID id;

   public AttributeModifier(String var1, double var2, AttributeModifier.Operation var4) {
      this(Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()), var1, var2, var4);
   }

   public AttributeModifier(UUID var1, String var2, double var3, AttributeModifier.Operation var5) {
      super();
      this.id = var1;
      this.name = var2;
      this.amount = var3;
      this.operation = var5;
   }

   public UUID getId() {
      return this.id;
   }

   public AttributeModifier.Operation getOperation() {
      return this.operation;
   }

   public double getAmount() {
      return this.amount;
   }

   @Override
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

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public String toString() {
      return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + this.name + "', id=" + this.id + "}";
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", this.name);
      var1.putDouble("Amount", this.amount);
      var1.putInt("Operation", this.operation.toValue());
      var1.putUUID("UUID", this.id);
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

   public static enum Operation implements StringRepresentable {
      ADDITION("addition", 0),
      MULTIPLY_BASE("multiply_base", 1),
      MULTIPLY_TOTAL("multiply_total", 2);

      private static final AttributeModifier.Operation[] OPERATIONS = new AttributeModifier.Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
      public static final Codec<AttributeModifier.Operation> CODEC = StringRepresentable.fromEnum(AttributeModifier.Operation::values);
      private final String name;
      private final int value;

      private Operation(String var3, int var4) {
         this.name = var3;
         this.value = var4;
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

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
