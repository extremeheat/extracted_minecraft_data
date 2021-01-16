package io.netty.handler.codec.smtp;

import java.util.List;

public interface SmtpResponse {
   int code();

   List<CharSequence> details();
}
