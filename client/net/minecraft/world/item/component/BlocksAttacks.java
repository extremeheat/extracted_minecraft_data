package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record BlocksAttacks(float blockDelaySeconds, float disableCooldownScale, List<DamageReduction> damageReductions, ItemDamageFunction itemDamage, Optional<Holder<SoundEvent>> blockSound, Optional<Holder<SoundEvent>> disableSound) {
   public static final Codec<BlocksAttacks> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("block_delay_seconds", 0.0F).forGetter(BlocksAttacks::blockDelaySeconds), ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("disable_cooldown_scale", 1.0F).forGetter(BlocksAttacks::disableCooldownScale), BlocksAttacks.DamageReduction.CODEC.listOf().optionalFieldOf("damage_reductions", List.of(BlocksAttacks.DamageReduction.BLOCK_ALL)).forGetter(BlocksAttacks::damageReductions), BlocksAttacks.ItemDamageFunction.CODEC.optionalFieldOf("item_damage", BlocksAttacks.ItemDamageFunction.DEFAULT).forGetter(BlocksAttacks::itemDamage), SoundEvent.CODEC.optionalFieldOf("block_sound").forGetter(BlocksAttacks::blockSound), SoundEvent.CODEC.optionalFieldOf("disabled_sound").forGetter(BlocksAttacks::disableSound)).apply(var0, BlocksAttacks::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, BlocksAttacks> STREAM_CODEC;

   public BlocksAttacks(float var1, float var2, List<DamageReduction> var3, ItemDamageFunction var4, Optional<Holder<SoundEvent>> var5, Optional<Holder<SoundEvent>> var6) {
      super();
      this.blockDelaySeconds = var1;
      this.disableCooldownScale = var2;
      this.damageReductions = var3;
      this.itemDamage = var4;
      this.blockSound = var5;
      this.disableSound = var6;
   }

   public void onBlocked(ServerLevel var1, LivingEntity var2) {
      this.blockSound.ifPresent((var2x) -> var1.playSound((Entity)null, var2.getX(), var2.getY(), var2.getZ(), var2x, var2.getSoundSource(), 1.0F, 0.8F + var1.random.nextFloat() * 0.4F));
   }

   public void disable(ServerLevel var1, LivingEntity var2, float var3, ItemStack var4) {
      int var5 = this.disableBlockingForTicks(var3);
      if (var5 > 0) {
         if (var2 instanceof Player) {
            Player var6 = (Player)var2;
            var6.getCooldowns().addCooldown(var4, var5);
         }

         var2.stopUsingItem();
         this.disableSound.ifPresent((var2x) -> var1.playSound((Entity)null, var2.getX(), var2.getY(), var2.getZ(), var2x, var2.getSoundSource(), 0.8F, 0.8F + var1.random.nextFloat() * 0.4F));
      }

   }

   public void hurtBlockingItem(Level var1, ItemStack var2, LivingEntity var3, InteractionHand var4, float var5) {
      if (var3 instanceof Player var6) {
         if (!var1.isClientSide) {
            var6.awardStat(Stats.ITEM_USED.get(var2.getItem()));
         }

         int var7 = this.itemDamage.apply(var5);
         if (var7 > 0) {
            var2.hurtAndBreak(var7, var3, LivingEntity.getSlotForHand(var4));
         }

      }
   }

   private int disableBlockingForTicks(float var1) {
      float var2 = var1 * this.disableCooldownScale;
      return var2 > 0.0F ? Math.round(var2 * 20.0F) : 0;
   }

   public int blockDelayTicks() {
      return Math.round(this.blockDelaySeconds * 20.0F);
   }

   public float resolveBlockedDamage(DamageSource var1, float var2) {
      float var3 = 0.0F;

      for(DamageReduction var5 : this.damageReductions) {
         var3 += var5.resolve(var1, var2);
      }

      return Mth.clamp(var3, 0.0F, var2);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, BlocksAttacks::blockDelaySeconds, ByteBufCodecs.FLOAT, BlocksAttacks::disableCooldownScale, BlocksAttacks.DamageReduction.STREAM_CODEC.apply(ByteBufCodecs.list()), BlocksAttacks::damageReductions, BlocksAttacks.ItemDamageFunction.STREAM_CODEC, BlocksAttacks::itemDamage, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), BlocksAttacks::blockSound, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), BlocksAttacks::disableSound, BlocksAttacks::new);
   }

   public static record DamageReduction(Optional<HolderSet<DamageType>> type, float base, float factor) {
      public static final Codec<DamageReduction> CODEC = RecordCodecBuilder.create((var0) -> var0.group(RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).optionalFieldOf("type").forGetter(DamageReduction::type), Codec.FLOAT.fieldOf("base").forGetter(DamageReduction::base), Codec.FLOAT.fieldOf("factor").forGetter(DamageReduction::factor)).apply(var0, DamageReduction::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, DamageReduction> STREAM_CODEC;
      public static final DamageReduction BLOCK_ALL;

      public DamageReduction(Optional<HolderSet<DamageType>> var1, float var2, float var3) {
         super();
         this.type = var1;
         this.base = var2;
         this.factor = var3;
      }

      public float resolve(DamageSource var1, float var2) {
         return this.type.isPresent() && !((HolderSet)this.type.get()).contains(var1.typeHolder()) ? 0.0F : Mth.clamp(this.base + this.factor * var2, 0.0F, var2);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.DAMAGE_TYPE).apply(ByteBufCodecs::optional), DamageReduction::type, ByteBufCodecs.FLOAT, DamageReduction::base, ByteBufCodecs.FLOAT, DamageReduction::factor, DamageReduction::new);
         BLOCK_ALL = new DamageReduction(Optional.empty(), 0.0F, 1.0F);
      }
   }

   public static record ItemDamageFunction(float threshold, float base, float factor) {
      public static final Codec<ItemDamageFunction> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("threshold").forGetter(ItemDamageFunction::threshold), Codec.FLOAT.fieldOf("base").forGetter(ItemDamageFunction::base), Codec.FLOAT.fieldOf("factor").forGetter(ItemDamageFunction::factor)).apply(var0, ItemDamageFunction::new));
      public static final StreamCodec<ByteBuf, ItemDamageFunction> STREAM_CODEC;
      public static final ItemDamageFunction DEFAULT;

      public ItemDamageFunction(float var1, float var2, float var3) {
         super();
         this.threshold = var1;
         this.base = var2;
         this.factor = var3;
      }

      public int apply(float var1) {
         return var1 < this.threshold ? 0 : Mth.floor(this.base + this.factor * var1);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, ItemDamageFunction::threshold, ByteBufCodecs.FLOAT, ItemDamageFunction::base, ByteBufCodecs.FLOAT, ItemDamageFunction::factor, ItemDamageFunction::new);
         DEFAULT = new ItemDamageFunction(1.0F, 0.0F, 1.0F);
      }
   }
}
