package io.sentry;

public interface IHub extends ISentryClient {
  IHub clone();
}
