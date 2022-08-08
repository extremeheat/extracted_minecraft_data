package net.minecraft.client.renderer.item;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightBlock;

public class ItemProperties {
   private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = Maps.newHashMap();
   private static final String TAG_CUSTOM_MODEL_DATA = "CustomModelData";
   private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
   private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
   private static final ClampedItemPropertyFunction PROPERTY_DAMAGED = (var0, var1, var2, var3) -> {
      return var0.isDamaged() ? 1.0F : 0.0F;
   };
   private static final ClampedItemPropertyFunction PROPERTY_DAMAGE = (var0, var1, var2, var3) -> {
      return Mth.clamp((float)var0.getDamageValue() / (float)var0.getMaxDamage(), 0.0F, 1.0F);
   };
   private static final Map<Item, Map<ResourceLocation, ItemPropertyFunction>> PROPERTIES = Maps.newHashMap();

   public ItemProperties() {
      super();
   }

   private static ClampedItemPropertyFunction registerGeneric(ResourceLocation var0, ClampedItemPropertyFunction var1) {
      GENERIC_PROPERTIES.put(var0, var1);
      return var1;
   }

   private static void registerCustomModelData(ItemPropertyFunction var0) {
      GENERIC_PROPERTIES.put(new ResourceLocation("custom_model_data"), var0);
   }

   private static void register(Item var0, ResourceLocation var1, ClampedItemPropertyFunction var2) {
      ((Map)PROPERTIES.computeIfAbsent(var0, (var0x) -> {
         return Maps.newHashMap();
      })).put(var1, var2);
   }

