package net.minecraft.world.entity.player;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record Input(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean shift, boolean sprint) {
   private static final byte FLAG_FORWARD = 1;
   private static final byte FLAG_BACKWARD = 2;
   private static final byte FLAG_LEFT = 4;
   private static final byte FLAG_RIGHT = 8;
   private static final byte FLAG_JUMP = 16;
   private static final byte FLAG_SHIFT = 32;
   private static final byte FLAG_SPRINT = 64;
   public static final StreamCodec<FriendlyByteBuf, Input> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, Input>() {
      public void encode(FriendlyByteBuf var1, Input var2) {
         byte var3 = 0;
         var3 = (byte)(var3 | (var2.forward() ? 1 : 0));
         var3 = (byte)(var3 | (var2.backward() ? 2 : 0));
         var3 = (byte)(var3 | (var2.left() ? 4 : 0));
         var3 = (byte)(var3 | (var2.right() ? 8 : 0));
         var3 = (byte)(var3 | (var2.jump() ? 16 : 0));
         var3 = (byte)(var3 | (var2.shift() ? 32 : 0));
         var3 = (byte)(var3 | (var2.sprint() ? 64 : 0));
         var1.writeByte(var3);
      }

      public Input decode(FriendlyByteBuf var1) {
         byte var2 = var1.readByte();
         boolean var3 = (var2 & 1) != 0;
         boolean var4 = (var2 & 2) != 0;
         boolean var5 = (var2 & 4) != 0;
         boolean var6 = (var2 & 8) != 0;
         boolean var7 = (var2 & 16) != 0;
         boolean var8 = (var2 & 32) != 0;
         boolean var9 = (var2 & 64) != 0;
         return new Input(var3, var4, var5, var6, var7, var8, var9);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((FriendlyByteBuf)var1, (Input)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((FriendlyByteBuf)var1);
      }
   };
   public static Input EMPTY = new Input(false, false, false, false, false, false, false);

   public Input(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      super();
      this.forward = var1;
      this.backward = var2;
      this.left = var3;
      this.right = var4;
      this.jump = var5;
      this.shift = var6;
      this.sprint = var7;
   }
}
