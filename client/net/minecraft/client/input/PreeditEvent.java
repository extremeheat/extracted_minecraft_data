package net.minecraft.client.input;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

public record PreeditEvent(String fullText, int caretPosition, List<String> blocks, int focusedBlock) {
   public PreeditEvent {
      super();
      Preconditions.checkElementIndex(focusedBlock, blocks.size());
   }

   public static @Nullable PreeditEvent fromSdlTextEditing(final @Nullable String text, final int selectionStart, final int selectionLength) {
      if (text != null && !text.isEmpty()) {
         int totalCodepoints = text.codePointCount(0, text.length());
         int caretCodepoint = selectionStart < 0 ? totalCodepoints : Math.min(selectionStart, totalCodepoints);
         int caretChar = text.offsetByCodePoints(0, caretCodepoint);
         if (selectionLength > 0) {
            int selectionEndCodepoint = Math.min(caretCodepoint + selectionLength, totalCodepoints);
            int selectionEndChar = text.offsetByCodePoints(0, selectionEndCodepoint);
            if (selectionEndChar <= caretChar) {
               return new PreeditEvent(text, caretChar, List.of(text), 0);
            } else {
               ImmutableList.Builder<String> blocks = ImmutableList.builder();
               int focusedBlock = 0;
               if (caretChar > 0) {
                  blocks.add(text.substring(0, caretChar));
                  focusedBlock = 1;
               }

               blocks.add(text.substring(caretChar, selectionEndChar));
               if (selectionEndChar < text.length()) {
                  blocks.add(text.substring(selectionEndChar));
               }

               return new PreeditEvent(text, caretChar, blocks.build(), focusedBlock);
            }
         } else {
            return new PreeditEvent(text, caretChar, ImmutableList.of(text), 0);
         }
      } else {
         return null;
      }
   }

   public MutableComponent toFormattedText(final Style focusedStyle) {
      int blockCount = this.blocks.size();
      if (blockCount == 1) {
         return Component.literal((String)this.blocks.getFirst()).withStyle(focusedStyle);
      } else {
         MutableComponent result = Component.empty();

         for(int i = 0; i < blockCount; ++i) {
            MutableComponent part = Component.literal((String)this.blocks.get(i));
            if (i == this.focusedBlock) {
               part.withStyle(focusedStyle);
            }

            result.append((Component)part);
         }

         return result;
      }
   }
}
