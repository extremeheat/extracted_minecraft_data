package net.minecraft.client.renderer.item;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightBlock;

public class ItemProperties {
   private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = Maps.newHashMap();
   private static final String TAG_CUSTOM_MODEL_DATA = "CustomModelData";
   private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
   private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
   private static final ClampedItemPropertyFunction PROPERTY_DAMAGED = (var0x, var1, var2, var3) -> var0x.isDamaged() ? 1.0F : 0.0F;
   private static final ClampedItemPropertyFunction PROPERTY_DAMAGE = (var0x, var1, var2, var3) -> Mth.clamp(
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
      GENERIC_PROPERTIES.put(new ResourceLocation("custom_model_data"), var0);
   }

   private static void register(Item var0, ResourceLocation var1, ClampedItemPropertyFunction var2) {
      PROPERTIES.computeIfAbsent(var0, var0x -> Maps.newHashMap()).put(var1, var2);
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

      ItemPropertyFunction var2 = GENERIC_PROPERTIES.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         Map var3 = PROPERTIES.get(var0);
         return var3 == null ? null : (ItemPropertyFunction)var3.get(var1);
      }
   }

   static {
      registerGeneric(new ResourceLocation("lefthanded"), (var0x, var1, var2, var3) -> var2 != null && var2.getMainArm() != HumanoidArm.RIGHT ? 1.0F : 0.0F);
      registerGeneric(
         new ResourceLocation("cooldown"),
         (var0x, var1, var2, var3) -> var2 instanceof Player ? ((Player)var2).getCooldowns().getCooldownPercent(var0x.getItem(), 0.0F) : 0.0F
      );
      ClampedItemPropertyFunction var0 = (var0x, var1, var2, var3) -> {
         if (!var0x.is(ItemTags.TRIMMABLE_ARMOR)) {
            return -1.0F / 0.0F;
         } else if (var1 == null) {
            return 0.0F;
         } else {
            return !var1.enabledFeatures().contains(FeatureFlags.UPDATE_1_20)
               ? -1.0F / 0.0F
               : ArmorTrim.getTrim(var1.registryAccess(), var0x).map(ArmorTrim::material).map(Holder::value).map(TrimMaterial::itemModelIndex).orElse(0.0F);
         }
      };
      registerGeneric(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, var0);
      registerCustomModelData((var0x, var1, var2, var3) -> var0x.hasTag() ? (float)var0x.getTag().getInt("CustomModelData") : 0.0F);
      register(Items.BOW, new ResourceLocation("pull"), (var0x, var1, var2, var3) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            return var2.getUseItem() != var0x ? 0.0F : (float)(var0x.getUseDuration() - var2.getUseItemRemainingTicks()) / 20.0F;
         }
      });
      register(
         Items.BRUSH,
         new ResourceLocation("brushing"),
         (var0x, var1, var2, var3) -> var2 != null && var2.getUseItem() == var0x ? (float)(var2.getUseItemRemainingTicks() % 10) / 10.0F : 0.0F
      );
      register(
         Items.BOW,
         new ResourceLocation("pulling"),
         (var0x, var1, var2, var3) -> var2 != null && var2.isUsingItem() && var2.getUseItem() == var0x ? 1.0F : 0.0F
      );
      register(Items.BUNDLE, new ResourceLocation("filled"), (var0x, var1, var2, var3) -> BundleItem.getFullnessDisplay(var0x));
      register(Items.CLOCK, new ResourceLocation("time"), new ClampedItemPropertyFunction() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         @Override
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
      register(
         Items.COMPASS,
         new ResourceLocation("angle"),
         new CompassItemPropertyFunction(
            (var0x, var1, var2) -> CompassItem.isLodestoneCompass(var1)
                  ? CompassItem.getLodestonePosition(var1.getOrCreateTag())
                  : CompassItem.getSpawnPosition(var0x)
         )
      );
      register(
         Items.RECOVERY_COMPASS,
         new ResourceLocation("angle"),
         new CompassItemPropertyFunction((var0x, var1, var2) -> var2 instanceof Player var3 ? var3.getLastDeathLocation().orElse(null) : null)
      );
      register(
         Items.CROSSBOW,
         new ResourceLocation("pull"),
         (var0x, var1, var2, var3) -> {
            if (var2 == null) {
               return 0.0F;
            } else {
               return CrossbowItem.isCharged(var0x)
                  ? 0.0F
                  : (float)(var0x.getUseDuration() - var2.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(var0x);
            }
         }
      );
      register(
         Items.CROSSBOW,
         new ResourceLocation("pulling"),
         (var0x, var1, var2, var3) -> var2 != null && var2.isUsingItem() && var2.getUseItem() == var0x && !CrossbowItem.isCharged(var0x) ? 1.0F : 0.0F
      );
      register(Items.CROSSBOW, new ResourceLocation("charged"), (var0x, var1, var2, var3) -> CrossbowItem.isCharged(var0x) ? 1.0F : 0.0F);
      register(
         Items.CROSSBOW,
         new ResourceLocation("firework"),
         (var0x, var1, var2, var3) -> CrossbowItem.isCharged(var0x) && CrossbowItem.containsChargedProjectile(var0x, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F
      );
      register(Items.ELYTRA, new ResourceLocation("broken"), (var0x, var1, var2, var3) -> ElytraItem.isFlyEnabled(var0x) ? 0.0F : 1.0F);
      register(Items.FISHING_ROD, new ResourceLocation("cast"), (var0x, var1, var2, var3) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            boolean var4 = var2.getMainHandItem() == var0x;
            boolean var5 = var2.getOffhandItem() == var0x;
            if (var2.getMainHandItem().getItem() instanceof FishingRodItem) {
               var5 = false;
            }

            return (var4 || var5) && var2 instanceof Player && ((Player)var2).fishing != null ? 1.0F : 0.0F;
         }
      });
      register(
         Items.SHIELD,
         new ResourceLocation("blocking"),
         (var0x, var1, var2, var3) -> var2 != null && var2.isUsingItem() && var2.getUseItem() == var0x ? 1.0F : 0.0F
      );
      register(
         Items.TRIDENT,
         new ResourceLocation("throwing"),
         (var0x, var1, var2, var3) -> var2 != null && var2.isUsingItem() && var2.getUseItem() == var0x ? 1.0F : 0.0F
      );
      register(Items.LIGHT, new ResourceLocation("level"), (var0x, var1, var2, var3) -> {
         CompoundTag var4 = var0x.getTagElement("BlockStateTag");

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
      register(
         Items.GOAT_HORN,
         new ResourceLocation("tooting"),
         (var0x, var1, var2, var3) -> var2 != null && var2.isUsingItem() && var2.getUseItem() == var0x ? 1.0F : 0.0F
      );
   }
}
