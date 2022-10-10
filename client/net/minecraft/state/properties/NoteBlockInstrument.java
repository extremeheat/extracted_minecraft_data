package net.minecraft.state.properties;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundEvent;

public enum NoteBlockInstrument implements IStringSerializable {
   HARP("harp", SoundEvents.field_187682_dG),
   BASEDRUM("basedrum", SoundEvents.field_187676_dE),
   SNARE("snare", SoundEvents.field_187688_dI),
   HAT("hat", SoundEvents.field_187685_dH),
   BASS("bass", SoundEvents.field_187679_dF),
   FLUTE("flute", SoundEvents.field_193809_ey),
   BELL("bell", SoundEvents.field_193807_ew),
   GUITAR("guitar", SoundEvents.field_193810_ez),
   CHIME("chime", SoundEvents.field_193808_ex),
   XYLOPHONE("xylophone", SoundEvents.field_193785_eE);

   private final String field_196042_k;
   private final SoundEvent field_196043_l;

   private NoteBlockInstrument(String var3, SoundEvent var4) {
      this.field_196042_k = var3;
      this.field_196043_l = var4;
   }

   public String func_176610_l() {
      return this.field_196042_k;
   }

   public SoundEvent func_208088_a() {
      return this.field_196043_l;
   }

   public static NoteBlockInstrument func_208087_a(IBlockState var0) {
      Block var1 = var0.func_177230_c();
      if (var1 == Blocks.field_150435_aG) {
         return FLUTE;
      } else if (var1 == Blocks.field_150340_R) {
         return BELL;
      } else if (var1.func_203417_a(BlockTags.field_199897_a)) {
         return GUITAR;
      } else if (var1 == Blocks.field_150403_cj) {
         return CHIME;
      } else if (var1 == Blocks.field_189880_di) {
         return XYLOPHONE;
      } else {
         Material var2 = var0.func_185904_a();
         if (var2 == Material.field_151576_e) {
            return BASEDRUM;
         } else if (var2 == Material.field_151595_p) {
            return SNARE;
         } else if (var2 == Material.field_151592_s) {
            return HAT;
         } else {
            return var2 == Material.field_151575_d ? BASS : HARP;
         }
      }
   }
}
