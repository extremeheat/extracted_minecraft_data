package net.minecraft.world.entity.decoration;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class BlockAttachedEntity extends Entity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private int checkInterval;
   protected BlockPos pos;

   protected BlockAttachedEntity(final EntityType<? extends BlockAttachedEntity> type, final Level level) {
      super(type, level);
   }

   protected BlockAttachedEntity(final EntityType<? extends BlockAttachedEntity> type, final Level level, final BlockPos pos) {
      this(type, level);
      this.pos = pos;
   }

   protected abstract void recalculateBoundingBox();

   public void tick() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel level) {
         this.checkBelowWorld();
         if (this.checkInterval++ == 100) {
            this.checkInterval = 0;
            if (!this.isRemoved() && !this.survives()) {
               this.discard();
               this.dropItem(level, (Entity)null);
            }
         }
      }

   }

   public abstract boolean survives();

   public boolean isPickable() {
      return true;
   }

   public boolean skipAttackInteraction(final Entity source) {
      if (source instanceof Player player) {
         return !this.level().mayInteract(player, this.pos) ? true : this.hurtOrSimulate(this.damageSources().playerAttack(player), 0.0F);
      } else {
         return false;
      }
   }

   public boolean hurtClient(final DamageSource source) {
      return !this.isInvulnerableToBase(source);
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      if (this.isInvulnerableToBase(source)) {
         return false;
      } else if (!(Boolean)level.getGameRules().get(GameRules.MOB_GRIEFING) && source.getEntity() instanceof Mob) {
         return false;
      } else {
         if (!this.isRemoved()) {
            this.kill(level);
            this.markHurt();
            this.dropItem(level, source.getEntity());
         }

         return true;
      }
   }

   public boolean ignoreExplosion(final Explosion explosion) {
      Entity directEntity = explosion.getDirectSourceEntity();
      if (directEntity != null && directEntity.isInWater()) {
         return true;
      } else {
         return explosion.shouldAffectBlocklikeEntities() ? super.ignoreExplosion(explosion) : true;
      }
   }

   public void move(final MoverType moverType, final Vec3 delta) {
      Level var4 = this.level();
      if (var4 instanceof ServerLevel level) {
         if (!this.isRemoved() && delta.lengthSqr() > 0.0) {
            this.kill(level);
            this.dropItem(level, (Entity)null);
         }
      }

   }

   public void push(final double xa, final double ya, final double za) {
      Level var8 = this.level();
      if (var8 instanceof ServerLevel level) {
         if (!this.isRemoved() && xa * xa + ya * ya + za * za > 0.0) {
            this.kill(level);
            this.dropItem(level, (Entity)null);
         }
      }

   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      output.store("block_pos", BlockPos.CODEC, this.getPos());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      BlockPos storedPos = (BlockPos)input.read("block_pos", BlockPos.CODEC).orElse((Object)null);
      if (storedPos != null && storedPos.closerThan(this.blockPosition(), 16.0)) {
         this.pos = storedPos;
      } else {
         LOGGER.error("Block-attached entity at invalid position: {}", storedPos);
      }
   }

   public abstract void dropItem(ServerLevel level, @Nullable Entity causedBy);

   protected boolean repositionEntityAfterLoad() {
      return false;
   }

   public void setPos(final double x, final double y, final double z) {
      this.pos = BlockPos.containing(x, y, z);
      this.recalculateBoundingBox();
      this.needsSync = true;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void thunderHit(final ServerLevel level, final LightningBolt lightningBolt) {
   }

   public void refreshDimensions() {
   }
}
