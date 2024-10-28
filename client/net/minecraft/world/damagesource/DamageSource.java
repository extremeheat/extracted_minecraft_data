package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
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

public class DamageSource {
   private final Holder<DamageType> type;
   @Nullable
   private final Entity causingEntity;
   @Nullable
   private final Entity directEntity;
   @Nullable
   private final Vec3 damageSourcePosition;

   public String toString() {
      return "DamageSource (" + this.type().msgId() + ")";
   }

   public float getFoodExhaustion() {
      return this.type().exhaustion();
   }

   public boolean isDirect() {
      return this.causingEntity == this.directEntity;
   }

   private DamageSource(Holder<DamageType> var1, @Nullable Entity var2, @Nullable Entity var3, @Nullable Vec3 var4) {
      super();
      this.type = var1;
      this.causingEntity = var3;
      this.directEntity = var2;
      this.damageSourcePosition = var4;
   }

   public DamageSource(Holder<DamageType> var1, @Nullable Entity var2, @Nullable Entity var3) {
      this(var1, var2, var3, (Vec3)null);
   }

   public DamageSource(Holder<DamageType> var1, Vec3 var2) {
      this(var1, (Entity)null, (Entity)null, var2);
   }

   public DamageSource(Holder<DamageType> var1, @Nullable Entity var2) {
      this(var1, var2, var2);
   }

   public DamageSource(Holder<DamageType> var1) {
      this(var1, (Entity)null, (Entity)null, (Vec3)null);
   }

   @Nullable
   public Entity getDirectEntity() {
      return this.directEntity;
   }

   @Nullable
   public Entity getEntity() {
      return this.causingEntity;
   }

   @Nullable
   public ItemStack getWeaponItem() {
      return this.directEntity != null ? this.directEntity.getWeaponItem() : null;
   }

   public Component getLocalizedDeathMessage(LivingEntity var1) {
      String var2 = "death.attack." + this.type().msgId();
      if (this.causingEntity == null && this.directEntity == null) {
         LivingEntity var7 = var1.getKillCredit();
         String var8 = var2 + ".player";
         return var7 != null ? Component.translatable(var8, var1.getDisplayName(), var7.getDisplayName()) : Component.translatable(var2, var1.getDisplayName());
      } else {
         Component var3 = this.causingEntity == null ? this.directEntity.getDisplayName() : this.causingEntity.getDisplayName();
         Entity var6 = this.causingEntity;
         ItemStack var10000;
         if (var6 instanceof LivingEntity) {
            LivingEntity var5 = (LivingEntity)var6;
            var10000 = var5.getMainHandItem();
         } else {
            var10000 = ItemStack.EMPTY;
         }

         ItemStack var4 = var10000;
         return !var4.isEmpty() && var4.has(DataComponents.CUSTOM_NAME) ? Component.translatable(var2 + ".item", var1.getDisplayName(), var3, var4.getDisplayName()) : Component.translatable(var2, var1.getDisplayName(), var3);
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
      if (var2 instanceof Player var1) {
         if (var1.getAbilities().instabuild) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   @Nullable
   public Vec3 getSourcePosition() {
      if (this.damageSourcePosition != null) {
         return this.damageSourcePosition;
      } else {
         return this.directEntity != null ? this.directEntity.position() : null;
      }
   }

   @Nullable
   public Vec3 sourcePositionRaw() {
      return this.damageSourcePosition;
   }

   public boolean is(TagKey<DamageType> var1) {
      return this.type.is(var1);
   }

   public boolean is(ResourceKey<DamageType> var1) {
      return this.type.is(var1);
   }

   public DamageType type() {
      return (DamageType)this.type.value();
   }

   public Holder<DamageType> typeHolder() {
      return this.type;
   }
}
