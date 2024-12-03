package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record TeleportRandomlyConsumeEffect(float diameter) implements ConsumeEffect {
   private static final float DEFAULT_DIAMETER = 16.0F;
   public static final MapCodec<TeleportRandomlyConsumeEffect> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("diameter", 16.0F).forGetter(TeleportRandomlyConsumeEffect::diameter)).apply(var0, TeleportRandomlyConsumeEffect::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, TeleportRandomlyConsumeEffect> STREAM_CODEC;

   public TeleportRandomlyConsumeEffect() {
      this(16.0F);
   }

   public TeleportRandomlyConsumeEffect(float var1) {
      super();
      this.diameter = var1;
   }

   public ConsumeEffect.Type<TeleportRandomlyConsumeEffect> getType() {
      return ConsumeEffect.Type.TELEPORT_RANDOMLY;
   }

   public boolean apply(Level var1, ItemStack var2, LivingEntity var3) {
      boolean var4 = false;

      for(int var5 = 0; var5 < 16; ++var5) {
         double var6 = var3.getX() + (var3.getRandom().nextDouble() - 0.5) * (double)this.diameter;
         double var8 = Mth.clamp(var3.getY() + (var3.getRandom().nextDouble() - 0.5) * (double)this.diameter, (double)var1.getMinY(), (double)(var1.getMinY() + ((ServerLevel)var1).getLogicalHeight() - 1));
         double var10 = var3.getZ() + (var3.getRandom().nextDouble() - 0.5) * (double)this.diameter;
         if (var3.isPassenger()) {
            var3.stopRiding();
         }

         Vec3 var12 = var3.position();
         if (var3.randomTeleport(var6, var8, var10, true)) {
            var1.gameEvent(GameEvent.TELEPORT, var12, GameEvent.Context.of((Entity)var3));
            SoundSource var13;
            SoundEvent var14;
            if (var3 instanceof Fox) {
               var14 = SoundEvents.FOX_TELEPORT;
               var13 = SoundSource.NEUTRAL;
            } else {
               var14 = SoundEvents.CHORUS_FRUIT_TELEPORT;
               var13 = SoundSource.PLAYERS;
            }

            var1.playSound((Player)null, var3.getX(), var3.getY(), var3.getZ(), var14, var13);
            var3.resetFallDistance();
            var4 = true;
            break;
         }
      }

      if (var4 && var3 instanceof Player var15) {
         var15.resetCurrentImpulseContext();
      }

      return var4;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, TeleportRandomlyConsumeEffect::diameter, TeleportRandomlyConsumeEffect::new);
   }
}
