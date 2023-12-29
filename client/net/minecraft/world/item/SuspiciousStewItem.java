package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

public class SuspiciousStewItem extends Item {
   public static final String EFFECTS_TAG = "effects";
   public static final int DEFAULT_DURATION = 160;

   public SuspiciousStewItem(Item.Properties var1) {
      super(var1);
   }

   public static void saveMobEffects(ItemStack var0, List<SuspiciousEffectHolder.EffectEntry> var1) {
      CompoundTag var2 = var0.getOrCreateTag();
      SuspiciousEffectHolder.EffectEntry.LIST_CODEC.encodeStart(NbtOps.INSTANCE, var1).result().ifPresent(var1x -> var2.put("effects", var1x));
   }

   public static void appendMobEffects(ItemStack var0, List<SuspiciousEffectHolder.EffectEntry> var1) {
      CompoundTag var2 = var0.getOrCreateTag();
      ArrayList var3 = new ArrayList();
      listPotionEffects(var0, var3::add);
      var3.addAll(var1);
      SuspiciousEffectHolder.EffectEntry.LIST_CODEC.encodeStart(NbtOps.INSTANCE, var3).result().ifPresent(var1x -> var2.put("effects", var1x));
   }

   private static void listPotionEffects(ItemStack var0, Consumer<SuspiciousEffectHolder.EffectEntry> var1) {
      CompoundTag var2 = var0.getTag();
      if (var2 != null && var2.contains("effects", 9)) {
         SuspiciousEffectHolder.EffectEntry.LIST_CODEC.parse(NbtOps.INSTANCE, var2.getList("effects", 10)).result().ifPresent(var1x -> var1x.forEach(var1));
      }
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      if (var4.isCreative()) {
         ArrayList var5 = new ArrayList();
         listPotionEffects(var1, var1x -> var5.add(var1x.createEffectInstance()));
         PotionUtils.addPotionTooltip(var5, var3, 1.0F, var2 == null ? 20.0F : var2.tickRateManager().tickrate());
      }
   }

   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      ItemStack var4 = super.finishUsingItem(var1, var2, var3);
      listPotionEffects(var4, var1x -> var3.addEffect(var1x.createEffectInstance()));
      return var3 instanceof Player && ((Player)var3).getAbilities().instabuild ? var4 : new ItemStack(Items.BOWL);
   }
}
