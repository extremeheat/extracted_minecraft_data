package net.minecraft.world.level.block.state.properties;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum TestBlockMode implements StringRepresentable {
   START(0, "start"),
   LOG(1, "log"),
   FAIL(2, "fail"),
   ACCEPT(3, "accept");

   public static final Codec<TestBlockMode> CODEC = StringRepresentable.<TestBlockMode>fromEnum(TestBlockMode::values);
   public static final StreamCodec<ByteBuf, TestBlockMode> STREAM_CODEC = ByteBufCodecs.enumCodec(TestBlockMode.class, (mode) -> mode.id);
   private final int id;
   private final String name;
   private final Component displayName;
   private final Component detailedMessage;

   private TestBlockMode(final int id, final String name) {
      this.id = id;
      this.name = name;
      this.displayName = Component.translatable("test_block.mode." + name);
      this.detailedMessage = Component.translatable("test_block.mode_info." + name);
   }

   public String getSerializedName() {
      return this.name;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public Component getDetailedMessage() {
      return this.detailedMessage;
   }

   // $FF: synthetic method
   private static TestBlockMode[] $values() {
      return new TestBlockMode[]{START, LOG, FAIL, ACCEPT};
   }
}
