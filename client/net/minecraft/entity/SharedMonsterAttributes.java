package net.minecraft.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   private static final Logger field_151476_f = LogManager.getLogger();
   public static final IAttribute field_111267_a = (new RangedAttribute((IAttribute)null, "generic.maxHealth", 20.0D, 0.0D, 1024.0D)).func_111117_a("Max Health").func_111112_a(true);
   public static final IAttribute field_111265_b = (new RangedAttribute((IAttribute)null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).func_111117_a("Follow Range");
   public static final IAttribute field_111266_c = (new RangedAttribute((IAttribute)null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).func_111117_a("Knockback Resistance");
   public static final IAttribute field_111263_d = (new RangedAttribute((IAttribute)null, "generic.movementSpeed", 0.699999988079071D, 0.0D, 1024.0D)).func_111117_a("Movement Speed").func_111112_a(true);
   public static final IAttribute field_193334_e = (new RangedAttribute((IAttribute)null, "generic.flyingSpeed", 0.4000000059604645D, 0.0D, 1024.0D)).func_111117_a("Flying Speed").func_111112_a(true);
   public static final IAttribute field_111264_e = new RangedAttribute((IAttribute)null, "generic.attackDamage", 2.0D, 0.0D, 2048.0D);
   public static final IAttribute field_188790_f = (new RangedAttribute((IAttribute)null, "generic.attackSpeed", 4.0D, 0.0D, 1024.0D)).func_111112_a(true);
   public static final IAttribute field_188791_g = (new RangedAttribute((IAttribute)null, "generic.armor", 0.0D, 0.0D, 30.0D)).func_111112_a(true);
   public static final IAttribute field_189429_h = (new RangedAttribute((IAttribute)null, "generic.armorToughness", 0.0D, 0.0D, 20.0D)).func_111112_a(true);
   public static final IAttribute field_188792_h = (new RangedAttribute((IAttribute)null, "generic.luck", 0.0D, -1024.0D, 1024.0D)).func_111112_a(true);

   public static NBTTagList func_111257_a(AbstractAttributeMap var0) {
      NBTTagList var1 = new NBTTagList();
      Iterator var2 = var0.func_111146_a().iterator();

      while(var2.hasNext()) {
         IAttributeInstance var3 = (IAttributeInstance)var2.next();
         var1.add((INBTBase)func_111261_a(var3));
      }

      return var1;
   }

   private static NBTTagCompound func_111261_a(IAttributeInstance var0) {
      NBTTagCompound var1 = new NBTTagCompound();
      IAttribute var2 = var0.func_111123_a();
      var1.func_74778_a("Name", var2.func_111108_a());
      var1.func_74780_a("Base", var0.func_111125_b());
      Collection var3 = var0.func_111122_c();
      if (var3 != null && !var3.isEmpty()) {
         NBTTagList var4 = new NBTTagList();
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            AttributeModifier var6 = (AttributeModifier)var5.next();
            if (var6.func_111165_e()) {
               var4.add((INBTBase)func_111262_a(var6));
            }
         }

         var1.func_74782_a("Modifiers", var4);
      }

      return var1;
   }

   public static NBTTagCompound func_111262_a(AttributeModifier var0) {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74778_a("Name", var0.func_111166_b());
      var1.func_74780_a("Amount", var0.func_111164_d());
      var1.func_74768_a("Operation", var0.func_111169_c());
      var1.func_186854_a("UUID", var0.func_111167_a());
      return var1;
   }

   public static void func_151475_a(AbstractAttributeMap var0, NBTTagList var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         IAttributeInstance var4 = var0.func_111152_a(var3.func_74779_i("Name"));
         if (var4 == null) {
            field_151476_f.warn("Ignoring unknown attribute '{}'", var3.func_74779_i("Name"));
         } else {
            func_111258_a(var4, var3);
         }
      }

   }

   private static void func_111258_a(IAttributeInstance var0, NBTTagCompound var1) {
      var0.func_111128_a(var1.func_74769_h("Base"));
      if (var1.func_150297_b("Modifiers", 9)) {
         NBTTagList var2 = var1.func_150295_c("Modifiers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            AttributeModifier var4 = func_111259_a(var2.func_150305_b(var3));
            if (var4 != null) {
               AttributeModifier var5 = var0.func_111127_a(var4.func_111167_a());
               if (var5 != null) {
                  var0.func_111124_b(var5);
               }

               var0.func_111121_a(var4);
            }
         }
      }

   }

   @Nullable
   public static AttributeModifier func_111259_a(NBTTagCompound var0) {
      UUID var1 = var0.func_186857_a("UUID");

      try {
         return new AttributeModifier(var1, var0.func_74779_i("Name"), var0.func_74769_h("Amount"), var0.func_74762_e("Operation"));
      } catch (Exception var3) {
         field_151476_f.warn("Unable to create attribute: {}", var3.getMessage());
         return null;
      }
   }
}
