package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class DamageSources {
   private final Registry<DamageType> damageTypes;
   private final DamageSource inFire;
   private final DamageSource campfire;
   private final DamageSource lightningBolt;
   private final DamageSource onFire;
   private final DamageSource lava;
   private final DamageSource hotFloor;
   private final DamageSource inWall;
   private final DamageSource cramming;
   private final DamageSource drown;
   private final DamageSource starve;
   private final DamageSource cactus;
   private final DamageSource fall;
   private final DamageSource flyIntoWall;
   private final DamageSource fellOutOfWorld;
   private final DamageSource generic;
   private final DamageSource magic;
   private final DamageSource wither;
   private final DamageSource dragonBreath;
   private final DamageSource dryOut;
   private final DamageSource sweetBerryBush;
   private final DamageSource freeze;
   private final DamageSource stalagmite;
   private final DamageSource outsideBorder;
   private final DamageSource genericKill;

   public DamageSources(RegistryAccess var1) {
      super();
      this.damageTypes = var1.registryOrThrow(Registries.DAMAGE_TYPE);
      this.inFire = this.source(DamageTypes.IN_FIRE);
      this.campfire = this.source(DamageTypes.CAMPFIRE);
      this.lightningBolt = this.source(DamageTypes.LIGHTNING_BOLT);
      this.onFire = this.source(DamageTypes.ON_FIRE);
      this.lava = this.source(DamageTypes.LAVA);
      this.hotFloor = this.source(DamageTypes.HOT_FLOOR);
      this.inWall = this.source(DamageTypes.IN_WALL);
      this.cramming = this.source(DamageTypes.CRAMMING);
      this.drown = this.source(DamageTypes.DROWN);
      this.starve = this.source(DamageTypes.STARVE);
      this.cactus = this.source(DamageTypes.CACTUS);
      this.fall = this.source(DamageTypes.FALL);
      this.flyIntoWall = this.source(DamageTypes.FLY_INTO_WALL);
      this.fellOutOfWorld = this.source(DamageTypes.FELL_OUT_OF_WORLD);
      this.generic = this.source(DamageTypes.GENERIC);
      this.magic = this.source(DamageTypes.MAGIC);
      this.wither = this.source(DamageTypes.WITHER);
      this.dragonBreath = this.source(DamageTypes.DRAGON_BREATH);
      this.dryOut = this.source(DamageTypes.DRY_OUT);
      this.sweetBerryBush = this.source(DamageTypes.SWEET_BERRY_BUSH);
      this.freeze = this.source(DamageTypes.FREEZE);
      this.stalagmite = this.source(DamageTypes.STALAGMITE);
      this.outsideBorder = this.source(DamageTypes.OUTSIDE_BORDER);
      this.genericKill = this.source(DamageTypes.GENERIC_KILL);
   }

   private DamageSource source(ResourceKey<DamageType> var1) {
      return new DamageSource(this.damageTypes.getHolderOrThrow(var1));
   }

   private DamageSource source(ResourceKey<DamageType> var1, @Nullable Entity var2) {
      return new DamageSource(this.damageTypes.getHolderOrThrow(var1), var2);
   }

   private DamageSource source(ResourceKey<DamageType> var1, @Nullable Entity var2, @Nullable Entity var3) {
      return new DamageSource(this.damageTypes.getHolderOrThrow(var1), var2, var3);
   }

   public DamageSource inFire() {
      return this.inFire;
   }

   public DamageSource campfire() {
      return this.campfire;
   }

   public DamageSource lightningBolt() {
      return this.lightningBolt;
   }

   public DamageSource onFire() {
      return this.onFire;
   }

   public DamageSource lava() {
      return this.lava;
   }

   public DamageSource hotFloor() {
      return this.hotFloor;
   }

   public DamageSource inWall() {
      return this.inWall;
   }

   public DamageSource cramming() {
      return this.cramming;
   }

   public DamageSource drown() {
      return this.drown;
   }

   public DamageSource starve() {
      return this.starve;
   }

   public DamageSource cactus() {
      return this.cactus;
   }

   public DamageSource fall() {
      return this.fall;
   }

   public DamageSource flyIntoWall() {
      return this.flyIntoWall;
   }

   public DamageSource fellOutOfWorld() {
      return this.fellOutOfWorld;
   }

   public DamageSource generic() {
      return this.generic;
   }

   public DamageSource magic() {
      return this.magic;
   }

   public DamageSource wither() {
      return this.wither;
   }

   public DamageSource dragonBreath() {
      return this.dragonBreath;
   }

   public DamageSource dryOut() {
      return this.dryOut;
   }

   public DamageSource sweetBerryBush() {
      return this.sweetBerryBush;
   }

   public DamageSource freeze() {
      return this.freeze;
   }

   public DamageSource stalagmite() {
      return this.stalagmite;
   }

   public DamageSource fallingBlock(Entity var1) {
      return this.source(DamageTypes.FALLING_BLOCK, var1);
   }

   public DamageSource anvil(Entity var1) {
      return this.source(DamageTypes.FALLING_ANVIL, var1);
   }

   public DamageSource fallingStalactite(Entity var1) {
      return this.source(DamageTypes.FALLING_STALACTITE, var1);
   }

   public DamageSource sting(LivingEntity var1) {
      return this.source(DamageTypes.STING, var1);
   }

   public DamageSource mobAttack(LivingEntity var1) {
      return this.source(DamageTypes.MOB_ATTACK, var1);
   }

   public DamageSource noAggroMobAttack(LivingEntity var1) {
      return this.source(DamageTypes.MOB_ATTACK_NO_AGGRO, var1);
   }

   public DamageSource playerAttack(Player var1) {
      return this.source(DamageTypes.PLAYER_ATTACK, var1);
   }

   public DamageSource arrow(AbstractArrow var1, @Nullable Entity var2) {
      return this.source(DamageTypes.ARROW, var1, var2);
   }

   public DamageSource trident(Entity var1, @Nullable Entity var2) {
      return this.source(DamageTypes.TRIDENT, var1, var2);
   }

   public DamageSource mobProjectile(Entity var1, @Nullable LivingEntity var2) {
      return this.source(DamageTypes.MOB_PROJECTILE, var1, var2);
   }

   public DamageSource spit(Entity var1, @Nullable LivingEntity var2) {
      return this.source(DamageTypes.SPIT, var1, var2);
   }

   public DamageSource windCharge(Entity var1, @Nullable LivingEntity var2) {
      return this.source(DamageTypes.WIND_CHARGE, var1, var2);
   }

   public DamageSource fireworks(FireworkRocketEntity var1, @Nullable Entity var2) {
      return this.source(DamageTypes.FIREWORKS, var1, var2);
   }

   public DamageSource fireball(Fireball var1, @Nullable Entity var2) {
      return var2 == null ? this.source(DamageTypes.UNATTRIBUTED_FIREBALL, var1) : this.source(DamageTypes.FIREBALL, var1, var2);
   }

   public DamageSource witherSkull(WitherSkull var1, Entity var2) {
      return this.source(DamageTypes.WITHER_SKULL, var1, var2);
   }

   public DamageSource thrown(Entity var1, @Nullable Entity var2) {
      return this.source(DamageTypes.THROWN, var1, var2);
   }

   public DamageSource indirectMagic(Entity var1, @Nullable Entity var2) {
      return this.source(DamageTypes.INDIRECT_MAGIC, var1, var2);
   }

   public DamageSource thorns(Entity var1) {
      return this.source(DamageTypes.THORNS, var1);
   }

   public DamageSource explosion(@Nullable Explosion var1) {
      return var1 != null ? this.explosion(var1.getDirectSourceEntity(), var1.getIndirectSourceEntity()) : this.explosion((Entity)null, (Entity)null);
   }

   public DamageSource explosion(@Nullable Entity var1, @Nullable Entity var2) {
      return this.source(var2 != null && var1 != null ? DamageTypes.PLAYER_EXPLOSION : DamageTypes.EXPLOSION, var1, var2);
   }

   public DamageSource sonicBoom(Entity var1) {
      return this.source(DamageTypes.SONIC_BOOM, var1);
   }

   public DamageSource badRespawnPointExplosion(Vec3 var1) {
      return new DamageSource(this.damageTypes.getHolderOrThrow(DamageTypes.BAD_RESPAWN_POINT), var1);
   }

   public DamageSource outOfBorder() {
      return this.outsideBorder;
   }

   public DamageSource genericKill() {
      return this.genericKill;
   }
}
