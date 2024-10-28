package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public record ExplodeEffect(boolean attributeToUser, Optional<Holder<DamageType>> damageType, Optional<LevelBasedValue> knockbackMultiplier, Optional<HolderSet<Block>> immuneBlocks, Vec3 offset, LevelBasedValue radius, boolean createFire, Level.ExplosionInteraction blockInteraction, ParticleOptions smallParticle, ParticleOptions largeParticle, Holder<SoundEvent> sound) implements EnchantmentEntityEffect {
   public static final MapCodec<ExplodeEffect> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.BOOL.optionalFieldOf("attribute_to_user", false).forGetter(ExplodeEffect::attributeToUser), DamageType.CODEC.optionalFieldOf("damage_type").forGetter(ExplodeEffect::damageType), LevelBasedValue.CODEC.optionalFieldOf("knockback_multiplier").forGetter(ExplodeEffect::knockbackMultiplier), RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("immune_blocks").forGetter(ExplodeEffect::immuneBlocks), Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(ExplodeEffect::offset), LevelBasedValue.CODEC.fieldOf("radius").forGetter(ExplodeEffect::radius), Codec.BOOL.optionalFieldOf("create_fire", false).forGetter(ExplodeEffect::createFire), Level.ExplosionInteraction.CODEC.fieldOf("block_interaction").forGetter(ExplodeEffect::blockInteraction), ParticleTypes.CODEC.fieldOf("small_particle").forGetter(ExplodeEffect::smallParticle), ParticleTypes.CODEC.fieldOf("large_particle").forGetter(ExplodeEffect::largeParticle), SoundEvent.CODEC.fieldOf("sound").forGetter(ExplodeEffect::sound)).apply(var0, ExplodeEffect::new);
   });

   public ExplodeEffect(boolean attributeToUser, Optional<Holder<DamageType>> damageType, Optional<LevelBasedValue> knockbackMultiplier, Optional<HolderSet<Block>> immuneBlocks, Vec3 offset, LevelBasedValue radius, boolean createFire, Level.ExplosionInteraction blockInteraction, ParticleOptions smallParticle, ParticleOptions largeParticle, Holder<SoundEvent> sound) {
      super();
      this.attributeToUser = attributeToUser;
      this.damageType = damageType;
      this.knockbackMultiplier = knockbackMultiplier;
      this.immuneBlocks = immuneBlocks;
      this.offset = offset;
      this.radius = radius;
      this.createFire = createFire;
      this.blockInteraction = blockInteraction;
      this.smallParticle = smallParticle;
      this.largeParticle = largeParticle;
      this.sound = sound;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      Vec3 var6 = var5.add(this.offset);
      var1.explode(this.attributeToUser ? var4 : null, this.getDamageSource(var4, var6), new SimpleExplosionDamageCalculator(this.blockInteraction != Level.ExplosionInteraction.NONE, this.damageType.isPresent(), this.knockbackMultiplier.map((var1x) -> {
         return var1x.calculate(var2);
      }), this.immuneBlocks), var6.x(), var6.y(), var6.z(), Math.max(this.radius.calculate(var2), 0.0F), this.createFire, this.blockInteraction, this.smallParticle, this.largeParticle, this.sound);
   }

   @Nullable
   private DamageSource getDamageSource(Entity var1, Vec3 var2) {
      if (this.damageType.isEmpty()) {
         return null;
      } else {
         return this.attributeToUser ? new DamageSource((Holder)this.damageType.get(), var1) : new DamageSource((Holder)this.damageType.get(), var2);
      }
   }

   public MapCodec<ExplodeEffect> codec() {
      return CODEC;
   }

   public boolean attributeToUser() {
      return this.attributeToUser;
   }

   public Optional<Holder<DamageType>> damageType() {
      return this.damageType;
   }

   public Optional<LevelBasedValue> knockbackMultiplier() {
      return this.knockbackMultiplier;
   }

   public Optional<HolderSet<Block>> immuneBlocks() {
      return this.immuneBlocks;
   }

   public Vec3 offset() {
      return this.offset;
   }

   public LevelBasedValue radius() {
      return this.radius;
   }

   public boolean createFire() {
      return this.createFire;
   }

   public Level.ExplosionInteraction blockInteraction() {
      return this.blockInteraction;
   }

   public ParticleOptions smallParticle() {
      return this.smallParticle;
   }

   public ParticleOptions largeParticle() {
      return this.largeParticle;
   }

   public Holder<SoundEvent> sound() {
      return this.sound;
   }
}
