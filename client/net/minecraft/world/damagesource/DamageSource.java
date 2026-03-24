package net.minecraft.world.damagesource;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class DamageSource {
   private final Holder<DamageType> type;
   private final @Nullable Entity causingEntity;
   private final @Nullable Entity directEntity;
   private final @Nullable Vec3 damageSourcePosition;

   public String toString() {
      return "DamageSource (" + this.type().msgId() + ")";
   }

   public float getFoodExhaustion() {
      return this.type().exhaustion();
   }

   public boolean isDirect() {
      return this.causingEntity == this.directEntity;
   }

   private DamageSource(final Holder<DamageType> type, final @Nullable Entity directEntity, final @Nullable Entity causingEntity, final @Nullable Vec3 damageSourcePosition) {
      super();
      this.type = type;
      this.causingEntity = causingEntity;
      this.directEntity = directEntity;
      this.damageSourcePosition = damageSourcePosition;
   }

   public DamageSource(final Holder<DamageType> type, final @Nullable Entity directEntity, final @Nullable Entity causingEntity) {
      this(type, directEntity, causingEntity, (Vec3)null);
   }

   public DamageSource(final Holder<DamageType> type, final Vec3 damageSourcePosition) {
      this(type, (Entity)null, (Entity)null, damageSourcePosition);
   }

   public DamageSource(final Holder<DamageType> type, final @Nullable Entity causingEntity) {
      this(type, causingEntity, causingEntity);
   }

   public DamageSource(final Holder<DamageType> type) {
      this(type, (Entity)null, (Entity)null, (Vec3)null);
   }

   public @Nullable Entity getDirectEntity() {
      return this.directEntity;
   }

   public @Nullable Entity getEntity() {
      return this.causingEntity;
   }

   public @Nullable ItemStack getWeaponItem() {
      return this.directEntity != null ? this.directEntity.getWeaponItem() : null;
   }

   public Component getLocalizedDeathMessage(final LivingEntity victim) {
      String deathMsg = "death.attack." + this.type().msgId();
      if (this.causingEntity == null && this.directEntity == null) {
         LivingEntity source = victim.getKillCredit();
         String playerMsg = deathMsg + ".player";
         return source != null ? Component.translatable(playerMsg, victim.getDisplayName(), source.getDisplayName()) : Component.translatable(deathMsg, victim.getDisplayName());
      } else {
         Component name = this.causingEntity == null ? this.directEntity.getDisplayName() : this.causingEntity.getDisplayName();
         Entity var6 = this.causingEntity;
         ItemStack var10000;
         if (var6 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)var6;
            var10000 = livingEntity.getMainHandItem();
         } else {
            var10000 = ItemStack.EMPTY;
         }

         ItemStack held = var10000;
         return !held.isEmpty() && held.has(DataComponents.CUSTOM_NAME) ? Component.translatable(deathMsg + ".item", victim.getDisplayName(), name, held.getDisplayName()) : Component.translatable(deathMsg, victim.getDisplayName(), name);
      }
   }

   public String getMsgId() {
      return this.type().msgId();
   }

   public boolean scalesWithDifficulty() {
      boolean var10000;
      switch (this.type().scaling()) {
         case NEVER -> var10000 = false;
         case WHEN_CAUSED_BY_LIVING_NON_PLAYER -> var10000 = this.causingEntity instanceof LivingEntity && !(this.causingEntity instanceof Player);
         case ALWAYS -> var10000 = true;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean isCreativePlayer() {
      Entity var2 = this.getEntity();
      boolean var10000;
      if (var2 instanceof Player player) {
         if (player.getAbilities().instabuild) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public @Nullable Vec3 getSourcePosition() {
      if (this.damageSourcePosition != null) {
         return this.damageSourcePosition;
      } else {
         return this.directEntity != null ? this.directEntity.position() : null;
      }
   }

   public @Nullable Vec3 sourcePositionRaw() {
      return this.damageSourcePosition;
   }

   public boolean is(final TagKey<DamageType> tag) {
      return this.type.is(tag);
   }

   public boolean is(final ResourceKey<DamageType> typeKey) {
      return this.type.is(typeKey);
   }

   public DamageType type() {
      return this.type.value();
   }

   public Holder<DamageType> typeHolder() {
      return this.type;
   }
}
