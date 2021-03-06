package io.sentry;

import io.sentry.util.Nullable;

/** Sentry SDK internal diagnostic logger. */
class DiagnosticLogger implements ILogger {
  private SentryOptions options;
  private ILogger logger;

  /**
   * Creates a new instance of DiagnosticLogger with the wrapped ILogger.
   *
   * @param options
   * @param logger
   */
  DiagnosticLogger(SentryOptions options, @Nullable ILogger logger) {
    if (options == null) {
      throw new IllegalArgumentException("SentryOptions is required.");
    }
    this.options = options;
    this.logger = logger;
  }

  /**
   * Whether this logger is enabled for the specified SentryLevel.
   *
   * @param level The SentryLevel to test against.
   * @return True if a log message would be recorded for the level. Otherwise false.
   */
  public boolean isEnabled(SentryLevel level) {
    SentryLevel diagLevel = options.getDiagnosticLevel();
    if (level == null || diagLevel == null) {
      return false;
    }
    return options.isDebug() && level.ordinal() >= diagLevel.ordinal();
  }

  /**
   * Logs a message with the specified level, message and optional arguments.
   *
   * @param level The SentryLevel.
   * @param message The message.
   * @param args The optional arguments to format the message.
   */
  @Override
  public void log(SentryLevel level, String message, Object... args) {
    if (logger != null && isEnabled(level)) {
      logger.log(level, message, args);
    }
  }

  /**
   * Logs a message with the specified level, message and throwable.
   *
   * @param level The SentryLevel.
   * @param message The message.
   * @param throwable The throwable to log.
   */
  @Override
  public void log(SentryLevel level, String message, Throwable throwable) {
    if (logger != null && isEnabled(level)) {
      logger.log(level, message, throwable);
    }
  }
}
