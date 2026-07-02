package com.utp.myapp.shared.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events.
 */
public interface DomainEvent {

    /** Unique identifier for this event instance. */
    default String eventId() {
        return UUID.randomUUID().toString();
    }

    /** When the event occurred. */
    default Instant occurredAt() {
        return Instant.now();
    }

    /** The type/name of this event for routing. */
    String eventType();

    /** The aggregate identifier that emitted this event. */
    String aggregateId();
}
