package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record Weapon(int damagePerAttack, boolean canDisableBlocking) {
   public static final Codec<Weapon> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("damage_per_attack", 1).forGetter(Weapon::damagePerAttack), Codec.BOOL.optionalFieldOf("can_disable_blocking", false).forGetter(Weapon::canDisableBlocking)).apply(var0, Weapon::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, Weapon> STREAM_CODEC;

   public Weapon(int var1, boolean var2) {
      super();
      this.damagePerAttack = var1;
      this.canDisableBlocking = var2;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Weapon::damagePerAttack, ByteBufCodecs.BOOL, Weapon::canDisableBlocking, Weapon::new);
   }
}
