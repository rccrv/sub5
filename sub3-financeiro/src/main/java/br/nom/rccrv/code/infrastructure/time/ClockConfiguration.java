package br.nom.rccrv.code.infrastructure.time;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.time.Clock;

@ApplicationScoped
public class ClockConfiguration {

  @Produces
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
