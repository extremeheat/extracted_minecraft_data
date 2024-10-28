package net.minecraft.world.item;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrossbowItem extends ProjectileWeaponItem {
   private static final float MAX_CHARGE_DURATION = 1.25F;
   public static final int DEFAULT_RANGE = 8;
   private boolean startSoundPlayed = false;
   private boolean midLoadSoundPlayed = false;
   private static final float START_SOUND_PERCENT = 0.2F;
   private static final float MID_SOUND_PERCENT = 0.5F;
   private static final float ARROW_POWER = 3.15F;
   private static final float FIREWORK_POWER = 1.6F;
   public static final float MOB_ARROW_POWER = 1.6F;
   private static final ChargingSounds DEFAULT_SOUNDS;

   public CrossbowItem(Item.Properties var1) {
      super(var1);
   }

   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return ARROW_OR_FIREWORK;
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_ONLY;
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      ChargedProjectiles var5 = (ChargedProjectiles)var4.get(DataComponents.CHARGED_PROJECTILES);
      if (var5 != null && !var5.isEmpty()) {
         this.performShooting(var1, var2, var3, var4, getShootingPower(var5), 1.0F, (LivingEntity)null);
         return InteractionResult.CONSUME;
      } else if (!var2.getProjectile(var4).isEmpty()) {
         this.startSoundPlayed = false;
         this.midLoadSoundPlayed = false;
         var2.startUsingItem(var3);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.FAIL;
      }
   }

   private static float getShootingPower(ChargedProjectiles var0) {
      return var0.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
   }

   public boolean releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      int var5 = this.getUseDuration(var1, var3) - var4;
      float var6 = getPowerForTime(var5, var1, var3);
      if (var6 >= 1.0F && !isCharged(var1) && tryLoadProjectiles(var3, var1)) {
         ChargingSounds var7 = this.getChargingSounds(var1);
         var7.end().ifPresent((var2x) -> {
            var2.playSound((Player)null, var3.getX(), var3.getY(), var3.getZ(), (SoundEvent)((SoundEvent)var2x.value()), var3.getSoundSource(), 1.0F, 1.0F / (var2.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
         });
         return true;
      } else {
         return false;
      }
   }

   private static boolean tryLoadProjectiles(LivingEntity var0, ItemStack var1) {
      List var2 = draw(var1, var0.getProjectile(var1), var0);
      if (!var2.isEmpty()) {
         var1.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(var2));
         return true;
      } else {
         return false;
      }
   }

   public static boolean isCharged(ItemStack var0) {
      ChargedProjectiles var1 = (ChargedProjectiles)var0.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
      return !var1.isEmpty();
   }

   protected void shootProjectile(LivingEntity var1, Projectile var2, int var3, float var4, float var5, float var6, @Nullable LivingEntity var7) {
      Vector3f var8;
      if (var7 != null) {
         double var9 = var7.getX() - var1.getX();
         double var11 = var7.getZ() - var1.getZ();
         double var13 = Math.sqrt(var9 * var9 + var11 * var11);
         double var15 = var7.getY(0.3333333333333333) - var2.getY() + var13 * 0.20000000298023224;
         var8 = getProjectileShotVector(var1, new Vec3(var9, var15, var11), var6);
      } else {
         Vec3 var18 = var1.getUpVector(1.0F);
         Quaternionf var10 = (new Quaternionf()).setAngleAxis((double)(var6 * 0.017453292F), var18.x, var18.y, var18.z);
         Vec3 var17 = var1.getViewVector(1.0F);
         var8 = var17.toVector3f().rotate(var10);
      }

      var2.shoot((double)var8.x(), (double)var8.y(), (double)var8.z(), var4, var5);
      float var19 = getShotPitch(var1.getRandom(), var3);
      var1.level().playSound((Player)null, var1.getX(), var1.getY(), var1.getZ(), (SoundEvent)SoundEvents.CROSSBOW_SHOOT, var1.getSoundSource(), 1.0F, var19);
   }

   private static Vector3f getProjectileShotVector(LivingEntity var0, Vec3 var1, float var2) {
      Vector3f var3 = var1.toVector3f().normalize();
      Vector3f var4 = (new Vector3f(var3)).cross(new Vector3f(0.0F, 1.0F, 0.0F));
      if ((double)var4.lengthSquared() <= 1.0E-7) {
         Vec3 var5 = var0.getUpVector(1.0F);
         var4 = (new Vector3f(var3)).cross(var5.toVector3f());
      }

      Vector3f var6 = (new Vector3f(var3)).rotateAxis(1.5707964F, var4.x, var4.y, var4.z);
      return (new Vector3f(var3)).rotateAxis(var2 * 0.017453292F, var6.x, var6.y, var6.z);
   }

   protected Projectile createProjectile(Level var1, LivingEntity var2, ItemStack var3, ItemStack var4, boolean var5) {
      if (var4.is(Items.FIREWORK_ROCKET)) {
         return new FireworkRocketEntity(var1, var4, var2, var2.getX(), var2.getEyeY() - 0.15000000596046448, var2.getZ(), true);
      } else {
         Projectile var6 = super.createProjectile(var1, var2, var3, var4, var5);
         if (var6 instanceof AbstractArrow) {
            AbstractArrow var7 = (AbstractArrow)var6;
            var7.setSoundEvent(SoundEvents.CROSSBOW_HIT);
         }

         return var6;
      }
   }

   protected int getDurabilityUse(ItemStack var1) {
      return var1.is(Items.FIREWORK_ROCKET) ? 3 : 1;
   }

   public void performShooting(Level var1, LivingEntity var2, InteractionHand var3, ItemStack var4, float var5, float var6, @Nullable LivingEntity var7) {
      if (var1 instanceof ServerLevel var8) {
         ChargedProjectiles var9 = (ChargedProjectiles)var4.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
         if (var9 != null && !var9.isEmpty()) {
            this.shoot(var8, var2, var3, var4, var9.getItems(), var5, var6, var2 instanceof Player, var7);
            if (var2 instanceof ServerPlayer) {
               ServerPlayer var10 = (ServerPlayer)var2;
               CriteriaTriggers.SHOT_CROSSBOW.trigger(var10, var4);
               var10.awardStat(Stats.ITEM_USED.get(var4.getItem()));
            }

         }
      }
   }

   private static float getShotPitch(RandomSource var0, int var1) {
      return var1 == 0 ? 1.0F : getRandomShotPitch((var1 & 1) == 1, var0);
   }

   private static float getRandomShotPitch(boolean var0, RandomSource var1) {
      float var2 = var0 ? 0.63F : 0.43F;
      return 1.0F / (var1.nextFloat() * 0.5F + 1.8F) + var2;
   }

   public void onUseTick(Level var1, LivingEntity var2, ItemStack var3, int var4) {
      if (!var1.isClientSide) {
         ChargingSounds var5 = this.getChargingSounds(var3);
         float var6 = (float)(var3.getUseDuration(var2) - var4) / (float)getChargeDuration(var3, var2);
         if (var6 < 0.2F) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
         }

         if (var6 >= 0.2F && !this.startSoundPlayed) {
            this.startSoundPlayed = true;
            var5.start().ifPresent((var2x) -> {
               var1.playSound((Player)null, var2.getX(), var2.getY(), var2.getZ(), (SoundEvent)((SoundEvent)var2x.value()), SoundSource.PLAYERS, 0.5F, 1.0F);
            });
         }

         if (var6 >= 0.5F && !this.midLoadSoundPlayed) {
            this.midLoadSoundPlayed = true;
            var5.mid().ifPresent((var2x) -> {
               var1.playSound((Player)null, var2.getX(), var2.getY(), var2.getZ(), (SoundEvent)((SoundEvent)var2x.value()), SoundSource.PLAYERS, 0.5F, 1.0F);
            });
         }
      }

   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return getChargeDuration(var1, var2) + 3;
   }

   public static int getChargeDuration(ItemStack var0, LivingEntity var1) {
      float var2 = EnchantmentHelper.modifyCrossbowChargingTime(var0, var1, 1.25F);
      return Mth.floor(var2 * 20.0F);
   }

   public ItemUseAnimation getUseAnimation(ItemStack var1) {
      return ItemUseAnimation.CROSSBOW;
   }

   ChargingSounds getChargingSounds(ItemStack var1) {
      return (ChargingSounds)EnchantmentHelper.pickHighestLevel(var1, EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS).orElse(DEFAULT_SOUNDS);
   }

   private static float getPowerForTime(int var0, ItemStack var1, LivingEntity var2) {
      float var3 = (float)var0 / (float)getChargeDuration(var1, var2);
      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      return var3;
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      ChargedProjectiles var5 = (ChargedProjectiles)var1.get(DataComponents.CHARGED_PROJECTILES);
      if (var5 != null && !var5.isEmpty()) {
         ItemStack var6 = (ItemStack)var5.getItems().get(0);
         var3.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(var6.getDisplayName()));
         if (var4.isAdvanced() && var6.is(Items.FIREWORK_ROCKET)) {
            ArrayList var7 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendHoverText(var6, var2, var7, var4);
            if (!var7.isEmpty()) {
               for(int var8 = 0; var8 < var7.size(); ++var8) {
                  var7.set(var8, Component.literal("  ").append((Component)var7.get(var8)).withStyle(ChatFormatting.GRAY));
               }

               var3.addAll(var7);
            }
         }

      }
   }

   public boolean useOnRelease(ItemStack var1) {
      return var1.is((Item)this);
   }

   public int getDefaultProjectileRange() {
      return 8;
   }

   static {
      DEFAULT_SOUNDS = new ChargingSounds(Optional.of(SoundEvents.CROSSBOW_LOADING_START), Optional.of(SoundEvents.CROSSBOW_LOADING_MIDDLE), Optional.of(SoundEvents.CROSSBOW_LOADING_END));
   }

   public static record ChargingSounds(Optional<Holder<SoundEvent>> start, Optional<Holder<SoundEvent>> mid, Optional<Holder<SoundEvent>> end) {
      public static final Codec<ChargingSounds> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(SoundEvent.CODEC.optionalFieldOf("start").forGetter(ChargingSounds::start), SoundEvent.CODEC.optionalFieldOf("mid").forGetter(ChargingSounds::mid), SoundEvent.CODEC.optionalFieldOf("end").forGetter(ChargingSounds::end)).apply(var0, ChargingSounds::new);
      });

      public ChargingSounds(Optional<Holder<SoundEvent>> var1, Optional<Holder<SoundEvent>> var2, Optional<Holder<SoundEvent>> var3) {
         super();
         this.start = var1;
         this.mid = var2;
         this.end = var3;
      }

      public Optional<Holder<SoundEvent>> start() {
         return this.start;
      }

      public Optional<Holder<SoundEvent>> mid() {
         return this.mid;
      }

      public Optional<Holder<SoundEvent>> end() {
         return this.end;
      }
   }
}
