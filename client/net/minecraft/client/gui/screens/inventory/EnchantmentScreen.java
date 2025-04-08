package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
   private static final ResourceLocation[] ENABLED_LEVEL_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3")};
   private static final ResourceLocation[] DISABLED_LEVEL_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1_disabled"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2_disabled"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3_disabled")};
   private static final ResourceLocation ENCHANTMENT_SLOT_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
   private static final ResourceLocation ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_highlighted");
   private static final ResourceLocation ENCHANTMENT_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot");
   private static final ResourceLocation ENCHANTING_TABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation ENCHANTING_BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enchanting_table_book.png");
   private final RandomSource random = RandomSource.create();
   private BookModel bookModel;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private ItemStack last;

   public EnchantmentScreen(EnchantmentMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.last = ItemStack.EMPTY;
   }

   protected void init() {
      super.init();
      this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
   }

   public void containerTick() {
      super.containerTick();
      this.minecraft.player.experienceDisplayStartTick = this.minecraft.player.tickCount;
      this.tickBook();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = (this.width - this.imageWidth) / 2;
      int var7 = (this.height - this.imageHeight) / 2;

      for(int var8 = 0; var8 < 3; ++var8) {
         double var9 = var1 - (double)(var6 + 60);
         double var11 = var3 - (double)(var7 + 14 + 19 * var8);
         if (var9 >= 0.0 && var11 >= 0.0 && var9 < 108.0 && var11 < 19.0 && ((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, var8)) {
            this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, var8);
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(RenderPipelines.GUI_TEXTURED, ENCHANTING_TABLE_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      this.renderBook(var1, var5, var6);
      EnchantmentNames.getInstance().initSeed((long)((EnchantmentMenu)this.menu).getEnchantmentSeed());
      int var7 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var8 = 0; var8 < 3; ++var8) {
         int var9 = var5 + 60;
         int var10 = var9 + 20;
         int var11 = (this.menu).costs[var8];
         if (var11 == 0) {
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ENCHANTMENT_SLOT_DISABLED_SPRITE, var9, var6 + 14 + 19 * var8, 108, 19);
         } else {
            String var12 = "" + var11;
            int var13 = 86 - this.font.width(var12);
            FormattedText var14 = EnchantmentNames.getInstance().getRandomName(this.font, var13);
            int var15 = 6839882;
            if ((var7 < var8 + 1 || this.minecraft.player.experienceLevel < var11) && !this.minecraft.player.hasInfiniteMaterials()) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ENCHANTMENT_SLOT_DISABLED_SPRITE, var9, var6 + 14 + 19 * var8, 108, 19);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)DISABLED_LEVEL_SPRITES[var8], var9 + 1, var6 + 15 + 19 * var8, 16, 16);
               var1.drawWordWrap(this.font, var14, var10, var6 + 16 + 19 * var8, var13, (var15 & 16711422) >> 1, false);
               var15 = 4226832;
            } else {
               int var16 = var3 - (var5 + 60);
               int var17 = var4 - (var6 + 14 + 19 * var8);
               if (var16 >= 0 && var17 >= 0 && var16 < 108 && var17 < 19) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE, var9, var6 + 14 + 19 * var8, 108, 19);
                  var15 = 16777088;
               } else {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ENCHANTMENT_SLOT_SPRITE, var9, var6 + 14 + 19 * var8, 108, 19);
               }

               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ENABLED_LEVEL_SPRITES[var8], var9 + 1, var6 + 15 + 19 * var8, 16, 16);
               var1.drawWordWrap(this.font, var14, var10, var6 + 16 + 19 * var8, var13, var15, false);
               var15 = 8453920;
            }

            var1.drawString(this.font, var12, var10 + 86 - this.font.width(var12), var6 + 16 + 19 * var8 + 7, var15);
         }
      }

   }

   private void renderBook(GuiGraphics var1, int var2, int var3) {
      float var4 = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
      float var5 = Mth.lerp(var4, this.oOpen, this.open);
      float var6 = Mth.lerp(var4, this.oFlip, this.flip);
      int var7 = var2 + 14;
      int var8 = var3 + 14;
      int var9 = var7 + 38;
      int var10 = var8 + 31;
      var1.submitBookModelRenderState(this.bookModel, ENCHANTING_BOOK_LOCATION, 40.0F, var5, var6, var7, var8, var9, var10);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      float var5 = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
      super.render(var1, var2, var3, var5);
      this.renderTooltip(var1, var2, var3);
      boolean var6 = this.minecraft.player.hasInfiniteMaterials();
      int var7 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var8 = 0; var8 < 3; ++var8) {
         int var9 = (this.menu).costs[var8];
         Optional var10 = this.minecraft.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get((this.menu).enchantClue[var8]);
         if (!var10.isEmpty()) {
            int var11 = (this.menu).levelClue[var8];
            int var12 = var8 + 1;
            if (this.isHovering(60, 14 + 19 * var8, 108, 17, (double)var2, (double)var3) && var9 > 0 && var11 >= 0 && var10 != null) {
               ArrayList var13 = Lists.newArrayList();
               var13.add(Component.translatable("container.enchant.clue", Enchantment.getFullname((Holder)var10.get(), var11)).withStyle(ChatFormatting.WHITE));
               if (!var6) {
                  var13.add(CommonComponents.EMPTY);
                  if (this.minecraft.player.experienceLevel < var9) {
                     var13.add(Component.translatable("container.enchant.level.requirement", (this.menu).costs[var8]).withStyle(ChatFormatting.RED));
                  } else {
                     MutableComponent var14;
                     if (var12 == 1) {
                        var14 = Component.translatable("container.enchant.lapis.one");
                     } else {
                        var14 = Component.translatable("container.enchant.lapis.many", var12);
                     }

                     var13.add(var14.withStyle(var7 >= var12 ? ChatFormatting.GRAY : ChatFormatting.RED));
                     MutableComponent var15;
                     if (var12 == 1) {
                        var15 = Component.translatable("container.enchant.level.one");
                     } else {
                        var15 = Component.translatable("container.enchant.level.many", var12);
                     }

                     var13.add(var15.withStyle(ChatFormatting.GRAY));
                  }
               }

               var1.renderComponentTooltip(this.font, var13, var2, var3);
               break;
            }
         }
      }

   }

   public void tickBook() {
      ItemStack var1 = ((EnchantmentMenu)this.menu).getSlot(0).getItem();
      if (!ItemStack.matches(var1, this.last)) {
         this.last = var1;

         do {
            this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
         } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
      }

      this.oFlip = this.flip;
      this.oOpen = this.open;
      boolean var2 = false;

      for(int var3 = 0; var3 < 3; ++var3) {
         if ((this.menu).costs[var3] != 0) {
            var2 = true;
         }
      }

      if (var2) {
         this.open += 0.2F;
      } else {
         this.open -= 0.2F;
      }

      this.open = Mth.clamp(this.open, 0.0F, 1.0F);
      float var5 = (this.flipT - this.flip) * 0.4F;
      float var4 = 0.2F;
      var5 = Mth.clamp(var5, -0.2F, 0.2F);
      this.flipA += (var5 - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }
}
