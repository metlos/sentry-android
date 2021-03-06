package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.util.NonNull;

public final class Sentry {

  private Sentry() {}

  private static ThreadLocal<IHub> currentHub = new ThreadLocal<>();

  private static volatile IHub mainHub = NoOpHub.getInstance();

  static IHub getCurrentHub() {
    IHub hub = currentHub.get();
    if (hub == null) {
      currentHub.set(mainHub.clone());
    }
    return currentHub.get();
  }

  public static boolean isEnabled() {
    return getCurrentHub().isEnabled();
  }

  public static void init() {
    init(new SentryOptions());
  }

  public static void init(@NonNull OptionsConfiguration optionsConfiguration) {
    SentryOptions options = new SentryOptions();
    if (optionsConfiguration != null) {
      optionsConfiguration.configure(options);
    }
    init(options);
  }

  static synchronized void init(@NonNull SentryOptions options) {
    String dsn = options.getDsn();
    if (dsn == null || dsn.isEmpty()) {
      close();
      return;
    }

    Dsn parsedDsn = new Dsn(dsn);

    ILogger logger = options.getLogger();
    if (logger != null) {
      logger.log(SentryLevel.Info, "Initializing SDK with DSN: '%d'", options.getDsn());
    }

    IHub hub = getCurrentHub();
    mainHub = new Hub(options);
    currentHub.set(mainHub);
    hub.close();
  }

  public static synchronized void close() {
    IHub hub = getCurrentHub();
    mainHub = NoOpHub.getInstance();
    hub.close();
  }

  public static SentryId captureEvent(SentryEvent event) {
    return getCurrentHub().captureEvent(event);
  }

  public static SentryId captureMessage(String message) {
    return getCurrentHub().captureMessage(message);
  }

  public static SentryId captureException(Throwable throwable) {
    return getCurrentHub().captureException(throwable);
  }

  public interface OptionsConfiguration {
    void configure(SentryOptions options);
  }
}
