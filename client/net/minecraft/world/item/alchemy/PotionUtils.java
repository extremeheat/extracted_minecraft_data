package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public class PotionUtils {
   public static final String TAG_CUSTOM_POTION_EFFECTS = "CustomPotionEffects";
   public static final String TAG_CUSTOM_POTION_COLOR = "CustomPotionColor";
   public static final String TAG_POTION = "Potion";
   private static final int EMPTY_COLOR = 16253176;
   private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);

   public PotionUtils() {
      super();
   }

   public static List<MobEffectInstance> getMobEffects(ItemStack var0) {
      return getAllEffects(var0.getTag());
   }

   public static List<MobEffectInstance> getAllEffects(Potion var0, Collection<MobEffectInstance> var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.addAll(var0.getEffects());
      var2.addAll(var1);
      return var2;
   }

   public static List<MobEffectInstance> getAllEffects(@Nullable CompoundTag var0) {
      ArrayList var1 = Lists.newArrayList();
      var1.addAll(getPotion(var0).getEffects());
      getCustomEffects(var0, var1);
      return var1;
   }

   public static List<MobEffectInstance> getCustomEffects(ItemStack var0) {
      return getCustomEffects(var0.getTag());
   }

   public static List<MobEffectInstance> getCustomEffects(@Nullable CompoundTag var0) {
      ArrayList var1 = Lists.newArrayList();
      getCustomEffects(var0, var1);
      return var1;
   }

   public static void getCustomEffects(@Nullable CompoundTag var0, List<MobEffectInstance> var1) {
      if (var0 != null && var0.contains("CustomPotionEffects", 9)) {
         ListTag var2 = var0.getList("CustomPotionEffects", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            CompoundTag var4 = var2.getCompound(var3);
            MobEffectInstance var5 = MobEffectInstance.load(var4);
            if (var5 != null) {
               var1.add(var5);
            }
         }
      }
   }

   public static int getColor(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      if (var1 != null && var1.contains("CustomPotionColor", 99)) {
         return var1.getInt("CustomPotionColor");
      } else {
         return getPotion(var0) == Potions.EMPTY ? 16253176 : getColor(getMobEffects(var0));
      }
   }

   public static int getColor(Potion var0) {
      return var0 == Potions.EMPTY ? 16253176 : getColor(var0.getEffects());
   }

   public static int getColor(Collection<MobEffectInstance> var0) {
      int var1 = 3694022;
      if (var0.isEmpty()) {
         return 3694022;
      } else {
         float var2 = 0.0F;
         float var3 = 0.0F;
         float var4 = 0.0F;
         int var5 = 0;

         for(MobEffectInstance var7 : var0) {
            if (var7.isVisible()) {
               int var8 = var7.getEffect().getColor();
               int var9 = var7.getAmplifier() + 1;
               var2 += (float)(var9 * (var8 >> 16 & 0xFF)) / 255.0F;
               var3 += (float)(var9 * (var8 >> 8 & 0xFF)) / 255.0F;
               var4 += (float)(var9 * (var8 >> 0 & 0xFF)) / 255.0F;
               var5 += var9;
            }
         }

         if (var5 == 0) {
            return 0;
         } else {
            var2 = var2 / (float)var5 * 255.0F;
            var3 = var3 / (float)var5 * 255.0F;
            var4 = var4 / (float)var5 * 255.0F;
            return (int)var2 << 16 | (int)var3 << 8 | (int)var4;
         }
      }
   }

   public static Potion getPotion(ItemStack var0) {
      return getPotion(var0.getTag());
   }

   public static Potion getPotion(@Nullable CompoundTag var0) {
      return var0 == null ? Potions.EMPTY : Potion.byName(var0.getString("Potion"));
   }

   public static ItemStack setPotion(ItemStack var0, Potion var1) {
      ResourceLocation var2 = BuiltInRegistries.POTION.getKey(var1);
      if (var1 == Potions.EMPTY) {
         var0.removeTagKey("Potion");
      } else {
         var0.getOrCreateTag().putString("Potion", var2.toString());
      }

      return var0;
   }

   public static ItemStack setCustomEffects(ItemStack var0, Collection<MobEffectInstance> var1) {
      if (var1.isEmpty()) {
         return var0;
      } else {
         CompoundTag var2 = var0.getOrCreateTag();
         ListTag var3 = var2.getList("CustomPotionEffects", 9);

         for(MobEffectInstance var5 : var1) {
            var3.add(var5.save(new CompoundTag()));
         }

         var2.put("CustomPotionEffects", var3);
         return var0;
      }
   }

   public static void addPotionTooltip(ItemStack var0, List<Component> var1, float var2) {
      addPotionTooltip(getMobEffects(var0), var1, var2);
   }

   public static void addPotionTooltip(List<MobEffectInstance> var0, List<Component> var1, float var2) {
      ArrayList var3 = Lists.newArrayList();
      if (var0.isEmpty()) {
         var1.add(NO_EFFECT);
      } else {
         for(MobEffectInstance var5 : var0) {
            MutableComponent var6 = Component.translatable(var5.getDescriptionId());
            MobEffect var7 = var5.getEffect();
            Map var8 = var7.getAttributeModifiers();
            if (!var8.isEmpty()) {
               for(Entry var10 : var8.entrySet()) {
                  AttributeModifier var11 = (AttributeModifier)var10.getValue();
                  AttributeModifier var12 = new AttributeModifier(
                     var11.getName(), var7.getAttributeModifierValue(var5.getAmplifier(), var11), var11.getOperation()
                  );
                  var3.add(new Pair((Attribute)var10.getKey(), var12));
               }
            }

            if (var5.getAmplifier() > 0) {
               var6 = Component.translatable("potion.withAmplifier", var6, Component.translatable("potion.potency." + var5.getAmplifier()));
            }

            if (var5.getDuration() > 20) {
               var6 = Component.translatable("potion.withDuration", var6, MobEffectUtil.formatDuration(var5, var2));
            }

            var1.add(var6.withStyle(var7.getCategory().getTooltipFormatting()));
         }
      }

      if (!var3.isEmpty()) {
         var1.add(CommonComponents.EMPTY);
         var1.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

         for(Pair var14 : var3) {
            AttributeModifier var15 = (AttributeModifier)var14.getSecond();
            double var16 = var15.getAmount();
            double var17;
            if (var15.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && var15.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
               var17 = var15.getAmount();
            } else {
               var17 = var15.getAmount() * 100.0;
            }

            if (var16 > 0.0) {
               var1.add(
                  Component.translatable(
                        "attribute.modifier.plus." + var15.getOperation().toValue(),
                        ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(var17),
                        Component.translatable(((Attribute)var14.getFirst()).getDescriptionId())
                     )
                     .withStyle(ChatFormatting.BLUE)
               );
            } else if (var16 < 0.0) {
               var17 *= -1.0;
               var1.add(
                  Component.translatable(
                        "attribute.modifier.take." + var15.getOperation().toValue(),
                        ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(var17),
                        Component.translatable(((Attribute)var14.getFirst()).getDescriptionId())
                     )
                     .withStyle(ChatFormatting.RED)
               );
            }
         }
      }
   }
}
