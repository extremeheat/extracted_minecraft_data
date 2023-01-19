package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class DamageSource {
   public static final DamageSource IN_FIRE = new DamageSource("inFire").bypassArmor().setIsFire();
   public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
   public static final DamageSource ON_FIRE = new DamageSource("onFire").bypassArmor().setIsFire();
   public static final DamageSource LAVA = new DamageSource("lava").setIsFire();
   public static final DamageSource HOT_FLOOR = new DamageSource("hotFloor").setIsFire();
   public static final DamageSource IN_WALL = new DamageSource("inWall").bypassArmor();
   public static final DamageSource CRAMMING = new DamageSource("cramming").bypassArmor();
   public static final DamageSource DROWN = new DamageSource("drown").bypassArmor();
   public static final DamageSource STARVE = new DamageSource("starve").bypassArmor().bypassMagic();
   public static final DamageSource CACTUS = new DamageSource("cactus");
   public static final DamageSource FALL = new DamageSource("fall").bypassArmor().setIsFall();
   public static final DamageSource FLY_INTO_WALL = new DamageSource("flyIntoWall").bypassArmor();
   public static final DamageSource OUT_OF_WORLD = new DamageSource("outOfWorld").bypassArmor().bypassInvul();
   public static final DamageSource GENERIC = new DamageSource("generic").bypassArmor();
   public static final DamageSource MAGIC = new DamageSource("magic").bypassArmor().setMagic();
   public static final DamageSource WITHER = new DamageSource("wither").bypassArmor();
   public static final DamageSource DRAGON_BREATH = new DamageSource("dragonBreath").bypassArmor();
   public static final DamageSource DRY_OUT = new DamageSource("dryout");
   public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
   public static final DamageSource FREEZE = new DamageSource("freeze").bypassArmor();
   public static final DamageSource STALAGMITE = new DamageSource("stalagmite").bypassArmor().setIsFall();
   private boolean damageHelmet;
   private boolean bypassArmor;
   private boolean bypassInvul;
   private boolean bypassMagic;
   private boolean bypassEnchantments;
   private float exhaustion = 0.1F;
   private boolean isFireSource;
   private boolean isProjectile;
   private boolean scalesWithDifficulty;
   private boolean isMagic;
   private boolean isExplosion;
   private boolean isFall;
   private boolean noAggro;
   public final String msgId;

   public static DamageSource fallingBlock(Entity var0) {
      return new EntityDamageSource("fallingBlock", var0).damageHelmet();
   }

   public static DamageSource anvil(Entity var0) {
      return new EntityDamageSource("anvil", var0).damageHelmet();
   }

   public static DamageSource fallingStalactite(Entity var0) {
      return new EntityDamageSource("fallingStalactite", var0).damageHelmet();
   }

   public static DamageSource sting(LivingEntity var0) {
      return new EntityDamageSource("sting", var0);
   }

   public static DamageSource mobAttack(LivingEntity var0) {
      return new EntityDamageSource("mob", var0);
   }

   public static DamageSource indirectMobAttack(Entity var0, @Nullable LivingEntity var1) {
      return new IndirectEntityDamageSource("mob", var0, var1);
   }

   public static DamageSource playerAttack(Player var0) {
      return new EntityDamageSource("player", var0);
   }

   public static DamageSource arrow(AbstractArrow var0, @Nullable Entity var1) {
      return new IndirectEntityDamageSource("arrow", var0, var1).setProjectile();
   }

   public static DamageSource trident(Entity var0, @Nullable Entity var1) {
      return new IndirectEntityDamageSource("trident", var0, var1).setProjectile();
   }

   public static DamageSource fireworks(FireworkRocketEntity var0, @Nullable Entity var1) {
      return new IndirectEntityDamageSource("fireworks", var0, var1).setExplosion();
   }

   public static DamageSource fireball(Fireball var0, @Nullable Entity var1) {
      return var1 == null
         ? new IndirectEntityDamageSource("onFire", var0, var0).setIsFire().setProjectile()
         : new IndirectEntityDamageSource("fireball", var0, var1).setIsFire().setProjectile();
   }

   public static DamageSource witherSkull(WitherSkull var0, Entity var1) {
      return new IndirectEntityDamageSource("witherSkull", var0, var1).setProjectile();
   }

   public static DamageSource thrown(Entity var0, @Nullable Entity var1) {
      return new IndirectEntityDamageSource("thrown", var0, var1).setProjectile();
   }

   public static DamageSource indirectMagic(Entity var0, @Nullable Entity var1) {
      return new IndirectEntityDamageSource("indirectMagic", var0, var1).bypassArmor().setMagic();
   }

   public static DamageSource thorns(Entity var0) {
      return new EntityDamageSource("thorns", var0).setThorns().setMagic();
   }

   public static DamageSource explosion(@Nullable Explosion var0) {
      return var0 != null ? explosion(var0.getDirectSourceEntity(), var0.getIndirectSourceEntity()) : explosion(null, null);
   }

   public static DamageSource explosion(@Nullable Entity var0, @Nullable Entity var1) {
      if (var1 != null && var0 != null) {
         return new IndirectEntityDamageSource("explosion.player", var0, var1).setScalesWithDifficulty().setExplosion();
      } else {
         return var0 != null
            ? new EntityDamageSource("explosion", var0).setScalesWithDifficulty().setExplosion()
            : new DamageSource("explosion").setScalesWithDifficulty().setExplosion();
      }
   }

   public static DamageSource sonicBoom(Entity var0) {
      return new EntityDamageSource("sonic_boom", var0).bypassArmor().bypassEnchantments().setMagic();
   }

   public static DamageSource badRespawnPointExplosion(Vec3 var0) {
      return new BadRespawnPointDamage(var0);
   }

   @Override
   public String toString() {
      return "DamageSource (" + this.msgId + ")";
   }

   public boolean isProjectile() {
      return this.isProjectile;
   }

   public DamageSource setProjectile() {
      this.isProjectile = true;
      return this;
   }

   public boolean isExplosion() {
      return this.isExplosion;
   }

   public DamageSource setExplosion() {
      this.isExplosion = true;
      return this;
   }

   public boolean isBypassArmor() {
      return this.bypassArmor;
   }

   public boolean isDamageHelmet() {
      return this.damageHelmet;
   }

   public float getFoodExhaustion() {
      return this.exhaustion;
   }

   public boolean isBypassInvul() {
      return this.bypassInvul;
   }

   public boolean isBypassMagic() {
      return this.bypassMagic;
   }

   public boolean isBypassEnchantments() {
      return this.bypassEnchantments;
   }

   protected DamageSource(String var1) {
      super();
      this.msgId = var1;
   }

   @Nullable
   public Entity getDirectEntity() {
      return this.getEntity();
   }

   @Nullable
   public Entity getEntity() {
      return null;
   }

   protected DamageSource bypassArmor() {
      this.bypassArmor = true;
      this.exhaustion = 0.0F;
      return this;
   }

   protected DamageSource damageHelmet() {
      this.damageHelmet = true;
      return this;
   }

   protected DamageSource bypassInvul() {
      this.bypassInvul = true;
      return this;
   }

   protected DamageSource bypassMagic() {
      this.bypassMagic = true;
      this.exhaustion = 0.0F;
      return this;
   }

   protected DamageSource bypassEnchantments() {
      this.bypassEnchantments = true;
      return this;
   }

   protected DamageSource setIsFire() {
      this.isFireSource = true;
      return this;
   }

   public DamageSource setNoAggro() {
      this.noAggro = true;
      return this;
   }

   public Component getLocalizedDeathMessage(LivingEntity var1) {
      LivingEntity var2 = var1.getKillCredit();
      String var3 = "death.attack." + this.msgId;
      String var4 = var3 + ".player";
      return var2 != null ? Component.translatable(var4, var1.getDisplayName(), var2.getDisplayName()) : Component.translatable(var3, var1.getDisplayName());
   }

   public boolean isFire() {
      return this.isFireSource;
   }

   public boolean isNoAggro() {
      return this.noAggro;
   }

   public String getMsgId() {
      return this.msgId;
   }

   public DamageSource setScalesWithDifficulty() {
      this.scalesWithDifficulty = true;
      return this;
   }

   public boolean scalesWithDifficulty() {
      return this.scalesWithDifficulty;
   }

   public boolean isMagic() {
      return this.isMagic;
   }

   public DamageSource setMagic() {
      this.isMagic = true;
      return this;
   }

   public boolean isFall() {
      return this.isFall;
   }

   public DamageSource setIsFall() {
      this.isFall = true;
      return this;
   }

   public boolean isCreativePlayer() {
      Entity var1 = this.getEntity();
      return var1 instanceof Player && ((Player)var1).getAbilities().instabuild;
   }

   @Nullable
   public Vec3 getSourcePosition() {
      return null;
   }
}
