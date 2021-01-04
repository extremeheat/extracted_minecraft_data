package net.minecraft.world.entity.monster;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Attribute MAX_HEALTH = (new RangedAttribute((Attribute)null, "generic.maxHealth", 20.0D, 0.0D, 1024.0D)).importLegacyName("Max Health").setSyncable(true);
   public static final Attribute FOLLOW_RANGE = (new RangedAttribute((Attribute)null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).importLegacyName("Follow Range");
   public static final Attribute KNOCKBACK_RESISTANCE = (new RangedAttribute((Attribute)null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).importLegacyName("Knockback Resistance");
   public static final Attribute MOVEMENT_SPEED = (new RangedAttribute((Attribute)null, "generic.movementSpeed", 0.699999988079071D, 0.0D, 1024.0D)).importLegacyName("Movement Speed").setSyncable(true);
   public static final Attribute FLYING_SPEED = (new RangedAttribute((Attribute)null, "generic.flyingSpeed", 0.4000000059604645D, 0.0D, 1024.0D)).importLegacyName("Flying Speed").setSyncable(true);
   public static final Attribute ATTACK_DAMAGE = new RangedAttribute((Attribute)null, "generic.attackDamage", 2.0D, 0.0D, 2048.0D);
   public static final Attribute ATTACK_KNOCKBACK = new RangedAttribute((Attribute)null, "generic.attackKnockback", 0.0D, 0.0D, 5.0D);
   public static final Attribute ATTACK_SPEED = (new RangedAttribute((Attribute)null, "generic.attackSpeed", 4.0D, 0.0D, 1024.0D)).setSyncable(true);
   public static final Attribute ARMOR = (new RangedAttribute((Attribute)null, "generic.armor", 0.0D, 0.0D, 30.0D)).setSyncable(true);
   public static final Attribute ARMOR_TOUGHNESS = (new RangedAttribute((Attribute)null, "generic.armorToughness", 0.0D, 0.0D, 20.0D)).setSyncable(true);
   public static final Attribute LUCK = (new RangedAttribute((Attribute)null, "generic.luck", 0.0D, -1024.0D, 1024.0D)).setSyncable(true);

   public static ListTag saveAttributes(BaseAttributeMap var0) {
      ListTag var1 = new ListTag();
      Iterator var2 = var0.getAttributes().iterator();

      while(var2.hasNext()) {
         AttributeInstance var3 = (AttributeInstance)var2.next();
         var1.add(saveAttribute(var3));
      }

      return var1;
   }

   private static CompoundTag saveAttribute(AttributeInstance var0) {
      CompoundTag var1 = new CompoundTag();
      Attribute var2 = var0.getAttribute();
      var1.putString("Name", var2.getName());
      var1.putDouble("Base", var0.getBaseValue());
      Collection var3 = var0.getModifiers();
      if (var3 != null && !var3.isEmpty()) {
         ListTag var4 = new ListTag();
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            AttributeModifier var6 = (AttributeModifier)var5.next();
            if (var6.isSerializable()) {
               var4.add(saveAttributeModifier(var6));
            }
         }

         var1.put("Modifiers", var4);
      }

      return var1;
   }

   public static CompoundTag saveAttributeModifier(AttributeModifier var0) {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", var0.getName());
      var1.putDouble("Amount", var0.getAmount());
      var1.putInt("Operation", var0.getOperation().toValue());
      var1.putUUID("UUID", var0.getId());
      return var1;
   }

   public static void loadAttributes(BaseAttributeMap var0, ListTag var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         CompoundTag var3 = var1.getCompound(var2);
         AttributeInstance var4 = var0.getInstance(var3.getString("Name"));
         if (var4 == null) {
            LOGGER.warn("Ignoring unknown attribute '{}'", var3.getString("Name"));
         } else {
            loadAttribute(var4, var3);
         }
      }

   }

   private static void loadAttribute(AttributeInstance var0, CompoundTag var1) {
      var0.setBaseValue(var1.getDouble("Base"));
      if (var1.contains("Modifiers", 9)) {
         ListTag var2 = var1.getList("Modifiers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            AttributeModifier var4 = loadAttributeModifier(var2.getCompound(var3));
            if (var4 != null) {
               AttributeModifier var5 = var0.getModifier(var4.getId());
               if (var5 != null) {
                  var0.removeModifier(var5);
               }

               var0.addModifier(var4);
            }
         }
      }

   }

   @Nullable
   public static AttributeModifier loadAttributeModifier(CompoundTag var0) {
      UUID var1 = var0.getUUID("UUID");

      try {
         AttributeModifier.Operation var2 = AttributeModifier.Operation.fromValue(var0.getInt("Operation"));
         return new AttributeModifier(var1, var0.getString("Name"), var0.getDouble("Amount"), var2);
      } catch (Exception var3) {
         LOGGER.warn("Unable to create attribute: {}", var3.getMessage());
         return null;
      }
   }
}
