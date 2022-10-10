package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IAnimal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class EntityGolem extends EntityCreature implements IAnimal {
   protected EntityGolem(EntityType<?> var1, World var2) {
      super(var1, var2);
   }

   public void func_180430_e(float var1, float var2) {
   }

   @Nullable
   protected SoundEvent func_184639_G() {
      return null;
   }

   @Nullable
   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return null;
   }

   @Nullable
   protected SoundEvent func_184615_bR() {
      return null;
   }

   public int func_70627_aG() {
      return 120;
   }

   public boolean func_70692_ba() {
      return false;
   }
}
