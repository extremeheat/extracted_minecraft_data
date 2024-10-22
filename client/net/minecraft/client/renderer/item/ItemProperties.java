package net.minecraft.client.renderer.item;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.LightBlock;

public class ItemProperties {
   private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = Maps.newHashMap();
   private static final ResourceLocation DAMAGED = ResourceLocation.withDefaultNamespace("damaged");
   private static final ResourceLocation DAMAGE = ResourceLocation.withDefaultNamespace("damage");
   private static final ClampedItemPropertyFunction PROPERTY_DAMAGED = (var0x, var1, var2x, var3) -> var0x.isDamaged() ? 1.0F : 0.0F;
   private static final ClampedItemPropertyFunction PROPERTY_DAMAGE = (var0x, var1, var2x, var3) -> Mth.clamp(
         (float)var0x.getDamageValue() / (float)var0x.getMaxDamage(), 0.0F, 1.0F
      );
   private static final Map<Item, Map<ResourceLocation, ItemPropertyFunction>> PROPERTIES = Maps.newHashMap();

   public ItemProperties() {
      super();
   }

   private static ClampedItemPropertyFunction registerGeneric(ResourceLocation var0, ClampedItemPropertyFunction var1) {
      GENERIC_PROPERTIES.put(var0, var1);
      return var1;
   }

   private static void registerCustomModelData(ItemPropertyFunction var0) {
      GENERIC_PROPERTIES.put(ResourceLocation.withDefaultNamespace("custom_model_data"), var0);
   }

   private static void register(Item var0, ResourceLocation var1, ClampedItemPropertyFunction var2) {
      PROPERTIES.computeIfAbsent(var0, var0x -> Maps.newHashMap()).put(var1, var2);
   }

   private static int honeyLevelProperty(ItemStack var0) {
      BlockItemStateProperties var1 = var0.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
      Integer var2 = var1.get(BeehiveBlock.HONEY_LEVEL);
      return var2 != null && var2 == 5 ? 1 : 0;
   }

