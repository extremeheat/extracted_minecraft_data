package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class SuspiciousStewItem extends Item {
   public static final String EFFECTS_TAG = "Effects";
   public static final String EFFECT_ID_TAG = "EffectId";
   public static final String EFFECT_DURATION_TAG = "EffectDuration";
   public static final int DEFAULT_DURATION = 160;

   public SuspiciousStewItem(Item.Properties var1) {
      super(var1);
   }

   public static void saveMobEffect(ItemStack var0, MobEffect var1, int var2) {
      CompoundTag var3 = var0.getOrCreateTag();
      ListTag var4 = var3.getList("Effects", 9);
      CompoundTag var5 = new CompoundTag();
      var5.putInt("EffectId", MobEffect.getId(var1));
      var5.putInt("EffectDuration", var2);
      var4.add(var5);
      var3.put("Effects", var4);
   }

   private static void listPotionEffects(ItemStack var0, Consumer<MobEffectInstance> var1) {
      CompoundTag var2 = var0.getTag();
      if (var2 != null && var2.contains("Effects", 9)) {
         ListTag var3 = var2.getList("Effects", 10);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            CompoundTag var5 = var3.getCompound(var4);
            int var6;
            if (var5.contains("EffectDuration", 99)) {
               var6 = var5.getInt("EffectDuration");
            } else {
               var6 = 160;
            }

            MobEffect var7 = MobEffect.byId(var5.getInt("EffectId"));
            if (var7 != null) {
               var1.accept(new MobEffectInstance(var7, var6));
            }
         }
      }
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      if (var4.isCreative()) {
         ArrayList var5 = new ArrayList();
         listPotionEffects(var1, var5::add);
         PotionUtils.addPotionTooltip(var5, var3, 1.0F);
      }
   }

   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      ItemStack var4 = super.finishUsingItem(var1, var2, var3);
      listPotionEffects(var4, var3::addEffect);
      return var3 instanceof Player && ((Player)var3).getAbilities().instabuild ? var4 : new ItemStack(Items.BOWL);
   }
}
