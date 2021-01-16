package org.apache.logging.log4j.message;

public interface FlowMessageFactory {
   EntryMessage newEntryMessage(Message var1);

   ExitMessage newExitMessage(Object var1, Message var2);

   ExitMessage newExitMessage(EntryMessage var1);

   ExitMessage newExitMessage(Object var1, EntryMessage var2);
}
