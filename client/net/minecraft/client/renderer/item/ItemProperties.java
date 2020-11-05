package net.minecraft.client.renderer.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemProperties {
   private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = Maps.newHashMap();
   private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
   private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
   private static final ItemPropertyFunction PROPERTY_DAMAGED = (var0, var1, var2) -> {
      return var0.isDamaged() ? 1.0F : 0.0F;
   };
   private static final ItemPropertyFunction PROPERTY_DAMAGE = (var0, var1, var2) -> {
      return Mth.clamp((float)var0.getDamageValue() / (float)var0.getMaxDamage(), 0.0F, 1.0F);
   };
   private static final Map<Item, Map<ResourceLocation, ItemPropertyFunction>> PROPERTIES = Maps.newHashMap();

   private static ItemPropertyFunction registerGeneric(ResourceLocation var0, ItemPropertyFunction var1) {
      GENERIC_PROPERTIES.put(var0, var1);
      return var1;
   }

   private static void register(Item var0, ResourceLocation var1, ItemPropertyFunction var2) {
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
      registerGeneric(new ResourceLocation("lefthanded"), (var0, var1, var2) -> {
         return var2 != null && var2.getMainArm() != HumanoidArm.RIGHT ? 1.0F : 0.0F;
      });
      registerGeneric(new ResourceLocation("cooldown"), (var0, var1, var2) -> {
         return var2 instanceof Player ? ((Player)var2).getCooldowns().getCooldownPercent(var0.getItem(), 0.0F) : 0.0F;
      });
      registerGeneric(new ResourceLocation("custom_model_data"), (var0, var1, var2) -> {
         return var0.hasTag() ? (float)var0.getTag().getInt("CustomModelData") : 0.0F;
      });
      register(Items.BOW, new ResourceLocation("pull"), (var0, var1, var2) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            return var2.getUseItem() != var0 ? 0.0F : (float)(var0.getUseDuration() - var2.getUseItemRemainingTicks()) / 20.0F;
         }
      });
      register(Items.BOW, new ResourceLocation("pulling"), (var0, var1, var2) -> {
         return var2 != null && var2.isUsingItem() && var2.getUseItem() == var0 ? 1.0F : 0.0F;
      });
      register(Items.CLOCK, new ResourceLocation("time"), new ItemPropertyFunction() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         public float call(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3) {
            Object var4 = var3 != null ? var3 : var1.getEntityRepresentation();
            if (var4 == null) {
               return 0.0F;
            } else {
               if (var2 == null && ((Entity)var4).level instanceof ClientLevel) {
                  var2 = (ClientLevel)((Entity)var4).level;
               }

               if (var2 == null) {
                  return 0.0F;
               } else {
                  double var5;
                  if (var2.dimensionType().natural()) {
                     var5 = (double)var2.getTimeOfDay(1.0F);
                  } else {
                     var5 = Math.random();
                  }

                  var5 = this.wobble(var2, var5);
                  return (float)var5;
               }
            }
         }

         private double wobble(Level var1, double var2) {
            if (var1.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = var1.getGameTime();
               double var4 = var2 - this.rotation;
               var4 = Mth.positiveModulo(var4 + 0.5D, 1.0D) - 0.5D;
               this.rota += var4 * 0.1D;
               this.rota *= 0.9D;
               this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }
      });
      register(Items.COMPASS, new ResourceLocation("angle"), new ItemPropertyFunction() {
         private final ItemProperties.CompassWobble wobble = new ItemProperties.CompassWobble();
         private final ItemProperties.CompassWobble wobbleRandom = new ItemProperties.CompassWobble();

         public float call(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3) {
            Object var4 = var3 != null ? var3 : var1.getEntityRepresentation();
            if (var4 == null) {
               return 0.0F;
            } else {
               if (var2 == null && ((Entity)var4).level instanceof ClientLevel) {
                  var2 = (ClientLevel)((Entity)var4).level;
               }

               BlockPos var5 = CompassItem.isLodestoneCompass(var1) ? this.getLodestonePosition(var2, var1.getOrCreateTag()) : this.getSpawnPosition(var2);
               long var6 = var2.getGameTime();
               if (var5 != null && ((Entity)var4).position().distanceToSqr((double)var5.getX() + 0.5D, ((Entity)var4).position().y(), (double)var5.getZ() + 0.5D) >= 9.999999747378752E-6D) {
                  boolean var15 = var3 instanceof Player && ((Player)var3).isLocalPlayer();
                  double var9 = 0.0D;
                  if (var15) {
                     var9 = (double)var3.yRot;
                  } else if (var4 instanceof ItemFrame) {
                     var9 = this.getFrameRotation((ItemFrame)var4);
                  } else if (var4 instanceof ItemEntity) {
                     var9 = (double)(180.0F - ((ItemEntity)var4).getSpin(0.5F) / 6.2831855F * 360.0F);
                  } else if (var3 != null) {
                     var9 = (double)var3.yBodyRot;
                  }

                  var9 = Mth.positiveModulo(var9 / 360.0D, 1.0D);
                  double var11 = this.getAngleTo(Vec3.atCenterOf(var5), (Entity)var4) / 6.2831854820251465D;
                  double var13;
                  if (var15) {
                     if (this.wobble.shouldUpdate(var6)) {
                        this.wobble.update(var6, 0.5D - (var9 - 0.25D));
                     }

                     var13 = var11 + this.wobble.rotation;
                  } else {
                     var13 = 0.5D - (var9 - 0.25D - var11);
                  }

                  return Mth.positiveModulo((float)var13, 1.0F);
               } else {
                  if (this.wobbleRandom.shouldUpdate(var6)) {
                     this.wobbleRandom.update(var6, Math.random());
                  }

                  double var8 = this.wobbleRandom.rotation + (double)((float)var1.hashCode() / 2.14748365E9F);
                  return Mth.positiveModulo((float)var8, 1.0F);
               }
            }
         }

         @Nullable
         private BlockPos getSpawnPosition(ClientLevel var1) {
            return var1.dimensionType().natural() ? var1.getSharedSpawnPos() : null;
         }

         @Nullable
         private BlockPos getLodestonePosition(Level var1, CompoundTag var2) {
            boolean var3 = var2.contains("LodestonePos");
            boolean var4 = var2.contains("LodestoneDimension");
            if (var3 && var4) {
               Optional var5 = CompassItem.getLodestoneDimension(var2);
               if (var5.isPresent() && var1.dimension() == var5.get()) {
                  return NbtUtils.readBlockPos(var2.getCompound("LodestonePos"));
               }
            }

            return null;
         }

         private double getFrameRotation(ItemFrame var1) {
            Direction var2 = var1.getDirection();
            int var3 = var2.getAxis().isVertical() ? 90 * var2.getAxisDirection().getStep() : 0;
            return (double)Mth.wrapDegrees(180 + var2.get2DDataValue() * 90 + var1.getRotation() * 45 + var3);
         }

         private double getAngleTo(Vec3 var1, Entity var2) {
            return Math.atan2(var1.z() - var2.getZ(), var1.x() - var2.getX());
         }
      });
      register(Items.CROSSBOW, new ResourceLocation("pull"), (var0, var1, var2) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            return CrossbowItem.isCharged(var0) ? 0.0F : (float)(var0.getUseDuration() - var2.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(var0);
         }
      });
      register(Items.CROSSBOW, new ResourceLocation("pulling"), (var0, var1, var2) -> {
         return var2 != null && var2.isUsingItem() && var2.getUseItem() == var0 && !CrossbowItem.isCharged(var0) ? 1.0F : 0.0F;
      });
      register(Items.CROSSBOW, new ResourceLocation("charged"), (var0, var1, var2) -> {
         return var2 != null && CrossbowItem.isCharged(var0) ? 1.0F : 0.0F;
      });
      register(Items.CROSSBOW, new ResourceLocation("firework"), (var0, var1, var2) -> {
         return var2 != null && CrossbowItem.isCharged(var0) && CrossbowItem.containsChargedProjectile(var0, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
      });
      register(Items.ELYTRA, new ResourceLocation("broken"), (var0, var1, var2) -> {
         return ElytraItem.isFlyEnabled(var0) ? 0.0F : 1.0F;
      });
      register(Items.FISHING_ROD, new ResourceLocation("cast"), (var0, var1, var2) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            boolean var3 = var2.getMainHandItem() == var0;
            boolean var4 = var2.getOffhandItem() == var0;
            if (var2.getMainHandItem().getItem() instanceof FishingRodItem) {
               var4 = false;
            }

            return (var3 || var4) && var2 instanceof Player && ((Player)var2).fishing != null ? 1.0F : 0.0F;
         }
      });
      register(Items.SHIELD, new ResourceLocation("blocking"), (var0, var1, var2) -> {
         return var2 != null && var2.isUsingItem() && var2.getUseItem() == var0 ? 1.0F : 0.0F;
      });
      register(Items.TRIDENT, new ResourceLocation("throwing"), (var0, var1, var2) -> {
         return var2 != null && var2.isUsingItem() && var2.getUseItem() == var0 ? 1.0F : 0.0F;
      });
   }

   static class CompassWobble {
      private double rotation;
      private double deltaRotation;
      private long lastUpdateTick;

      private CompassWobble() {
         super();
      }

      private boolean shouldUpdate(long var1) {
         return this.lastUpdateTick != var1;
      }

      private void update(long var1, double var3) {
         this.lastUpdateTick = var1;
         double var5 = var3 - this.rotation;
         var5 = Mth.positiveModulo(var5 + 0.5D, 1.0D) - 0.5D;
         this.deltaRotation += var5 * 0.1D;
         this.deltaRotation *= 0.8D;
         this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
      }

      // $FF: synthetic method
      CompassWobble(Object var1) {
         this();
      }
   }
}
