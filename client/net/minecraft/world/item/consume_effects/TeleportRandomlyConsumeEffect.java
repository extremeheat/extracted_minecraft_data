package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record TeleportRandomlyConsumeEffect(float diameter, boolean directionalParticles) implements ConsumeEffect {
   private static final float DEFAULT_DIAMETER = 16.0F;
   public static final MapCodec<TeleportRandomlyConsumeEffect> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("diameter", 16.0F).forGetter(TeleportRandomlyConsumeEffect::diameter), Codec.BOOL.optionalFieldOf("directional_particles", true).forGetter(TeleportRandomlyConsumeEffect::directionalParticles)).apply(i, TeleportRandomlyConsumeEffect::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, TeleportRandomlyConsumeEffect> STREAM_CODEC;

   public TeleportRandomlyConsumeEffect() {
      this(16.0F, true);
   }

   public TeleportRandomlyConsumeEffect {
      super();
   }

   public ConsumeEffect.Type<TeleportRandomlyConsumeEffect> getType() {
      return ConsumeEffect.Type.TELEPORT_RANDOMLY;
   }

   public boolean apply(final Level level, final ItemStack stack, final LivingEntity user) {
      for(int attempt = 0; attempt < 16; ++attempt) {
         double xx = user.getX() + (user.getRandom().nextDouble() - 0.5) * (double)this.diameter;
         double yy = Mth.clamp(user.getY() + (user.getRandom().nextDouble() - 0.5) * (double)this.diameter, (double)level.getMinY(), (double)(level.getMinY() + ((ServerLevel)level).getLogicalHeight() - 1));
         double zz = user.getZ() + (user.getRandom().nextDouble() - 0.5) * (double)this.diameter;
         if (user.isPassenger()) {
            user.stopRiding();
         }

         Vec3 oldPos = user.position();
         if (user.randomTeleport(xx, yy, zz, true, BlockTags.CONSUMABLE_DOES_NOT_TELEPORT_TO)) {
            level.gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of((Entity)user));
            SoundSource soundSource;
            SoundEvent soundEvent;
            if (user instanceof Fox) {
               soundEvent = SoundEvents.FOX_TELEPORT;
               soundSource = SoundSource.NEUTRAL;
            } else {
               soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
               soundSource = SoundSource.PLAYERS;
            }

            level.playSound((Entity)null, user.getX(), user.getY(), user.getZ(), soundEvent, soundSource);
            if (this.directionalParticles) {
               BlockPos origin = BlockPos.containing(oldPos);
               BlockPos target = user.blockPosition();
               level.levelEvent(2017, origin, BlockUtil.clampedPackDifferenceInPosition(origin, target, 127, 127, 127));
            }

            user.resetFallDistance();
            user.resetCurrentImpulseContext();
            return true;
         }
      }

      return false;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, TeleportRandomlyConsumeEffect::diameter, ByteBufCodecs.BOOL, TeleportRandomlyConsumeEffect::directionalParticles, TeleportRandomlyConsumeEffect::new);
   }
}
