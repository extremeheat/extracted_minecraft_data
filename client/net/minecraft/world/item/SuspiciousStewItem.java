package net.minecraft.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SuspiciousStewItem extends Item {
   public static final String EFFECTS_TAG = "Effects";
   public static final String EFFECT_ID_TAG = "EffectId";
   public static final String EFFECT_DURATION_TAG = "EffectDuration";

   public SuspiciousStewItem(Item.Properties var1) {
      super(var1);
   }

   public static void saveMobEffect(ItemStack var0, MobEffect var1, int var2) {
      CompoundTag var3 = var0.getOrCreateTag();
      ListTag var4 = var3.getList("Effects", 9);
      CompoundTag var5 = new CompoundTag();
      var5.putByte("EffectId", (byte)MobEffect.getId(var1));
      var5.putInt("EffectDuration", var2);
      var4.add(var5);
      var3.put("Effects", var4);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      ItemStack var4 = super.finishUsingItem(var1, var2, var3);
      CompoundTag var5 = var1.getTag();
      if (var5 != null && var5.contains("Effects", 9)) {
         ListTag var6 = var5.getList("Effects", 10);

         for(int var7 = 0; var7 < var6.size(); ++var7) {
            int var8 = 160;
            CompoundTag var9 = var6.getCompound(var7);
            if (var9.contains("EffectDuration", 3)) {
               var8 = var9.getInt("EffectDuration");
            }

            MobEffect var10 = MobEffect.byId(var9.getByte("EffectId"));
            if (var10 != null) {
               var3.addEffect(new MobEffectInstance(var10, var8));
            }
         }
      }

      return var3 instanceof Player && ((Player)var3).getAbilities().instabuild ? var4 : new ItemStack(Items.BOWL);
   }
}
