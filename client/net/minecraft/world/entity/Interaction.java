package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Interaction extends Entity implements Attackable, Targeting {
   private static final EntityDataAccessor<Float> DATA_WIDTH_ID;
   private static final EntityDataAccessor<Float> DATA_HEIGHT_ID;
   private static final EntityDataAccessor<Boolean> DATA_RESPONSE_ID;
   private static final String TAG_WIDTH = "width";
   private static final String TAG_HEIGHT = "height";
   private static final String TAG_ATTACK = "attack";
   private static final String TAG_INTERACTION = "interaction";
   private static final String TAG_RESPONSE = "response";
   private static final float DEFAULT_WIDTH = 1.0F;
   private static final float DEFAULT_HEIGHT = 1.0F;
   private static final boolean DEFAULT_RESPONSE = false;
   @Nullable
   private PlayerAction attack;
   @Nullable
   private PlayerAction interaction;

   public Interaction(EntityType<?> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_WIDTH_ID, 1.0F);
      var1.define(DATA_HEIGHT_ID, 1.0F);
      var1.define(DATA_RESPONSE_ID, false);
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      this.setWidth(var1.getFloatOr("width", 1.0F));
      this.setHeight(var1.getFloatOr("height", 1.0F));
      this.attack = (PlayerAction)var1.read("attack", Interaction.PlayerAction.CODEC).orElse((Object)null);
      this.interaction = (PlayerAction)var1.read("interaction", Interaction.PlayerAction.CODEC).orElse((Object)null);
      this.setResponse(var1.getBooleanOr("response", false));
      this.setBoundingBox(this.makeBoundingBox());
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      var1.putFloat("width", this.getWidth());
      var1.putFloat("height", this.getHeight());
      var1.storeNullable("attack", Interaction.PlayerAction.CODEC, this.attack);
      var1.storeNullable("interaction", Interaction.PlayerAction.CODEC, this.interaction);
      var1.putBoolean("response", this.getResponse());
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_HEIGHT_ID.equals(var1) || DATA_WIDTH_ID.equals(var1)) {
         this.refreshDimensions();
      }

   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public boolean isPickable() {
      return true;
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   public boolean skipAttackInteraction(Entity var1) {
      if (var1 instanceof Player var2) {
         this.attack = new PlayerAction(var2.getUUID(), this.level().getGameTime());
         if (var2 instanceof ServerPlayer var3) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(var3, this, var2.damageSources().generic(), 1.0F, 1.0F, false);
         }

         return !this.getResponse();
      } else {
         return false;
      }
   }

   public final boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      return false;
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      if (this.level().isClientSide()) {
         return this.getResponse() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      } else {
         this.interaction = new PlayerAction(var1.getUUID(), this.level().getGameTime());
         return InteractionResult.CONSUME;
      }
   }

   public void tick() {
   }

   @Nullable
   public LivingEntity getLastAttacker() {
      return this.attack != null ? this.level().getPlayerByUUID(this.attack.player()) : null;
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.interaction != null ? this.level().getPlayerByUUID(this.interaction.player()) : null;
   }

   private void setWidth(float var1) {
      this.entityData.set(DATA_WIDTH_ID, var1);
   }

   private float getWidth() {
      return (Float)this.entityData.get(DATA_WIDTH_ID);
   }

   private void setHeight(float var1) {
      this.entityData.set(DATA_HEIGHT_ID, var1);
   }

   private float getHeight() {
      return (Float)this.entityData.get(DATA_HEIGHT_ID);
   }

   private void setResponse(boolean var1) {
      this.entityData.set(DATA_RESPONSE_ID, var1);
   }

   private boolean getResponse() {
      return (Boolean)this.entityData.get(DATA_RESPONSE_ID);
   }

   private EntityDimensions getDimensions() {
      return EntityDimensions.scalable(this.getWidth(), this.getHeight());
   }

   public EntityDimensions getDimensions(Pose var1) {
      return this.getDimensions();
   }

   protected AABB makeBoundingBox(Vec3 var1) {
      return this.getDimensions().makeBoundingBox(var1);
   }

   static {
      DATA_WIDTH_ID = SynchedEntityData.<Float>defineId(Interaction.class, EntityDataSerializers.FLOAT);
      DATA_HEIGHT_ID = SynchedEntityData.<Float>defineId(Interaction.class, EntityDataSerializers.FLOAT);
      DATA_RESPONSE_ID = SynchedEntityData.<Boolean>defineId(Interaction.class, EntityDataSerializers.BOOLEAN);
   }

   static record PlayerAction(UUID player, long timestamp) {
      public static final Codec<PlayerAction> CODEC = RecordCodecBuilder.create((var0) -> var0.group(UUIDUtil.CODEC.fieldOf("player").forGetter(PlayerAction::player), Codec.LONG.fieldOf("timestamp").forGetter(PlayerAction::timestamp)).apply(var0, PlayerAction::new));

      PlayerAction(UUID var1, long var2) {
         super();
         this.player = var1;
         this.timestamp = var2;
      }
   }
}
