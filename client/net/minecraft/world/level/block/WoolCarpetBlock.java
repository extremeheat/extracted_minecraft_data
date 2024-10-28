package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WoolCarpetBlock extends CarpetBlock implements Equipable {
   public static final MapCodec<WoolCarpetBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(DyeColor.CODEC.fieldOf("color").forGetter(WoolCarpetBlock::getColor), propertiesCodec()).apply(var0, WoolCarpetBlock::new);
   });
   private final DyeColor color;

   public MapCodec<WoolCarpetBlock> codec() {
      return CODEC;
   }

   protected WoolCarpetBlock(DyeColor var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.color = var1;
   }

   public DyeColor getColor() {
      return this.color;
   }

   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.BODY;
   }

   public Holder<SoundEvent> getEquipSound() {
      return SoundEvents.LLAMA_SWAG;
   }
}
