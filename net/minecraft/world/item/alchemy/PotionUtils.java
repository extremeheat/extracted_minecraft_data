package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public class PotionUtils {
   public static List getMobEffects(ItemStack var0) {
      return getAllEffects(var0.getTag());
   }

   public static List getAllEffects(Potion var0, Collection var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.addAll(var0.getEffects());
      var2.addAll(var1);
      return var2;
   }

   public static List getAllEffects(@Nullable CompoundTag var0) {
      ArrayList var1 = Lists.newArrayList();
      var1.addAll(getPotion(var0).getEffects());
      getCustomEffects(var0, var1);
      return var1;
   }

   public static List getCustomEffects(ItemStack var0) {
      return getCustomEffects(var0.getTag());
   }

   public static List getCustomEffects(@Nullable CompoundTag var0) {
      ArrayList var1 = Lists.newArrayList();
      getCustomEffects(var0, var1);
      return var1;
   }

   public static void getCustomEffects(@Nullable CompoundTag var0, List var1) {
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
         return getPotion(var0) == Potions.EMPTY ? 16253176 : getColor((Collection)getMobEffects(var0));
      }
   }

   public static int getColor(Potion var0) {
      return var0 == Potions.EMPTY ? 16253176 : getColor((Collection)var0.getEffects());
   }

   public static int getColor(Collection var0) {
      int var1 = 3694022;
      if (var0.isEmpty()) {
         return 3694022;
      } else {
         float var2 = 0.0F;
         float var3 = 0.0F;
         float var4 = 0.0F;
         int var5 = 0;
         Iterator var6 = var0.iterator();

         while(var6.hasNext()) {
            MobEffectInstance var7 = (MobEffectInstance)var6.next();
            if (var7.isVisible()) {
               int var8 = var7.getEffect().getColor();
               int var9 = var7.getAmplifier() + 1;
               var2 += (float)(var9 * (var8 >> 16 & 255)) / 255.0F;
               var3 += (float)(var9 * (var8 >> 8 & 255)) / 255.0F;
               var4 += (float)(var9 * (var8 >> 0 & 255)) / 255.0F;
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
      ResourceLocation var2 = Registry.POTION.getKey(var1);
      if (var1 == Potions.EMPTY) {
         var0.removeTagKey("Potion");
      } else {
         var0.getOrCreateTag().putString("Potion", var2.toString());
      }

      return var0;
   }

   public static ItemStack setCustomEffects(ItemStack var0, Collection var1) {
      if (var1.isEmpty()) {
         return var0;
      } else {
         CompoundTag var2 = var0.getOrCreateTag();
         ListTag var3 = var2.getList("CustomPotionEffects", 9);
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            MobEffectInstance var5 = (MobEffectInstance)var4.next();
            var3.add(var5.save(new CompoundTag()));
         }

         var2.put("CustomPotionEffects", var3);
         return var0;
      }
   }

   public static void addPotionTooltip(ItemStack var0, List var1, float var2) {
      List var3 = getMobEffects(var0);
      ArrayList var4 = Lists.newArrayList();
      Iterator var5;
      TranslatableComponent var7;
      MobEffect var8;
      if (var3.isEmpty()) {
         var1.add((new TranslatableComponent("effect.none", new Object[0])).withStyle(ChatFormatting.GRAY));
      } else {
         for(var5 = var3.iterator(); var5.hasNext(); var1.add(var7.withStyle(var8.getCategory().getTooltipFormatting()))) {
            MobEffectInstance var6 = (MobEffectInstance)var5.next();
            var7 = new TranslatableComponent(var6.getDescriptionId(), new Object[0]);
            var8 = var6.getEffect();
            Map var9 = var8.getAttributeModifiers();
            if (!var9.isEmpty()) {
               Iterator var10 = var9.entrySet().iterator();

               while(var10.hasNext()) {
                  Entry var11 = (Entry)var10.next();
                  AttributeModifier var12 = (AttributeModifier)var11.getValue();
                  AttributeModifier var13 = new AttributeModifier(var12.getName(), var8.getAttributeModifierValue(var6.getAmplifier(), var12), var12.getOperation());
                  var4.add(new Tuple(((Attribute)var11.getKey()).getName(), var13));
               }
            }

            if (var6.getAmplifier() > 0) {
               var7.append(" ").append((Component)(new TranslatableComponent("potion.potency." + var6.getAmplifier(), new Object[0])));
            }

            if (var6.getDuration() > 20) {
               var7.append(" (").append(MobEffectUtil.formatDuration(var6, var2)).append(")");
            }
         }
      }

      if (!var4.isEmpty()) {
         var1.add(new TextComponent(""));
         var1.add((new TranslatableComponent("potion.whenDrank", new Object[0])).withStyle(ChatFormatting.DARK_PURPLE));
         var5 = var4.iterator();

         while(var5.hasNext()) {
            Tuple var14 = (Tuple)var5.next();
            AttributeModifier var15 = (AttributeModifier)var14.getB();
            double var16 = var15.getAmount();
            double var17;
            if (var15.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && var15.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
               var17 = var15.getAmount();
            } else {
               var17 = var15.getAmount() * 100.0D;
            }

            if (var16 > 0.0D) {
               var1.add((new TranslatableComponent("attribute.modifier.plus." + var15.getOperation().toValue(), new Object[]{ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(var17), new TranslatableComponent("attribute.name." + (String)var14.getA(), new Object[0])})).withStyle(ChatFormatting.BLUE));
            } else if (var16 < 0.0D) {
               var17 *= -1.0D;
               var1.add((new TranslatableComponent("attribute.modifier.take." + var15.getOperation().toValue(), new Object[]{ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(var17), new TranslatableComponent("attribute.name." + (String)var14.getA(), new Object[0])})).withStyle(ChatFormatting.RED));
            }
         }
      }

   }
}