   @Nullable
   public static ItemPropertyFunction getProperty(Item var0, ResourceLocation var1) {
      if (var0.getMaxDamage() > 0) {
         if (DAMAGE.equals(var1)) {
            return PROPERTY_DAMAGE;
         }

         if (DAMAGED.equals(var1)) {
            return PROPERTY_DAMAGED;
         }
      }

      ItemPropertyFunction var2 = (ItemPropertyFunction)GENERIC_PROPERTIES.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         Map var3 = (Map)PROPERTIES.get(var0);
         return var3 == null ? null : (ItemPropertyFunction)var3.get(var1);
      }
   }

   static {
      registerGeneric(new ResourceLocation("lefthanded"), (var0, var1, var2, var3) -> {
         return var2 != null && var2.getMainArm() != HumanoidArm.RIGHT ? 1.0F : 0.0F;
      });
      registerGeneric(new ResourceLocation("cooldown"), (var0, var1, var2, var3) -> {
         return var2 instanceof Player ? ((Player)var2).getCooldowns().getCooldownPercent(var0.getItem(), 0.0F) : 0.0F;
      });
      registerCustomModelData((var0, var1, var2, var3) -> {
         return var0.hasTag() ? (float)var0.getTag().getInt("CustomModelData") : 0.0F;
      });
      register(Items.BOW, new ResourceLocation("pull"), (var0, var1, var2, var3) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            return var2.getUseItem() != var0 ? 0.0F : (float)(var0.getUseDuration() - var2.getUseItemRemainingTicks()) / 20.0F;
         }
      });
      register(Items.BOW, new ResourceLocation("pulling"), (var0, var1, var2, var3) -> {
         return var2 != null && var2.isUsingItem() && var2.getUseItem() == var0 ? 1.0F : 0.0F;
      });
      register(Items.BUNDLE, new ResourceLocation("filled"), (var0, var1, var2, var3) -> {
         return BundleItem.getFullnessDisplay(var0);
      });
      register(Items.CLOCK, new ResourceLocation("time"), new ClampedItemPropertyFunction() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         public float unclampedCall(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
            Object var5 = var3 != null ? var3 : var1.getEntityRepresentation();
            if (var5 == null) {
               return 0.0F;
            } else {
               if (var2 == null && ((Entity)var5).level instanceof ClientLevel) {
                  var2 = (ClientLevel)((Entity)var5).level;
               }

               if (var2 == null) {
                  return 0.0F;
               } else {
                  double var6;
                  if (var2.dimensionType().natural()) {
                     var6 = (double)var2.getTimeOfDay(1.0F);
                  } else {
                     var6 = Math.random();
                  }

                  var6 = this.wobble(var2, var6);
                  return (float)var6;
               }
            }
         }

         private double wobble(Level var1, double var2) {
            if (var1.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = var1.getGameTime();
               double var4 = var2 - this.rotation;
               var4 = Mth.positiveModulo(var4 + 0.5, 1.0) - 0.5;
               this.rota += var4 * 0.1;
               this.rota *= 0.9;
               this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0);
            }

            return this.rotation;
         }
      });
      register(Items.COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction((var0, var1, var2) -> {
         return CompassItem.isLodestoneCompass(var1) ? CompassItem.getLodestonePosition(var1.getOrCreateTag()) : CompassItem.getSpawnPosition(var0);
      }));
      register(Items.RECOVERY_COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction((var0, var1, var2) -> {
         if (var2 instanceof Player var3) {
            return (GlobalPos)var3.getLastDeathLocation().orElse((Object)null);
         } else {
            return null;
         }
      }));
      register(Items.CROSSBOW, new ResourceLocation("pull"), (var0, var1, var2, var3) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            return CrossbowItem.isCharged(var0) ? 0.0F : (float)(var0.getUseDuration() - var2.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(var0);
         }
      });
      register(Items.CROSSBOW, new ResourceLocation("pulling"), (var0, var1, var2, var3) -> {
         return var2 != null && var2.isUsingItem() && var2.getUseItem() == var0 && !CrossbowItem.isCharged(var0) ? 1.0F : 0.0F;
      });
      register(Items.CROSSBOW, new ResourceLocation("charged"), (var0, var1, var2, var3) -> {
         return var2 != null && CrossbowItem.isCharged(var0) ? 1.0F : 0.0F;
      });
      register(Items.CROSSBOW, new ResourceLocation("firework"), (var0, var1, var2, var3) -> {
         return var2 != null && CrossbowItem.isCharged(var0) && CrossbowItem.containsChargedProjectile(var0, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
      });
      register(Items.ELYTRA, new ResourceLocation("broken"), (var0, var1, var2, var3) -> {
         return ElytraItem.isFlyEnabled(var0) ? 0.0F : 1.0F;
      });
      register(Items.FISHING_ROD, new ResourceLocation("cast"), (var0, var1, var2, var3) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            boolean var4 = var2.getMainHandItem() == var0;
            boolean var5 = var2.getOffhandItem() == var0;
            if (var2.getMainHandItem().getItem() instanceof FishingRodItem) {
               var5 = false;
            }

            return (var4 || var5) && var2 instanceof Player && ((Player)var2).fishing != null ? 1.0F : 0.0F;
         }
      });
      register(Items.SHIELD, new ResourceLocation("blocking"), (var0, var1, var2, var3) -> {
         return var2 != null && var2.isUsingItem() && var2.getUseItem() == var0 ? 1.0F : 0.0F;
      });
      register(Items.TRIDENT, new ResourceLocation("throwing"), (var0, var1, var2, var3) -> {
         return var2 != null && var2.isUsingItem() && var2.getUseItem() == var0 ? 1.0F : 0.0F;
      });
      register(Items.LIGHT, new ResourceLocation("level"), (var0, var1, var2, var3) -> {
         CompoundTag var4 = var0.getTagElement("BlockStateTag");

         try {
            if (var4 != null) {
               Tag var5 = var4.get(LightBlock.LEVEL.getName());
               if (var5 != null) {
                  return (float)Integer.parseInt(var5.getAsString()) / 16.0F;
               }
            }
         } catch (NumberFormatException var6) {
         }

         return 1.0F;
      });
      register(Items.GOAT_HORN, new ResourceLocation("tooting"), (var0, var1, var2, var3) -> {
         return var2 != null && var2.isUsingItem() && var2.getUseItem() == var0 ? 1.0F : 0.0F;
      });
   }
}
