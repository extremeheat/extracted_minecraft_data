package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public enum MusicTicker$MusicType {
   MENU(new ResourceLocation("minecraft:music.menu"), 20, 600),
   GAME(new ResourceLocation("minecraft:music.game"), 12000, 24000),
   CREATIVE(new ResourceLocation("minecraft:music.game.creative"), 1200, 3600),
   CREDITS(new ResourceLocation("minecraft:music.game.end.credits"), 2147483647, 2147483647),
   NETHER(new ResourceLocation("minecraft:music.game.nether"), 1200, 3600),
   END_BOSS(new ResourceLocation("minecraft:music.game.end.dragon"), 0, 0),
   END(new ResourceLocation("minecraft:music.game.end"), 6000, 24000);

   private final ResourceLocation field_148645_h;
   private final int field_148646_i;
   private final int field_148643_j;

   private MusicTicker$MusicType(ResourceLocation var3, int var4, int var5) {
      this.field_148645_h = var3;
      this.field_148646_i = var4;
      this.field_148643_j = var5;
   }

   public ResourceLocation func_148635_a() {
      return this.field_148645_h;
   }

   public int func_148634_b() {
      return this.field_148646_i;
   }

   public int func_148633_c() {
      return this.field_148643_j;
   }
}
