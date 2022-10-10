package net.minecraft.entity.monster;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public abstract class AbstractIllager extends EntityMob {
   protected static final DataParameter<Byte> field_193080_a;

   protected AbstractIllager(EntityType<?> var1, World var2) {
      super(var1, var2);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_193080_a, (byte)0);
   }

   protected boolean func_193078_a(int var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_193080_a);
      return (var2 & var1) != 0;
   }

   protected void func_193079_a(int var1, boolean var2) {
      byte var3 = (Byte)this.field_70180_af.func_187225_a(field_193080_a);
      int var4;
      if (var2) {
         var4 = var3 | var1;
      } else {
         var4 = var3 & ~var1;
      }

      this.field_70180_af.func_187227_b(field_193080_a, (byte)(var4 & 255));
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.ILLAGER;
   }

   public AbstractIllager.IllagerArmPose func_193077_p() {
      return AbstractIllager.IllagerArmPose.CROSSED;
   }

   static {
      field_193080_a = EntityDataManager.func_187226_a(AbstractIllager.class, DataSerializers.field_187191_a);
   }

   public static enum IllagerArmPose {
      CROSSED,
      ATTACKING,
      SPELLCASTING,
      BOW_AND_ARROW;

      private IllagerArmPose() {
      }
   }
}