   @Nullable
   public static ItemPropertyFunction getProperty(ItemStack var0, ResourceLocation var1) {
      if (var0.getMaxDamage() > 0) {
         if (DAMAGE.equals(var1)) {
            return PROPERTY_DAMAGE;
         }

         if (DAMAGED.equals(var1)) {
            return PROPERTY_DAMAGED;
         }
      }

      ItemPropertyFunction var2 = GENERIC_PROPERTIES.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         Map var3 = PROPERTIES.get(var0.getItem());
         return var3 == null ? null : (ItemPropertyFunction)var3.get(var1);
      }
   }

   static {
      registerGeneric(
         ResourceLocation.withDefaultNamespace("lefthanded"),
         (var0x, var1, var2x, var3) -> var2x != null && var2x.getMainArm() != HumanoidArm.RIGHT ? 1.0F : 0.0F
      );
      registerGeneric(
         ResourceLocation.withDefaultNamespace("cooldown"),
         (var0x, var1, var2x, var3) -> var2x instanceof Player ? ((Player)var2x).getCooldowns().getCooldownPercent(var0x, 0.0F) : 0.0F
      );
      ClampedItemPropertyFunction var0 = (var0x, var1, var2x, var3) -> {
         ArmorTrim var4 = var0x.get(DataComponents.TRIM);
         return var4 != null ? var4.material().value().itemModelIndex() : -1.0F / 0.0F;
      };
      registerGeneric(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, var0);
      registerGeneric(ResourceLocation.withDefaultNamespace("broken"), (var0x, var1, var2x, var3) -> var0x.nextDamageWillBreak() ? 1.0F : 0.0F);
      registerCustomModelData((var0x, var1, var2x, var3) -> (float)var0x.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.DEFAULT).value());
      register(Items.BOW, ResourceLocation.withDefaultNamespace("pull"), (var0x, var1, var2x, var3) -> {
         if (var2x == null) {
            return 0.0F;
         } else {
            return var2x.getUseItem() != var0x ? 0.0F : (float)(var0x.getUseDuration(var2x) - var2x.getUseItemRemainingTicks()) / 20.0F;
         }
      });
      register(
         Items.BRUSH,
         ResourceLocation.withDefaultNamespace("brushing"),
         (var0x, var1, var2x, var3) -> var2x != null && var2x.getUseItem() == var0x ? (float)(var2x.getUseItemRemainingTicks() % 10) / 10.0F : 0.0F
      );
      register(
         Items.BOW,
         ResourceLocation.withDefaultNamespace("pulling"),
         (var0x, var1, var2x, var3) -> var2x != null && var2x.isUsingItem() && var2x.getUseItem() == var0x ? 1.0F : 0.0F
      );

      for (BundleItem var2 : BundleItem.getAllBundleItemColors()) {
         register(var2.asItem(), ResourceLocation.withDefaultNamespace("filled"), (var0x, var1, var2x, var3) -> BundleItem.getFullnessDisplay(var0x));
      }

      register(Items.CLOCK, ResourceLocation.withDefaultNamespace("time"), new ClampedItemPropertyFunction() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         @Override
         public float unclampedCall(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
            Object var5 = var3 != null ? var3 : var1.getEntityRepresentation();
            if (var5 == null) {
               return 0.0F;
            } else {
               if (var2 == null && ((Entity)var5).level() instanceof ClientLevel) {
                  var2 = (ClientLevel)((Entity)var5).level();
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
      register(Items.COMPASS, ResourceLocation.withDefaultNamespace("angle"), new CompassItemPropertyFunction((var0x, var1, var2x) -> {
         LodestoneTracker var3 = var1.get(DataComponents.LODESTONE_TRACKER);
         return var3 != null ? var3.target().orElse(null) : CompassItem.getSpawnPosition(var0x);
      }));
      register(
         Items.RECOVERY_COMPASS,
         ResourceLocation.withDefaultNamespace("angle"),
         new CompassItemPropertyFunction((var0x, var1, var2x) -> var2x instanceof Player var3 ? var3.getLastDeathLocation().orElse(null) : null)
      );
      register(
         Items.CROSSBOW,
         ResourceLocation.withDefaultNamespace("pull"),
         (var0x, var1, var2x, var3) -> {
            if (var2x == null) {
               return 0.0F;
            } else {
               return CrossbowItem.isCharged(var0x)
                  ? 0.0F
                  : (float)(var0x.getUseDuration(var2x) - var2x.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(var0x, var2x);
            }
         }
      );
      register(
         Items.CROSSBOW,
         ResourceLocation.withDefaultNamespace("pulling"),
         (var0x, var1, var2x, var3) -> var2x != null && var2x.isUsingItem() && var2x.getUseItem() == var0x && !CrossbowItem.isCharged(var0x) ? 1.0F : 0.0F
      );
      register(Items.CROSSBOW, ResourceLocation.withDefaultNamespace("charged"), (var0x, var1, var2x, var3) -> CrossbowItem.isCharged(var0x) ? 1.0F : 0.0F);
      register(Items.CROSSBOW, ResourceLocation.withDefaultNamespace("firework"), (var0x, var1, var2x, var3) -> {
         ChargedProjectiles var4 = var0x.get(DataComponents.CHARGED_PROJECTILES);
         return var4 != null && var4.contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
      });
      register(Items.FISHING_ROD, ResourceLocation.withDefaultNamespace("cast"), (var0x, var1, var2x, var3) -> {
         if (var2x == null) {
            return 0.0F;
         } else {
            boolean var4 = var2x.getMainHandItem() == var0x;
            boolean var5 = var2x.getOffhandItem() == var0x;
            if (var2x.getMainHandItem().getItem() instanceof FishingRodItem) {
               var5 = false;
            }

            return (var4 || var5) && var2x instanceof Player && ((Player)var2x).fishing != null ? 1.0F : 0.0F;
         }
      });
      register(
         Items.SHIELD,
         ResourceLocation.withDefaultNamespace("blocking"),
         (var0x, var1, var2x, var3) -> var2x != null && var2x.isUsingItem() && var2x.getUseItem() == var0x ? 1.0F : 0.0F
      );
      register(
         Items.TRIDENT,
         ResourceLocation.withDefaultNamespace("throwing"),
         (var0x, var1, var2x, var3) -> var2x != null && var2x.isUsingItem() && var2x.getUseItem() == var0x ? 1.0F : 0.0F
      );
      register(Items.LIGHT, ResourceLocation.withDefaultNamespace("level"), (var0x, var1, var2x, var3) -> {
         BlockItemStateProperties var4 = var0x.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
         Integer var5 = var4.get(LightBlock.LEVEL);
         return var5 != null ? (float)var5.intValue() / 16.0F : 1.0F;
      });
      register(
         Items.GOAT_HORN,
         ResourceLocation.withDefaultNamespace("tooting"),
         (var0x, var1, var2x, var3) -> var2x != null && var2x.isUsingItem() && var2x.getUseItem() == var0x ? 1.0F : 0.0F
      );
      register(Items.BEE_NEST, ResourceLocation.withDefaultNamespace("honey_level"), (var0x, var1, var2x, var3) -> (float)honeyLevelProperty(var0x));
      register(Items.BEEHIVE, ResourceLocation.withDefaultNamespace("honey_level"), (var0x, var1, var2x, var3) -> (float)honeyLevelProperty(var0x));
   }
}
